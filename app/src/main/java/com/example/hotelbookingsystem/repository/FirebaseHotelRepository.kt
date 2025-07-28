package com.example.hotelbookingsystem.repository

import android.util.Log
import com.example.hotelbookingsystem.model.Hotel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseHotelRepository {
    private val db = FirebaseFirestore.getInstance()
    private val hotelsCollection = db.collection("hotels")
    
    // Get all hotels
    fun getAllHotels(): Flow<List<Hotel>> = flow {
        try {
            Log.d("FirebaseHotelRepository", "Fetching all hotels from Firestore")
            
            val snapshot = hotelsCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val hotels = snapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Hotel::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    Log.e("FirebaseHotelRepository", "Error parsing hotel document: ${e.message}")
                    null
                }
            }
            
            Log.d("FirebaseHotelRepository", "Fetched ${hotels.size} hotels from Firestore")
            hotels.forEach { hotel ->
                Log.d("FirebaseHotelRepository", "Hotel: ${hotel.name} (ID: ${hotel.id}) - Active: ${hotel.isActive}")
            }
            
            emit(hotels)
        } catch (e: Exception) {
            Log.e("FirebaseHotelRepository", "Error fetching hotels: ${e.message}", e)
            emit(emptyList())
        }
    }
    
    // Get hotel by ID
    suspend fun getHotelById(id: String): Hotel? {
        return try {
            Log.d("FirebaseHotelRepository", "Fetching hotel by ID: $id")
            
            val document = hotelsCollection.document(id).get().await()
            if (document.exists()) {
                document.toObject(Hotel::class.java)?.copy(id = document.id)
            } else {
                Log.w("FirebaseHotelRepository", "Hotel with ID $id not found")
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseHotelRepository", "Error fetching hotel by ID: ${e.message}", e)
            null
        }
    }
    
    // Add new hotel
    suspend fun addHotel(hotel: Hotel): Result<Hotel> {
        return try {
            Log.d("FirebaseHotelRepository", "Adding hotel to Firestore: ${hotel.name}")
            
            val hotelWithId = hotel.copy(
                id = UUID.randomUUID().toString(),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            val documentRef = hotelsCollection.document(hotelWithId.id)
            documentRef.set(hotelWithId).await()
            
            Log.d("FirebaseHotelRepository", "Hotel added successfully: ${hotelWithId.name} with ID: ${hotelWithId.id}")
            Result.success(hotelWithId)
        } catch (e: Exception) {
            Log.e("FirebaseHotelRepository", "Error adding hotel: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Update hotel
    suspend fun updateHotel(hotel: Hotel): Result<Hotel> {
        return try {
            Log.d("FirebaseHotelRepository", "Updating hotel in Firestore: ${hotel.name}")
            
            val hotelWithTimestamp = hotel.copy(
                updatedAt = System.currentTimeMillis()
            )
            
            hotelsCollection.document(hotel.id).set(hotelWithTimestamp).await()
            
            Log.d("FirebaseHotelRepository", "Hotel updated successfully: ${hotelWithTimestamp.name}")
            Result.success(hotelWithTimestamp)
        } catch (e: Exception) {
            Log.e("FirebaseHotelRepository", "Error updating hotel: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Delete hotel
    suspend fun deleteHotel(hotelId: String): Result<Unit> {
        return try {
            Log.d("FirebaseHotelRepository", "Deleting hotel from Firestore: $hotelId")
            
            hotelsCollection.document(hotelId).delete().await()
            
            Log.d("FirebaseHotelRepository", "Hotel deleted successfully: $hotelId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseHotelRepository", "Error deleting hotel: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Toggle hotel status
    suspend fun toggleHotelStatus(hotelId: String, isActive: Boolean): Result<Unit> {
        return try {
            Log.d("FirebaseHotelRepository", "Toggling hotel status: $hotelId to $isActive")
            
            hotelsCollection.document(hotelId).update(
                mapOf(
                    "isActive" to isActive,
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()
            
            Log.d("FirebaseHotelRepository", "Hotel status updated successfully: $hotelId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseHotelRepository", "Error updating hotel status: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Search hotels
    fun searchHotels(query: String): Flow<List<Hotel>> = flow {
        try {
            Log.d("FirebaseHotelRepository", "Searching hotels with query: $query")
            
            val snapshot = hotelsCollection
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + '\uf8ff')
                .get()
                .await()
            
            val hotels = snapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Hotel::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    Log.e("FirebaseHotelRepository", "Error parsing hotel document: ${e.message}")
                    null
                }
            }
            
            Log.d("FirebaseHotelRepository", "Search returned ${hotels.size} hotels")
            emit(hotels)
        } catch (e: Exception) {
            Log.e("FirebaseHotelRepository", "Error searching hotels: ${e.message}", e)
            emit(emptyList())
        }
    }
    
    // Get hotels by city
    suspend fun getHotelsByCity(city: String): List<Hotel> {
        return try {
            Log.d("FirebaseHotelRepository", "Fetching hotels by city: $city")
            
            val snapshot = hotelsCollection
                .whereEqualTo("city", city)
                .whereEqualTo("isActive", true)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Hotel::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    Log.e("FirebaseHotelRepository", "Error parsing hotel document: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseHotelRepository", "Error fetching hotels by city: ${e.message}", e)
            emptyList()
        }
    }
    
    // Get active hotels
    suspend fun getActiveHotels(): List<Hotel> {
        return try {
            Log.d("FirebaseHotelRepository", "Fetching active hotels")
            
            val snapshot = hotelsCollection
                .whereEqualTo("isActive", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Hotel::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    Log.e("FirebaseHotelRepository", "Error parsing hotel document: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseHotelRepository", "Error fetching active hotels: ${e.message}", e)
            emptyList()
        }
    }
    
    // Get hotel statistics
    suspend fun getHotelStatistics(): Map<String, Any> {
        return try {
            Log.d("FirebaseHotelRepository", "Fetching hotel statistics")
            
            val snapshot = hotelsCollection.get().await()
            val hotels = snapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Hotel::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    Log.e("FirebaseHotelRepository", "Error parsing hotel document: ${e.message}")
                    null
                }
            }
            
            val totalHotels = hotels.size
            val activeHotels = hotels.count { it.isActive }
            val inactiveHotels = totalHotels - activeHotels
            val averageRating = if (hotels.isNotEmpty()) {
                hotels.map { it.rating }.average()
            } else 0.0
            val topRatedHotel = hotels.maxByOrNull { it.rating }
            
            mapOf(
                "totalHotels" to totalHotels,
                "activeHotels" to activeHotels,
                "inactiveHotels" to inactiveHotels,
                "averageRating" to averageRating,
                "topRatedHotel" to (topRatedHotel?.name ?: "None")
            )
        } catch (e: Exception) {
            Log.e("FirebaseHotelRepository", "Error fetching hotel statistics: ${e.message}", e)
            mapOf(
                "totalHotels" to 0,
                "activeHotels" to 0,
                "inactiveHotels" to 0,
                "averageRating" to 0.0,
                "topRatedHotel" to "None"
            )
        }
    }
} 