package com.example.hotelbookingsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.hotelbookingsystem.navigation.AppNavigation
import com.example.hotelbookingsystem.ui.theme.HotelBookingSystemTheme
import com.example.hotelbookingsystem.viewmodel.AuthViewModel
import com.example.hotelbookingsystem.viewmodel.AuthState
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        enableEdgeToEdge()
        setContent {
            HotelBookingSystemTheme {
                HotelBookingApp()
            }
        }
    }
}

@Composable
fun HotelBookingApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    
    val authState by authViewModel.authState.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val successMessage by authViewModel.successMessage.collectAsState()
    
    AppNavigation(
        navController = navController,
        onLoginClick = { email, password ->
            authViewModel.login(email, password)
        },
        onSignUpClick = { name, email, password, confirmPassword ->
            authViewModel.signUp(name, email, password, confirmPassword)
        },
        onLogout = { authViewModel.logout() },
        onProfileUpdate = { updatedUser ->
            authViewModel.updateProfile(updatedUser)
        },
        isLoading = isLoading,
        errorMessage = errorMessage,
        currentUser = if (authState is AuthState.Authenticated) (authState as AuthState.Authenticated).user else null
    )
}

@Preview(showBackground = true)
@Composable
fun HotelBookingAppPreview() {
    HotelBookingSystemTheme {
        HotelBookingApp()
    }
}