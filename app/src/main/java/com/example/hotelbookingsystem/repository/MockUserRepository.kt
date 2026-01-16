package com.example.hotelbookingsystem.repository

import android.util.Log
import com.example.hotelbookingsystem.model.*
import com.example.hotelbookingsystem.data.MockData
import com.example.hotelbookingsystem.utils.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class MockUserRepository {
    private var users = MockData.mockUsers.toMutableList()
    
    // Get all users
    fun getAllUsers(): Flow<List<User>> = flow {
        try {
            emit(users)
        } catch (e: Exception) {
            Log.e("MockUserRepository", "Error getting users: ${e.message}", e)
            emit(emptyList())
        }
    }
    
    // Get user by ID
    suspend fun getUserById(id: String): User? {
        return try {
            users.find { it.id == id }
        } catch (e: Exception) {
            Log.e("MockUserRepository", "Error getting user by ID: ${e.message}", e)
            null
        }
    }
    
    // Get user by email
    suspend fun getUserByEmail(email: String): User? {
        return try {
            users.find { it.email.equals(email, ignoreCase = true) }
        } catch (e: Exception) {
            Log.e("MockUserRepository", "Error getting user by email: ${e.message}", e)
            null
        }
    }
    
    // Add new user
    suspend fun addUser(user: User): Result<User> {
        return try {
            val userWithTimestamp = user.copy(
                id = UUID.randomUUID().toString(),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            users.add(userWithTimestamp)
            
            Log.d("MockUserRepository", "User added successfully: ${userWithTimestamp.email}")
            Result.success(userWithTimestamp)
        } catch (e: Exception) {
            Log.e("MockUserRepository", "Error adding user: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Update user
    suspend fun updateUser(user: User): Result<User> {
        return try {
            val userWithTimestamp = user.copy(
                updatedAt = System.currentTimeMillis()
            )
            
            val index = users.indexOfFirst { it.id == user.id }
            if (index != -1) {
                users[index] = userWithTimestamp
            }
            
            Log.d("MockUserRepository", "User updated successfully: ${userWithTimestamp.email}")
            Result.success(userWithTimestamp)
        } catch (e: Exception) {
            Log.e("MockUserRepository", "Error updating user: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Delete user
    suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            users.removeAll { it.id == userId }
            Log.d("MockUserRepository", "User deleted successfully: $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MockUserRepository", "Error deleting user: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Update user role
    suspend fun updateUserRole(userId: String, role: UserRole): Result<Unit> {
        return try {
            val index = users.indexOfFirst { it.id == userId }
            if (index != -1) {
                users[index] = users[index].copy(
                    userRole = role,
                    updatedAt = System.currentTimeMillis()
                )
            }
            Log.d("MockUserRepository", "User role updated: $userId to $role")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MockUserRepository", "Error updating user role: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Toggle user status
    suspend fun toggleUserStatus(userId: String): Result<Unit> {
        return try {
            val index = users.indexOfFirst { it.id == userId }
            if (index != -1) {
                users[index] = users[index].copy(
                    isActive = !users[index].isActive,
                    updatedAt = System.currentTimeMillis()
                )
            }
            Log.d("MockUserRepository", "User status toggled: $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MockUserRepository", "Error toggling user status: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Update email verification status
    suspend fun updateEmailVerificationStatus(userId: String, isVerified: Boolean): Result<Unit> {
        return try {
            val index = users.indexOfFirst { it.id == userId }
            if (index != -1) {
                users[index] = users[index].copy(
                    isEmailVerified = isVerified,
                    updatedAt = System.currentTimeMillis()
                )
            }
            Log.d("MockUserRepository", "Email verification updated: $userId to $isVerified")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MockUserRepository", "Error updating email verification: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Update last login time
    suspend fun updateLastLoginTime(userId: String): Result<Unit> {
        return try {
            val index = users.indexOfFirst { it.id == userId }
            if (index != -1) {
                users[index] = users[index].copy(
                    lastLoginAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            }
            Log.d("MockUserRepository", "Last login time updated: $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MockUserRepository", "Error updating last login time: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Search users
    fun searchUsers(query: String): Flow<List<User>> = flow {
        try {
            val filteredUsers = users.filter { user ->
                user.displayName.contains(query, ignoreCase = true) ||
                user.email.contains(query, ignoreCase = true) ||
                user.firstName.contains(query, ignoreCase = true) ||
                user.lastName.contains(query, ignoreCase = true)
            }
            emit(filteredUsers)
        } catch (e: Exception) {
            Log.e("MockUserRepository", "Error searching users: ${e.message}", e)
            emit(emptyList())
        }
    }
    
    // Get users by role
    fun getUsersByRole(role: UserRole): Flow<List<User>> = flow {
        try {
            val filteredUsers = users.filter { it.userRole == role }
            emit(filteredUsers)
        } catch (e: Exception) {
            Log.e("MockUserRepository", "Error getting users by role: ${e.message}", e)
            emit(emptyList())
        }
    }
    
    // Get active users
    fun getActiveUsers(): Flow<List<User>> = flow {
        try {
            val activeUsers = users.filter { it.isActive }
            emit(activeUsers)
        } catch (e: Exception) {
            Log.e("MockUserRepository", "Error getting active users: ${e.message}", e)
            emit(emptyList())
        }
    }
    
    // Get users by date range
    fun getUsersByDateRange(startDate: Long, endDate: Long): Flow<List<User>> = flow {
        try {
            val filteredUsers = users.filter { 
                it.createdAt in startDate..endDate 
            }
            emit(filteredUsers)
        } catch (e: Exception) {
            Log.e("MockUserRepository", "Error getting users by date range: ${e.message}", e)
            emit(emptyList())
        }
    }
    
    // Create user from auth (mock implementation)
    suspend fun createUserFromAuth(email: String, displayName: String?): Result<User> {
        return try {
            val newUser = User(
                id = UUID.randomUUID().toString(),
                email = email,
                displayName = displayName ?: email.split("@").first(),
                firstName = displayName?.split(" ")?.firstOrNull() ?: "",
                lastName = displayName?.split(" ")?.drop(1)?.joinToString(" ") ?: "",
                userRole = UserRole.USER,
                isActive = true,
                isEmailVerified = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            users.add(newUser)
            
            Log.d("MockUserRepository", "User created from auth: ${newUser.email}")
            Result.success(newUser)
        } catch (e: Exception) {
            Log.e("MockUserRepository", "Error creating user from auth: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // Get user statistics
    fun getUserStatistics(): Map<String, Any> {
        return mapOf(
            "totalUsers" to users.size,
            "activeUsers" to users.count { it.isActive },
            "verifiedUsers" to users.count { it.isEmailVerified },
            "adminUsers" to users.count { it.userRole == UserRole.ADMIN },
            "regularUsers" to users.count { it.userRole == UserRole.USER },
            "totalRevenue" to users.sumOf { it.totalSpent },
            "averageUserRevenue" to if (users.isNotEmpty()) users.sumOf { it.totalSpent } / users.size else 0.0
        )
    }
} 