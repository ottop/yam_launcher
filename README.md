> [!NOTE]
> Ottop stopped developing this launcher. Thanks to him for the amazing groundwork! We'll continue supporting this project. If you made a issue on the main repo from Ottop, please make one here. We'll only implement features asked for by our community.
<p align="center">
<img src="https://codeberg.org/ottoptj/yamlauncher/raw/branch/main/metadata/en-US/images/featureGraphic.png" width=100%/>
</p>

# YAM Launcher

YAM (Yet Another Minimalist) Launcher is a minimalist text-based launcher for Android with weather integration.

## Table of Contents

- [What is a Launcher?](#what-is-a-launcher)
- [Why Choose YAM Launcher?](#why-choose-yam-launcher)
- [Key Features](#key-features)
- [Getting Started](#getting-started)
- [Installation](#installation)
- [Screenshots](#screenshots)
- [Privacy](#privacy)
- [Permissions](#android-permissions)
- [Accessibility Services](#accessibility-services)

## What is a Launcher?

A launcher is the home screen application on your Android device that allows you to access your apps, widgets, and other features. By default, Android comes with a launcher (like Google's Pixel Launcher), but you can install and use alternative launchers that offer different features and customization options.

## Why Choose YAM Launcher?

If you're looking for a clean, distraction-free Android experience that focuses on what matters most - your apps and information - YAM Launcher is for you. It's designed for users who value:

- **Digital Minimalism**: No distracting icons, animations, or clutter - just clean text-based app shortcuts
- **Performance**: Lightweight design that won't slow down your device
- **Privacy**: No data collection or tracking by default
- **Customization**: Highly configurable while maintaining simplicity

## Key Features

- No flashy effects, icons or other distractions. Perfect for digital minimalism.
- Customizable shortcuts for your most used apps.
- Weather integration with [Open-Meteo](https://open-meteo.com/). (optional)
- Work profile support.
- Search on the bottom of the screen.
- Contacts searching functionality. (optional)
- Customizable fonts and font styles, defaults to your system font.
- Material You support.
- Simple by default, customizable under the hood. 
- Accessibility Actions support.
- Open-source under the MIT License.

## Getting Started

1. **Install YAM Launcher** using the instructions below
2. **Set as Default** when prompted, or manually set it in Settings > Apps > Default apps > Home app
3. **Configure Shortcuts** by long-pressing on the default "App" shortcuts and selecting your most used apps
4. **Customize** the look and feel in Settings (accessed by long-pressing on the clock or date)

For a complete list of features and customization options, see the full documentation below.

## Installation

### Installing from APK

1. Download the latest APK from the [Releases tab](../../releases)
2. Open the downloaded APK file
3. If prompted, enable installation from this source in your device settings
4. Follow the prompts to install the app

### Setting YAM Launcher as Your Default Home App

After installation, Android will usually prompt you to set YAM Launcher as your default home app. If not:

1. Press the Home button
2. Select "YAM Launcher" from the options
3. Choose "Always" to set it as default

Alternatively, you can manually set it in your device settings:
1. Go to Settings > Apps > Default apps (or similar, depending on your device)
2. Tap "Home app" 
3. Select "YAM Launcher"

### First-Time Setup

When you first launch YAM Launcher:

1. You'll see a clean, minimalist home screen with default "App" shortcuts
2. Long-press on any shortcut to select the app you want there
3. Customize the look and feel through Settings (access by long-pressing the clock or date)

### Important Notes for APK Installations

If you wish to use the double tap to lock feature, Android blocks the accessibility settings for manually installed APKs by default. 

To fix (only if you want to enable the double tap to lock feature):

1. Go to Settings -> Apps -> YAM Launcher
2. Click on the three-dot button on the top right of the screen. 
3. Allow restricted settings

## Screenshots

<p align="center">
<img src="https://codeberg.org/ottoptj/yamlauncher/raw/branch/main/metadata/en-US/images/phoneScreenshots/1.png"
    alt="Weather At Your Fingertips"
    width="19%">
<img src="https://codeberg.org/ottoptj/yamlauncher/raw/branch/main/metadata/en-US/images/phoneScreenshots/2.png"
    alt="Absolute Minimalism"
    width="19%">
<img src="https://codeberg.org/ottoptj/yamlauncher/raw/branch/main/metadata/en-US/images/phoneScreenshots/3.png"
    alt="Function Over Form"
    width="19%">
<img src="https://codeberg.org/ottoptj/yamlauncher/raw/branch/main/metadata/en-US/images/phoneScreenshots/4.png"
    alt="Material You"
    width="19%">
<img src="https://codeberg.org/ottoptj/yamlauncher/raw/branch/main/metadata/en-US/images/phoneScreenshots/5.png"
    alt="Make It Yours"
    width="19%">
</p>

## Privacy

### Your Privacy Matters

We take your privacy seriously. YAM Launcher is designed with privacy as a core principle:

- **No Data Collection**: By default, no data leaves your device and no data is sent to the developer or any third parties
- **No Tracking**: No analytics, no user behavior tracking, no advertising
- **Open Source**: You can verify our privacy claims by reviewing the source code yourself
- **Local Processing**: All app management, searching, and customization happens locally on your device

### Optional Features & Data Usage

Some optional features require external services:

- **Weather Integration** (optional): When enabled, coordinates are sent to [Open-Meteo](https://open-meteo.com/) (a privacy-focused weather service). They may log your IP address for server statistics, but no personal data is stored.
- **Contacts Search** (optional): Requires permission to read contacts, but contact data never leaves your device

### Permissions Explained

We only request permissions that are essential for launcher functionality. See the detailed permissions list below for explanations of why each permission is needed.

### Android Permissions

YAM Launcher requests several permissions to provide its functionality. Here's what each permission does and why it's needed:

- `REQUEST_DELETE_PACKAGES` - Allows uninstallation of apps through the launcher's activity menu (when you long-press an app and select "Uninstall")
- `EXPAND_STATUS_BAR` - Allows pulling down the status bar/notification panel when you swipe down on the home screen
- `INTERNET` - Required for API calls to Open-Meteo weather service. Only used if weather integration is enabled in settings
- `ACCESS_COARSE_LOCATION` - Used to determine your location for weather forecasts when GPS location is enabled. Not used unless you enable weather with GPS
- `READ_CONTACTS` - Used to search and access your contacts. Only requested if you enable the contacts search feature
- `SET_ALARM` - Used when you tap the clock to open your default alarm/clock app
- `QUERY_ALL_PACKAGES` - Required to detect all installed apps on your device so they appear in the app drawer

### Accessibility Services

YAM Launcher can optionally use Android's accessibility service for the "Double Tap to Lock Screen" gesture. 

**Important**: This feature is completely optional and disabled by default. If you choose to enable it:

1. The accessibility service is only used to lock your screen - nothing else
2. No data is collected or transmitted
3. You must explicitly grant permission in Android's accessibility settings

This is a limitation of Android - only accessibility services can lock the screen programmatically.

## Ready to Simplify Your Home Screen?

YAM Launcher offers a refreshing alternative to cluttered, resource-heavy launchers. With its focus on simplicity, performance, and privacy, it's perfect for users who want quick access to their apps without distractions.

Give it a try and experience a cleaner, more focused Android home screen today!
