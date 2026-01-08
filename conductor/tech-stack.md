# Tech Stack - Muse

## Core Technologies
- **Language:** Kotlin
- **Multiplatform:** Kotlin Multiplatform (KMP) for shared logic across Android and iOS.
- **UI Framework:** Compose Multiplatform for shared UI components.
- **Dependency Injection:** Koin for lightweight dependency management.

## Backend and Networking
- **Networking:** Ktor for asynchronous HTTP requests.
- **Serialization:** Kotlinx Serialization for JSON parsing.

## Data Management
- **Local Database:** SQLDelight for typesafe local storage.
- **Persistence:** Android DataStore for preference storage.

## Media and Processing
- **Audio Playback/Processing:** Media3 (ExoPlayer/Transformer) on Android, Native Darwin APIs on iOS.

## Testing and Quality
- **Unit Testing:** Kotlin Test, JUnit.
- **Error Reporting:** xCrash for Android crash monitoring.
