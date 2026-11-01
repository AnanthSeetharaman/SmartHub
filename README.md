# ⚡ SmartHub — Universal TV & Device Controller (Android)

<p align="center">
  <img src="app/res/drawable/ic_launcher.png" width="120" alt="SmartHub Logo">
  <br>
  <b>An open-source, ultra-lightweight Android app to control Samsung Smart TVs, Xiaomi Mi TVs, and Google Cast devices on your home Wi-Fi network.</b>
</p>

---

## 🌟 Key Features

* **🌙 Background Audio Mode (Picture Off / Display Sleep)**:
  * Powers off the TV screen and backlight while music, YouTube, and podcasts continue playing in the background through the TV speakers.
  * Native support for **Samsung Smart TV** (`KEY_TOOLS` -> `KEY_ENTER` Picture Off macro) and **Xiaomi Mi TV** (Android display sleep intent).
* **🎮 Universal Tactile Remote**:
  * Large, thumb-friendly D-Pad navigation (`▲`, `◀`, `OK`, `▶`, `▼`).
  * Dedicated volume rocker, mute, and source switcher.
  * Real **haptic vibration feedback** on every button tap.
* **📺 Direct Zero-Lag YouTube Streamer**:
  * Bypasses the laggy Smart Hub home screen and DIALs straight into the hardware video decoder on port 8080.
  * 1-touch presets for 24/7 Lofi Chillhop, 4K Nature, News, and High-Fidelity Speaker Tests.
* **📶 Network Device Discovery & Dynamic Ports**:
  * Scans local Wi-Fi subnet and lists all controllable smart devices:
    * Samsung Smart TV (`UA32H5500`) over WebSocket port 8001
    * Xiaomi Mi TV (`MiTV-AXSO2`) over Android ADB port 5555
    * Google Cast / Mi devices (`AniRudh`) over Eureka port 8008
* **🎚 5-Band Audio Equalizer**:
  * Calibrated sound profiles (*Amplify*, *Movie*, *Music*) with sub-bass and vocal clarity sliders.

---

## 📱 Installation

### Method 1: Direct ADB Install (USB)
```powershell
adb install dist/SmartHub.apk
```

### Method 2: Sideload onto Android Phone
1. Transfer `dist/SmartHub.apk` to your phone's **Download** folder.
2. Open your phone's file manager, tap `SmartHub.apk`, and tap **Install**.

---

## 🛠 Local Build Instructions

### Prerequisites
* **Java Development Kit (JDK 17+)**
* **Android SDK Build-Tools 34+** (`aapt2`, `d8`, `zipalign`, `apksigner`)
* **Android Platform 34+** (`android.jar`)

### Compile APK
Run the included build script in PowerShell:
```powershell
.\build.ps1
```
The output signed APK will be created at `dist/SmartHub.apk`.

---

## 🤖 Automated CI/CD (GitHub Actions)
The included workflow `.github/workflows/build-apk.yml` automatically compiles, signs, and attaches `SmartHub.apk` as a downloadable artifact on every commit and GitHub release!

---

## 📄 License
Open-source under the Apache-2.0 License.
