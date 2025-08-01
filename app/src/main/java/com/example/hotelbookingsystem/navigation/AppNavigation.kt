package com.example.hotelbookingsystem.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.hotelbookingsystem.ui.screens.LoginScreen
import com.example.hotelbookingsystem.ui.screens.SignUpScreen
import com.example.hotelbookingsystem.ui.screens.SplashScreen
import com.example.hotelbookingsystem.ui.screens.HomeScreen
import com.example.hotelbookingsystem.ui.screens.AdminDashboard
import com.example.hotelbookingsystem.ui.screens.UserDashboard
import com.example.hotelbookingsystem.ui.screens.HotelManagementScreen
import com.example.hotelbookingsystem.ui.screens.AddEditHotelScreen
import com.example.hotelbookingsystem.ui.screens.BookingManagementScreen
import com.example.hotelbookingsystem.ui.screens.UserManagementScreen
import com.example.hotelbookingsystem.ui.screens.UserHotelSearchScreen
import com.example.hotelbookingsystem.ui.screens.UserBookingScreen
import com.example.hotelbookingsystem.ui.screens.UserMyBookingsScreen
import com.example.hotelbookingsystem.ui.screens.UserProfileScreen
import com.example.hotelbookingsystem.ui.screens.HelpSupportScreen
import com.example.hotelbookingsystem.ui.screens.MainScreen
// import com.example.hotelbookingsystem.ui.screens.ReportManagementScreen
// import com.example.hotelbookingsystem.ui.screens.ReportViewScreen
import com.example.hotelbookingsystem.utils.UserRoleDetector
import com.example.hotelbookingsystem.model.Hotel
import com.example.hotelbookingsystem.model.Booking
import com.example.hotelbookingsystem.model.User
// import com.example.hotelbookingsystem.model.SystemReport
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object AdminDashboard : Screen("admin_dashboard")
    object UserDashboard : Screen("user_dashboard")
    object HotelManagement : Screen("hotel_management")
    object AddEditHotel : Screen("add_edit_hotel")
    object BookingManagement : Screen("booking_management")
    object UserManagement : Screen("user_management")
    object UserHotelSearch : Screen("user_hotel_search")
    object UserBooking : Screen("user_booking")
    object UserMyBookings : Screen("user_my_bookings")
    object UserProfile : Screen("user_profile")
    object HelpSupport : Screen("help_support")
    object Main : Screen("main")
            // Report screens removed
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    onLoginClick: (String, String) -> Unit,
    onSignUpClick: (String, String, String, String) -> Unit,
    onLogout: () -> Unit,
    onProfileUpdate: (com.example.hotelbookingsystem.model.MockFirebaseUser) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    currentUser: com.example.hotelbookingsystem.model.MockFirebaseUser? = null
) {
    // Shared ViewModels
    val sharedHotelViewModel: com.example.hotelbookingsystem.viewmodel.HotelViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val sharedBookingViewModel: com.example.hotelbookingsystem.viewmodel.BookingViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val sharedUserViewModel: com.example.hotelbookingsystem.viewmodel.UserViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    var selectedHotel by remember { mutableStateOf<Hotel?>(null) }
    var selectedBooking by remember { mutableStateOf<Booking?>(null) }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var selectedHotelForBooking by remember { mutableStateOf<Hotel?>(null) }
    // Report selection removed
    val startDestination = Screen.Splash.route
    
    // Navigate to appropriate dashboard when user is authenticated
    LaunchedEffect(currentUser) {
        if (currentUser != null && navController.currentDestination?.route == Screen.Login.route) {
            val route = if (UserRoleDetector.isAdmin(currentUser)) {
                Screen.AdminDashboard.route
            } else {
                Screen.UserDashboard.route
            }
            navController.navigate(route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
                popUpTo(Screen.Login.route) { inclusive = true }
                popUpTo(Screen.SignUp.route) { inclusive = true }
            }
        }
    }
    
    // Debug: Log current destination
    LaunchedEffect(navController.currentDestination?.route) {
        println("Current route: ${navController.currentDestination?.route}")
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = onLoginClick,
                onSignUpClick = {
                    navController.navigate(Screen.SignUp.route)
                },
                isLoading = isLoading,
                errorMessage = errorMessage
            )
        }
        
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpClick = onSignUpClick,
                isLoading = isLoading,
                errorMessage = errorMessage
            )
        }
        
        composable(Screen.Home.route) {
            currentUser?.let { user ->
                HomeScreen(
                    user = user,
                    onLogout = onLogout
                )
            } ?: run {
                // If no user, navigate back to login
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            }
        }
        
        composable(Screen.AdminDashboard.route) {
            currentUser?.let { user ->
                AdminDashboard(
                    user = user,
                    onLogout = onLogout,
                    onManageHotels = {
                        navController.navigate(Screen.HotelManagement.route)
                    },
                    onManageBookings = {
                        navController.navigate(Screen.BookingManagement.route)
                    },
                    onManageUsers = {
                        navController.navigate(Screen.UserManagement.route)
                    },
                    hotelViewModel = sharedHotelViewModel,
                    bookingViewModel = sharedBookingViewModel,
                    userViewModel = sharedUserViewModel
                )
            } ?: run {
                // If no user, navigate back to login
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.AdminDashboard.route) { inclusive = true }
                    }
                }
            }
        }
        
        composable(Screen.UserDashboard.route) {
            currentUser?.let { user ->
                UserDashboard(
                    user = user,
                    onLogout = onLogout,
                    onSearchHotels = {
                        navController.navigate(Screen.UserHotelSearch.route)
                    },
                    onMyBookings = {
                        navController.navigate(Screen.UserMyBookings.route)
                    },
                    onBookHotel = {
                        navController.navigate(Screen.UserHotelSearch.route)
                    },
                    onProfile = {
                        navController.navigate(Screen.UserProfile.route)
                    },
                    onHelp = {
                        navController.navigate(Screen.HelpSupport.route)
                    },
                    onMainScreen = {
                        navController.navigate(Screen.Main.route)
                    },
                    bookingViewModel = sharedBookingViewModel
                )
            } ?: run {
                // If no user, navigate back to login
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.UserDashboard.route) { inclusive = true }
                    }
                }
            }
        }
        
        composable(Screen.HotelManagement.route) {
            HotelManagementScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onAddHotelClick = {
                    selectedHotel = null
                    navController.navigate(Screen.AddEditHotel.route)
                },
                onEditHotelClick = { hotel ->
                    selectedHotel = hotel
                    navController.navigate(Screen.AddEditHotel.route)
                },
                hotelViewModel = sharedHotelViewModel
            )
        }
        
        composable(Screen.AddEditHotel.route) {
            AddEditHotelScreen(
                hotel = selectedHotel,
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveSuccess = {
                    // Refresh the hotel list when returning
                    navController.popBackStack()
                },
                hotelViewModel = sharedHotelViewModel
            )
        }
        
        composable(Screen.BookingManagement.route) {
            BookingManagementScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onEditBookingClick = { booking ->
                    selectedBooking = booking
                    // For now, just show a message. You can add an edit booking screen later
                    // navController.navigate(Screen.EditBooking.route)
                },
                bookingViewModel = sharedBookingViewModel
            )
        }
        
        composable(Screen.UserManagement.route) {
            UserManagementScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onEditUserClick = { user ->
                    selectedUser = user
                    // For now, just show a message. You can add an edit user screen later
                    // navController.navigate(Screen.EditUser.route)
                },
                userViewModel = sharedUserViewModel,
                bookingViewModel = sharedBookingViewModel
            )
        }
        
        // User Hotel Search Screen
        composable(Screen.UserHotelSearch.route) {
            UserHotelSearchScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onHotelClick = { hotel ->
                    selectedHotelForBooking = hotel
                    navController.navigate(Screen.UserBooking.route)
                },
                hotelViewModel = sharedHotelViewModel
            )
        }
        
        // User Booking Screen
        composable(Screen.UserBooking.route) {
            selectedHotelForBooking?.let { hotel ->
                UserBookingScreen(
                    hotel = hotel,
                    currentUser = currentUser,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onBookingSuccess = {
                        navController.popBackStack()
                        navController.popBackStack()
                    },
                    bookingViewModel = sharedBookingViewModel
                )
            }
        }
        
        // User My Bookings Screen
        composable(Screen.UserMyBookings.route) {
            UserMyBookingsScreen(
                currentUser = currentUser,
                onBackClick = {
                    navController.popBackStack()
                },
                onBookingClick = { booking ->
                    // TODO: Add booking details screen
                },
                bookingViewModel = sharedBookingViewModel
            )
        }
        
        // User Profile Screen
        composable(Screen.UserProfile.route) {
            currentUser?.let { user ->
                UserProfileScreen(
                    user = user,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onProfileUpdated = { updatedUser ->
                        onProfileUpdate(updatedUser)
                        navController.popBackStack()
                    }
                )
            }
        }
        
        // Help & Support Screen
        composable(Screen.HelpSupport.route) {
            HelpSupportScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        // Main Screen with Bottom Navigation
        composable(Screen.Main.route) {
            MainScreen(
                currentUser = currentUser,
                onLogout = onLogout,
                onNavigateToBookings = {
                    navController.navigate(Screen.UserMyBookings.route)
                },
                onNavigateToPayments = {
                    // TODO: Add payments screen
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.UserProfile.route)
                }
            )
        }
        // Report management and view screens removed
    }
} 