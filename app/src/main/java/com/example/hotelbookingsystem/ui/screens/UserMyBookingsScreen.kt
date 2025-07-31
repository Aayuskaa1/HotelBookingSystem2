package com.example.hotelbookingsystem.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
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
import com.example.hotelbookingsystem.model.Booking
import com.example.hotelbookingsystem.model.BookingStatus
import com.example.hotelbookingsystem.model.PaymentStatus
import com.example.hotelbookingsystem.viewmodel.BookingViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMyBookingsScreen(
    currentUser: com.example.hotelbookingsystem.model.MockFirebaseUser?,
    onBackClick: () -> Unit,
    onBookingClick: (Booking) -> Unit,
    bookingViewModel: BookingViewModel
) {
    val bookings by bookingViewModel.bookings.collectAsState()
    val isLoading by bookingViewModel.isLoading.collectAsState()
    val errorMessage by bookingViewModel.errorMessage.collectAsState()
    val successMessage by bookingViewModel.successMessage.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf<BookingStatus?>(null) }
    var showCancelDialog by remember { mutableStateOf<Booking?>(null) }
    
    // Load user's bookings on first launch
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            bookingViewModel.loadUserBookings(currentUser.uid)
        } else {
            bookingViewModel.loadBookings()
        }
    }
    
    // Filter bookings for current user
    val userBookings = remember(bookings, searchQuery, selectedStatusFilter, currentUser) {
        bookings.filter { booking ->
            // Only show bookings for the current user
            val isUserBooking = currentUser?.uid == booking.userId || 
                               currentUser?.email == booking.userEmail
            
            val matchesSearch = searchQuery.isEmpty() || 
                booking.hotelName.contains(searchQuery, ignoreCase = true) ||
                booking.userName.contains(searchQuery, ignoreCase = true)
            
            val matchesStatus = selectedStatusFilter == null || booking.bookingStatus == selectedStatusFilter
            
            isUserBooking && matchesSearch && matchesStatus
        }
    }
    
    // Clear messages after showing
    LaunchedEffect(errorMessage, successMessage) {
        if (errorMessage != null || successMessage != null) {
            kotlinx.coroutines.delay(3000)
            bookingViewModel.clearMessages()
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
                    text = "My Bookings",
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
                IconButton(onClick = { 
                    selectedStatusFilter = if (selectedStatusFilter == null) BookingStatus.CONFIRMED else null
                }) {
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
            placeholder = { Text("Search bookings...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            },
            singleLine = true
        )
        
        // Stats Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UserBookingStatCard(
                title = "Total Bookings",
                value = userBookings.size.toString(),
                modifier = Modifier.weight(1f)
            )
            UserBookingStatCard(
                title = "Active Bookings",
                value = userBookings.count { it.bookingStatus == BookingStatus.CONFIRMED }.toString(),
                modifier = Modifier.weight(1f)
            )
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
        
        // Bookings List
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (userBookings.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.BookOnline,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No bookings found" else "No bookings yet",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(userBookings) { booking ->
                    UserBookingCard(
                        booking = booking,
                        onClick = { onBookingClick(booking) },
                        onCancelClick = { showCancelDialog = booking }
                    )
                }
            }
        }
    }
    
    // Cancel Confirmation Dialog
    showCancelDialog?.let { booking ->
        AlertDialog(
            onDismissRequest = { showCancelDialog = null },
            title = { Text("Cancel Booking") },
            text = { Text("Are you sure you want to cancel your booking at '${booking.hotelName}'? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        bookingViewModel.updateBookingStatus(booking.id, BookingStatus.CANCELLED)
                        showCancelDialog = null
                    }
                ) {
                    Text("Cancel Booking", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = null }) {
                    Text("Keep Booking")
                }
            }
        )
    }
}

@Composable
fun UserBookingStatCard(
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
                fontSize = 24.sp,
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
fun UserBookingCard(
    booking: Booking,
    onClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (booking.bookingStatus) {
                BookingStatus.CONFIRMED -> MaterialTheme.colorScheme.surface
                BookingStatus.PENDING -> MaterialTheme.colorScheme.surfaceVariant
                BookingStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
                BookingStatus.COMPLETED -> MaterialTheme.colorScheme.primaryContainer
                BookingStatus.NO_SHOW -> MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with hotel name and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.hotelName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(booking.checkInDate)} to ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(booking.checkOutDate)}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Status chip
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when (booking.bookingStatus) {
                            BookingStatus.CONFIRMED -> Color(0xFF4CAF50)
                            BookingStatus.PENDING -> Color(0xFFFF9800)
                            BookingStatus.CANCELLED -> Color(0xFFF44336)
                            BookingStatus.COMPLETED -> Color(0xFF2196F3)
                            BookingStatus.NO_SHOW -> Color(0xFFF44336)
                        }
                    )
                ) {
                    Text(
                        text = booking.bookingStatus.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Booking details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${booking.numberOfGuests} guest(s)",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "USD ${NumberFormat.getNumberInstance().format(booking.totalAmount)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                // Payment status
                Text(
                    text = booking.paymentStatus.name,
                    fontSize = 12.sp,
                    color = when (booking.paymentStatus) {
                        PaymentStatus.PAID -> Color(0xFF4CAF50)
                        PaymentStatus.PENDING -> Color(0xFFFF9800)
                        PaymentStatus.FAILED -> Color(0xFFF44336)
                        PaymentStatus.REFUNDED -> Color(0xFF9E9E9E)
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onClick) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View Details")
                }
                
                if (booking.bookingStatus == BookingStatus.CONFIRMED || booking.bookingStatus == BookingStatus.PENDING) {
                    TextButton(
                        onClick = onCancelClick,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Cancel,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cancel")
                    }
                }
            }
        }
    }
} 