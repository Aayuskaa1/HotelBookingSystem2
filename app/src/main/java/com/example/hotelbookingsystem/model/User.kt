package com.example.hotelbookingsystem.model

import java.util.UUID
import com.example.hotelbookingsystem.utils.UserRole

data class User(
    val id: String = UUID.randomUUID().toString(),
    val email: String = "",
    val displayName: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val dateOfBirth: Long = 0L,
    val address: String = "",
    val city: String = "",
    val country: String = "",
    val userRole: UserRole = UserRole.USER,
    val isActive: Boolean = true,
    val isEmailVerified: Boolean = false,
    val profileImageUrl: String = "",
    val totalBookings: Int = 0,
    val totalSpent: Double = 0.0,
    val currency: String = "USD",
    val preferences: Map<String, Any> = emptyMap(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = 0L
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "email" to email,
            "displayName" to displayName,
            "firstName" to firstName,
            "lastName" to lastName,
            "phoneNumber" to phoneNumber,
            "dateOfBirth" to dateOfBirth,
            "address" to address,
            "city" to city,
            "country" to country,
            "userRole" to userRole.name,
            "isActive" to isActive,
            "isEmailVerified" to isEmailVerified,
            "profileImageUrl" to profileImageUrl,
            "totalBookings" to totalBookings,
            "totalSpent" to totalSpent,
            "currency" to currency,
            "preferences" to preferences,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "lastLoginAt" to lastLoginAt
        )
    }
    
    companion object {
        fun fromMap(map: Map<String, Any>): User {
            return User(
                id = map["id"] as? String ?: "",
                email = map["email"] as? String ?: "",
                displayName = map["displayName"] as? String ?: "",
                firstName = map["firstName"] as? String ?: "",
                lastName = map["lastName"] as? String ?: "",
                phoneNumber = map["phoneNumber"] as? String ?: "",
                dateOfBirth = (map["dateOfBirth"] as? Number)?.toLong() ?: 0L,
                address = map["address"] as? String ?: "",
                city = map["city"] as? String ?: "",
                country = map["country"] as? String ?: "",
                userRole = UserRole.valueOf(map["userRole"] as? String ?: UserRole.USER.name),
                isActive = map["isActive"] as? Boolean ?: true,
                isEmailVerified = map["isEmailVerified"] as? Boolean ?: false,
                profileImageUrl = map["profileImageUrl"] as? String ?: "",
                totalBookings = (map["totalBookings"] as? Number)?.toInt() ?: 0,
                totalSpent = (map["totalSpent"] as? Number)?.toDouble() ?: 0.0,
                currency = map["currency"] as? String ?: "USD",
                preferences = (map["preferences"] as? Map<*, *>)?.mapKeys { it.key.toString() }?.mapValues { it.value as Any } ?: emptyMap(),
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                lastLoginAt = (map["lastLoginAt"] as? Number)?.toLong() ?: 0L
            )
        }
    }
} 