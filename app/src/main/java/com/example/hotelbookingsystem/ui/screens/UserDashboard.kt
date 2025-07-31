package com.example.hotelbookingsystem.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Help
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
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun UserDashboard(
    user: MockFirebaseUser,
    onLogout: () -> Unit,
    onSearchHotels: () -> Unit = {},
    onMyBookings: () -> Unit = {},
    onBookHotel: () -> Unit = {},
    onProfile: () -> Unit = {},
    onHelp: () -> Unit = {},
    bookingViewModel: com.example.hotelbookingsystem.viewmodel.BookingViewModel
) {
    val bookings by bookingViewModel.bookings.collectAsState()
    
    // Load bookings on first launch
    LaunchedEffect(user) {
        bookingViewModel.loadUserBookings(user.uid)
    }
    
    // Calculate user statistics
    val userBookings = remember(bookings, user) {
        bookings.filter { 
            it.userId == user.uid || it.userEmail == user.email 
        }
    }
    val activeBookings = userBookings.count { it.bookingStatus == com.example.hotelbookingsystem.model.BookingStatus.CONFIRMED }
    val totalSpent = userBookings.sumOf { it.totalAmount }
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
                            imageVector = Icons.Default.Person,
                            contentDescription = "User",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "Welcome Back!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "${user.displayName ?: user.email ?: "User"}",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            // Quick Stats
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    UserStatCard(
                        title = "My Bookings",
                        value = userBookings.size.toString(),
                        icon = Icons.Default.BookOnline,
                        modifier = Modifier.weight(1f)
                    )
                    UserStatCard(
                        title = "Active Bookings",
                        value = activeBookings.toString(),
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Search Hotels Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    onClick = onSearchHotels
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Search Hotels",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            
                            Text(
                                text = "Find and book your perfect stay",
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
                UserActionCard(
                    title = "My Bookings",
                    description = "View and manage your hotel bookings",
                    icon = Icons.Default.BookOnline,
                    onClick = onMyBookings
                )
            }
            
            item {
                UserActionCard(
                    title = "Book a Hotel",
                    description = "Make a new hotel reservation",
                    icon = Icons.Default.Add,
                    onClick = onBookHotel
                )
            }
            
            item {
                UserActionCard(
                    title = "My Profile",
                    description = "Update your profile information",
                    icon = Icons.Default.Person,
                    onClick = onProfile
                )
            }
            
            item {
                UserActionCard(
                    title = "Help & Support",
                    description = "Get help and contact support",
                    icon = Icons.AutoMirrored.Filled.Help,
                    onClick = onHelp
                )
            }
            
            // Recent Activity
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Recent Activity",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            // Recent Activity - Show actual recent bookings
            userBookings.take(3).forEach { booking ->
                item {
                    RecentActivityCard(
                        title = when (booking.bookingStatus) {
                            com.example.hotelbookingsystem.model.BookingStatus.CONFIRMED -> "Booking Confirmed"
                            com.example.hotelbookingsystem.model.BookingStatus.PENDING -> "Booking Pending"
                            com.example.hotelbookingsystem.model.BookingStatus.CANCELLED -> "Booking Cancelled"
                            com.example.hotelbookingsystem.model.BookingStatus.COMPLETED -> "Booking Completed"
                            com.example.hotelbookingsystem.model.BookingStatus.NO_SHOW -> "No Show"
                        },
                        description = "${booking.hotelName} - ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(booking.checkInDate)} to ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(booking.checkOutDate)}",
                        time = "NPR ${NumberFormat.getNumberInstance().format(booking.totalAmount)}",
                        icon = when (booking.bookingStatus) {
                            com.example.hotelbookingsystem.model.BookingStatus.CONFIRMED -> Icons.Default.CheckCircle
                            com.example.hotelbookingsystem.model.BookingStatus.PENDING -> Icons.Default.Schedule
                            com.example.hotelbookingsystem.model.BookingStatus.CANCELLED -> Icons.Default.Cancel
                            com.example.hotelbookingsystem.model.BookingStatus.COMPLETED -> Icons.Default.Done
                            com.example.hotelbookingsystem.model.BookingStatus.NO_SHOW -> Icons.Default.Cancel
                        }
                    )
                }
            }
            
            // Show message if no bookings
            if (userBookings.isEmpty()) {
                item {
                    RecentActivityCard(
                        title = "No Bookings Yet",
                        description = "Start by searching for hotels and making your first booking",
                        time = "Get started",
                        icon = Icons.Default.Search
                    )
                }
            }
            
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
}

@Composable
private fun UserStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
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
private fun UserActionCard(
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
private fun RecentActivityCard(
    title: String,
    description: String,
    time: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
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
                
                Text(
                    text = time,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
} 