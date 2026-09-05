package com.arekgrabka90.qplusledcontrol.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.arekgrabka90.qplusledcontrol.models.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val PREFERENCE_NAME = "qplus_led_control_prefs"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCE_NAME)

/**
 * Preference keys for persistence.
 */
private object PreferenceKeys {
    val LED_MODE = stringPreferencesKey("led_mode")
    val SELECTED_COLOR = stringPreferencesKey("selected_color")
    val CURRENT_COLOR = stringPreferencesKey("current_color")
    val LAST_COLOR = stringPreferencesKey("last_color")
    val LAUNCH_AT_BOOT = booleanPreferencesKey("launch_at_boot")
    val BACKGROUND_CONTROL = booleanPreferencesKey("background_control")
    val DETECT_APP_CHANGES = booleanPreferencesKey("detect_app_changes")
    val REACT_TO_STANDBY = booleanPreferencesKey("react_to_standby")
    val LED_RESPONSE_TIME_MS = longPreferencesKey("led_response_time_ms")
    val DEFAULT_COLOR = stringPreferencesKey("default_color")
    val UNKNOWN_APP_BEHAVIOR = stringPreferencesKey("unknown_app_behavior")
    val APP_PROFILES = stringPreferencesKey("app_profiles")
    val STANDBY_CONFIG = stringPreferencesKey("standby_config")
    val NIGHT_MODE_CONFIG = stringPreferencesKey("night_mode_config")
    val TRANSITION_TYPE = stringPreferencesKey("transition_type")
    val TRANSITION_SPEED = stringPreferencesKey("transition_speed")
}

/**
 * Data repository for persistence using DataStore.
 */
class PreferenceRepository(private val context: Context) {
    private val gson = Gson()

    val ledMode: Flow<LEDMode> = context.dataStore.data.map { prefs ->
        LEDMode.valueOf(prefs[PreferenceKeys.LED_MODE] ?: LEDMode.FIXED.name)
    }

    val selectedColor: Flow<RGBColor> = context.dataStore.data.map { prefs ->
        val colorJson = prefs[PreferenceKeys.SELECTED_COLOR]
        if (colorJson != null) {
            gson.fromJson(colorJson, RGBColor::class.java)
        } else {
            PresetColors.WHITE
        }
    }

    val currentColor: Flow<RGBColor> = context.dataStore.data.map { prefs ->
        val colorJson = prefs[PreferenceKeys.CURRENT_COLOR]
        if (colorJson != null) {
            gson.fromJson(colorJson, RGBColor::class.java)
        } else {
            PresetColors.WHITE
        }
    }

    val lastColor: Flow<RGBColor> = context.dataStore.data.map { prefs ->
        val colorJson = prefs[PreferenceKeys.LAST_COLOR]
        if (colorJson != null) {
            gson.fromJson(colorJson, RGBColor::class.java)
        } else {
            PresetColors.WHITE
        }
    }

    val appSettings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            launchAtBoot = prefs[PreferenceKeys.LAUNCH_AT_BOOT] ?: false,
            backgroundControl = prefs[PreferenceKeys.BACKGROUND_CONTROL] ?: true,
            detectAppChanges = prefs[PreferenceKeys.DETECT_APP_CHANGES] ?: true,
            reactToStandby = prefs[PreferenceKeys.REACT_TO_STANDBY] ?: true,
            ledResponseTimeMs = prefs[PreferenceKeys.LED_RESPONSE_TIME_MS] ?: 100L,
            defaultColor = prefs[PreferenceKeys.DEFAULT_COLOR]?.let {
                gson.fromJson(it, RGBColor::class.java)
            } ?: PresetColors.WHITE,
            unknownAppBehavior = UnknownAppBehavior.valueOf(
                prefs[PreferenceKeys.UNKNOWN_APP_BEHAVIOR] ?: UnknownAppBehavior.DEFAULT_COLOR.name
            )
        )
    }

    val applicationProfiles: Flow<List<ApplicationProfile>> = context.dataStore.data.map { prefs ->
        val profilesJson = prefs[PreferenceKeys.APP_PROFILES]
        if (profilesJson != null) {
            gson.fromJson(profilesJson, Array<ApplicationProfile>::class.java).toList()
        } else {
            getDefaultProfiles()
        }
    }

    val standbyConfig: Flow<StandbyConfig> = context.dataStore.data.map { prefs ->
        val configJson = prefs[PreferenceKeys.STANDBY_CONFIG]
        if (configJson != null) {
            gson.fromJson(configJson, StandbyConfig::class.java)
        } else {
            StandbyConfig(
                mode = LEDMode.FIXED,
                color = PresetColors.WHITE
            )
        }
    }

    val nightModeConfig: Flow<NightModeConfig> = context.dataStore.data.map { prefs ->
        val configJson = prefs[PreferenceKeys.NIGHT_MODE_CONFIG]
        if (configJson != null) {
            gson.fromJson(configJson, NightModeConfig::class.java)
        } else {
            NightModeConfig(
                enabled = false,
                startHour = 22,
                startMinute = 0,
                endHour = 7,
                endMinute = 0,
                behavior = NightModeBehavior.LED_OFF
            )
        }
    }

    suspend fun setLedMode(mode: LEDMode) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.LED_MODE] = mode.name
        }
    }

    suspend fun setSelectedColor(color: RGBColor) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.SELECTED_COLOR] = gson.toJson(color)
        }
    }

    suspend fun setCurrentColor(color: RGBColor) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.CURRENT_COLOR] = gson.toJson(color)
        }
    }

    suspend fun setLastColor(color: RGBColor) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.LAST_COLOR] = gson.toJson(color)
        }
    }

    suspend fun updateApplicationProfiles(profiles: List<ApplicationProfile>) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.APP_PROFILES] = gson.toJson(profiles)
        }
    }

    suspend fun updateStandbyConfig(config: StandbyConfig) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.STANDBY_CONFIG] = gson.toJson(config)
        }
    }

    suspend fun updateNightModeConfig(config: NightModeConfig) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.NIGHT_MODE_CONFIG] = gson.toJson(config)
        }
    }

    suspend fun updateAppSettings(settings: AppSettings) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.LAUNCH_AT_BOOT] = settings.launchAtBoot
            prefs[PreferenceKeys.BACKGROUND_CONTROL] = settings.backgroundControl
            prefs[PreferenceKeys.DETECT_APP_CHANGES] = settings.detectAppChanges
            prefs[PreferenceKeys.REACT_TO_STANDBY] = settings.reactToStandby
            prefs[PreferenceKeys.LED_RESPONSE_TIME_MS] = settings.ledResponseTimeMs
            prefs[PreferenceKeys.DEFAULT_COLOR] = gson.toJson(settings.defaultColor)
            prefs[PreferenceKeys.UNKNOWN_APP_BEHAVIOR] = settings.unknownAppBehavior.name
        }
    }

    private fun getDefaultProfiles(): List<ApplicationProfile> {
        return listOf(
            ApplicationProfile("com.niklabs.tivimate_plus", "TiviMate", PresetColors.BLUE),
            ApplicationProfile("com.stremio.one", "Stremio", PresetColors.PURPLE),
            ApplicationProfile("com.google.android.youtube.tv", "YouTube", PresetColors.RED),
            ApplicationProfile("org.xbmc.kodi", "Kodi", PresetColors.GREEN),
            ApplicationProfile("com.netflix.ninja", "Netflix", PresetColors.RED)
        )
    }
}
