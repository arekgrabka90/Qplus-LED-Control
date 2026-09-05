package com.arekgrabka90.qplusledcontrol.led

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Represents the result of an LED operation.
 */
data class LEDOpResult(
    val success: Boolean,
    val message: String,
    val accessMethod: LEDAccessMethod = LEDAccessMethod.UNKNOWN
)

/**
 * Enum for LED access methods tried.
 */
enum class LEDAccessMethod {
    SYSTEM_API,
    PRIVILEGED,
    ROOT_SU,
    SYSFS,
    UNKNOWN,
    UNAVAILABLE
}

/**
 * Represents the current LED state.
 */
data class LEDState(
    val normalLED: LEDStatus = LEDStatus.UNKNOWN,
    val standbyLED: LEDStatus = LEDStatus.UNKNOWN,
    val networkLED: LEDStatus = LEDStatus.UNKNOWN,
    val accessMethod: LEDAccessMethod = LEDAccessMethod.UNKNOWN,
    val hasRootAccess: Boolean = false,
    val hasSysfsAccess: Boolean = false
)

/**
 * Enum for individual LED status.
 */
enum class LEDStatus {
    ON,
    OFF,
    UNAVAILABLE,
    UNKNOWN
}

/**
 * Central LED control abstraction.
 * Implements multi-tier access strategy:
 * 1. Manufacturer/system API
 * 2. Privileged/system mechanism
 * 3. Root/su
 * 4. Direct sysfs/GPIO access
 * 5. Graceful "unavailable" state with diagnostics
 */
interface ILEDController {
    suspend fun setNormalLED(on: Boolean): LEDOpResult
    suspend fun setStandbyLED(on: Boolean): LEDOpResult
    suspend fun setNetworkLED(on: Boolean): LEDOpResult
    suspend fun getState(): LEDState
    suspend fun testAllLEDs(): LEDOpResult
    suspend fun testNormalLED(): LEDOpResult
    suspend fun testStandbyLED(): LEDOpResult
    suspend fun testNetworkLED(): LEDOpResult
}

/**
 * Default implementation of LED controller.
 * Attempts multiple access methods to control LEDs.
 */
class LEDController(private val context: Context) : ILEDController {

    companion object {
        private const val TAG = "LEDController"
        private const val NORMAL_LED_PATH = "/sys/class/gpio_sw/normal_led/light"
        private const val STANDBY_LED_PATH = "/sys/class/gpio_sw/standby_led/light"
        private const val NETWORK_LED_PATH = "/sys/class/gpio_sw/network_led/light"
        private const val TEST_DURATION_MS = 500L
    }

    private var cachedState = LEDState()
    private var rootProcess: Process? = null

    init {
        Log.d(TAG, "LEDController initialized")
    }

    /**
     * Try to get root access via 'su' command.
     */
    private suspend fun obtainRootAccess(): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            rootProcess = Runtime.getRuntime().exec("su")
            rootProcess != null
        } catch (e: Exception) {
            Log.d(TAG, "Root access failed: ${e.message}")
            false
        }
    }

    /**
     * Execute shell command with root if available.
     */
    private suspend fun executeCommand(command: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val process = if (rootProcess?.isAlive == true) {
                // Use existing root process
                rootProcess?.outputStream?.write("$command\n".toByteArray())
                rootProcess?.outputStream?.flush()
                rootProcess
            } else {
                // Try direct execution
                Runtime.getRuntime().exec(command)
            }
            process?.waitFor() == 0
        } catch (e: Exception) {
            Log.d(TAG, "Command execution failed: ${e.message}")
            false
        }
    }

    /**
     * Try to write directly to sysfs file.
     */
    private suspend fun writeSysfsFile(path: String, value: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val file = File(path)
            if (!file.exists()) {
                Log.w(TAG, "Sysfs file does not exist: $path")
                return@withContext false
            }
            if (!file.canWrite()) {
                Log.w(TAG, "Sysfs file is not writable: $path")
                return@withContext false
            }
            file.writeText(value)
            Log.d(TAG, "Successfully wrote to $path: $value")
            true
        } catch (e: Exception) {
            Log.w(TAG, "Failed to write to sysfs $path: ${e.message}")
            false
        }
    }

    /**
     * Try to read from sysfs file.
     */
    private suspend fun readSysfsFile(path: String): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val file = File(path)
            if (!file.exists() || !file.canRead()) {
                Log.w(TAG, "Cannot read sysfs file: $path")
                return@withContext null
            }
            file.readText().trim()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to read from sysfs $path: ${e.message}")
            null
        }
    }

    /**
     * Set LED state via multiple fallback methods.
     */
    private suspend fun setLED(path: String, on: Boolean): LEDOpResult = withContext(Dispatchers.IO) {
        val value = if (on) "1" else "0"
        val ledName = when (path) {
            NORMAL_LED_PATH -> "Normal LED"
            STANDBY_LED_PATH -> "Standby LED"
            NETWORK_LED_PATH -> "Network LED"
            else -> "Unknown LED"
        }

        Log.d(TAG, "Attempting to set $ledName to $value")

        // Method 1: Direct sysfs write
        if (writeSysfsFile(path, value)) {
            return@withContext LEDOpResult(
                success = true,
                message = "$ledName set via sysfs",
                accessMethod = LEDAccessMethod.SYSFS
            )
        }

        // Method 2: Try with su/root
        if (obtainRootAccess()) {
            if (executeCommand("echo $value > $path")) {
                return@withContext LEDOpResult(
                    success = true,
                    message = "$ledName set via root",
                    accessMethod = LEDAccessMethod.ROOT_SU
                )
            }
        }

        // All methods failed
        return@withContext LEDOpResult(
            success = false,
            message = "No access method available for $ledName. Check diagnostics.",
            accessMethod = LEDAccessMethod.UNAVAILABLE
        )
    }

    override suspend fun setNormalLED(on: Boolean): LEDOpResult {
        val result = setLED(NORMAL_LED_PATH, on)
        if (!result.success) {
            Log.e(TAG, "Failed to set normal LED: ${result.message}")
        }
        return result
    }

    override suspend fun setStandbyLED(on: Boolean): LEDOpResult {
        val result = setLED(STANDBY_LED_PATH, on)
        if (!result.success) {
            Log.e(TAG, "Failed to set standby LED: ${result.message}")
        }
        return result
    }

    override suspend fun setNetworkLED(on: Boolean): LEDOpResult {
        val result = setLED(NETWORK_LED_PATH, on)
        if (!result.success) {
            Log.e(TAG, "Failed to set network LED: ${result.message}")
        }
        return result
    }

    override suspend fun getState(): LEDState = withContext(Dispatchers.IO) {
        val normalStatus = when (readSysfsFile(NORMAL_LED_PATH)) {
            "1" -> LEDStatus.ON
            "0" -> LEDStatus.OFF
            null -> LEDStatus.UNAVAILABLE
            else -> LEDStatus.UNKNOWN
        }

        val standbyStatus = when (readSysfsFile(STANDBY_LED_PATH)) {
            "1" -> LEDStatus.ON
            "0" -> LEDStatus.OFF
            null -> LEDStatus.UNAVAILABLE
            else -> LEDStatus.UNKNOWN
        }

        val networkStatus = when (readSysfsFile(NETWORK_LED_PATH)) {
            "1" -> LEDStatus.ON
            "0" -> LEDStatus.OFF
            null -> LEDStatus.UNAVAILABLE
            else -> LEDStatus.UNKNOWN
        }

        val hasRoot = try {
            Runtime.getRuntime().exec("su -c id").waitFor() == 0
        } catch (e: Exception) {
            false
        }

        val hasSysfs = File(NORMAL_LED_PATH).canWrite() || File(STANDBY_LED_PATH).canWrite() || File(NETWORK_LED_PATH).canWrite()

        cachedState = LEDState(
            normalLED = normalStatus,
            standbyLED = standbyStatus,
            networkLED = networkStatus,
            accessMethod = if (hasSysfs) LEDAccessMethod.SYSFS else if (hasRoot) LEDAccessMethod.ROOT_SU else LEDAccessMethod.UNAVAILABLE,
            hasRootAccess = hasRoot,
            hasSysfsAccess = hasSysfs
        )

        return@withContext cachedState
    }

    override suspend fun testAllLEDs(): LEDOpResult = withContext(Dispatchers.IO) {
        Log.d(TAG, "Testing all LEDs")
        val results = mutableListOf<LEDOpResult>()

        // Test normal LED
        results.add(testNormalLED())
        kotlinx.coroutines.delay(TEST_DURATION_MS)

        // Test standby LED
        results.add(testStandbyLED())
        kotlinx.coroutines.delay(TEST_DURATION_MS)

        // Test network LED
        results.add(testNetworkLED())
        kotlinx.coroutines.delay(TEST_DURATION_MS)

        val allSuccess = results.all { it.success }
        return@withContext LEDOpResult(
            success = allSuccess,
            message = "LED test ${if (allSuccess) "passed" else "failed"}"
        )
    }

    override suspend fun testNormalLED(): LEDOpResult = withContext(Dispatchers.IO) {
        Log.d(TAG, "Testing normal LED")
        setNormalLED(true)
        kotlinx.coroutines.delay(TEST_DURATION_MS)
        val result = setNormalLED(false)
        return@withContext result.copy(message = "Normal LED test completed")
    }

    override suspend fun testStandbyLED(): LEDOpResult = withContext(Dispatchers.IO) {
        Log.d(TAG, "Testing standby LED")
        setStandbyLED(true)
        kotlinx.coroutines.delay(TEST_DURATION_MS)
        val result = setStandbyLED(false)
        return@withContext result.copy(message = "Standby LED test completed")
    }

    override suspend fun testNetworkLED(): LEDOpResult = withContext(Dispatchers.IO) {
        Log.d(TAG, "Testing network LED")
        setNetworkLED(true)
        kotlinx.coroutines.delay(TEST_DURATION_MS)
        val result = setNetworkLED(false)
        return@withContext result.copy(message = "Network LED test completed")
    }
}
