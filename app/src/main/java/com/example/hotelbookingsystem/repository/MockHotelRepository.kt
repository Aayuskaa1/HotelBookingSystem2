package com.example.hotelbookingsystem.repository

import android.util.Log
import com.example.hotelbookingsystem.model.Hotel
import com.example.hotelbookingsystem.data.MockData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class MockHotelRepository {
    private var hotels = MockData.mockHotels.toMutableList()
    
    // Get all hotels
    fun getAllHotels(): Flow<List<Hotel>> = flow {
        try {
            println("MockHotelRepository: Emitting ${hotels.size} hotels")
            hotels.forEach { hotel ->
                println("MockHotelRepository: Hotel: ${hotel.name} (ID: ${hotel.id})")
            }
            emit(hotels)
        } catch (e: Exception) {
            Log.e("MockHotelRepository", "Error getting hotels: ${e.message}", e)
            emit(emptyList())
        }
    }
    
    // Get hotel by ID
    suspend fun getHotelById(id: String): Hotel? {
        return try {
            hotels.find { it.id == id }
        } catch (e: Exception) {
            Log.e("MockHotelRepository", "Error getting hotel by ID: ${e.message}", e)
            null
        }
    }
    
    // Add new hotel
    suspend fun addHotel(hotel: Hotel): Result<Hotel> {
        return try {
            val hotelWithTimestamp = hotel.copy(
                id = UUID.randomUUID().toString(),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            hotels.add(hotelWithTimestamp)
            
            println("MockHotelRepository: Hotel added successfully: ${hotelWithTimestamp.name} with ID: ${hotelWithTimestamp.id}")
            println("MockHotelRepository: Total hotels now: ${hotels.size}")
            Log.d("MockHotelRepository", "Hotel added successfully: ${hotelWithTimestamp.name}")
            Result.success(hotelWithTimestamp)
        } catch (e: Exception) {
            println("MockHotelRepository: Error adding hotel: ${e.message}")
            Log.e("MockHotelRepository", "Error adding hotel: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Update hotel
    suspend fun updateHotel(hotel: Hotel): Result<Hotel> {
        return try {
            val hotelWithTimestamp = hotel.copy(
                updatedAt = System.currentTimeMillis()
            )
            
            val index = hotels.indexOfFirst { it.id == hotel.id }
            if (index != -1) {
                hotels[index] = hotelWithTimestamp
            }
            
            Log.d("MockHotelRepository", "Hotel updated successfully: ${hotelWithTimestamp.name}")
            Result.success(hotelWithTimestamp)
        } catch (e: Exception) {
            Log.e("MockHotelRepository", "Error updating hotel: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Delete hotel
    suspend fun deleteHotel(hotelId: String): Result<Unit> {
        return try {
            hotels.removeAll { it.id == hotelId }
            Log.d("MockHotelRepository", "Hotel deleted successfully: $hotelId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MockHotelRepository", "Error deleting hotel: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Toggle hotel status
    suspend fun toggleHotelStatus(hotelId: String, isActive: Boolean): Result<Unit> {
        return try {
            val index = hotels.indexOfFirst { it.id == hotelId }
            if (index != -1) {
                hotels[index] = hotels[index].copy(
                    isActive = isActive,
                    updatedAt = System.currentTimeMillis()
                )
            }
            Log.d("MockHotelRepository", "Hotel status toggled: $hotelId to $isActive")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MockHotelRepository", "Error toggling hotel status: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Search hotels
    fun searchHotels(query: String): Flow<List<Hotel>> = flow {
        try {
            val filteredHotels = hotels.filter { hotel ->
                hotel.name.contains(query, ignoreCase = true) ||
                hotel.city.contains(query, ignoreCase = true) ||
                hotel.country.contains(query, ignoreCase = true) ||
                hotel.description.contains(query, ignoreCase = true)
            }
            emit(filteredHotels)
        } catch (e: Exception) {
            Log.e("MockHotelRepository", "Error searching hotels: ${e.message}", e)
            emit(emptyList())
        }
    }
    
    // Get hotels by city
    fun getHotelsByCity(city: String): Flow<List<Hotel>> = flow {
        try {
            val filteredHotels = hotels.filter { it.city.equals(city, ignoreCase = true) }
            emit(filteredHotels)
        } catch (e: Exception) {
            Log.e("MockHotelRepository", "Error getting hotels by city: ${e.message}", e)
            emit(emptyList())
        }
    }
    
    // Get active hotels
    fun getActiveHotels(): Flow<List<Hotel>> = flow {
        try {
            val activeHotels = hotels.filter { it.isActive }
            emit(activeHotels)
        } catch (e: Exception) {
            Log.e("MockHotelRepository", "Error getting active hotels: ${e.message}", e)
            emit(emptyList())
        }
    }
    
    // Get hotel statistics
    fun getHotelStatistics(): Map<String, Any> {
        val totalHotels = hotels.size
        val activeHotels = hotels.count { it.isActive }
        val averageRating = hotels.mapNotNull { it.rating?.toDouble() }.average()
        val totalRevenue = hotels.sumOf { it.pricePerNight }
        val topRatedHotel = hotels.maxByOrNull { it.rating ?: 0.0f }?.name ?: ""
        
        return mapOf(
            "totalHotels" to totalHotels,
            "activeHotels" to activeHotels,
            "averageRating" to averageRating,
            "totalRevenue" to totalRevenue,
            "topRatedHotel" to topRatedHotel
        )
    }
} 