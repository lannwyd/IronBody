# 🏋️ IronBody

> **A modern, sleek, and privacy-first Android gym tracking app built with Jetpack Compose & Material 3.**

IronBody allows gym-goers to organize their strength workouts across muscle groups, track current weights with single-tap quick controls, reorder routines effortlessly, and keep all workout data 100% offline and secure on-device.

---

## ✨ Features

- **⚡ Target Muscle Group Sections**:
  - Organize exercises into 8 primary muscle groups: **Chest**, **Back**, **Biceps**, **Triceps**, **Forearms**, **Shoulders**, **Abs**, and **Legs**.
  - Horizontal quick-filter bar with live badge counts showing how many exercises you have per muscle group.
  - Grouped section view when viewing all exercises at once.

- **🎯 Current Weight Tracking**:
  - Quick `+` and `–` adjustment buttons on each exercise card with smooth rolling number animations.
  - Full support for both **KG** and **LBS** units.

- **📸 Custom Exercise Photos**:
  - Attach exercise demonstrations, seat settings, or equipment photos directly using Android's privacy-preserving Photo Picker.

- **🖐️ Intuitive Gestures**:
  - **Drag-to-Reorder**: Reorder exercises within sections using dedicated drag handles.
  - **Swipe-to-Dismiss**: Swipe an exercise card left or right to delete, complete with an instant **Undo** snackbar.

- **🔍 Real-Time Search & Filters**:
  - Instantly search through exercise names and personal notes/form cues.
  - Filter exercises by unit (KG / LBS) or muscle section.

- **🔒 100% Offline & Private**:
  - No account required, no tracking, and no external cloud servers. All your personal gym progress stays on your device.

---

## 🛠️ Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/) (JVM 17 / 21)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material Design 3](https://m3.material.io/)
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture (Data, Domain, Presentation layers)
- **Dependency Injection**: [Dagger Hilt](https://dagger.dev/hilt/)
- **Local Database**: [Room Database](https://developer.android.com/training/data-storage/room) with [KSP](https://kotlinlang.org/docs/ksp-overview.html)
- **Key-Value Storage**: [Jetpack DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore)
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/)
- **Reordering**: [Calvin Reorderable](https://github.com/calvin-reorderable)
- **Build System**: Gradle Kotlin DSL (`.gradle.kts`) with Version Catalogs (`libs.versions.toml`)

---

## 📂 Project Structure

```
IronLog/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/ironlog/gym/
│   │   │   ├── data/
│   │   │   │   ├── datastore/       # DataStore preferences (units, settings)
│   │   │   │   ├── local/           # Room Database, DAOs, and Entities
│   │   │   │   └── repository/      # Repository implementations
│   │   │   ├── di/                  # Hilt Dependency Injection modules
│   │   │   ├── domain/
│   │   │   │   ├── model/           # Core models (Exercise, MuscleSection)
│   │   │   │   └── repository/      # Repository interfaces
│   │   │   ├── ui/
│   │   │   │   ├── components/      # Reusable UI widgets, bars, buttons
│   │   │   │   ├── navigation/      # Compose Navigation Host & routes
│   │   │   │   ├── screens/         # Feature screens (ExerciseList, ExerciseEdit)
│   │   │   │   └── theme/           # Material 3 colors, typography, shapes
│   │   │   └── util/                # Weight formatters, image storage utils
│   │   └── res/                     # Drawables, mipmaps, strings, colors
├── gradle/                          # Gradle wrapper and version catalog
├── build.gradle.kts                 # Root build script
├── settings.gradle.kts              # Project repositories & module settings
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: Ladybug (2024.2.1+) or newer
- **Java Development Kit**: JDK 17 or JDK 21
- **Android SDK**:
  - `minSdk` = 26 (Android 8.0 Oreo)
  - `targetSdk` = 35 (Android 15)
  - `compileSdk` = 35

### Setup & Run

1. **Clone the repository**:
   ```bash
   git clone <repo-url>
   cd IronLog
   ```

2. **Open in Android Studio**:
   - Open Android Studio, click **Open**, and select the project directory.
   - Let Gradle sync the dependencies.

3. **Build the Debug APK**:
   ```powershell
   ./gradlew assembleDebug
   ```
   The APK will be generated at:
   `app/build/outputs/apk/debug/app-debug.apk`

4. **Install to Connected Device**:
   Ensure USB Debugging is enabled on your phone:
   ```powershell
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```
   Or click the green **Run (▶)** button in Android Studio.

---

## 📜 License

This project is licensed under the MIT License - feel free to customize and use it for your workouts!
