package com.example.hotelbookingsystem.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbookingsystem.model.MockFirebaseUser

@Composable
fun AdminDashboard(
    user: MockFirebaseUser,
    onLogout: () -> Unit,
    onManageHotels: () -> Unit = {},
    onManageBookings: () -> Unit = {},
    onManageUsers: () -> Unit = {},
    hotelViewModel: com.example.hotelbookingsystem.viewmodel.HotelViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    bookingViewModel: com.example.hotelbookingsystem.viewmodel.BookingViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    userViewModel: com.example.hotelbookingsystem.viewmodel.UserViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var showHotelsDialog by remember { mutableStateOf(false) }
    var showBookingsDialog by remember { mutableStateOf(false) }
    var showUsersDialog by remember { mutableStateOf(false) }
    var showRevenueDialog by remember { mutableStateOf(false) }
    val hotels by hotelViewModel.hotels.collectAsState()
    val bookings by bookingViewModel.bookings.collectAsState()
    val users by userViewModel.users.collectAsState()
    
    // Load data on first launch
    LaunchedEffect(Unit) {
        hotelViewModel.loadHotels()
        bookingViewModel.loadBookings()
        userViewModel.loadUsers()
    }
    
    // Filter user-generated hotels (not mock data)
    val userGeneratedHotels = hotels.filter { hotel ->
        // Mock hotels have IDs like "hotel_1", "hotel_2", etc.
        // User-generated hotels have UUID-based IDs
        !hotel.id.startsWith("hotel_")
    }
    
    // Filter bookings for user-generated hotels and users (not mock data)
    val userGeneratedBookings = bookings.filter { booking ->
        // Mock bookings have IDs like "booking_1", "booking_2", etc.
        // Mock hotels have IDs like "hotel_1", "hotel_2", etc.
        // Mock users have IDs like "user_1", "user_2", etc.
        // User-generated bookings have UUID-based IDs and reference real hotels/users
        !booking.id.startsWith("booking_") &&
        !booking.hotelId.startsWith("hotel_") &&
        !booking.userId.startsWith("user_")
    }
    
    // Calculate statistics
    val totalHotels = userGeneratedHotels.size
    val activeBookings = userGeneratedBookings.count { it.bookingStatus == com.example.hotelbookingsystem.model.BookingStatus.CONFIRMED }
    val totalUsers = users.size
    val totalRevenue = userGeneratedBookings
        .filter { it.paymentStatus == com.example.hotelbookingsystem.model.PaymentStatus.PAID }
        .sumOf { it.totalAmount }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E3A8A),
                        Color(0xFF3B82F6),
                        Color(0xFF60A5FA)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "Admin Dashboard",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "Welcome, ${user.displayName ?: user.email ?: "Admin"}",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            // Stats Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "User Hotels",
                        value = totalHotels.toString(),
                        icon = Icons.Default.Business,
                        modifier = Modifier.weight(1f),
                        onClick = { showHotelsDialog = true }
                    )
                    StatCard(
                        title = "Active Bookings",
                        value = activeBookings.toString(),
                        icon = Icons.Default.BookOnline,
                        modifier = Modifier.weight(1f),
                        onClick = { showBookingsDialog = true }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total Users",
                        value = totalUsers.toString(),
                        icon = Icons.Default.People,
                        modifier = Modifier.weight(1f),
                        onClick = { showUsersDialog = true }
                    )
                    StatCard(
                        title = "Revenue",
                        value = "$${String.format("%.1f", totalRevenue / 1000)}K",
                        icon = Icons.Default.AttachMoney,
                        modifier = Modifier.weight(1f),
                        onClick = { showRevenueDialog = true }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Quick Actions
            item {
                Text(
                    text = "Quick Actions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            // Action Cards
            item {
                ActionCard(
                    title = "Manage Hotels",
                    description = "Add, edit, or remove hotels from the system",
                    icon = Icons.Default.Business,
                    onClick = onManageHotels
                )
            }
            
            item {
                ActionCard(
                    title = "Manage Bookings",
                    description = "View and manage all hotel bookings",
                    icon = Icons.Default.BookOnline,
                    onClick = onManageBookings
                )
            }
            
            item {
                ActionCard(
                    title = "Manage Users",
                    description = "View and manage user accounts",
                    icon = Icons.Default.People,
                    onClick = onManageUsers
                )
            }
            
            // View Reports Card removed
            
            // Logout Button
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Logout",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Logout",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    
    // Dialogs for detailed data views
    if (showHotelsDialog) {
        HotelsDetailDialog(
            hotels = userGeneratedHotels,
            onDismiss = { showHotelsDialog = false }
        )
    }
    
    if (showBookingsDialog) {
        BookingsDetailDialog(
            bookings = userGeneratedBookings,
            onDismiss = { showBookingsDialog = false }
        )
    }
    
    if (showUsersDialog) {
        UsersDetailDialog(
            users = users,
            onDismiss = { showUsersDialog = false }
        )
    }
    
    if (showRevenueDialog) {
        RevenueDetailDialog(
            bookings = bookings,
            onDismiss = { showRevenueDialog = false }
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = onClick ?: {}
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = Color.Gray
            )
        }
    }
}

@Composable
private fun HotelsDetailDialog(
    hotels: List<com.example.hotelbookingsystem.model.Hotel>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "User-Generated Hotels: ${hotels.size}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(hotels) { hotel ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = hotel.name,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "${hotel.city}, ${hotel.country}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${hotel.currency} ${hotel.pricePerNight}/night",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Rating: ${hotel.rating}/5",
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = if (hotel.isActive) "Active" else "Inactive",
                                    fontSize = 12.sp,
                                    color = if (hotel.isActive) Color.Green else Color.Red
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun BookingsDetailDialog(
    bookings: List<com.example.hotelbookingsystem.model.Booking>,
    onDismiss: () -> Unit
) {
    val activeBookings = bookings.count { it.bookingStatus == com.example.hotelbookingsystem.model.BookingStatus.CONFIRMED }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "User-Generated Active Bookings: $activeBookings",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(bookings.filter { it.bookingStatus == com.example.hotelbookingsystem.model.BookingStatus.CONFIRMED }) { booking ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = booking.hotelName,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Guest: ${booking.userName}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Check-in: ${booking.checkInDate}",
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Check-out: ${booking.checkOutDate}",
                                fontSize = 14.sp
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${booking.numberOfGuests} guests",
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "${booking.currency} ${booking.totalAmount}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun UsersDetailDialog(
    users: List<com.example.hotelbookingsystem.model.User>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Total Users: ${users.size}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(users) { user ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = user.displayName,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                            Text(
                                text = user.email,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Role: ${user.userRole}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = if (user.isActive) "Active" else "Inactive",
                                    fontSize = 12.sp,
                                    color = if (user.isActive) Color.Green else Color.Red
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun RevenueDetailDialog(
    bookings: List<com.example.hotelbookingsystem.model.Booking>,
    onDismiss: () -> Unit
) {
    val paidBookings = bookings.filter { it.paymentStatus == com.example.hotelbookingsystem.model.PaymentStatus.PAID }
    val totalRevenue = paidBookings.sumOf { it.totalAmount }
    val averageRevenue = if (paidBookings.isNotEmpty()) totalRevenue / paidBookings.size else 0.0
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Revenue Details",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Summary cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$${String.format("%.0f", totalRevenue)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Total Revenue",
                                fontSize = 12.sp
                            )
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$${String.format("%.0f", averageRevenue)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Average",
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                
                // Paid bookings list
                Text(
                    text = "Paid Bookings (${paidBookings.size})",
                    fontWeight = FontWeight.Medium
                )
                
                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(paidBookings) { booking ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = booking.hotelName,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = booking.userName,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "${booking.currency} ${booking.totalAmount}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}