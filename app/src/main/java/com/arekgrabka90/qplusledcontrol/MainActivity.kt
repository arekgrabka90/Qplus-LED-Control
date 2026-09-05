package com.arekgrabka90.qplusledcontrol

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontSize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.arekgrabka90.qplusledcontrol.led.LEDController
import com.arekgrabka90.qplusledcontrol.service.LEDControlService
import com.arekgrabka90.qplusledcontrol.ui.screens.*
import com.arekgrabka90.qplusledcontrol.data.PreferenceRepository
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var preferenceRepository: PreferenceRepository
    private lateinit var ledController: LEDController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MainActivity created")

        preferenceRepository = PreferenceRepository(this)
        ledController = LEDController(this)

        // Start LED control service
        startLEDService()

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    color = Color.Black
                ) {
                    MainScreen(
                        preferenceRepository = preferenceRepository,
                        ledController = ledController,
                        onNavigate = { screen ->
                            // Handle navigation
                        }
                    )
                }
            }
        }
    }

    private fun startLEDService() {
        try {
            val intent = Intent(this, LEDControlService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            Log.d(TAG, "LED Control Service started")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start LED Control Service: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MainActivity destroyed")
    }
}

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = Color(0xFF1f78d1),
            secondary = Color(0xFF1f78d1),
            tertiary = Color(0xFF1f78d1),
            background = Color.Black,
            surface = Color(0xFF1a1a1a),
            onBackground = Color.White,
            onSurface = Color.White
        )
    ) {
        content()
    }
}

@Composable
fun MainScreen(
    preferenceRepository: PreferenceRepository,
    ledController: LEDController,
    onNavigate: (String) -> Unit
) {
    var currentScreen by remember { mutableStateOf("dashboard") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .onKeyEvent { event ->
                when {
                    event.key == Key.DirectionLeft -> {
                        currentScreen = when (currentScreen) {
                            "dashboard" -> "about"
                            "ledmode" -> "dashboard"
                            "color" -> "ledmode"
                            "test" -> "color"
                            "standby" -> "test"
                            "settings" -> "standby"
                            "about" -> "settings"
                            else -> currentScreen
                        }
                        true
                    }
                    event.key == Key.DirectionRight -> {
                        currentScreen = when (currentScreen) {
                            "dashboard" -> "ledmode"
                            "ledmode" -> "color"
                            "color" -> "test"
                            "test" -> "standby"
                            "standby" -> "settings"
                            "settings" -> "about"
                            "about" -> "dashboard"
                            else -> currentScreen
                        }
                        true
                    }
                    else -> false
                }
            }
            .focusRequester(focusRequester)
            .focusable()
    ) {
        when (currentScreen) {
            "dashboard" -> DashboardScreen(
                preferenceRepository = preferenceRepository,
                ledController = ledController,
                onNavigate = { currentScreen = it }
            )
            "ledmode" -> LEDModeScreen(
                preferenceRepository = preferenceRepository,
                ledController = ledController
            )
            "color" -> ColorPickerScreen(
                preferenceRepository = preferenceRepository,
                ledController = ledController
            )
            "test" -> TestScreen(
                ledController = ledController
            )
            "standby" -> StandbyScreen(
                preferenceRepository = preferenceRepository
            )
            "settings" -> SettingsScreen(
                preferenceRepository = preferenceRepository
            )
            "about" -> AboutScreen()
            "diagnostics" -> DiagnosticsScreen(
                preferenceRepository = preferenceRepository,
                ledController = ledController
            )
        }
    }
}
