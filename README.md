# IronBody

A modern, sleek, and privacy-first Android gym tracking application built with Jetpack Compose and Material 3.

IronBody is designed for serious lifters and fitness enthusiasts who want a fast, reliable, and completely offline workout tracker. It features a pre-loaded library of exercises with demonstration photos, routine and split management, active session logging with background timers, body weight tracking, and a customizable dual-theme design.

---

## Features

### Pre-loaded Exercise Library with Real Photos
- Over 100 gym exercises pre-loaded across 8 muscle groups (Chest, Back, Biceps, Triceps, Forearms, Shoulders, Abs, Legs).
- Real demonstration photographs for every exercise sourced from the open-source free-exercise-db dataset.
- Compact, fully offline storage: all photos are optimized and bundled directly within the app, requiring zero network access.
- Quick-add bottom sheet: select any exercise from the library, input your starting weight and personal notes, and add it directly to your routine.

### Exercise Management and Tracking
- Organize exercises by muscle groups with horizontal filter chips and live count badges.
- Quick single-tap weight adjustments (+ / -) with smooth animated numeric counters.
- Support for both Metric (KG) and Imperial (LBS) units.
- Attach custom equipment photos, seat settings, or form demonstrations using Android Photo Picker.
- Drag-and-drop reordering with dedicated drag handles.
- Swipe-to-dismiss gesture for deleting exercises with an instant Undo snackbar.
- Real-time search by exercise name, notes, or equipment settings.

### Splits and Routine Planning
- Create and customize multi-day workout splits (such as Push/Pull/Legs, Upper/Lower, or custom programs).
- Organize specific exercises into individual workout days.
- Pre-built split presets for quick program setup.
- Active split switcher, inline renaming, and export/share capabilities.

### Live Workout Sessions and Logging
- Pre-Session Overview: Preview scheduled exercises and target weights before starting a workout.
- Dual Chronometers: Independent live timers tracking active exercise duration and total session time.
- Integrated Rest Timer: Built-in countdown timer with quick preset chips (30s, 60s, 90s, 120s, and custom duration).
- Interactive Set Tracking: Mark sets as completed with instant visual feedback and rest timer triggers.
- Persistent Notification: A foreground service keeps workout timers running accurately in the notification shade even when the screen is off or the app is minimized.
- Workout Completion Summary: Finish session dialog summarizing total workout time, exercises performed, and notes before saving to the database.

### Workout History
- Dedicated workout history screen displaying past completed sessions.
- Detailed logs showing date, workout duration, exercises completed, and notes.
- Ability to manage and delete previous workout logs.

### Body Weight Tracking
- Track body weight progression over time with date-stamped entries.
- Log weight changes in kilograms or pounds.
- Historical log view for monitoring fitness goals and body composition trends.

### Standalone Rest and Interval Timer
- Dedicated timer tab featuring countdown intervals and stopwatches for circuit training, HIIT, or general gym timing.

### Modern UI, Animations, and Theming
- Dual Indigo and Cyan Theme:
  - Dark Mode: Deep `#0D0E14` background, `#171923` card surfaces, `#818CF8` primary indigo accent, and `#22D3EE` cyan badges.
  - Light Mode: Clean `#F5F6FB` background, `#FFFFFF` card surfaces, `#6366F1` primary indigo accent, and `#0891B2` cyan highlights.
  - One-tap light/dark toggle in the top navigation bar.
  - Automatic system theme adaptation via Jetpack Compose.
- Motion and Transitions: Pulsing timers, smooth color transitions, and animated card expand/collapse states.
- Hardware Navigation Compatibility: Fully supports modern gesture navigation as well as traditional 3-button system navigation without layout clipping.

### Privacy and Offline Guarantee
- 100% offline functionality.
- No user accounts, registration, analytics, or tracking libraries.
- All workout logs, splits, exercises, and photos reside exclusively on your local device.

---

## Technical Stack and Architecture

- **Language**: Kotlin
- **UI Toolkit**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM (Model-View-ViewModel) with Clean Architecture principles
- **Dependency Injection**: Dagger Hilt
- **Local Persistence**: Room Database (SQLite) with Kotlin Symbol Processing (KSP)
- **Preferences**: Jetpack DataStore Preferences
- **Image Loading**: Coil
- **List Reordering**: Calvin Reorderable
- **Build System**: Gradle Kotlin DSL with Version Catalogs (`libs.versions.toml`)

---

## Project Structure

```
IronLog/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/ironlog/gym/
│   │   │   ├── data/
│   │   │   │   ├── datastore/       # DataStore preferences (theme, units)
│   │   │   │   ├── library/         # Pre-loaded exercise library definitions
│   │   │   │   ├── local/           # Room Database, DAOs, and Entities
│   │   │   │   └── repository/      # Repository implementations
│   │   │   ├── di/                  # Hilt dependency injection modules
│   │   │   ├── domain/
│   │   │   │   ├── model/           # Domain models (Exercise, Split, Session, etc.)
│   │   │   │   └── repository/      # Repository interfaces
│   │   │   ├── service/             # Foreground services for persistent session timers
│   │   │   ├── ui/
│   │   │   │   ├── components/      # Common UI elements (TopBar, BottomBar, Cards)
│   │   │   │   ├── navigation/      # Navigation graphs and type-safe routes
│   │   │   │   ├── screens/         # Feature screens (Exercises, Splits, Session, Timer, History)
│   │   │   │   └── theme/           # Color palette, typography, shapes
│   │   │   └── util/                # Formatters, converters, and helper utilities
│   │   └── res/                     # Vector drawables, exercise demonstration images, mipmaps
├── gradle/                          # Gradle wrapper and version catalog
├── build.gradle.kts                 # Root Gradle configuration
├── settings.gradle.kts              # Project modules and repositories
└── README.md
```

---

## Getting Started

### Prerequisites

- Android Studio Ladybug (2024.2.1+) or newer
- JDK 17 or JDK 21
- Android SDK:
  - Minimum SDK: 26 (Android 8.0)
  - Target SDK: 35 (Android 15)
  - Compile SDK: 35

### Building from Source

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd IronLog
   ```

2. Open the project in Android Studio and synchronize Gradle dependencies.

3. Build the release APK via command line:
   ```bash
   ./gradlew assembleRelease
   ```
   The APK will be located at:
   `app/build/outputs/apk/release/app-release.apk`

4. Install to a connected device via ADB:
   ```bash
   adb install -r -d app/build/outputs/apk/release/app-release.apk
   ```

---

## License

This project is licensed under the MIT License.
