# Skhaftin - Food Donation Android App

## Project Title
Skhaftin: A Comprehensive Food Donation and Sharing Platform
<a href="https://youtu.be/dyvt-6z32p8">
    <img src="https://img.shields.io/badge/Watch_Demo-YouTube-red?style=for-the-badge&logo=youtube" alt="Watch Demo on YouTube">
  </a>
## TEAM Members
- **Student Name**: Okuhle Nwayo
- **Student Number**: St10299658
- **Student Name**: Cebo Nwayo
- **Student Number**: St10293982
- **Student Name**: Luke Lutchmiah
- **Student Number**: St10288560
- **Student Name**: Lethabo Penniston
- **Student Number**: St10302369
  

## Project Overview
Skhaftin is a sophisticated Android application. The application leverages modern Android development practices to create a platform that facilitates food donation and sharing within communities. By connecting surplus food donors with those in need, Skhaftin addresses food waste and food insecurity issues in South Africa.

The project demonstrates advanced software engineering principles, including scalable architecture, offline-first design, real-time communication, and comprehensive testing strategies.

## Objectives
- Develop a user-friendly mobile application for food donation coordination
- Implement secure authentication and user management systems
- Create an efficient offline-capable data synchronization system
- Build real-time communication features for donor-recipient interaction
- Ensure scalability and maintainability through proper architectural patterns
- Provide multi-language support for diverse user base
- Implement comprehensive testing and quality assurance

## Features

### Core Features
- **User Authentication**: Secure login and registration with email/password and Google Sign-In
- **Food Donation Management**: Create, list, and manage food donations with images and details
- **Offline Image Handling**: Save images locally when offline and display them from local storage
- **Item Discovery**: Browse available food items with filtering and selection
- **Real-time Chat**: Connect with donators through in-app messaging
- **Performance Metrics**: Track donation impact, active listings, and user ratings
- **Multi-language Support**: Interface available in English, Afrikaans, isiZulu, and isiXhosa
- **Location-based Services**: Location-aware item posting and discovery

### Advanced Features
- **Impact Tracking**: Monitor environmental and social impact of donations
- **Scheduled Donations**: Plan and manage recurring food donations
- **Push Notifications**: Real-time alerts for new donations and messages
- **Data Synchronization**: Robust offline-online data sync with conflict resolution
- **Security**: Firebase security rules and encrypted local storage

## Tech Stack

### Frontend
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose, Material Design 3
- **Architecture**: MVVM with ViewModels and Repository pattern
- **Database**: Room for local storage
- **Dependency Injection**: Manual dependency injection

### Backend
- **Platform**: Firebase
  - Authentication (Email/Password, Google Sign-In)
  - Firestore Database (NoSQL document database)
  - Firebase Storage (Image storage)
  - Firebase Cloud Messaging (Push notifications)

### Development Tools
- **Build Tool**: Gradle with Kotlin DSL
- **Version Control**: Git
- **IDE**: Android Studio
- **Testing**: JUnit, Espresso
- **CI/CD**: GitHub Actions (planned)

## Prerequisites

Before setting up the project, ensure you have the following installed:

- **Java Development Kit (JDK) 11 or higher**
- **Android Studio** (latest stable version)
- **Firebase CLI** (for Firebase management)
- **Git** (for version control)

## Quick Setup

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd skhaftin-android
   ```

2. **Open in Android Studio**:
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory and select it
   - Wait for Gradle sync to complete

3. **Firebase Setup**:
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Add an Android app with package name `com.skhaftin`
   - Download `google-services.json` and place it in `app/` directory
   - Enable Authentication and Firestore in Firebase Console

4. **Run the app**:
   - Connect an Android device or start an emulator
   - Click "Run" in Android Studio or use `./gradlew installDebug`

## Detailed Setup Guide

For comprehensive setup instructions including Firebase CLI installation, emulator setup, and troubleshooting, see [ANDROID_EMULATOR_SETUP.md](ANDROID_EMULATOR_SETUP.md).

## Project Structure

```
app/
├── src/main/
│   ├── java/com/skhaftin/
│   │   ├── data/           # Data layer (Repository, API calls)
│   │   ├── model/          # Data models
│   │   ├── ui/             # UI components and screens
│   │   ├── viewmodel/      # ViewModels for MVVM
│   │   └── MainActivity.kt # Main activity
│   ├── res/                # Resources (layouts, drawables, values)
│   └── AndroidManifest.xml
├── build.gradle.kts        # App-level build configuration
└── google-services.json     # Firebase configuration
```

## Key Components

- **Screens.kt**: All composable UI screens (Login, Register, Home, etc.)
- **MainActivity.kt**: Main activity handling navigation
- **DataRepository.kt**: Central data access point
- **UserViewModel.kt**: User authentication and profile management
- **ItemViewModel.kt**: Food item management

## Building and Running

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### Run Tests
```bash
./gradlew test
```

### Run on Device
```bash
./gradlew installDebug
```

## System Architecture

### MVVM Architecture Pattern
The application follows the Model-View-ViewModel (MVVM) architectural pattern, ensuring separation of concerns and testability:

- **Model**: Data classes and business logic (User, Item, Message models)
- **View**: Jetpack Compose UI components (Screens.kt, composables)
- **ViewModel**: State management and business logic (UserViewModel, ItemViewModel)

### Data Flow
1. User interactions trigger events in ViewModels
2. ViewModels communicate with Repository layer
3. Repository handles data operations (local Room DB and remote Firebase)
4. Data changes are observed via LiveData/Flow and update the UI

### Offline-First Design
- Local Room database for offline data storage
- Firebase synchronization when online
- Conflict resolution for data consistency

## Database Design

### Firebase Firestore Schema
```
users/{userId}
├── name: String
├── email: String
├── location: String
├── preferredLanguage: String
└── impact: Impact object

items/{itemId}
├── name: String
├── quantity: String
├── description: String
├── location: String
├── imageUrl: String
├── ownerId: String
├── uniqueCode: String
└── timestamp: Timestamp

chats/{chatId}
├── participants: Array<String>
├── lastMessage: String
└── timestamp: Timestamp

messages/{messageId}
├── chatId: String
├── senderId: String
├── content: String
└── timestamp: Timestamp
```

### Local Room Database
- Users table for cached user data
- Items table for offline item storage
- Synchronization flags for conflict resolution

## Implementation Details

### Key Technologies Used
- **Kotlin Coroutines**: Asynchronous programming
- **Flow**: Reactive data streams
- **Hilt/Dagger**: Dependency injection (planned for future)
- **Coil**: Image loading and caching
- **Material Design 3**: Modern UI components

### Security Implementation
- Firebase Authentication with secure token management
- Firestore security rules preventing unauthorized access
- Encrypted local storage for sensitive data
- Input validation and sanitization

### Performance Optimizations
- Lazy loading for lists
- Image compression and caching
- Background sync workers
- Memory-efficient data structures

## Testing Strategy

### Unit Testing
- ViewModel business logic testing with JUnit
- Repository layer testing with mock data
- Utility function testing

### Integration Testing
- Database operations testing
- Firebase integration testing
- API endpoint testing

### UI Testing
- Espresso for UI component testing
- Screenshot testing for visual regression
- Accessibility testing

### Test Coverage
- Target: 80%+ code coverage
- Automated CI/CD pipeline for continuous testing

## Challenges and Solutions

### Challenge 1: Offline Synchronization
**Problem**: Ensuring data consistency between local and remote databases.
**Solution**: Implemented conflict resolution algorithms and timestamp-based syncing.

### Challenge 2: Real-time Chat
**Problem**: Managing real-time message delivery and UI updates.
**Solution**: Used Firebase Firestore real-time listeners with efficient UI state management.

### Challenge 3: Multi-language Support
**Problem**: Implementing dynamic language switching.
**Solution**: Created LocaleUtils class with resource reloading mechanism.

## Future Enhancements

- **Machine Learning**: AI-powered food matching and waste prediction
- **IoT Integration**: Smart fridge connectivity for automatic listings
- **Blockchain**: Transparent donation tracking
- **AR Features**: Augmented reality for food quality inspection
- **Cross-platform**: iOS and web versions

## Conclusion

Over the 4-year development period, Skhaftin evolved from a simple concept to a comprehensive platform addressing real-world problems. The project successfully demonstrated:

- Advanced Android development skills
- Scalable architecture design
- Full-stack development capabilities
- Problem-solving in complex systems
- User-centered design principles

The application not only fulfills its core purpose of reducing food waste and hunger but also serves as a testament to the power of technology in creating positive social impact.

## References

1. Android Developers Documentation - https://developer.android.com/
2. Firebase Documentation - https://firebase.google.com/docs
3. Jetpack Compose Documentation - https://developer.android.com/jetpack/compose
4. Material Design Guidelines - https://material.io/design
5. Kotlin Programming Language - https://kotlinlang.org/

## Acknowledgments

Special thanks to:
- IIE Varsity College faculty and mentors
- Firebase team for excellent documentation
- Android developer community
- Open-source contributors

## Firebase Configuration

The app uses Firebase for:
- User authentication (Email/Password and Google Sign-In)
- Firestore database for user data and food listings
- Firebase Storage for images (if implemented)

Ensure your Firebase project has the correct security rules configured.

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Create a Pull Request
