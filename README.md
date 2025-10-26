# Kairo — Modern Task Management (Android)

Kairo is a modern, Material 3 Android app for planning, tracking, and completing tasks. It uses a clean MVVM architecture with Jetpack Compose, Room, Hilt, Coroutines, and DataStore.

## Features
- Task management with title, description, priority, category, due date, and repeat
- Swipe to delete, mark complete, sort and filter
- Safe, consistent confirmation dialogs for deletions:
  - Delete single task (incl. swipe)
  - Clear completed tasks (Home, Settings)
  - Delete all tasks (Settings)
- Google Calendar sync:
  - Connect/disconnect in `Settings → Integrations → Google Calendar`
  - Automatically creates a dedicated "Kairo" calendar and stores its ID
  - Shows Kairo Calendar ID in Settings with a copy-to-clipboard button
  - Shows each task’s Google Calendar Event ID on the Add/Edit Task screen with copy-to-clipboard
  - Handles duplicate creation, stale event IDs, and ensures unique sync enqueuing

## Tech Stack
- Kotlin, Jetpack Compose (Material 3)
- MVVM, Hilt, Coroutines/Flow
- Room, DataStore, Navigation Compose

## Getting Started
1. Clone the repository:
   ```bash
   git clone https://github.com/e24141042-glitch/Kairo
   ```
2. Open in Android Studio and let Gradle sync.
3. Run on a device/emulator from Android Studio or via CLI.

## Build & Install (CLI)
- Build debug APK:
  ```bash
  ./gradlew assembleDebug
  ```
- Install on a connected device:
  ```bash
  ./gradlew installDebug
  ```
  Ensure a device/emulator is connected and authorized.

## Google Calendar Integration
- Connect your Google account in `Settings → Integrations → Google Calendar`.
- On connect, Kairo creates a dedicated calendar and stores its ID.
- View and copy the Kairo Calendar ID in Settings.
- For tasks that are synced, the Add/Edit Task screen displays the Google Calendar Event ID with a copy button.

## Development Notes
- Build outputs directory `app_build/` is ignored by Git (see `.gitignore`).
- Latest feature work was pushed to branch `Calender_mod`; review and merge into `main` as needed.

## Contributing
- Fork the repo, create a feature branch, open a PR.

## License
- MIT (see `LICENSE`).

