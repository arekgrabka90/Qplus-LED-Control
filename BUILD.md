# Q+ LED Control - Quick Start

## Build & Install

```bash
# Build debug APK
./gradlew assembleDebug

# Install via ADB
adb install app/build/outputs/apk/debug/app-debug.apk
```

## First Run

1. Launch "Q+ LED Control" from app drawer
2. Navigate using D-pad (remote control)
3. Press OK/Select to activate buttons
4. Go to SETTINGS → DIAGNOSTICS
5. Verify LED status shows "OK"

## LED Control

- **DASHBOARD:** Main view with current status
- **LED MODE:** Select between Fixed, Automatic, Application, Manual
- **COLOR:** Pick color (if hardware supports)
- **LED TEST:** Test individual LEDs
- **STANDBY:** Configure standby behavior
- **SETTINGS:** App preferences

## Key Features

✅ **Multi-tier LED access** (sysfs → root → unavailable)  
✅ **Application color profiles** (TiviMate, Stremio, YouTube, Kodi, Netflix)  
✅ **Automatic color transitions** (step/smooth, multiple speeds)  
✅ **Manual LED control** (test all LEDs)  
✅ **Night mode** (22:00-07:00, customizable)  
✅ **Full diagnostics** (hardware status, access method, permission test)  
✅ **Android TV optimized** (D-pad navigation, landscape, overscan aware)  
✅ **Persistent settings** (DataStore-based)  

## Troubleshooting

**LEDs show UNAVAILABLE:**
- Grant sysfs write permissions (needs root)
- Or run app with su access
- Check `/sys/class/gpio_sw/` paths exist

**App won't start:**
- Check minimum SDK 28+
- Verify Android 9 or higher
- Check logcat: `adb logcat | grep LEDController`

**Settings don't save:**
- Clear app data: `adb shell pm clear com.arekgrabka90.qplusledcontrol`
- Reinstall app

## Next Steps

1. Review [IMPLEMENTATION.md](IMPLEMENTATION.md) for detailed documentation
2. Check diagnostics for LED hardware status
3. Configure application profiles
4. Set up night mode if desired
5. Enable launch at boot in settings
