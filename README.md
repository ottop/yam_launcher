<p align="center">
  <img src=https://codeberg.org/ottoptj/yamlauncher/raw/branch/main/app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp />
</p>

# YAM Launcher

YAM (Yet Another Minimalist) Launcher is a minimalist text-based launcher for Android with weather integration.

Key features:
- No flashy effects, icons or other distractions. Perfect for digital minimalism.
- Customizable shortcuts for your most used apps.
- Weather integration with [Open-Meteo](https://open-meteo.com/). (optional)
- Work profile support.
- Search on the bottom of the screen.
- System fonts for an uniform look with your Android.
- Simple by default, customizable under the hood. 
- Open-source under the MIT License.

## Installation

### F-Droid

Coming soon...

### Google Play

Coming soon...

### APK

1. Download the APK.
2. Enable permission to install apps to your browser/files (app you are installing the launcher from).
3. Install the launcher.

#### Important for APK installations

If you wish to use the double tap to lock feature, Android blocks the accessibility settings for manually installed APKs by default. 

To fix (only if you want to enable the double tap to lock feature):

1. Go to Settings -> Apps -> YAM Launcher
2. Click on the three-dot button on the top right of the screen. 
3. Allow restricted settings

## Screenshots

<p align="center">
<img src=https://github.com/user-attachments/assets/4435e05e-3f8e-4b65-b5c1-e7e718db14f5 />
</p>

## Bugs, feature requests, etc

Submit an issue on the *Issues* tab.

## Contributing

### Development

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
- `QUERY_ALL_PACKAGES` - Used to ensure that the app properly detects all installed apps. 
