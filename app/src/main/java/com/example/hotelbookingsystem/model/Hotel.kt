package com.example.hotelbookingsystem.model

import java.util.UUID

data class Hotel(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String = "",
    val address: String = "",
    val city: String = "",
    val country: String = "",
    val phone: String = "",
    val email: String = "",
    val website: String = "",
    val rating: Float = 0.0f,
    val pricePerNight: Double = 0.0,
    val currency: String = "USD",
    val amenities: List<String> = emptyList(),
    val images: List<String> = emptyList(),
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "name" to name,
            "description" to description,
            "address" to address,
            "city" to city,
            "country" to country,
            "phone" to phone,
            "email" to email,
            "website" to website,
            "rating" to rating,
            "pricePerNight" to pricePerNight,
            "currency" to currency,
            "amenities" to amenities,
            "images" to images,
            "isActive" to isActive,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
    
    companion object {
        fun fromMap(map: Map<String, Any>): Hotel {
            return Hotel(
                id = map["id"] as? String ?: "",
                name = map["name"] as? String ?: "",
                description = map["description"] as? String ?: "",
                address = map["address"] as? String ?: "",
                city = map["city"] as? String ?: "",
                country = map["country"] as? String ?: "",
                phone = map["phone"] as? String ?: "",
                email = map["email"] as? String ?: "",
                website = map["website"] as? String ?: "",
                rating = (map["rating"] as? Number)?.toFloat() ?: 0.0f,
                pricePerNight = (map["pricePerNight"] as? Number)?.toDouble() ?: 0.0,
                currency = map["currency"] as? String ?: "USD",
                amenities = (map["amenities"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                images = (map["images"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                isActive = map["isActive"] as? Boolean ?: true,
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }
    }
} 