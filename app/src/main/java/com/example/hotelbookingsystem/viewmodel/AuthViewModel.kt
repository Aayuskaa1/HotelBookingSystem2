package com.example.hotelbookingsystem.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.example.hotelbookingsystem.utils.UserRoleDetector
import com.example.hotelbookingsystem.model.MockFirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class AuthViewModel : ViewModel() {
    // Test admin credentials
    private val adminEmail = "admin@hotelbooking.com"
    private val adminPassword = "admin123456"
    
    // Firebase user credentials
    private val firebaseEmail = "aayuska@gmail.com"
    private val firebasePassword = "Aayuska@123"
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()
    
    init {
        // Don't automatically authenticate on app start
        // Let the user go through the proper flow: Splash -> Login -> Dashboard
        _authState.value = AuthState.Initial
    }
    

    fun login(email: String, password: String) {
        Log.d("AuthViewModel", "Login attempt - Email: '$email', Password: '$password'")
        Log.d("AuthViewModel", "Expected admin email: '$adminEmail', Expected user email: '$firebaseEmail'")
        
        if (email.isEmpty() || password.isEmpty()) {
            _errorMessage.value = "Please fill in all fields"
            return
        }
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                // Simulate network delay
                delay(1000)
                
                // Check admin credentials (case-insensitive email)
                if (email.equals(adminEmail, ignoreCase = true) && password == adminPassword) {
                    Log.d("AuthViewModel", "Admin logged in successfully")
                    val mockUser = MockFirebaseUser(email, "Admin User")
                    _authState.value = AuthState.Authenticated(mockUser)
                }
                // Check Firebase user credentials (case-insensitive email)
                else if (email.equals(firebaseEmail, ignoreCase = true) && password == firebasePassword) {
                    Log.d("AuthViewModel", "Firebase user logged in successfully")
                    val mockUser = MockFirebaseUser(email, "Aayuska")
                    _authState.value = AuthState.Authenticated(mockUser)
                }
                else {
                    _errorMessage.value = "Invalid email or password. Use:\n• Admin: admin@hotelbooking.com / admin123456\n• User: Aayuska@gmail.com / Aayuska@123"
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login error: ${e.message}", e)
                _errorMessage.value = "Login failed: ${e.message ?: "Unknown error"}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun signUp(name: String, email: String, password: String, confirmPassword: String) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _errorMessage.value = "Please fill in all fields"
            return
        }
        
        if (password != confirmPassword) {
            _errorMessage.value = "Passwords do not match"
            return
        }
        
        if (password.length < 6) {
            _errorMessage.value = "Password must be at least 6 characters"
            return
        }
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                // Simulate network delay
                delay(1000)
                
                // For demo purposes, create a mock user
                Log.d("AuthViewModel", "User signed up: $email")
                val mockUser = MockFirebaseUser(email, name)
                _authState.value = AuthState.Authenticated(mockUser)
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Signup error: ${e.message}", e)
                _errorMessage.value = "Sign up failed: ${e.message ?: "Unknown error"}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun logout() {
        _authState.value = AuthState.Unauthenticated
    }
    
    fun updateProfile(updatedUser: MockFirebaseUser) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                // Simulate network delay
                delay(500)
                
                // Update the authenticated user
                _authState.value = AuthState.Authenticated(updatedUser)
                _successMessage.value = "Profile updated successfully!"
                
                Log.d("AuthViewModel", "Profile updated: ${updatedUser.displayName}")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Profile update error: ${e.message}", e)
                _errorMessage.value = "Failed to update profile: ${e.message ?: "Unknown error"}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    fun clearSuccess() {
        _successMessage.value = null
    }
}

sealed class AuthState {
    object Initial : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: MockFirebaseUser) : AuthState()
} 