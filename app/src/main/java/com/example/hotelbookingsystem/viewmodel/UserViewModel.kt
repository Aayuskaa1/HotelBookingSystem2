package com.example.hotelbookingsystem.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbookingsystem.model.User
import com.example.hotelbookingsystem.model.UserRole
import com.example.hotelbookingsystem.repository.MockUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val repository = MockUserRepository()
    
    // State flows
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()
    
    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser: StateFlow<User?> = _selectedUser.asStateFlow()
    
    private val _selectedRoleFilter = MutableStateFlow<UserRole?>(null)
    val selectedRoleFilter: StateFlow<UserRole?> = _selectedRoleFilter.asStateFlow()
    
    // Load all users
    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                repository.getAllUsers().collect { userList ->
                    _users.value = userList
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load users: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Add new user
    fun addUser(user: User) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            repository.addUser(user).fold(
                onSuccess = { newUser ->
                    _successMessage.value = "User '${newUser.email}' added successfully!"
                    loadUsers() // Refresh the list
                },
                onFailure = { exception ->
                    _errorMessage.value = "Failed to add user: ${exception.message}"
                }
            )
            
            _isLoading.value = false
        }
    }
    
    // Update user
    fun updateUser(user: User) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            repository.updateUser(user).fold(
                onSuccess = { updatedUser ->
                    _successMessage.value = "User '${updatedUser.email}' updated successfully!"
                    loadUsers() // Refresh the list
                },
                onFailure = { exception ->
                    _errorMessage.value = "Failed to update user: ${exception.message}"
                }
            )
            
            _isLoading.value = false
        }
    }
    
    // Delete user
    fun deleteUser(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            repository.deleteUser(userId).fold(
                onSuccess = {
                    _successMessage.value = "User deleted successfully!"
                    loadUsers() // Refresh the list
                },
                onFailure = { exception ->
                    _errorMessage.value = "Failed to delete user: ${exception.message}"
                }
            )
            
            _isLoading.value = false
        }
    }
    
    // Update user role
    fun updateUserRole(userId: String, role: UserRole) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            repository.updateUserRole(userId, role).fold(
                onSuccess = {
                    _successMessage.value = "User role updated to ${role.name}!"
                    loadUsers() // Refresh the list
                },
                onFailure = { exception ->
                    _errorMessage.value = "Failed to update user role: ${exception.message}"
                }
            )
            
            _isLoading.value = false
        }
    }
    
    // Toggle user status
    fun toggleUserStatus(userId: String, isActive: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            repository.toggleUserStatus(userId).fold(
                onSuccess = {
                    val status = if (isActive) "activated" else "deactivated"
                    _successMessage.value = "User $status successfully!"
                    loadUsers() // Refresh the list
                },
                onFailure = { exception ->
                    _errorMessage.value = "Failed to update user status: ${exception.message}"
                }
            )
            
            _isLoading.value = false
        }
    }
    
    // Update email verification status
    fun updateEmailVerificationStatus(userId: String, isVerified: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            repository.updateEmailVerificationStatus(userId, isVerified).fold(
                onSuccess = {
                    val status = if (isVerified) "verified" else "unverified"
                    _successMessage.value = "Email $status successfully!"
                    loadUsers() // Refresh the list
                },
                onFailure = { exception ->
                    _errorMessage.value = "Failed to update email verification: ${exception.message}"
                }
            )
            
            _isLoading.value = false
        }
    }
    
    // Search users
    fun searchUsers(query: String) {
        if (query.isBlank()) {
            loadUsers()
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                repository.searchUsers(query).collect { searchResults ->
                    _users.value = searchResults
                }
            } catch (e: Exception) {
                _errorMessage.value = "Search failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Filter users by role
    fun filterUsersByRole(role: UserRole?) {
        _selectedRoleFilter.value = role
        
        if (role == null) {
            loadUsers()
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                repository.getUsersByRole(role).collect { filteredUsers ->
                    _users.value = filteredUsers
                }
            } catch (e: Exception) {
                _errorMessage.value = "Filter failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Get active users only
    fun getActiveUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                repository.getActiveUsers().collect { activeUsers ->
                    _users.value = activeUsers
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load active users: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Select user for editing
    fun selectUser(user: User?) {
        _selectedUser.value = user
    }
    
    // Clear messages
    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
    
    // Get user by ID
    fun getUserById(id: String): User? {
        return _users.value.find { it.id == id }
    }
    
    // Get statistics
    fun getUserStatistics(): Map<String, Int> {
        val users = _users.value
        return mapOf(
            "total" to users.size,
            "active" to users.count { it.isActive },
            "inactive" to users.count { !it.isActive },
            "verified" to users.count { it.isEmailVerified },
            "unverified" to users.count { !it.isEmailVerified },
            "admins" to users.count { it.userRole == UserRole.ADMIN },
            "moderators" to users.count { it.userRole == UserRole.MODERATOR },
            "premium" to users.count { it.userRole == UserRole.PREMIUM_USER },
            "regular" to users.count { it.userRole == UserRole.USER }
        )
    }
    
    // Get total revenue from all users
    fun getTotalUserRevenue(): Double {
        return _users.value.sumOf { it.totalSpent }
    }
    
    // Get users registered in last 30 days
    fun getRecentUsers(): List<User> {
        val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000)
        return _users.value.filter { it.createdAt >= thirtyDaysAgo }
    }
    
    // Get top spending users
    fun getTopSpendingUsers(limit: Int = 10): List<User> {
        return _users.value
            .sortedByDescending { it.totalSpent }
            .take(limit)
    }
    
    // Get most active users (by bookings)
    fun getMostActiveUsers(limit: Int = 10): List<User> {
        return _users.value
            .sortedByDescending { it.totalBookings }
            .take(limit)
    }
} 