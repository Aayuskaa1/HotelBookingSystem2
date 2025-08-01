package com.example.hotelbookingsystem.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hotelbookingsystem.model.Hotel
import com.example.hotelbookingsystem.model.Booking
import com.example.hotelbookingsystem.model.BookingStatus
import com.example.hotelbookingsystem.model.PaymentStatus
import com.example.hotelbookingsystem.viewmodel.BookingViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserBookingScreen(
    hotel: Hotel,
    currentUser: com.example.hotelbookingsystem.model.MockFirebaseUser?,
    onBackClick: () -> Unit,
    onBookingSuccess: () -> Unit,
    bookingViewModel: BookingViewModel
) {
    val isLoading by bookingViewModel.isLoading.collectAsState()
    val errorMessage by bookingViewModel.errorMessage.collectAsState()
    val successMessage by bookingViewModel.successMessage.collectAsState()
    
    var checkInDate by remember { mutableStateOf("") }
    var checkOutDate by remember { mutableStateOf("") }
    var numberOfGuests by remember { mutableStateOf("1") }
    var specialRequests by remember { mutableStateOf("") }
    var guestName by remember { mutableStateOf(currentUser?.displayName ?: "") }
    var guestEmail by remember { mutableStateOf(currentUser?.email?.split("@")?.firstOrNull() ?: "") }
    var guestPhone by remember { mutableStateOf("") }
    
    // Simple validation states
    var checkInError by remember { mutableStateOf<String?>(null) }
    var checkOutError by remember { mutableStateOf<String?>(null) }
    var guestsError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    
    // Simple total price calculation
    val totalPrice = remember(checkInDate, checkOutDate, numberOfGuests) {
        if (checkInDate.isNotEmpty() && checkOutDate.isNotEmpty() && numberOfGuests.isNotEmpty()) {
            try {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val checkIn = dateFormat.parse(checkInDate)
                val checkOut = dateFormat.parse(checkOutDate)
                
                if (checkIn != null && checkOut != null) {
                    val days = ((checkOut.time - checkIn.time) / (1000 * 60 * 60 * 24)).toInt()
                    val guests = numberOfGuests.toIntOrNull() ?: 1
                    
                    // Validate booking duration
                    if (days < 1) {
                        checkOutError = "Minimum booking is 1 day"
                        return@remember 0.0
                    }
                    if (days > 30) {
                        checkOutError = "Maximum booking is 30 days"
                        return@remember 0.0
                    }
                    
                    // Convert USD to NPR (approximate rate: 1 USD = 130 NPR)
                    val priceInNPR = hotel.pricePerNight * 130
                    val total = priceInNPR * days * guests
                    
                    // Clear errors if calculation is successful
                    checkOutError = null
                    total
                } else {
                    0.0
                }
            } catch (e: Exception) {
                0.0
            }
        } else {
            0.0
        }
    }
    
    // Calculate booking duration for display
    val bookingDuration = remember(checkInDate, checkOutDate) {
        if (checkInDate.isNotEmpty() && checkOutDate.isNotEmpty()) {
            try {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val checkIn = dateFormat.parse(checkInDate)
                val checkOut = dateFormat.parse(checkOutDate)
                
                if (checkIn != null && checkOut != null) {
                    val days = ((checkOut.time - checkIn.time) / (1000 * 60 * 60 * 24)).toInt()
                    when {
                        days == 1 -> "1 day"
                        days > 1 -> "$days days"
                        else -> ""
                    }
                } else {
                    ""
                }
            } catch (e: Exception) {
                ""
            }
        } else {
            ""
        }
    }
    
    // Clear messages after showing
    LaunchedEffect(errorMessage, successMessage) {
        if (successMessage != null) {
            kotlinx.coroutines.delay(2000) // Show success message longer
            onBookingSuccess()
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
                    text = "Book Hotel",
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
            }
        )
        
        // Hotel Info Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = hotel.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${hotel.city}, ${hotel.country}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "NPR ${NumberFormat.getNumberInstance().format(hotel.pricePerNight * 130)}/night",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
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
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your booking will appear in 'My Bookings' section",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 12.sp
                    )
                }
            }
        }
        
        // Booking Form
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Simple Dates Section
            Text(
                text = "Booking Dates",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Simple Check-in Date
            OutlinedTextField(
                value = checkInDate,
                onValueChange = { 
                    checkInDate = it
                    checkInError = null
                },
                label = { Text("Check-in Date *") },
                placeholder = { Text("DD/MM/YYYY") },
                modifier = Modifier.fillMaxWidth(),
                isError = checkInError != null,
                supportingText = {
                    if (checkInError != null) {
                        Text(checkInError!!)
                    } else {
                        Text("Enter check-in date")
                    }
                },
                singleLine = true
            )
            
            // Simple Check-out Date
            OutlinedTextField(
                value = checkOutDate,
                onValueChange = { 
                    checkOutDate = it
                    checkOutError = null
                },
                label = { Text("Check-out Date *") },
                placeholder = { Text("DD/MM/YYYY") },
                modifier = Modifier.fillMaxWidth(),
                isError = checkOutError != null,
                supportingText = {
                    if (checkOutError != null) {
                        Text(checkOutError!!)
                    } else {
                        Text("Enter check-out date")
                    }
                },
                singleLine = true
            )
            
            // Number of Guests
            OutlinedTextField(
                value = numberOfGuests,
                onValueChange = { 
                    numberOfGuests = it
                    guestsError = null
                },
                label = { Text("Number of Guests *") },
                modifier = Modifier.fillMaxWidth(),
                isError = guestsError != null,
                supportingText = guestsError?.let { { Text(it) } },
                singleLine = true
            )
            
            // Guest Information Section
            Text(
                text = "Guest Information",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Guest Name
            OutlinedTextField(
                value = guestName,
                onValueChange = { 
                    guestName = it
                    nameError = null
                },
                label = { Text("Full Name *") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError != null,
                supportingText = nameError?.let { { Text(it) } },
                singleLine = true
            )
            
            // Guest Email
            OutlinedTextField(
                value = guestEmail,
                onValueChange = { 
                    guestEmail = it
                    emailError = null
                },
                label = { Text("Email Name *") },
                placeholder = { Text("Enter email name (before @)") },
                modifier = Modifier.fillMaxWidth(),
                isError = emailError != null,
                supportingText = emailError?.let { { Text(it) } },
                singleLine = true
            )
            
            // Guest Phone
            OutlinedTextField(
                value = guestPhone,
                onValueChange = { guestPhone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Special Requests
            OutlinedTextField(
                value = specialRequests,
                onValueChange = { specialRequests = it },
                label = { Text("Special Requests") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            
            // Enhanced Total Price Display
            if (totalPrice > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "📋 Booking Summary",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Booking details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Duration:", fontSize = 14.sp)
                            Text(bookingDuration, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Guests:", fontSize = 14.sp)
                            Text(numberOfGuests, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Price per night:", fontSize = 14.sp)
                            Text("NPR ${NumberFormat.getNumberInstance().format(hotel.pricePerNight * 130)}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Price:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "NPR ${NumberFormat.getNumberInstance().format(totalPrice)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
            
            // Book Now Button
            Button(
                onClick = {
                    // Validate form
                    checkInError = if (checkInDate.isBlank()) "Check-in date is required" else null
                    checkOutError = if (checkOutDate.isBlank()) "Check-out date is required" else null
                    
                    // Validate date format and range
                    if (checkInDate.isNotEmpty() && checkOutDate.isNotEmpty()) {
                        try {
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val checkIn = dateFormat.parse(checkInDate)
                            val checkOut = dateFormat.parse(checkOutDate)
                            
                            if (checkIn == null || checkOut == null) {
                                checkInError = "Invalid date format. Use DD/MM/YYYY"
                                checkOutError = "Invalid date format. Use DD/MM/YYYY"
                            } else if (checkOut <= checkIn) {
                                checkOutError = "Check-out date must be after check-in date"
                            }
                        } catch (e: Exception) {
                            checkInError = "Invalid date format. Use DD/MM/YYYY"
                            checkOutError = "Invalid date format. Use DD/MM/YYYY"
                        }
                    }
                    
                    guestsError = if (numberOfGuests.isBlank() || numberOfGuests.toIntOrNull() == null || numberOfGuests.toInt() < 1) {
                        "Valid number of guests is required"
                    } else null
                    nameError = if (guestName.isBlank()) "Guest name is required" else null
                    emailError = if (guestEmail.isBlank()) "Email name is required" else null
                    
                    if (checkInError == null && checkOutError == null && guestsError == null && 
                        nameError == null && emailError == null) {
                        
                        // Construct full email address
                        val fullEmail = if (guestEmail.contains("@")) {
                            guestEmail
                        } else {
                            "${guestEmail}@example.com"
                        }
                        
                        // Parse dates for booking
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val checkIn = dateFormat.parse(checkInDate)
                        val checkOut = dateFormat.parse(checkOutDate)
                        
                        val booking = Booking(
                            id = "",
                            hotelId = hotel.id,
                            hotelName = hotel.name,
                            userId = currentUser?.uid ?: "user_${System.currentTimeMillis()}",
                            userName = guestName,
                            userEmail = fullEmail,
                            checkInDate = checkIn?.time ?: 0L,
                            checkOutDate = checkOut?.time ?: 0L,
                            numberOfGuests = numberOfGuests.toInt(),
                            totalAmount = totalPrice,
                            currency = "NPR",
                            bookingStatus = BookingStatus.PENDING,
                            paymentStatus = PaymentStatus.PENDING,
                            specialRequests = specialRequests,
                            createdAt = System.currentTimeMillis()
                        )
                        
                        bookingViewModel.addBooking(booking)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !isLoading && totalPrice > 0
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "Book Now",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}