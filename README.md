# QuickTasks - Android To-Do List App

A modern, feature-rich Android to-do list application built with Jetpack Compose and MVVM architecture.

## Features

### Core Functionality
- ✅ Create, edit, delete, and mark tasks as complete/incomplete
- 📅 Set due dates with date picker
- ⏰ Set reminder times with notifications
- 🏷️ Organize tasks with categories/tags
- 🔄 Support for recurring tasks (daily, weekly, monthly)
- 🔍 Search and filter tasks by title, category, or priority
- 📊 Daily streak tracking and productivity stats

### User Interface
- 🎨 Modern Material 3 design with smooth animations
- 🌙 Light and dark theme support
- 👆 Swipe gestures for quick task completion and deletion
- 📱 Responsive layout for different screen sizes
- 🎯 Intuitive navigation between screens

### Notifications
- 🔔 Scheduled reminders based on task due dates
- ⏰ Custom notification times
- 📱 Actionable notifications (complete, snooze)
- 📈 Daily summary notifications

## Architecture

The app follows MVVM (Model-View-ViewModel) architecture with the following components:

### Data Layer
- **Room Database**: Local data persistence with Task entity
- **Repository Pattern**: Clean data access abstraction
- **DataStore**: User preferences and settings storage

### UI Layer
- **Jetpack Compose**: Modern declarative UI toolkit
- **Navigation Compose**: Type-safe navigation between screens
- **Material 3**: Latest Material Design components

### Business Logic
- **ViewModels**: State management and business logic
- **Hilt**: Dependency injection for clean architecture
- **Coroutines**: Asynchronous programming with Flow

## Project Structure

```
app/src/main/java/com/example/quicktasks/
├── data/                    # Data layer
│   ├── TaskDao.kt          # Room DAO for database operations
│   ├── TaskDatabase.kt     # Room database configuration
│   └── Converters.kt       # Type converters for Room
├── model/                   # Data models
│   └── Task.kt             # Task entity with all properties
├── repository/              # Repository pattern
│   ├── TaskRepository.kt   # Task data repository
│   └── SettingsRepository.kt # Settings data repository
├── viewmodel/               # ViewModels
│   ├── HomeViewModel.kt    # Home screen state management
│   ├── AddTaskViewModel.kt # Add/Edit task state management
│   ├── TaskDetailsViewModel.kt # Task details state management
│   ├── CategoriesViewModel.kt # Categories screen state management
│   └── SettingsViewModel.kt # Settings screen state management
├── ui/                      # UI layer
│   ├── theme/              # App theming
│   │   ├── Color.kt        # Color definitions
│   │   ├── Theme.kt        # Material theme setup
│   │   ├── Type.kt         # Typography definitions
│   │   └── ThemeManager.kt # Theme state management
│   ├── components/         # Reusable UI components
│   │   ├── TaskCard.kt     # Task card component
│   │   └── SwipeableTaskCard.kt # Swipeable task card
│   ├── screens/            # Screen composables
│   │   ├── HomeScreen.kt   # Main home screen
│   │   ├── AddTaskScreen.kt # Add/Edit task screen
│   │   ├── TaskDetailsScreen.kt # Task details screen
│   │   ├── CategoriesScreen.kt # Categories screen
│   │   └── SettingsScreen.kt # Settings screen
│   └── navigation/         # Navigation setup
│       ├── Screen.kt       # Screen route definitions
│       └── QuickTasksNavigation.kt # Navigation host
├── notifications/          # Notification system
│   ├── NotificationHelper.kt # Notification management
│   ├── TaskActionReceiver.kt # Action button handling
│   └── TaskNotificationWorker.kt # WorkManager for scheduling
├── di/                     # Dependency injection
│   └── DatabaseModule.kt   # Hilt database module
├── MainActivity.kt         # Main activity
└── QuickTasksApplication.kt # Application class
```

## Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24 or higher
- Kotlin 1.9.10 or later

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/e24141042-glitch/QuickTasks-master
   cd QuickTasks
   ```
2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the QuickTasks folder and open it

3. **Sync the project**
   - Android Studio will automatically sync the project
   - Wait for the sync to complete

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button or press Shift+F10

### Dependencies

The app uses the following key dependencies:

- **Jetpack Compose**: 1.6.0
- **Material 3**: 1.2.0
- **Navigation Compose**: 2.7.7
- **Room**: 2.6.1
- **Hilt**: 2.48
- **WorkManager**: 2.9.0
- **DataStore**: 1.0.0

## Usage

### Creating Tasks
1. Tap the "+" button on the home screen
2. Enter task title (required)
3. Add description, due date, priority, and category
4. Set reminder time if needed
5. Enable recurring if desired
6. Tap "Save"

### Managing Tasks
- **Complete**: Tap the checkbox or swipe right
- **Delete**: Tap the delete icon or swipe left
- **Edit**: Tap on a task to view details, then tap "Edit"
- **Search**: Use the search bar on the home screen

### Categories
- View all categories on the Categories screen
- Filter tasks by category
- Categories are automatically created when you add tasks

### Settings
- Toggle dark/light theme
- Enable/disable notifications
- Set notification time
- Enable cloud backup (placeholder)
- Clear all data

## Features in Detail

### Task Properties
- **Title**: Required field for task name
- **Description**: Optional detailed description
- **Due Date**: Optional deadline with date picker
- **Priority**: Low, Medium, High, or Urgent
- **Category**: Custom categories for organization
- **Reminder**: Optional notification time
- **Recurring**: Daily, weekly, or monthly repetition
- **Streak**: Automatic tracking of completion streaks

### Notifications
- **Task Reminders**: Notifications at specified times
- **Overdue Alerts**: Notifications for overdue tasks
- **Daily Summary**: Productivity summary notifications
- **Action Buttons**: Complete or snooze directly from notifications

### Themes
- **Light Theme**: Clean, bright interface
- **Dark Theme**: Easy on the eyes for low light
- **Dynamic Colors**: Android 12+ adaptive theming
- **Custom Accents**: Priority-based color coding

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Material Design 3 guidelines
- Android Jetpack libraries
- Jetpack Compose documentation
- Room database documentation

