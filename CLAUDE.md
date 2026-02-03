# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**ToDaily** is an Android todo list application built with Jetpack Compose and Kotlin. It features task management with priorities, tags, reminders, and data export/import capabilities.

- **Package**: `com.jsjh_todaily.test_todaily_ver1`
- **Min SDK**: 28 (Android 9.0)
- **Target SDK**: 36
- **Current Version**: 1.0.6 (versionCode: 8)

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK (uses debug signing config)
./gradlew assembleRelease

# Build and run tests
./gradlew build

# Run unit tests
./gradlew test

# Run instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest

# Clean build artifacts
./gradlew clean

# Assemble all variants
./gradlew assemble
```

## Architecture

### MVVM Pattern
The app follows the Model-View-ViewModel architecture:
- **Model**: Room database entities and repositories in `data/`
- **View**: Jetpack Compose UI in `ui/`
- **ViewModel**: `TodoViewModel` manages UI state and business logic

### Key Components

#### Data Layer (`data/`)
- **Todo.kt**: Main entity with fields for content, priority, tags, due date, reminder times, and completion status
- **TodoDatabase.kt**: Room database (version 2) with singleton pattern, uses `fallbackToDestructiveMigration()`
- **TodoDao.kt**: Data access object with Flow-based queries
- **TodoRepository.kt**: Repository pattern for data operations
- **Converters.kt**: Room type converters for Priority enum and List<String>/List<Long>

#### ViewModel Layer
- **TodoViewModel.kt**: AndroidViewModel that manages:
  - All todos via StateFlow
  - High priority todos
  - Filtered/sorted todos (by search query, completion status, sort order)
  - Data export/import operations
  - Alarm scheduling integration

#### UI Layer (`ui/`)
- **MainActivity.kt**: Entry point with permission handling (notifications, exact alarms), splash screen, and bottom navigation
- **screens/**: HomeScreen, ListScreen, SettingsScreen, SplashScreen
- **components/**: Reusable UI components (TodoItem, PriorityButton, TagChip)
- **dialogs/**: TodoDetailDialog, DeleteConfirmDialog
- **theme/**: Material3 theming with custom gradient background for light mode

#### Notification System (`notification/`)
- **AlarmScheduler.kt**: Manages alarm scheduling using AlarmManager
  - Uses `setExactAndAllowWhileIdle()` for precise timing
  - Generates unique request codes (todo.id for due date, todo.id * 1000 + index for reminders)
- **AlarmReceiver.kt**: BroadcastReceiver for alarm notifications

#### Data Management (`data/export/`)
- **DataManager.kt**: Handles JSON export/import using Gson
  - Exports to Downloads folder with timestamp
  - BackupData structure includes version, exportDate, and todos list

### Data Flow
1. User interactions in UI trigger ViewModel methods
2. ViewModel calls Repository for database operations
3. Repository uses DAO to interact with Room database
4. Database changes emit through Flow
5. StateFlows in ViewModel update UI automatically
6. AlarmScheduler is invoked for reminder management

### Navigation
Bottom navigation with 3 screens:
- Home (`"home"`)
- List (`"list"`)
- Settings (`"settings"`)

Navigation is handled by Jetpack Navigation Compose with single-top and state restoration.

### State Management
- Uses Kotlin Flow and StateFlow for reactive data
- `combine()` operator merges multiple state sources (todos, search, filters, sort order)
- ViewModel scoped to activity lifecycle

## Dependencies

Key libraries (defined in `gradle/libs.versions.toml`):
- **Compose BOM**: 2024.09.00
- **Room**: 2.6.1 (with KSP for annotation processing)
- **Navigation Compose**: 2.8.5
- **DataStore Preferences**: 1.1.1
- **Lifecycle ViewModel Compose**: 2.8.7
- **Material Icons Extended**
- **Gson**: 2.10.1 for JSON serialization

## Important Implementation Notes

### Room Database
- Current version: 2
- Uses `fallbackToDestructiveMigration()` - schema changes will wipe data
- Database name: `todaily_database`
- All queries use Flow for reactive updates

### Permissions
The app requires runtime permissions (handled in MainActivity):
- `POST_NOTIFICATIONS` (Android 13+)
- `SCHEDULE_EXACT_ALARM` and `USE_EXACT_ALARM` for precise reminders
- Storage permissions for export/import (up to SDK 32)

### Alarm Scheduling
- Alarms are scheduled/cancelled when todos are added, updated, or deleted
- Past timestamps are ignored (not scheduled)
- Each todo can have multiple reminder times
- AlarmScheduler must be called from ViewModel to maintain consistency

### Priority System
Priority enum (in `data/Priority.kt`) determines task importance. Stored as ordinal in database.

### Sorting Options
Available via `SortOrder` enum:
- NEWEST (by creation time descending)
- PRIORITY (high to low)
- TITLE (alphabetical)
- DUE_DATE (earliest first)

### Search Features
- Plain text search filters by content
- Tag search with `#` prefix (e.g., `#work`)

## File Locations

- Source code: `app/src/main/java/com/jsjh_todaily/test_todaily_ver1/`
- Resources: `app/src/main/res/`
- Build config: `app/build.gradle.kts`
- Version catalog: `gradle/libs.versions.toml`
- Manifest: `app/src/main/AndroidManifest.xml`

## Common Development Tasks

When modifying the database schema:
1. Update the `Todo` entity or add new entities
2. Increment `version` in `@Database` annotation
3. Note that current setup uses `fallbackToDestructiveMigration()` - consider implementing proper migrations for production

When adding new screens:
1. Create screen composable in `ui/screens/`
2. Add route to NavHost in MainActivity
3. Update bottom navigation items if needed

When modifying alarm behavior:
1. Update `AlarmScheduler` for scheduling logic
2. Update `AlarmReceiver` for notification handling
3. Ensure ViewModel calls scheduler on todo changes
