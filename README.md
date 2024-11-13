<p align="center">
<img src="https://codeberg.org/ottoptj/yamlauncher/raw/branch/main/metadata/en-US/images/featureGraphic.png" width=100%/>
</p>

# YAM Launcher

YAM (Yet Another Minimalist) Launcher is a minimalist text-based launcher for Android with weather integration.

Key features:
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

## Installation

### F-Droid
[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
    alt="Get it on F-Droid"
    height="80">](https://f-droid.org/en/packages/eu.ottop.yamlauncher)

### IzzyOnDroid
[<img src="https://github.com/user-attachments/assets/0e6a8084-f056-4db1-9ba3-58b4edb578d0"
alt="Get it on IzzyOnDroid"
height="55">](https://apt.izzysoft.de/fdroid/index/apk/eu.ottop.yamlauncher)

### Google Play
[<img src="https://github.com/user-attachments/assets/18e22711-eadc-4757-8b47-7588cfa9ab8d"
    alt="Get it on Google Play"
    height="55">](https://play.google.com/store/apps/details?id=eu.ottop.yamlauncher&pcampaignid=web_share)

### APK

1. Download the [APK](https://codeberg.org/ottoptj/yamlauncher/releases).
2. Enable the permission to install apps for your browser/files (app you are installing the launcher from).
3. Install the launcher.

#### Important for APK installations

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

## Bugs, feature requests, etc

Submit an issue on the *Issues* tab.

### Development tracker

You can check what I'm already working on here: [Taiga board](https://tree.taiga.io/project/ottoptj-yam-launcher/kanban)

## Contributing

### Development

A big thank you to all contributors. You can find a list of contributions in [CREDITS.md](https://codeberg.org/ottoptj/yamlauncher/src/branch/main/CREDITS.md). 

Feel free to contribute code improvements and bug fixes. For big changes, I would recommend creating an issue first so that I can figure out whether I want the change to be implemented. Generally I am open to optional features that can be enabled or disabled in the preferences as long as they are not too convoluted, but no guarantees.

### Donations

If you wish to support me and my work financially, I welcome all donations. These help me feed myself and get any equipment I may need for future development (Wouldn't it be cool to run my own instance of Open-Meteo instead of using the public one?).

One-time donations: [Stripe](https://donate.stripe.com/14k6s2bMJdnDgtW288)

Recurring donations: [Liberapay](https://liberapay.com/ottoptj/donate)

## Privacy

[Privacy Policy](https://codeberg.org/ottoptj/yamlauncher/src/branch/main/PrivacyPolicy.md)

No data leaves your device by default and no data is sent to the developer, ever. 

Enabling weather integration will send the coordinates that are set for the weather to Open-Meteo and they may also store your IP address.

### Permissions

- `REQUEST_DELETE_PACKAGES` - Allows uninstallation of apps through the launcher's activity menu.
- `EXPAND_STATUS_BAR` - Allows pulling down the status bar from the app.
- `INTERNET` - Required for API calls to Open-Meteo. Only used if weather integration is enabled.
- `ACCESS_COARSE_LOCATION` - Requested when enabling GPS location. Needs to be allowed to use GPS location.
- `READ_CONTACTS` - Used to find contacts. Only necessary if the contacts menu is enabled. 
- `SET_ALARM` - Used for the clock opening gesture on clicking the time. 
- `QUERY_ALL_PACKAGES` - Used to ensure that the app properly detects all installed apps. 

### Accessibility Services

You can optionally enable accessibility services for YAM Launcher if you choose to use the "Double Tap to Lock Screen" gesture. The accessibility services are exclusively used to lock the screen and are not used to collect any data.

## Mirrors

[Codeberg (primary)](https://codeberg.org/ottoptj/yamlauncher)

[GitHub (secondary)](https://github.com/ottop/yam_launcher)
