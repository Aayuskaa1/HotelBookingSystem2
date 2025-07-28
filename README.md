# Hotel Booking System

A modern Android application built with Jetpack Compose for hotel booking and management.

## Features

### 🔐 Authentication
- **Splash Screen**: Beautiful animated splash screen with hotel branding
- **Login Screen**: Welcome screen that directs users to sign up
- **Sign Up Screen**: User registration with name, email, and password validation
- **Admin Dashboard**: Comprehensive admin interface for hotel management
- **User Dashboard**: User-friendly interface for hotel booking and management
- **Admin Access**: Admin credentials are pre-configured in Firebase

### 🎨 UI/UX
- Modern Material Design 3 components
- Beautiful gradient backgrounds
- Smooth animations and transitions
- Responsive design for different screen sizes
- Password visibility toggle
- Form validation with error messages
- Loading states and progress indicators

### 🔧 Technical Features
- **Jetpack Compose**: Modern declarative UI toolkit
- **Firebase Authentication**: Secure user authentication
- **Navigation Compose**: Type-safe navigation between screens
- **ViewModel**: State management with Kotlin Flow
- **Material Icons**: Comprehensive icon library
- **Gradle**: Modern build system with version catalogs

## Screenshots

### Splash Screen
- Animated hotel icon with gradient background
- App branding and tagline
- Loading indicator

### Login Screen
- Welcome message explaining the registration process
- Information about admin access
- Direct navigation to sign up screen
- Beautiful gradient background

### Sign Up Screen
- Full name, email, password, and confirm password fields
- Real-time password validation
- Password mismatch detection
- Form validation
- Welcome message for new users

### Admin Dashboard
- **Statistics Overview**: Total hotels, active bookings, users, and revenue
- **Quick Actions**: Manage hotels, bookings, users, and reports
- **Admin Controls**: Comprehensive hotel management interface
- **Professional Design**: Clean, card-based layout with admin branding

### User Dashboard
- **Welcome Interface**: Personalized greeting with user information
- **Quick Stats**: My bookings and saved hotels counters
- **Search Hotels**: Prominent search functionality
- **Quick Actions**: My bookings, book hotel, profile, and help
- **Recent Activity**: Booking confirmations and payment history
- **User-Friendly Design**: Intuitive interface for hotel booking

## Setup Instructions

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 35
- Kotlin 2.0.0
- Firebase project with Authentication enabled

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd HotelBookingSystem2
   ```

2. **Configure Firebase**
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Enable Authentication with Email/Password provider
   - Download `google-services.json` and place it in the `app/` directory

3. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ```
   Or open the project in Android Studio and run it directly.

### Dependencies

The app uses the following major dependencies:

- **Jetpack Compose BOM**: 2024.04.01
- **Firebase Auth**: 24.0.0
- **Navigation Compose**: 2.7.7
- **Material Icons**: 1.6.3
- **Lifecycle ViewModel**: 2.9.2

## Project Structure

```
app/src/main/java/com/example/hotelbookingsystem/
├── MainActivity.kt                 # Main activity with app entry point
├── navigation/
│   └── AppNavigation.kt           # Navigation setup and screen routing
├── ui/screens/
│   ├── SplashScreen.kt            # Animated splash screen
│   ├── LoginScreen.kt             # Welcome screen with registration info
│   ├── SignUpScreen.kt            # User registration interface
│   ├── AdminDashboard.kt          # Admin dashboard with management tools
│   ├── UserDashboard.kt           # User dashboard for hotel booking
│   └── HomeScreen.kt              # Legacy home screen
├── viewmodel/
│   └── AuthViewModel.kt           # Authentication state management
├── utils/
│   └── UserRole.kt                # User role detection utilities
└── ui/theme/
    ├── Color.kt                   # App color definitions
    ├── Theme.kt                   # Material 3 theme setup
    └── Type.kt                    # Typography definitions
```

## Architecture

The app follows MVVM (Model-View-ViewModel) architecture:

- **View**: Compose UI components in the `ui/screens` package
- **ViewModel**: `AuthViewModel` manages authentication state and business logic
- **Model**: Firebase Authentication handles user data and authentication

### State Management
- Uses Kotlin Flow for reactive state management
- `AuthState` sealed class represents different authentication states
- Loading and error states are managed through StateFlow

### Navigation
- Single Activity architecture with Compose Navigation
- Type-safe navigation using sealed class `Screen`
- Automatic navigation based on authentication state

## Features in Detail

### Authentication Flow
1. **Splash Screen**: Shows for 2.5 seconds with animations
2. **Login Screen**: Welcome screen that explains the registration process
3. **Sign Up**: User creates new account
4. **Role Detection**: System determines if user is admin or regular user
5. **Dashboard**: Admin Dashboard for administrators, User Dashboard for regular users
6. **Logout**: Returns user to login screen
7. **Admin Access**: Admin can access with pre-configured Firebase credentials

### Form Validation
- Email format validation
- Password strength requirements (minimum 6 characters)
- Password confirmation matching
- Real-time validation feedback

### Error Handling
- Network error detection
- Firebase authentication error messages
- User-friendly error display
- Form validation errors

## Future Enhancements

- [ ] Hotel listing and search functionality
- [ ] Room booking and reservation system
- [ ] User profile management
- [ ] Booking history
- [ ] Payment integration
- [ ] Push notifications
- [ ] Offline support
- [ ] Multi-language support

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions, please open an issue in the repository or contact the development team. 