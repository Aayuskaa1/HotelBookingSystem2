package com.example.hotelbookingsystem.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbookingsystem.model.Hotel
import com.example.hotelbookingsystem.repository.FirebaseHotelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HotelViewModel : ViewModel() {
    companion object {
        private val sharedRepository = FirebaseHotelRepository()
    }
    
    private val repository = sharedRepository
    
    // State flows
    private val _hotels = MutableStateFlow<List<Hotel>>(emptyList())
    val hotels: StateFlow<List<Hotel>> = _hotels.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()
    
    private val _selectedHotel = MutableStateFlow<Hotel?>(null)
    val selectedHotel: StateFlow<Hotel?> = _selectedHotel.asStateFlow()
    
    // Load all hotels
    fun loadHotels() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            println("HotelViewModel: Starting to load hotels...")
            
            try {
                repository.getAllHotels().collect { hotelList ->
                    println("HotelViewModel: Loaded ${hotelList.size} hotels")
                    hotelList.forEach { hotel ->
                        println("HotelViewModel: Hotel: ${hotel.name} (ID: ${hotel.id}) - Active: ${hotel.isActive}")
                    }
                    _hotels.value = hotelList
                    println("HotelViewModel: Updated _hotels state with ${_hotels.value.size} hotels")
                }
            } catch (e: Exception) {
                println("HotelViewModel: Failed to load hotels: ${e.message}")
                _errorMessage.value = "Failed to load hotels: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Add new hotel
    fun addHotel(hotel: Hotel) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            println("HotelViewModel: Adding hotel: ${hotel.name}")
            println("HotelViewModel: Current hotels before adding: ${_hotels.value.size}")
            
            repository.addHotel(hotel).fold(
                onSuccess = { newHotel ->
                    println("HotelViewModel: Hotel added successfully: ${newHotel.name} with ID: ${newHotel.id}")
                    println("HotelViewModel: Hotel isActive: ${newHotel.isActive}")
                    _successMessage.value = "Hotel '${newHotel.name}' added successfully!"
                    println("HotelViewModel: About to refresh hotels list...")
                    loadHotels() // Refresh the list
                },
                onFailure = { exception ->
                    println("HotelViewModel: Failed to add hotel: ${exception.message}")
                    _errorMessage.value = "Failed to add hotel: ${exception.message}"
                }
            )
            
            _isLoading.value = false
        }
    }
    
    // Update hotel
    fun updateHotel(hotel: Hotel) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            repository.updateHotel(hotel).fold(
                onSuccess = { updatedHotel ->
                    _successMessage.value = "Hotel '${updatedHotel.name}' updated successfully!"
                    loadHotels() // Refresh the list
                },
                onFailure = { exception ->
                    _errorMessage.value = "Failed to update hotel: ${exception.message}"
                }
            )
            
            _isLoading.value = false
        }
    }
    
    // Delete hotel
    fun deleteHotel(hotelId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            repository.deleteHotel(hotelId).fold(
                onSuccess = {
                    _successMessage.value = "Hotel deleted successfully!"
                    loadHotels() // Refresh the list
                },
                onFailure = { exception ->
                    _errorMessage.value = "Failed to delete hotel: ${exception.message}"
                }
            )
            
            _isLoading.value = false
        }
    }
    
    // Search hotels
    fun searchHotels(query: String) {
        if (query.isBlank()) {
            loadHotels()
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                repository.searchHotels(query).collect { searchResults ->
                    _hotels.value = searchResults
                }
            } catch (e: Exception) {
                _errorMessage.value = "Search failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Toggle hotel status
    fun toggleHotelStatus(hotelId: String, isActive: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            repository.toggleHotelStatus(hotelId, isActive).fold(
                onSuccess = {
                    val status = if (isActive) "activated" else "deactivated"
                    _successMessage.value = "Hotel $status successfully!"
                    loadHotels() // Refresh the list
                },
                onFailure = { exception ->
                    _errorMessage.value = "Failed to update hotel status: ${exception.message}"
                }
            )
            
            _isLoading.value = false
        }
    }
    
    // Select hotel for editing
    fun selectHotel(hotel: Hotel?) {
        _selectedHotel.value = hotel
    }
    
    // Clear messages
    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
    
    // Get hotel by ID
    fun getHotelById(id: String): Hotel? {
        return _hotels.value.find { it.id == id }
    }
    
    // Get active hotels count
    fun getActiveHotelsCount(): Int {
        return _hotels.value.count { it.isActive }
    }
    
    // Get total hotels count
    fun getTotalHotelsCount(): Int {
        return _hotels.value.size
    }
} 