# 🏨 Hotel Booking System 2.0

A modern Android application built with **Jetpack Compose** and **Firebase** for comprehensive hotel booking management.

![Hotel Booking System](app/src/main/res/drawable/image.jpg)

## ✨ Features

### 🔐 Authentication System
- **Splash Screen** with branded hotel logo
- **Login Screen** with email/password authentication
- **Sign Up Screen** for new user registration
- **Role-based Access**: Admin and User dashboards
- **Firebase Authentication** integration

### 👨‍💼 Admin Dashboard
- **Hotel Management**: Add, edit, delete, and update hotels
- **Booking Management**: View and manage all hotel bookings
- **User Management**: Monitor and manage user accounts
- **Bulk Operations**: Select multiple hotels for batch actions
- **Real-time Statistics**: Hotel and booking analytics

### 👤 User Dashboard
- **Hotel Search**: Browse available hotels with filters
- **Booking System**: Make hotel reservations with detailed forms
- **My Bookings**: View and manage personal bookings
- **Profile Management**: Update personal information
- **Help & Support**: Contact support and report issues

### 🏗️ Technical Features
- **Modern UI**: Material Design 3 with Jetpack Compose
- **Firebase Integration**: Real-time data synchronization
- **Offline Support**: Mock data for testing
- **Navigation**: Seamless screen transitions
- **State Management**: MVVM architecture with ViewModels
- **Responsive Design**: Works on all Android screen sizes

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 27+
- Kotlin 1.8+
- Firebase project setup

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Aayuskaa1/HotelBookingSystem2.git
   cd HotelBookingSystem2
   ```

2. **Firebase Setup**
   - Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Enable Authentication and Firestore
   - Download `google-services.json` and place it in the `app/` directory
   - Replace the placeholder values in `app/google-services.json`

3. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ./gradlew installDebug
   ```

### Default Credentials

#### Admin Access
- **Email**: `admin@hotelbooking.com`
- **Password**: `admin123456`

#### User Access
- **Email**: `aayuska@gmail.com`
- **Password**: `Aayuska@123`

## 📱 Screenshots

### Login Screen
- Professional hotel logo
- Clean authentication form
- Error handling and validation

### Admin Dashboard
- Comprehensive management tools
- Real-time statistics
- Bulk operations support

### User Dashboard
- Hotel search and booking
- Personal booking management
- Profile customization

## 🛠️ Architecture

```
app/
├── src/main/java/com/example/hotelbookingsystem/
│   ├── model/           # Data models
│   ├── repository/      # Data access layer
│   ├── viewmodel/       # Business logic
│   ├── ui/screens/      # UI components
│   ├── navigation/      # Navigation logic
│   └── utils/           # Utility classes
```

### Key Components

- **MVVM Pattern**: Separation of concerns
- **Repository Pattern**: Data abstraction
- **Compose UI**: Declarative UI framework
- **Firebase**: Backend services
- **Coroutines**: Asynchronous programming

## 🔧 Configuration

### Firebase Setup
1. Enable Authentication (Email/Password)
2. Create Firestore database
3. Set up security rules
4. Configure `google-services.json`

### Build Configuration
- **Target SDK**: 35
- **Minimum SDK**: 27
- **Kotlin**: 1.9+
- **Compose BOM**: Latest version

## 📊 Features in Detail

### Hotel Management
- ✅ Add new hotels with details
- ✅ Edit existing hotel information
- ✅ Delete hotels from system
- ✅ Toggle hotel active/inactive status
- ✅ Bulk operations (activate/deactivate/delete)
- ✅ Search and filter hotels

### Booking System
- ✅ Create new bookings
- ✅ View booking details
- ✅ Update booking status
- ✅ Cancel bookings
- ✅ Payment status tracking
- ✅ Booking history

### User Management
- ✅ User registration
- ✅ Profile updates
- ✅ Role-based access control
- ✅ User statistics
- ✅ Account management

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Aayuska Adhikari**
- GitHub: [@Aayuskaa1](https://github.com/Aayuskaa1)
- Portfolio: [Your Portfolio Link]

## 🙏 Acknowledgments

- Material Design 3 guidelines
- Jetpack Compose documentation
- Firebase documentation
- Android developer community

## 📞 Support

For support and questions:
- Create an issue in this repository
- Contact: [Your Email]
- Documentation: [Your Docs Link]

---

⭐ **Star this repository if you find it helpful!** 