# Q+ LED Control - Complete Implementation Guide

**Version:** 1.0.0  
**Target Device:** Sunvell Q+ (Allwinner H6, Android 9, API 28)  
**Author:** arekgrabka90

## Overview

Q+ LED Control is a professional Android TV application for controlling LEDs on Sunvell Q+ TV boxes. The application provides a robust multi-tier architecture for LED access with comprehensive diagnostics and a TV-optimized user interface.

## Architecture

### Modular Structure

```
app/src/main/
├── java/com/arekgrabka90/qplusledcontrol/
│   ├── led/              # LED control abstraction
│   │   ├── LEDController.kt       # Main LED controller
│   │   ├── LEDEffects.kt          # Animation effects
│   │   └── (interfaces and enums)
│   ├── models/           # Data models
│   │   └── Models.kt     # Color, settings, profiles
│   ├── data/             # Persistence layer
│   │   └── PreferenceRepository.kt  # DataStore-based persistence
│   ├── system/           # System integration
│   │   └── SystemUtils.kt  # App detection, system info
│   ├── ui/screens/       # UI screens
│   │   ├── DashboardScreen.kt
│   │   ├── LEDModeScreen.kt
│   │   ├── ColorPickerScreen.kt
│   │   ├── TestScreen.kt
│   │   ├── StandbyScreen.kt
│   │   ├── SettingsScreen.kt
│   │   ├── DiagnosticsScreen.kt
│   │   └── AboutScreen.kt
│   ├── service/          # Background services
│   │   └── LEDControlService.kt
│   ├── receiver/         # Broadcast receivers
│   │   └── BootReceiver.kt
│   └── MainActivity.kt    # Main activity & navigation
├── res/                  # Resources
│   ├── values/
│   │   ├── strings.xml
│   │   ├── colors.xml
│   │   ├── themes.xml
│   │   └── dimens.xml
│   ├── drawable/         # Icons and drawables
│   ├── mipmap/           # App icons
│   └── xml/              # Security policies
└── AndroidManifest.xml   # App manifest
```

## LED Access Strategy (Multi-Tier)

The `LEDController` implements a robust fallback chain:

### 1. **Manufacturer/System API** (Tier 1)
Currently not available on standard Android 9 for this device.

### 2. **Privileged/System Mechanism** (Tier 2)
Limited availability - would require system-signed APK.

### 3. **Root Access via SU** (Tier 3)
```bash
echo 1 > /sys/class/gpio_sw/normal_led/light
echo 0 > /sys/class/gpio_sw/normal_led/light
```

### 4. **Direct Sysfs Access** (Tier 4) - ✅ **RECOMMENDED**
```kotlin
val normalLED = File("/sys/class/gpio_sw/normal_led/light")
normalLED.writeText("1") // Turn on
normalLED.writeText("0") // Turn off
```

### 5. **Graceful Unavailable State** (Fallback)
- Displays diagnostic information
- Suggests root access or permissions
- Never crashes

## Hardware Paths

```
/sys/class/gpio_sw/normal_led/light      # Main LED
/sys/class/gpio_sw/standby_led/light     # Standby LED
/sys/class/gpio_sw/network_led/light     # Network LED

Values:
  1 = ON
  0 = OFF
```

## Key Features

### 1. Dashboard
- Application status
- Current LED mode
- Foreground application detection
- Current color display
- LED control status
- Quick navigation buttons

### 2. LED Modes
- **Fixed Color:** Single static color
- **Automatic:** Transition between preset colors
- **Application:** Color based on foreground app
- **Manual:** Direct control of each LED

### 3. Automatic Color Transitions
- Stepwise transition
- Smooth transition (simulated)
- Speed presets: 1s, 3s, 5s, 10s, 30s
- Selectable and custom colors

### 4. Application Profiles
Default profiles:
- TiviMate → Blue
- Stremio → Purple
- YouTube → Red
- Kodi → Green
- Netflix → Red

Users can:
- Add installed applications
- Assign custom colors
- Edit/remove profiles
- Set default behavior for unknown apps

### 5. Color Picker
Preset colors:
- Red, Green, Blue
- Yellow, Purple, Cyan
- White, Orange, Pink
- Custom RGB/HEX (if hardware supports)

### 6. LED Effects
- Fixed (no animation)
- Smooth transition
- Step transition
- Rainbow (cycle through LEDs)
- Random (random states)
- Breathing (gradual on/off)
- Blinking (on/off cycles)
- Adjustable speed per effect

### 7. Manual Control
Direct controls for:
- Normal LED ON/OFF
- Standby LED ON/OFF
- Network LED ON/OFF
- Test all LEDs

### 8. Standby Mode
- Application color detection
- Fixed color selection
- Automatic mode support
- OFF mode
- Customizable wake behavior

### 9. Night Mode
Automation based on time:
- Enable/disable
- Start time (default 22:00)
- End time (default 07:00)
- Behaviors:
  - Different color
  - Reduced brightness
  - LED OFF

### 10. Automation
- Launch at Android boot
- Background operation
- Foreground app change detection
- Standby detection
- Wake detection
- Night mode automation

### 11. Settings
- Launch at boot toggle
- Background control toggle
- App change detection toggle
- Standby reaction toggle
- LED response time (ms)
- Default color selection
- Unknown app behavior
- Night mode configuration
- Diagnostics access
- Hardware test launch

### 12. Diagnostics
Display LED status:
```
NORMAL LED      [OK / UNAVAILABLE]
STANDBY LED     [OK / UNAVAILABLE]
NETWORK LED     [OK / UNAVAILABLE]
SYSTEM CONTROL  [OK / UNAVAILABLE]
ROOT / SU       [OK / UNAVAILABLE]
```

Functions:
- Test all LEDs
- Test individual LEDs
- Permission diagnostics
- Access method reporting

### 13. Information Screen
- App name and version
- Device model (Sunvell Q+)
- SoC (Allwinner H6)
- Android version (9)
- Author attribution
- Quick access to diagnostics and hardware test

### 14. Persistence
Using Android DataStore:
- LED mode
- Selected color
- Application profiles
- Standby behavior
- Wake behavior
- Night mode settings
- All automation settings
- Default values

### 15. Android TV UI
Optimized for TV experience:
- **Landscape orientation** (forced)
- **Dark theme** (AMOLED-friendly)
- **Large buttons** (80dp minimum height)
- **Large text** (28sp minimum)
- **Clear focus indication** (blue highlight)
- **D-pad navigation** (up/down/left/right)
- **OK/Select button** support
- **Back navigation** support
- **No touchscreen dependency**
- **TV overscan margins** (27dp)
- **Predictable focus order**

### 16. Error Handling
The application gracefully handles:
- ✅ Root access unavailable
- ✅ Sysfs inaccessible
- ✅ GPIO paths missing
- ✅ System API unavailable
- ✅ App detection failures
- ✅ Permission denials

Display useful diagnostics instead of crashing.

## Building the Project

### Prerequisites
- JDK 11 or higher
- Android SDK (API 28 minimum)
- Gradle 8.4+

### Build Commands

```bash
# Clone the repository
git clone https://github.com/arekgrabka90/Qplus-LED-Control.git
cd Qplus-LED-Control

# Debug APK
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk

# Release APK (unsigned)
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release-unsigned.apk

# Run tests
./gradlew test

# Build with variant
./gradlew buildVariants

# Clean build
./gradlew clean assembleDebug
```

## Installation

### Method 1: ADB (Recommended)

```bash
# Connect device via USB
adb devices

# Install debug APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Uninstall
adb uninstall com.arekgrabka90.qplusledcontrol
```

### Method 2: Manual File Transfer
1. Build debug APK
2. Transfer to USB drive
3. Connect USB to Q+ device
4. Use file manager to locate APK
5. Tap to install

### Method 3: GitHub Actions
1. Create a pull request or push to main/feature branches
2. GitHub Actions automatically builds APK
3. Download APK from workflow artifacts
4. Install via ADB or manual transfer

## LED Access Troubleshooting

### Issue: LED Test Shows "UNAVAILABLE"

**Diagnosis:**
```bash
# Check if sysfs files exist
adb shell ls -la /sys/class/gpio_sw/

# Test direct write (requires root)
adb shell su -c "echo 1 > /sys/class/gpio_sw/normal_led/light"

# Check file permissions
adb shell ls -la /sys/class/gpio_sw/normal_led/light
```

**Solutions:**

1. **Gain Read/Write Access:**
   ```bash
   # If you have root access
   adb shell
   su
   chmod 666 /sys/class/gpio_sw/normal_led/light
   chmod 666 /sys/class/gpio_sw/standby_led/light
   chmod 666 /sys/class/gpio_sw/network_led/light
   ```

2. **Run Q+ LED Control with SU:**
   - App will detect root and use su fallback
   - Requires su binary on device

3. **Check Device Tree Overlay (DTO):**
   - Some Allwinner devices use different GPIO paths
   - Run `adb shell find /sys -name "*led*"` to discover
   - Update LEDController paths if needed

### Permission Test

Diagnostics screen shows:
- **Sysfs Access OK:** Files are readable/writable
- **Root Access OK:** su command available
- **Access Method:** Which tier is active (SYSFS > ROOT_SU > UNAVAILABLE)

## Development

### Adding New LED Features

**1. Extend ILEDController:**
```kotlin
interface ILEDController {
    suspend fun customFeature(): LEDOpResult
}
```

**2. Implement in LEDController:**
```kotlin
override suspend fun customFeature(): LEDOpResult {
    // Implementation with fallbacks
}
```

**3. Create UI Screen:**
```kotlin
@Composable
fun CustomFeatureScreen(...) {
    // TV-optimized composable
}
```

**4. Add Navigation:**
Update `MainScreen` with new screen route.

### Adding Application Profile

```kotlin
// In PreferenceRepository.getDefaultProfiles()
ApplicationProfile(
    packageName = "com.example.app",
    appName = "Example App",
    color = PresetColors.CUSTOM // or new RGBColor(...)
)
```

### Testing LED Effects

```kotlin
// In DiagnosticsScreen or TestScreen
scope.launch {
    val effects = LEDEffects()
    effects.breathing(ledController, durationMs = 2000)
    effects.blinking(ledController, onDurationMs = 500, cycles = 5)
    effects.rainbow(ledController, stepDurationMs = 500, cycles = 3)
    effects.random(ledController, stepDurationMs = 500, cycles = 10)
}
```

## GitHub Actions CI/CD

The workflow (`.github/workflows/build.yml`) automatically:
1. ✅ Checks out repository
2. ✅ Sets up JDK 11
3. ✅ Builds debug APK
4. ✅ Builds release APK (if signing key available)
5. ✅ Uploads APK artifacts
6. ✅ Runs unit tests

**Download APK from GitHub:**
1. Go to Actions tab
2. Select latest workflow run
3. Download `qplus-led-control-debug` artifact
4. Extract APK and install

## Dependencies

```kotlin
// AndroidX
androidx.core-ktx
androidx.appcompat
androidx.lifecycle
androidx.activity.compose
androidx.datastore

// Jetpack Compose
androidx.compose.ui
androidx.compose.material3
androidx.compose.foundation

// Android TV
androidx.tvprovider
androidx.leanback

// Coroutines
kotlinx.coroutines

// JSON
gson
```

## API Level Compatibility

- **Minimum SDK:** 28 (Android 9) ✅
- **Target SDK:** 34 (Android 14)
- **Compile SDK:** 34

**Why API 28?**
- Supports Allwinner H6 Android 9 firmware
- Jetpack Compose support
- DataStore stability
- TV APIs available

## Testing Checklist

- [ ] Gradle build successful
- [ ] Debug APK builds without errors
- [ ] Unit tests pass
- [ ] AndroidManifest.xml valid (API 28)
- [ ] TV mode forces landscape
- [ ] D-pad navigation works
- [ ] Focus indicators visible
- [ ] No obvious crashes
- [ ] LED diagnostics show correct status
- [ ] Colors persist after app restart
- [ ] Persistence works across sessions
- [ ] GitHub Actions build succeeds

## Known Limitations

1. **Direct Hardware Color Control:**
   - Most Allwinner H6 GPIO LEDs are single-wire (on/off only)
   - RGB color control not possible on GPIO
   - Application shows available controls based on hardware

2. **Foreground App Detection:**
   - Requires `GET_TASKS` permission
   - API 31+ has stricter scopes
   - Fallback to package name extraction

3. **Standby Detection:**
   - No direct standby event broadcast
   - Emulates via app visibility changes
   - Not 100% accurate on all devices

4. **Root Access:**
   - Requires `su` binary on device
   - May not work with all root solutions
   - Gracefully falls back to sysfs

## Future Enhancements

- [ ] Real standby event detection via system APIs
- [ ] PWM brightness control if hardware supports
- [ ] Custom ringtone LED notification
- [ ] Network activity LED indicator
- [ ] Temperature monitoring
- [ ] Power consumption optimization
- [ ] Multi-device support

## Troubleshooting

### Build Issues

**Issue:** `Gradle sync failed`
```bash
./gradlew clean
./gradlew sync
```

**Issue:** `Unsupported Java version`
```bash
javac -version  # Check JDK version
# Ensure JDK 11+ is installed
export JAVA_HOME=/path/to/jdk11
```

### Runtime Issues

**Issue:** App crashes on startup
- Check logcat: `adb logcat | grep LEDController`
- Verify permissions in AndroidManifest.xml
- Check min SDK version

**Issue:** LEDs don't respond
- Check diagnostics screen
- Verify sysfs paths exist: `adb shell ls /sys/class/gpio_sw/`
- Check permissions: `adb shell stat /sys/class/gpio_sw/normal_led/light`

**Issue:** Settings don't persist
- Ensure DataStore files are being written
- Check app cache isn't being cleared
- Verify data directory permissions

## Contributing

1. Create feature branch: `git checkout -b feature/my-feature`
2. Commit changes: `git commit -am 'Add my feature'`
3. Push to branch: `git push origin feature/my-feature`
4. Create Pull Request with description

## License

This project is provided as-is for the Sunvell Q+ TV box community.

## Support

For issues or questions:
1. Check diagnostics screen first
2. Review logcat output: `adb logcat`
3. Verify device setup matches requirements
4. Check GitHub issues for similar problems

---

**Q+ LED Control v1.0.0**  
**For Sunvell Q+ (Allwinner H6, Android 9)**  
**Author:** arekgrabka90
