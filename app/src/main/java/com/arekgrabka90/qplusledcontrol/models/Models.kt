package com.arekgrabka90.qplusledcontrol.models

import android.graphics.Color

/**
 * Data class for RGB color representation.
 */
data class RGBColor(
    val red: Int,
    val green: Int,
    val blue: Int
) {
    fun toHex(): String = String.format("#%02x%02x%02x", red, green, blue)
    fun toInt(): Int = Color.rgb(red, green, blue)

    companion object {
        fun fromHex(hex: String): RGBColor? {
            return try {
                val color = Color.parseColor(hex)
                RGBColor(
                    Color.red(color),
                    Color.green(color),
                    Color.blue(color)
                )
            } catch (e: Exception) {
                null
            }
        }

        fun fromInt(color: Int): RGBColor {
            return RGBColor(
                Color.red(color),
                Color.green(color),
                Color.blue(color)
            )
        }
    }
}

/**
 * Preset colors available in the color picker.
 */
object PresetColors {
    val RED = RGBColor(255, 0, 0)
    val GREEN = RGBColor(0, 255, 0)
    val BLUE = RGBColor(0, 0, 255)
    val YELLOW = RGBColor(255, 255, 0)
    val PURPLE = RGBColor(128, 0, 128)
    val CYAN = RGBColor(0, 255, 255)
    val WHITE = RGBColor(255, 255, 255)
    val ORANGE = RGBColor(255, 165, 0)
    val PINK = RGBColor(255, 192, 203)

    val all = listOf(RED, GREEN, BLUE, YELLOW, PURPLE, CYAN, WHITE, ORANGE, PINK)
}

/**
 * LED modes.
 */
enum class LEDMode {
    FIXED,
    AUTOMATIC,
    APPLICATION,
    MANUAL
}

/**
 * Transition types for automatic mode.
 */
enum class TransitionType {
    STEP,
    SMOOTH
}

/**
 * Speed settings for automatic mode (in seconds).
 */
enum class TransitionSpeed(val durationSeconds: Int) {
    FAST(1),
    NORMAL(3),
    SLOW(5),
    SLOWER(10),
    SLOWEST(30)
}

/**
 * Application profile for automatic color selection.
 */
data class ApplicationProfile(
    val packageName: String,
    val appName: String,
    val color: RGBColor
)

/**
 * Configuration for standby mode.
 */
data class StandbyConfig(
    val mode: LEDMode,
    val color: RGBColor,
    val useDefaultColor: Boolean = true,
    val useLastColor: Boolean = false,
    val turnOff: Boolean = false
)

/**
 * Configuration for night mode.
 */
data class NightModeConfig(
    val enabled: Boolean,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val behavior: NightModeBehavior
)

/**
 * Behavior options for night mode.
 */
enum class NightModeBehavior {
    DIFFERENT_COLOR,
    REDUCED_BRIGHTNESS,
    LED_OFF
}

/**
 * Application settings/preferences.
 */
data class AppSettings(
    val launchAtBoot: Boolean = false,
    val backgroundControl: Boolean = true,
    val detectAppChanges: Boolean = true,
    val reactToStandby: Boolean = true,
    val ledResponseTimeMs: Long = 100,
    val defaultColor: RGBColor = PresetColors.WHITE,
    val unknownAppBehavior: UnknownAppBehavior = UnknownAppBehavior.DEFAULT_COLOR,
    val nightModeConfig: NightModeConfig = NightModeConfig(
        enabled = false,
        startHour = 22,
        startMinute = 0,
        endHour = 7,
        endMinute = 0,
        behavior = NightModeBehavior.LED_OFF
    )
)

/**
 * Behavior for unknown applications.
 */
enum class UnknownAppBehavior {
    DEFAULT_COLOR,
    LAST_COLOR,
    LED_OFF
}
