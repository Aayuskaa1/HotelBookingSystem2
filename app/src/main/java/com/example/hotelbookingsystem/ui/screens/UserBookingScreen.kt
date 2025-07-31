package com.example.hotelbookingsystem.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
    
    // Date picker states
    var showCheckInDatePicker by remember { mutableStateOf(false) }
    var showCheckOutDatePicker by remember { mutableStateOf(false) }
    var selectedCheckInDate by remember { mutableStateOf<Date?>(null) }
    var selectedCheckOutDate by remember { mutableStateOf<Date?>(null) }
    
    var checkInError by remember { mutableStateOf<String?>(null) }
    var checkOutError by remember { mutableStateOf<String?>(null) }
    var guestsError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    
    // Calculate total price with Nepalese Rupees
    val totalPrice = remember(selectedCheckInDate, selectedCheckOutDate, numberOfGuests) {
        if (selectedCheckInDate != null && selectedCheckOutDate != null && numberOfGuests.isNotEmpty()) {
            try {
                val days = ((selectedCheckOutDate!!.time - selectedCheckInDate!!.time) / (1000 * 60 * 60 * 24)).toInt()
                val guests = numberOfGuests.toIntOrNull() ?: 1
                // Convert USD to NPR (approximate rate: 1 USD = 130 NPR)
                val priceInNPR = hotel.pricePerNight * 130
                priceInNPR * days * guests
            } catch (e: Exception) {
                0.0
            }
        } else {
            0.0
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
            // Dates Section
            Text(
                text = "Booking Dates",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Select dates between 1999-2028",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Check-in Date
            OutlinedTextField(
                value = selectedCheckInDate?.let { 
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it) 
                } ?: "",
                onValueChange = { },
                label = { Text("Check-in Date *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCheckInDatePicker = true },
                isError = checkInError != null,
                supportingText = checkInError?.let { { Text(it) } },
                singleLine = true,
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Select Date",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
            
            // Check-out Date
            OutlinedTextField(
                value = selectedCheckOutDate?.let { 
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it) 
                } ?: "",
                onValueChange = { },
                label = { Text("Check-out Date *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCheckOutDatePicker = true },
                isError = checkOutError != null,
                supportingText = checkOutError?.let { { Text(it) } },
                singleLine = true,
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Select Date",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
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
            
            // Total Price Display
            if (totalPrice > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Booking Summary",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Total Price: NPR ${NumberFormat.getNumberInstance().format(totalPrice)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Book Now Button
            Button(
                onClick = {
                    // Validate form
                    checkInError = if (selectedCheckInDate == null) "Check-in date is required" else null
                    checkOutError = if (selectedCheckOutDate == null) "Check-out date is required" else null
                    
                    // Validate date range
                    if (selectedCheckInDate != null && selectedCheckOutDate != null) {
                        if (selectedCheckOutDate!! <= selectedCheckInDate!!) {
                            checkOutError = "Check-out date must be after check-in date"
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
                        
                        val booking = Booking(
                            id = "",
                            hotelId = hotel.id,
                            hotelName = hotel.name,
                            userId = currentUser?.uid ?: "user_${System.currentTimeMillis()}",
                            userName = guestName,
                            userEmail = fullEmail,
                            checkInDate = selectedCheckInDate?.time ?: 0L,
                            checkOutDate = selectedCheckOutDate?.time ?: 0L,
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
    
    // Date Pickers
    if (showCheckInDatePicker) {
        val checkInDatePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedCheckInDate?.time ?: System.currentTimeMillis(),
            yearRange = IntRange(1999, 2028)
        )
        
        DatePickerDialog(
            onDismissRequest = { showCheckInDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedCheckInDate = checkInDatePickerState.selectedDateMillis?.let { Date(it) }
                        showCheckInDatePicker = false
                        checkInError = null
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCheckInDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = checkInDatePickerState,
                showModeToggle = false
            )
        }
    }
    
    if (showCheckOutDatePicker) {
        val checkOutDatePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedCheckOutDate?.time ?: (selectedCheckInDate?.time ?: System.currentTimeMillis()),
            yearRange = IntRange(1999, 2028)
        )
        
        DatePickerDialog(
            onDismissRequest = { showCheckOutDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedCheckOutDate = checkOutDatePickerState.selectedDateMillis?.let { Date(it) }
                        showCheckOutDatePicker = false
                        checkOutError = null
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCheckOutDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = checkOutDatePickerState,
                showModeToggle = false
            )
        }
    }
} 