package com.example.hotelbookingsystem.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hotelbookingsystem.model.User
import com.example.hotelbookingsystem.model.UserRole
import com.example.hotelbookingsystem.viewmodel.UserViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    onBackClick: () -> Unit,
    onEditUserClick: (User) -> Unit,
    userViewModel: UserViewModel,
    bookingViewModel: com.example.hotelbookingsystem.viewmodel.BookingViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val users by userViewModel.users.collectAsState()
    val bookings by bookingViewModel.bookings.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val errorMessage by userViewModel.errorMessage.collectAsState()
    val successMessage by userViewModel.successMessage.collectAsState()
    val selectedRoleFilter by userViewModel.selectedRoleFilter.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf<User?>(null) }
    var showRoleFilterDialog by remember { mutableStateOf(false) }
    var showUserDetailsDialog by remember { mutableStateOf<User?>(null) }
    
    // Load users and bookings on first launch
    LaunchedEffect(Unit) {
        userViewModel.loadUsers()
        bookingViewModel.loadBookings()
    }
    
    // Filter users who have made bookings
    val usersWithBookings = remember(users, bookings) {
        val userEmailsWithBookings = bookings.map { it.userEmail }.toSet()
        val userIdsWithBookings = bookings.map { it.userId }.toSet()
        
        users.filter { user ->
            userEmailsWithBookings.contains(user.email) || userIdsWithBookings.contains(user.id)
        }
    }
    
    // Filter users based on search query and role filter
    val filteredUsers = remember(usersWithBookings, searchQuery, selectedRoleFilter) {
        usersWithBookings.filter { user ->
            val matchesSearch = searchQuery.isEmpty() || 
                user.email.contains(searchQuery, ignoreCase = true) ||
                user.displayName.contains(searchQuery, ignoreCase = true)
            
            val matchesRole = selectedRoleFilter == null || user.userRole == selectedRoleFilter
            
            matchesSearch && matchesRole
        }
    }
    
    // Clear messages after showing
    LaunchedEffect(errorMessage, successMessage) {
        if (errorMessage != null || successMessage != null) {
            kotlinx.coroutines.delay(3000)
            userViewModel.clearMessages()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        TopAppBar(
            title = {
                Text(
                    text = "Users with Bookings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                IconButton(onClick = { showRoleFilterDialog = true }) {
                    Icon(
                        imageVector = Icons.Filled.FilterList,
                        contentDescription = "Filter"
                    )
                }
            }
        )
        
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search users with bookings by email or name...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { 
                        searchQuery = ""
                        userViewModel.loadUsers()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            },
            singleLine = true
        )
        
        // Statistics Cards for Users with Bookings
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UserStatCard(
                title = "Users with Bookings",
                value = usersWithBookings.size.toString(),
                modifier = Modifier.weight(1f)
            )
            UserStatCard(
                title = "Total Bookings",
                value = bookings.size.toString(),
                modifier = Modifier.weight(1f)
            )
            UserStatCard(
                title = "Avg Bookings/User",
                value = if (usersWithBookings.isNotEmpty()) {
                    String.format("%.1f", bookings.size.toFloat() / usersWithBookings.size)
                } else "0.0",
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Revenue Card for Users with Bookings
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Revenue from Users with Bookings",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    val totalRevenue = bookings.sumOf { it.totalAmount }
                    Text(
                        text = "$${NumberFormat.getNumberInstance().format(totalRevenue)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Icon(
                    imageVector = Icons.Filled.AttachMoney,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Messages
        errorMessage?.let { message ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        
        successMessage?.let { message ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        // Users List
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (filteredUsers.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.People,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No users with bookings found" else "No users have made bookings yet",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                    if (searchQuery.isEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Only users who have made bookings are shown here",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredUsers) { user ->
                    UserCard(
                        user = user,
                        onEditClick = { onEditUserClick(user) },
                        onDeleteClick = { showDeleteDialog = user },
                        onDetailsClick = { showUserDetailsDialog = user },
                        onRoleChange = { role ->
                            userViewModel.updateUserRole(user.id, role)
                        },
                        onStatusChange = { isActive ->
                            userViewModel.toggleUserStatus(user.id, isActive)
                        },
                        onVerificationChange = { isVerified ->
                            userViewModel.updateEmailVerificationStatus(user.id, isVerified)
                        }
                    )
                }
            }
        }
    }
    
    // Role Filter Dialog
    if (showRoleFilterDialog) {
        AlertDialog(
            onDismissRequest = { showRoleFilterDialog = false },
            title = { Text("Filter by Role") },
            text = {
                Column {
                    UserRole.values().forEach { role ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedRoleFilter == role,
                                onClick = {
                                    userViewModel.filterUsersByRole(role)
                                    showRoleFilterDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = role.name,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = {
                            userViewModel.filterUsersByRole(null)
                            showRoleFilterDialog = false
                        }
                    ) {
                        Text("Show All")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRoleFilterDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // User Details Dialog
    showUserDetailsDialog?.let { user ->
        AlertDialog(
            onDismissRequest = { showUserDetailsDialog = null },
            title = { Text("User Details") },
            text = {
                Column {
                    Text("Email: ${user.email}")
                    Text("Name: ${user.displayName}")
                    Text("Role: ${user.userRole.name}")
                    Text("Status: ${if (user.isActive) "Active" else "Inactive"}")
                    Text("Email Verified: ${if (user.isEmailVerified) "Yes" else "No"}")
                    Text("Total Bookings: ${user.totalBookings}")
                    Text("Total Spent: $${NumberFormat.getNumberInstance().format(user.totalSpent)}")
                    if (user.phoneNumber.isNotEmpty()) {
                        Text("Phone: ${user.phoneNumber}")
                    }
                    if (user.city.isNotEmpty()) {
                        Text("Location: ${user.city}, ${user.country}")
                    }
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    Text("Joined: ${dateFormat.format(Date(user.createdAt))}")
                }
            },
            confirmButton = {
                TextButton(onClick = { showUserDetailsDialog = null }) {
                    Text("Close")
                }
            }
        )
    }
    
    // Delete Confirmation Dialog
    showDeleteDialog?.let { user ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete User") },
            text = { Text("Are you sure you want to delete user '${user.email}'? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        userViewModel.deleteUser(user.id)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun UserStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun UserCard(
    user: User,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDetailsClick: () -> Unit,
    onRoleChange: (UserRole) -> Unit,
    onStatusChange: (Boolean) -> Unit,
    onVerificationChange: (Boolean) -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (user.isActive) 
                MaterialTheme.colorScheme.surface 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with user info and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.displayName.ifEmpty { user.email },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = user.email,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Status indicators
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    AssistChip(
                        onClick = { },
                        label = { Text(user.userRole.name) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = when (user.userRole) {
                                UserRole.ADMIN -> Color.Red.copy(alpha = 0.2f)
                                UserRole.MODERATOR -> Color.Blue.copy(alpha = 0.2f)
                                UserRole.PREMIUM_USER -> Color(0xFFFFD700).copy(alpha = 0.2f)
                                UserRole.USER -> Color.Green.copy(alpha = 0.2f)
                            }
                        )
                    )
                    if (!user.isEmailVerified) {
                                                    AssistChip(
                                onClick = { },
                                label = { Text("Unverified") },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = Color(0xFFFF9800).copy(alpha = 0.2f)
                                )
                            )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // User stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Bookings",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = user.totalBookings.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column {
                    Text(
                        text = "Spent",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$${NumberFormat.getNumberInstance().format(user.totalSpent)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column {
                    Text(
                        text = "Joined",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = dateFormat.format(Date(user.createdAt)),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Quick actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = user.isActive,
                        onCheckedChange = onStatusChange
                    )
                }
                
                // Action buttons
                Row {
                    IconButton(onClick = onDetailsClick) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Details",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
} 