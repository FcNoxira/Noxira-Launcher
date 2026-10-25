# NOXIRA — Premium Roblox Launcher

**PLAY • FASTER • SMOOTHER**

Developer: Muhammad Nur Fazrullah

---

## What This Is

NOXIRA is a professionally engineered Android utility that automates launching
multiple Roblox instances in freeform windowing mode and tiles them into a grid.

It is a full Kotlin/Jetpack Compose rewrite of the original `ZetsuLauncher`
(`com.zetsu.rbxgrid`), featuring:

- Dark glassmorphism design system
- Persistent settings via DataStore
- Proper root detection and scoped su execution
- Freeform setup via `settings put global`
- App Cloner XML bounds patch
- ViewModel + Compose Navigation
- Unit tests for core logic

---

## Requirements

| Requirement | Detail |
|---|---|
| Android | API 29+ (Android 10) — minimum for `--windowingMode 5` via `am start` |
| Root | Required for freeform grid, tiling, stop-all. Not required for basic single Roblox launch. |
| Freeform | Must be enabled once (Settings → Developer options → Freeform windows) or via the in-app Setup button, then reboot. |
| Roblox | `com.roblox.client` or any App Cloner clone starting with `com.roblox` |

---

## Build Setup

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 11+
- Android SDK with API 35 and API 29 build tools

### Steps

```bash
git clone <your-repo>
cd NOXIRA
./gradlew assembleDebug
```

The debug APK is output at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Install

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Release Build

1. Create a keystore:
   ```bash
   keytool -genkey -v -keystore noxira-release.jks \
           -keyalg RSA -keysize 2048 -validity 10000 \
           -alias noxira
   ```

2. Add signing config to `app/build.gradle.kts`:
   ```kotlin
   signingConfigs {
       create("release") {
           storeFile = file("noxira-release.jks")
           storePassword = System.getenv("STORE_PASSWORD")
           keyAlias = "noxira"
           keyPassword = System.getenv("KEY_PASSWORD")
       }
   }
   buildTypes {
       release { signingConfig = signingConfigs.getByName("release") }
   }
   ```

3. Build:
   ```bash
   STORE_PASSWORD=... KEY_PASSWORD=... ./gradlew assembleRelease
   ```

**Never commit signing keys or passwords to source control.**

---

## Architecture

```
com.noxira.launcher/
├── MainActivity.kt              — single-activity host
├── NoxiraApp.kt                 — NavHost + route constants
├── MainViewModel.kt             — shared state, all coroutine actions
├── core/
│   ├── launcher/
│   │   └── LauncherEngine.kt   — Roblox detection, intent launch, grid, App Cloner patch
│   ├── root/
│   │   └── RootManager.kt      — su shell, root check, scoped commands
│   └── prefs/
│       └── NoxiraPrefs.kt      — DataStore, settings model
└── ui/
    ├── theme/
    │   └── NoxiraTheme.kt      — full design system (colors, type, spacing, radii)
    ├── components/
    │   ├── GlassComponents.kt  — GlassCard, NoxiraButton, StatusBadge, QuickActionChip…
    │   └── NoxiraEmblem.kt     — Canvas-drawn emblem + wordmark composable
    └── screens/
        ├── SplashScreen.kt
        ├── DashboardScreen.kt
        ├── WindowManagerScreen.kt
        ├── SettingsScreen.kt
        ├── DiagnosticsScreen.kt
        └── AboutScreen.kt
```

---

## APK Inspection Results (ZetsuLauncher.apk)

| Item | Value |
|---|---|
| Package | `com.zetsu.rbxgrid` |
| Version | `1.0` |
| Min SDK | API 14 (manifest), effective API 29 (uses am windowing mode 5) |
| Permissions | `QUERY_ALL_PACKAGES`, `SYSTEM_ALERT_WINDOW` |
| Classes | `MainActivity`, `Grid` |
| Languages | Indonesian UI strings |
| Root check | `echo __ROOT_UID_$(id -u)` via `su` |
| Launch method | `am start --windowingMode 5 -n com.roblox.client/com.roblox.client.ActivitySplash` |
| Grid method | `am stack resize`, `am task resize` |
| Freeform setup | `settings put global enable_freeform_support 1` |
| App Cloner patch | Write XML to `/data/data/<pkg>/shared_prefs/<pkg>.xml` |
| Views | Programmatic (no XML layouts) |

All verified capabilities are preserved in NOXIRA.

---

## Limitations

| Limitation | Detail |
|---|---|
| Root required for grid | `am stack resize` and `am task resize` require root on API 29+ |
| Freeform needs reboot | System setting change takes effect after reboot |
| Task IDs vary | Stack/task IDs change per Android build and session; `am stack list` is parsed each run |
| App Cloner versions | XML patch targets known field names; different App Cloner versions may differ |
| Device-specific | Freeform windowing works on most Android 10+ devices but not all OEM variants |
| Physical device untested | Build is verified to compile; physical-device run requires a rooted Android 10+ device |

---

## Test Status

| Test | Status |
|---|---|
| Default settings values | ✅ Unit-tested |
| Value clamping (delay, gap, cols) | ✅ Unit-tested |
| Roblox package prefix matching | ✅ Unit-tested |
| Best-columns algorithm | ✅ Unit-tested |
| Root check on device | ⬜ Not yet (needs rooted device) |
| Roblox install detection on device | ⬜ Not yet |
| Freeform launch on device | ⬜ Not yet |
| Grid tiling on device | ⬜ Not yet |
| App Cloner XML patch on device | ⬜ Not yet |
| Settings persistence across restart | ⬜ Not yet (DataStore — standard library behavior) |
| Screen rotation | ⬜ Not yet |

---

## Privacy

NOXIRA does not collect, store, or transmit any user data externally.  
No Roblox credentials are requested or stored.  
All shell operations are scoped to documented launcher functions.
