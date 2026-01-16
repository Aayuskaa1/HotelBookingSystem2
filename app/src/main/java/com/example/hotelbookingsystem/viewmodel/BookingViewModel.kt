package com.example.hotelbookingsystem.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbookingsystem.model.Booking
import com.example.hotelbookingsystem.model.BookingStatus
import com.example.hotelbookingsystem.model.PaymentStatus
import com.example.hotelbookingsystem.repository.MockBookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookingViewModel : ViewModel() {
    private val repository = MockBookingRepository()
    
    // State flows
    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()
    
    private val _selectedBooking = MutableStateFlow<Booking?>(null)
    val selectedBooking: StateFlow<Booking?> = _selectedBooking.asStateFlow()
    
    private val _selectedStatusFilter = MutableStateFlow<BookingStatus?>(null)
    val selectedStatusFilter: StateFlow<BookingStatus?> = _selectedStatusFilter.asStateFlow()
    
    // Load all bookings
    fun loadBookings() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                repository.getAllBookings().collect { bookingList ->
                    _bookings.value = bookingList
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load bookings: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Load bookings for specific user
    fun loadUserBookings(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                repository.getBookingsByUser(userId).collect { bookingList ->
                    _bookings.value = bookingList
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load user bookings: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Add new booking
    fun addBooking(booking: Booking) {
        println("DEBUG: BookingViewModel.addBooking called with booking: $booking")
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            println("DEBUG: Calling repository.addBooking...")
            repository.addBooking(booking).fold(
                onSuccess = { newBooking ->
                    println("DEBUG: Booking added successfully: $newBooking")
                    _successMessage.value = "Booking for ${newBooking.hotelName} added successfully!"
                    loadBookings() // Refresh the list
                },
                onFailure = { exception ->
                    println("DEBUG: Booking failed with exception: ${exception.message}")
                    _errorMessage.value = "Failed to add booking: ${exception.message}"
                }
            )
            
            _isLoading.value = false
        }
    }
    
    // Update booking
    fun updateBooking(booking: Booking) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            repository.updateBooking(booking).fold(
                onSuccess = { updatedBooking ->
                    _successMessage.value = "Booking updated successfully!"
                    loadBookings() // Refresh the list
                },
                onFailure = { exception ->
                    _errorMessage.value = "Failed to update booking: ${exception.message}"
                }
            )
            
            _isLoading.value = false
        }
    }
    
    // Delete booking
    fun deleteBooking(bookingId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            repository.deleteBooking(bookingId).fold(
                onSuccess = {
                    _successMessage.value = "Booking deleted successfully!"
                    loadBookings() // Refresh the list
                },
                onFailure = { exception ->
                    _errorMessage.value = "Failed to delete booking: ${exception.message}"
                }
            )
            
            _isLoading.value = false
        }
    }
    
    // Update booking status
    fun updateBookingStatus(bookingId: String, status: BookingStatus) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            repository.updateBookingStatus(bookingId, status).fold(
                onSuccess = {
                    _successMessage.value = "Booking status updated to ${status.name}!"
                    loadBookings() // Refresh the list
                },
                onFailure = { exception ->
                    _errorMessage.value = "Failed to update booking status: ${exception.message}"
                }
            )
            
            _isLoading.value = false
        }
    }
    
    // Update payment status
    fun updatePaymentStatus(bookingId: String, status: PaymentStatus) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            repository.updatePaymentStatus(bookingId, status).fold(
                onSuccess = {
                    _successMessage.value = "Payment status updated to ${status.name}!"
                    loadBookings() // Refresh the list
                },
                onFailure = { exception ->
                    _errorMessage.value = "Failed to update payment status: ${exception.message}"
                }
            )
            
            _isLoading.value = false
        }
    }
    
    // Search bookings
    fun searchBookings(query: String) {
        if (query.isBlank()) {
            loadBookings()
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                repository.searchBookings(query).collect { searchResults ->
                    _bookings.value = searchResults
                }
            } catch (e: Exception) {
                _errorMessage.value = "Search failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Filter bookings by status
    fun filterBookingsByStatus(status: BookingStatus?) {
        _selectedStatusFilter.value = status
        
        if (status == null) {
            loadBookings()
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                repository.getBookingsByStatus(status).collect { filteredBookings ->
                    _bookings.value = filteredBookings
                }
            } catch (e: Exception) {
                _errorMessage.value = "Filter failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Select booking for editing
    fun selectBooking(booking: Booking?) {
        _selectedBooking.value = booking
    }
    
    // Clear messages
    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
    
    // Get booking by ID
    fun getBookingById(id: String): Booking? {
        return _bookings.value.find { it.id == id }
    }
    
    // Get statistics
    fun getBookingStatistics(): Map<String, Int> {
        val bookings = _bookings.value
        return mapOf(
            "total" to bookings.size,
            "pending" to bookings.count { it.bookingStatus == BookingStatus.PENDING },
            "confirmed" to bookings.count { it.bookingStatus == BookingStatus.CONFIRMED },
            "cancelled" to bookings.count { it.bookingStatus == BookingStatus.CANCELLED },
            "completed" to bookings.count { it.bookingStatus == BookingStatus.COMPLETED },
            "paid" to bookings.count { it.paymentStatus == PaymentStatus.PAID },
            "pending_payment" to bookings.count { it.paymentStatus == PaymentStatus.PENDING }
        )
    }
    
    // Get total revenue
    fun getTotalRevenue(): Double {
        return _bookings.value
            .filter { it.paymentStatus == PaymentStatus.PAID }
            .sumOf { it.totalAmount }
    }
    
    // Get upcoming bookings (next 30 days)
    fun getUpcomingBookings(): List<Booking> {
        val currentTime = System.currentTimeMillis()
        val thirtyDaysFromNow = currentTime + (30 * 24 * 60 * 60 * 1000)
        
        return _bookings.value.filter { booking ->
            booking.checkInDate >= currentTime && 
            booking.checkInDate <= thirtyDaysFromNow &&
            booking.bookingStatus == BookingStatus.CONFIRMED
        }
    }
} 