package com.example.hotelbookingsystem.model

// Mock Firebase User for offline testing
data class MockFirebaseUser(
    val email: String,
    val displayName: String? = null,
    val uid: String = "mock_user_${System.currentTimeMillis()}"
) {
    fun isEmailVerified(): Boolean = true
} 