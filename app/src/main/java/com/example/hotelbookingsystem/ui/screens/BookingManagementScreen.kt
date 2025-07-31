package com.example.hotelbookingsystem.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.hotelbookingsystem.model.Booking
import com.example.hotelbookingsystem.model.BookingStatus
import com.example.hotelbookingsystem.model.PaymentStatus
import com.example.hotelbookingsystem.viewmodel.BookingViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingManagementScreen(
    onBackClick: () -> Unit,
    onEditBookingClick: (Booking) -> Unit,
    bookingViewModel: BookingViewModel
) {
    val bookings by bookingViewModel.bookings.collectAsState()
    
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
    
    val isLoading by bookingViewModel.isLoading.collectAsState()
    val errorMessage by bookingViewModel.errorMessage.collectAsState()
    val successMessage by bookingViewModel.successMessage.collectAsState()
    val selectedStatusFilter by bookingViewModel.selectedStatusFilter.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf<Booking?>(null) }
    var showStatusFilterDialog by remember { mutableStateOf(false) }
    
    // Load bookings on first launch
    LaunchedEffect(Unit) {
        bookingViewModel.loadBookings()
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
                    text = "User Bookings Management",
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
                IconButton(onClick = { showStatusFilterDialog = true }) {
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
            onValueChange = { 
                searchQuery = it
                bookingViewModel.searchBookings(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search bookings by hotel name...") },
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
                        bookingViewModel.loadBookings()
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
        
        // Statistics Cards for User-Generated Bookings
        val userStats = mapOf(
            "total" to userGeneratedBookings.size,
            "confirmed" to userGeneratedBookings.count { it.bookingStatus == com.example.hotelbookingsystem.model.BookingStatus.CONFIRMED },
            "pending" to userGeneratedBookings.count { it.bookingStatus == com.example.hotelbookingsystem.model.BookingStatus.PENDING }
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BookingStatCard(
                title = "Total",
                value = userStats["total"].toString(),
                modifier = Modifier.weight(1f)
            )
            BookingStatCard(
                title = "Confirmed",
                value = userStats["confirmed"].toString(),
                modifier = Modifier.weight(1f)
            )
            BookingStatCard(
                title = "Pending",
                value = userStats["pending"].toString(),
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Revenue Card
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
                        text = "Total Revenue",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "$${NumberFormat.getNumberInstance().format(userGeneratedBookings.filter { it.paymentStatus == com.example.hotelbookingsystem.model.PaymentStatus.PAID }.sumOf { it.totalAmount })}",
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
        
        // Bookings List
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (userGeneratedBookings.isEmpty()) {
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
                items(userGeneratedBookings) { booking ->
                    BookingCard(
                        booking = booking,
                        onEditClick = { onEditBookingClick(booking) },
                        onDeleteClick = { showDeleteDialog = booking },
                        onStatusChange = { status ->
                            bookingViewModel.updateBookingStatus(booking.id, status)
                        },
                        onPaymentStatusChange = { status ->
                            bookingViewModel.updatePaymentStatus(booking.id, status)
                        }
                    )
                }
            }
        }
    }
    
    // Status Filter Dialog
    if (showStatusFilterDialog) {
        AlertDialog(
            onDismissRequest = { showStatusFilterDialog = false },
            title = { Text("Filter by Status") },
            text = {
                Column {
                    BookingStatus.values().forEach { status ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedStatusFilter == status,
                                onClick = {
                                    bookingViewModel.filterBookingsByStatus(status)
                                    showStatusFilterDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = status.name,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = {
                            bookingViewModel.filterBookingsByStatus(null)
                            showStatusFilterDialog = false
                        }
                    ) {
                        Text("Show All")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStatusFilterDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Delete Confirmation Dialog
    showDeleteDialog?.let { booking ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Booking") },
            text = { Text("Are you sure you want to delete this booking for ${booking.hotelName}? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        bookingViewModel.deleteBooking(booking.id)
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
fun BookingStatCard(
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
fun BookingCard(
    booking: Booking,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onStatusChange: (BookingStatus) -> Unit,
    onPaymentStatusChange: (PaymentStatus) -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
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
                        text = booking.userName,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Status chip
                AssistChip(
                    onClick = { },
                    label = { Text(booking.bookingStatus.name) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (booking.bookingStatus) {
                            BookingStatus.CONFIRMED -> Color.Green.copy(alpha = 0.2f)
                            BookingStatus.PENDING -> Color(0xFFFF9800).copy(alpha = 0.2f)
                            BookingStatus.CANCELLED -> Color.Red.copy(alpha = 0.2f)
                            BookingStatus.COMPLETED -> Color.Blue.copy(alpha = 0.2f)
                            BookingStatus.NO_SHOW -> Color.Red.copy(alpha = 0.2f)
                        }
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Check-in",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = dateFormat.format(Date(booking.checkInDate)),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column {
                    Text(
                        text = "Check-out",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = dateFormat.format(Date(booking.checkOutDate)),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Price
                Text(
                    text = "${booking.currency} ${NumberFormat.getNumberInstance().format(booking.totalAmount)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Guests and Rooms
                Text(
                    text = "${booking.numberOfGuests} guests, ${booking.numberOfRooms} room(s)",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Status controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Booking Status
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Booking Status",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = booking.bookingStatus.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Payment Status
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Payment Status",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = booking.paymentStatus.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }
                
                TextButton(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete")
                }
            }
        }
    }
} 