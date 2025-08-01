package com.example.hotelbookingsystem.repository

import android.util.Log
import com.example.hotelbookingsystem.model.*
import com.example.hotelbookingsystem.data.MockData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class MockBookingRepository {
    private var bookings = MockData.mockBookings.toMutableList()
    
    // Get all bookings
    fun getAllBookings(): Flow<List<Booking>> = flow {
        try {
            emit(bookings)
        } catch (e: Exception) {
            Log.e("MockBookingRepository", "Error getting bookings: ${e.message}", e)
            emit(emptyList())
        }
    }
    
    // Get booking by ID
    suspend fun getBookingById(id: String): Booking? {
        return try {
            bookings.find { it.id == id }
        } catch (e: Exception) {
            Log.e("MockBookingRepository", "Error getting booking by ID: ${e.message}", e)
            null
        }
    }
    
    // Add new booking
    suspend fun addBooking(booking: Booking): Result<Booking> {
        println("DEBUG: MockBookingRepository.addBooking called with booking: $booking")
        return try {
            val bookingWithTimestamp = booking.copy(
                id = UUID.randomUUID().toString(),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            bookings.add(bookingWithTimestamp)
            
            println("DEBUG: Booking added to list successfully: ${bookingWithTimestamp.id}")
            Log.d("MockBookingRepository", "Booking added successfully: ${bookingWithTimestamp.id}")
            Result.success(bookingWithTimestamp)
        } catch (e: Exception) {
            println("DEBUG: Error adding booking: ${e.message}")
            Log.e("MockBookingRepository", "Error adding booking: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Update booking
    suspend fun updateBooking(booking: Booking): Result<Booking> {
        return try {
            val bookingWithTimestamp = booking.copy(
                updatedAt = System.currentTimeMillis()
            )
            
            val index = bookings.indexOfFirst { it.id == booking.id }
            if (index != -1) {
                bookings[index] = bookingWithTimestamp
            }
            
            Log.d("MockBookingRepository", "Booking updated successfully: ${bookingWithTimestamp.id}")
            Result.success(bookingWithTimestamp)
        } catch (e: Exception) {
            Log.e("MockBookingRepository", "Error updating booking: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Delete booking
    suspend fun deleteBooking(bookingId: String): Result<Unit> {
        return try {
            bookings.removeAll { it.id == bookingId }
            Log.d("MockBookingRepository", "Booking deleted successfully: $bookingId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MockBookingRepository", "Error deleting booking: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Update booking status
    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Unit> {
        return try {
            val index = bookings.indexOfFirst { it.id == bookingId }
            if (index != -1) {
                bookings[index] = bookings[index].copy(
                    bookingStatus = status,
                    updatedAt = System.currentTimeMillis()
                )
            }
            Log.d("MockBookingRepository", "Booking status updated: $bookingId to $status")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MockBookingRepository", "Error updating booking status: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Update payment status
    suspend fun updatePaymentStatus(bookingId: String, status: PaymentStatus): Result<Unit> {
        return try {
            val index = bookings.indexOfFirst { it.id == bookingId }
            if (index != -1) {
                bookings[index] = bookings[index].copy(
                    paymentStatus = status,
                    updatedAt = System.currentTimeMillis()
                )
            }
            Log.d("MockBookingRepository", "Payment status updated: $bookingId to $status")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MockBookingRepository", "Error updating payment status: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Search bookings
    fun searchBookings(query: String): Flow<List<Booking>> = flow {
        try {
            val filteredBookings = bookings.filter { booking ->
                booking.hotelName.contains(query, ignoreCase = true) ||
                booking.userName.contains(query, ignoreCase = true) ||
                booking.userEmail.contains(query, ignoreCase = true)
            }
            emit(filteredBookings)
        } catch (e: Exception) {
            Log.e("MockBookingRepository", "Error searching bookings: ${e.message}", e)
            emit(emptyList())
        }
    }
    
    // Get bookings by status
    fun getBookingsByStatus(status: BookingStatus): Flow<List<Booking>> = flow {
        try {
            val filteredBookings = bookings.filter { it.bookingStatus == status }
            emit(filteredBookings)
        } catch (e: Exception) {
            Log.e("MockBookingRepository", "Error getting bookings by status: ${e.message}", e)
            emit(emptyList())
        }
    }
    
    // Get bookings by user
    fun getBookingsByUser(userId: String): Flow<List<Booking>> = flow {
        try {
            val filteredBookings = bookings.filter { it.userId == userId }
            emit(filteredBookings)
        } catch (e: Exception) {
            Log.e("MockBookingRepository", "Error getting bookings by user: ${e.message}", e)
            emit(emptyList())
        }
    }
    
    // Get booking statistics
    fun getBookingStatistics(): Map<String, Any> {
        return mapOf(
            "totalBookings" to bookings.size,
            "confirmedBookings" to bookings.count { it.bookingStatus == BookingStatus.CONFIRMED },
            "pendingBookings" to bookings.count { it.bookingStatus == BookingStatus.PENDING },
            "completedBookings" to bookings.count { it.bookingStatus == BookingStatus.COMPLETED },
            "cancelledBookings" to bookings.count { it.bookingStatus == BookingStatus.CANCELLED },
            "totalRevenue" to bookings.sumOf { it.totalAmount },
            "averageBookingValue" to if (bookings.isNotEmpty()) bookings.sumOf { it.totalAmount } / bookings.size else 0.0
        )
    }
} 