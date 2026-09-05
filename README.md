# Q+ LED Control

Android TV application for controlling LEDs on Sunvell/Q+ Q Plus TV boxes.

**Version:** 1.0.0  
**Target Device:** Sunvell Q+ (Allwinner H6, Android 9, API 28)  
**Author:** arekgrabka90

## Features

- Dashboard with LED status and control
- Multiple LED modes (fixed, automatic, application-based, manual)
- Automatic color transitions with adjustable speed
- Application-specific color profiles
- Standby mode configuration
- Night mode automation
- Full diagnostics and hardware testing
- Android TV optimized UI (D-pad navigation, landscape)

## Building

```bash
./gradlew build
```

## Installation

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## LED Hardware

The application interfaces with LED hardware at:
- `/sys/class/gpio_sw/normal_led/light`
- `/sys/class/gpio_sw/standby_led/light`
- `/sys/class/gpio_sw/network_led/light`

## Architecture

Clean modular architecture with separate layers for UI, LED control, profiles, and system integration.
