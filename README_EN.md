# The Family App (English Overview)

## Project summary
The Family App is an Android application originally built in Java/Fragments to help families coordinate everyday life.  
The current codebase includes modules for:

- Shopping lists
- Meal planning
- Calendar
- Birthdays
- Wishlists
- Family chat
- User/family setup and profile/group screens

## Current technical state
- Platform: Android (Java, Fragment Navigation, SQLite)
- Persistence: Local SQLite via a custom `Database` helper
- Build system: Gradle 7.6.4 + Android Gradle Plugin 7.4.2
- Java runtime compatibility: validated with Java 17

## Build and run
1. Open the project root in Android Studio.
2. Ensure Android SDK is installed and configured.
3. Sync Gradle.
4. Build and run the `app` module on emulator/device.

From CLI:

```bash
./gradlew assembleDebug
./gradlew test
```

## Feature completeness notes
Implemented breadth is strong (many screens and flows exist), but several areas remain incomplete relative to the original design vision:

- Family map/location module is not complete
- No production backend/API architecture yet (local-first SQLite only)
- Real-time family sync strategy is not finalized
- Norwegian naming and UI text remain across code/resources
- Frontend polish is legacy/prototype-level compared to modern Android standards

## Design and architecture references
The project planning and architecture direction are aligned with the original design documents, including:

- `Systemdesign_Android_Gruppe06.pdf`
- `Mobilprogrammering 2020.txt`

Current direction:
- English-first naming and UI
- Backend/storage/API strategy evaluation (local-first vs cloud-backed)
- Modern frontend migration to Kotlin + Jetpack Compose + Material 3
