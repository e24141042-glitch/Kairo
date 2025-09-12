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

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24 or higher
- Kotlin 1.9.10 or later

## Installation

- **click on release**: download ```app-release.apk``` and install it

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

