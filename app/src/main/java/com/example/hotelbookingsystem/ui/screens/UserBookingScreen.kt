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
                // Try multiple date formats for better user experience
                val dateFormats = listOf(
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
                    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()),
                    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()),
                    SimpleDateFormat("dd/MM/yy", Locale.getDefault()),
                    SimpleDateFormat("dd-MM-yy", Locale.getDefault()),
                    SimpleDateFormat("dd/MM/yyyy", Locale.US),
                    SimpleDateFormat("MM/dd/yyyy", Locale.US),
                    SimpleDateFormat("yyyy-MM-dd", Locale.US)
                )
                
                var checkIn: Date? = null
                var checkOut: Date? = null
                
                // Try to parse check-in date with different formats
                for (format in dateFormats) {
                    try {
                        format.isLenient = true
                        checkIn = format.parse(checkInDate)
                        if (checkIn != null) break
                    } catch (e: Exception) {
                        // Continue to next format
                    }
                }
                
                // Try to parse check-out date with different formats
                for (format in dateFormats) {
                    try {
                        format.isLenient = true
                        checkOut = format.parse(checkOutDate)
                        if (checkOut != null) break
                    } catch (e: Exception) {
                        // Continue to next format
                    }
                }
                
                if (checkIn != null && checkOut != null) {
                    val days = ((checkOut.time - checkIn.time) / (1000 * 60 * 60 * 24)).toInt()
                    val guests = numberOfGuests.toIntOrNull() ?: 1
                    
                    // Validate booking duration
                    if (days < 1) {
                        return@remember 0.0
                    }
                    if (days > 30) {
                        return@remember 0.0
                    }
                    
                    // Convert USD to NPR (approximate rate: 1 USD = 130 NPR)
                    val priceInNPR = hotel.pricePerNight * 130
                    val total = priceInNPR * days * guests
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
                // Try multiple date formats for better user experience
                val dateFormats = listOf(
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
                    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()),
                    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()),
                    SimpleDateFormat("dd/MM/yy", Locale.getDefault()),
                    SimpleDateFormat("dd-MM-yy", Locale.getDefault()),
                    SimpleDateFormat("dd/MM/yyyy", Locale.US),
                    SimpleDateFormat("MM/dd/yyyy", Locale.US),
                    SimpleDateFormat("yyyy-MM-dd", Locale.US)
                )
                
                var checkIn: Date? = null
                var checkOut: Date? = null
                
                // Try to parse check-in date with different formats
                for (format in dateFormats) {
                    try {
                        format.isLenient = true
                        checkIn = format.parse(checkInDate)
                        if (checkIn != null) break
                    } catch (e: Exception) {
                        // Continue to next format
                    }
                }
                
                // Try to parse check-out date with different formats
                for (format in dateFormats) {
                    try {
                        format.isLenient = true
                        checkOut = format.parse(checkOutDate)
                        if (checkOut != null) break
                    } catch (e: Exception) {
                        // Continue to next format
                    }
                }
                
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
            
            // Debug info for button state
            val isButtonEnabled = !isLoading && checkInDate.isNotEmpty() && checkOutDate.isNotEmpty() && 
                                 numberOfGuests.isNotEmpty() && guestName.isNotEmpty() && guestEmail.isNotEmpty()
            
            println("DEBUG: Button enabled state: $isButtonEnabled")
            println("DEBUG: isLoading: $isLoading, checkInDate: '${checkInDate.isNotEmpty()}', checkOutDate: '${checkOutDate.isNotEmpty()}', numberOfGuests: '${numberOfGuests.isNotEmpty()}', guestName: '${guestName.isNotEmpty()}', guestEmail: '${guestEmail.isNotEmpty()}'")
            
            // Debug text to show why button might be disabled
            if (!isButtonEnabled) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "⚠️ Please fill all required fields to enable booking:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Check-in Date: ${if (checkInDate.isNotEmpty()) "✓" else "✗"}",
                            fontSize = 12.sp,
                            color = if (checkInDate.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "• Check-out Date: ${if (checkOutDate.isNotEmpty()) "✓" else "✗"}",
                            fontSize = 12.sp,
                            color = if (checkOutDate.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "• Number of Guests: ${if (numberOfGuests.isNotEmpty()) "✓" else "✗"}",
                            fontSize = 12.sp,
                            color = if (numberOfGuests.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "• Guest Name: ${if (guestName.isNotEmpty()) "✓" else "✗"}",
                            fontSize = 12.sp,
                            color = if (guestName.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "• Email Name: ${if (guestEmail.isNotEmpty()) "✓" else "✗"}",
                            fontSize = 12.sp,
                            color = if (guestEmail.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            // Book Now Button
            Button(
                onClick = {
                    println("DEBUG: Book Now button clicked!")
                    println("DEBUG: checkInDate = '$checkInDate'")
                    println("DEBUG: checkOutDate = '$checkOutDate'")
                    println("DEBUG: numberOfGuests = '$numberOfGuests'")
                    println("DEBUG: guestName = '$guestName'")
                    println("DEBUG: guestEmail = '$guestEmail'")
                    println("DEBUG: totalPrice = $totalPrice")
                    
                    // Validate form
                    checkInError = if (checkInDate.isBlank()) "Check-in date is required" else null
                    checkOutError = if (checkOutDate.isBlank()) "Check-out date is required" else null
                    
                    // Validate date format and range
                    if (checkInDate.isNotEmpty() && checkOutDate.isNotEmpty()) {
                        println("DEBUG: Validating dates - checkInDate: '$checkInDate', checkOutDate: '$checkOutDate'")
                        
                        try {
                            // Try multiple date formats for better user experience
                            val dateFormats = listOf(
                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
                                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()),
                                SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()),
                                SimpleDateFormat("dd/MM/yy", Locale.getDefault()),
                                SimpleDateFormat("dd-MM-yy", Locale.getDefault()),
                                SimpleDateFormat("dd/MM/yyyy", Locale.US),
                                SimpleDateFormat("MM/dd/yyyy", Locale.US),
                                SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            )
                            
                            var checkIn: Date? = null
                            var checkOut: Date? = null
                            
                            // Try to parse check-in date with different formats
                            for (format in dateFormats) {
                                try {
                                    format.isLenient = true // Make it more lenient
                                    checkIn = format.parse(checkInDate)
                                    if (checkIn != null) {
                                        println("DEBUG: Check-in date parsed successfully with format: ${format.toPattern()}")
                                        break
                                    }
                                } catch (e: Exception) {
                                    println("DEBUG: Failed to parse check-in with format ${format.toPattern()}: ${e.message}")
                                    // Continue to next format
                                }
                            }
                            
                            // Try to parse check-out date with different formats
                            for (format in dateFormats) {
                                try {
                                    format.isLenient = true // Make it more lenient
                                    checkOut = format.parse(checkOutDate)
                                    if (checkOut != null) {
                                        println("DEBUG: Check-out date parsed successfully with format: ${format.toPattern()}")
                                        break
                                    }
                                } catch (e: Exception) {
                                    println("DEBUG: Failed to parse check-out with format ${format.toPattern()}: ${e.message}")
                                    // Continue to next format
                                }
                            }
                            
                            if (checkIn == null || checkOut == null) {
                                println("DEBUG: Date parsing failed - checkIn: $checkIn, checkOut: $checkOut")
                                checkInError = "Invalid date format. Try DD/MM/YYYY or MM/DD/YYYY"
                                checkOutError = "Invalid date format. Try DD/MM/YYYY or MM/DD/YYYY"
                            } else {
                                println("DEBUG: Dates parsed successfully - checkIn: $checkIn, checkOut: $checkOut")
                                
                                // Check if dates are in the future (allow today as well)
                                val currentDate = Date()
                                val today = Calendar.getInstance()
                                today.set(Calendar.HOUR_OF_DAY, 0)
                                today.set(Calendar.MINUTE, 0)
                                today.set(Calendar.SECOND, 0)
                                today.set(Calendar.MILLISECOND, 0)
                                
                                val checkInCalendar = Calendar.getInstance()
                                checkInCalendar.time = checkIn
                                checkInCalendar.set(Calendar.HOUR_OF_DAY, 0)
                                checkInCalendar.set(Calendar.MINUTE, 0)
                                checkInCalendar.set(Calendar.SECOND, 0)
                                checkInCalendar.set(Calendar.MILLISECOND, 0)
                                
                                if (checkInCalendar.before(today)) {
                                    checkInError = "Check-in date must be today or in the future"
                                } else if (checkOut <= checkIn) {
                                    checkOutError = "Check-out date must be after check-in date"
                                } else {
                                    // Validate booking duration
                                    val days = ((checkOut.time - checkIn.time) / (1000 * 60 * 60 * 24)).toInt()
                                    println("DEBUG: Booking duration: $days days")
                                    if (days < 1) {
                                        checkOutError = "Minimum booking is 1 day"
                                    } else if (days > 30) {
                                        checkOutError = "Maximum booking is 30 days"
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            println("DEBUG: Exception during date validation: ${e.message}")
                            checkInError = "Invalid date format. Try DD/MM/YYYY or MM/DD/YYYY"
                            checkOutError = "Invalid date format. Try DD/MM/YYYY or MM/DD/YYYY"
                        }
                    }
                    
                    guestsError = if (numberOfGuests.isBlank() || numberOfGuests.toIntOrNull() == null || numberOfGuests.toInt() < 1) {
                        "Valid number of guests is required"
                    } else null
                    nameError = if (guestName.isBlank()) "Guest name is required" else null
                    emailError = if (guestEmail.isBlank()) "Email name is required" else null
                    
                    println("DEBUG: Validation errors - checkInError: $checkInError, checkOutError: $checkOutError, guestsError: $guestsError, nameError: $nameError, emailError: $emailError")
                    
                    if (checkInError == null && checkOutError == null && guestsError == null && 
                        nameError == null && emailError == null) {
                        
                        println("DEBUG: All validation passed, creating booking...")
                        
                        // Construct full email address
                        val fullEmail = if (guestEmail.contains("@")) {
                            guestEmail
                        } else {
                            "${guestEmail}@example.com"
                        }
                        
                        // Parse dates for booking using flexible format
                        val dateFormats = listOf(
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
                            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()),
                            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()),
                            SimpleDateFormat("dd/MM/yy", Locale.getDefault()),
                            SimpleDateFormat("dd-MM-yy", Locale.getDefault()),
                            SimpleDateFormat("dd/MM/yyyy", Locale.US),
                            SimpleDateFormat("MM/dd/yyyy", Locale.US),
                            SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        )
                        
                        var checkIn: Date? = null
                        var checkOut: Date? = null
                        
                        // Try to parse check-in date with different formats
                        for (format in dateFormats) {
                            try {
                                format.isLenient = true
                                checkIn = format.parse(checkInDate)
                                if (checkIn != null) break
                            } catch (e: Exception) {
                                // Continue to next format
                            }
                        }
                        
                        // Try to parse check-out date with different formats
                        for (format in dateFormats) {
                            try {
                                format.isLenient = true
                                checkOut = format.parse(checkOutDate)
                                if (checkOut != null) break
                            } catch (e: Exception) {
                                // Continue to next format
                            }
                        }
                        
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
                        
                        println("DEBUG: Calling bookingViewModel.addBooking with booking: $booking")
                        bookingViewModel.addBooking(booking)
                    } else {
                        println("DEBUG: Validation failed, not creating booking")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = isButtonEnabled
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