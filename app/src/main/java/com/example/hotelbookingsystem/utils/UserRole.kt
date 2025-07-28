package com.example.hotelbookingsystem.utils

import com.example.hotelbookingsystem.model.MockFirebaseUser

enum class UserRole {
    ADMIN,
    USER
}

object UserRoleDetector {
    // Add specific admin email addresses here
    private val adminEmails = setOf(
        "admin@hotelbooking.com",
        "administrator@hotelbooking.com",
        "manager@hotelbooking.com",
        "admin@gmail.com",
        "administrator@gmail.com"
        // Add your specific admin email addresses here
    )
    
    fun getUserRole(user: MockFirebaseUser): UserRole {
        val email = user.email.lowercase()
        
        // Check specific admin email addresses first
        if (adminEmails.contains(email)) {
            return UserRole.ADMIN
        }
        
        // Check email patterns for admin users
        return when {
            email.contains("admin") -> UserRole.ADMIN
            email.contains("administrator") -> UserRole.ADMIN
            email.contains("manager") -> UserRole.ADMIN
            email.contains("superuser") -> UserRole.ADMIN
            email.startsWith("admin.") -> UserRole.ADMIN
            email.endsWith("@admin.com") -> UserRole.ADMIN
            else -> UserRole.USER
        }
    }
    
    fun isAdmin(user: MockFirebaseUser): Boolean {
        return getUserRole(user) == UserRole.ADMIN
    }
    
    // Debug function to help identify admin users
    fun debugUserRole(user: MockFirebaseUser): String {
        val email = user.email
        val role = getUserRole(user)
        return "Email: $email, Role: $role, isAdmin: ${isAdmin(user)}"
    }
} 