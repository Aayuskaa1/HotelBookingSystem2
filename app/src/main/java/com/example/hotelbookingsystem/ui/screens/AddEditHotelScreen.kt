package com.example.hotelbookingsystem.ui.screens

import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hotelbookingsystem.model.Hotel
import com.example.hotelbookingsystem.viewmodel.HotelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditHotelScreen(
    hotel: Hotel? = null,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    hotelViewModel: HotelViewModel
) {
    val isLoading by hotelViewModel.isLoading.collectAsState()
    val errorMessage by hotelViewModel.errorMessage.collectAsState()
    val successMessage by hotelViewModel.successMessage.collectAsState()
    
    var name by remember { mutableStateOf(hotel?.name ?: "") }
    var description by remember { mutableStateOf(hotel?.description ?: "") }
    var address by remember { mutableStateOf(hotel?.address ?: "") }
    var city by remember { mutableStateOf(hotel?.city ?: "") }
    var country by remember { mutableStateOf(hotel?.country ?: "") }
    var phone by remember { mutableStateOf(hotel?.phone ?: "") }
    var email by remember { mutableStateOf(hotel?.email ?: "") }
    var website by remember { mutableStateOf(hotel?.website ?: "") }
    var pricePerNight by remember { mutableStateOf(hotel?.pricePerNight?.toString() ?: "") }
    var rating by remember { mutableStateOf(hotel?.rating?.toString() ?: "") }
    var currency by remember { mutableStateOf(hotel?.currency ?: "USD") }
    var isActive by remember { mutableStateOf(hotel?.isActive ?: true) }
    
    var nameError by remember { mutableStateOf<String?>(null) }
    var cityError by remember { mutableStateOf<String?>(null) }
    var countryError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }
    var ratingError by remember { mutableStateOf<String?>(null) }
    
    // Clear messages after showing
    LaunchedEffect(errorMessage, successMessage) {
        if (successMessage != null) {
            kotlinx.coroutines.delay(1000)
            onSaveSuccess()
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
                    text = if (hotel == null) "Add New Hotel" else "Edit Hotel",
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
                if (!isLoading) {
                    TextButton(
                        onClick = {
                            // Validate and save
                            nameError = if (name.isBlank()) "Hotel name is required" else null
                            cityError = if (city.isBlank()) "City is required" else null
                            countryError = if (country.isBlank()) "Country is required" else null
                            
                            val price = pricePerNight.toDoubleOrNull()
                            priceError = if (price == null || price <= 0) "Valid price is required" else null
                            
                            val ratingValue = rating.toFloatOrNull()
                            ratingError = if (rating.isNotBlank() && (ratingValue == null || ratingValue < 0 || ratingValue > 5)) {
                                "Rating must be between 0 and 5"
                            } else null
                            
                            if (nameError == null && cityError == null && countryError == null && 
                                priceError == null && ratingError == null) {
                                
                                val hotelToSave = Hotel(
                                    id = hotel?.id ?: "",
                                    name = name.trim(),
                                    description = description.trim(),
                                    address = address.trim(),
                                    city = city.trim(),
                                    country = country.trim(),
                                    phone = phone.trim(),
                                    email = email.trim(),
                                    website = website.trim(),
                                    pricePerNight = price!!,
                                    rating = ratingValue ?: 0.0f,
                                    currency = currency,
                                    isActive = isActive
                                )
                                
                                if (hotel == null) {
                                    hotelViewModel.addHotel(hotelToSave)
                                } else {
                                    hotelViewModel.updateHotel(hotelToSave)
                                }
                            }
                        }
                    ) {
                        Text(
                            text = if (hotel == null) "Add" else "Save",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        )
        
        // Messages
        errorMessage?.let { message ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        
        // Form
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Basic Information Section
            Text(
                text = "Basic Information",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Hotel Name
            OutlinedTextField(
                value = name,
                onValueChange = { 
                    name = it
                    nameError = null
                },
                label = { Text("Hotel Name *") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError != null,
                supportingText = nameError?.let { { Text(it) } }
            )
            
            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            
            // Location Section
            Text(
                text = "Location",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Address
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth()
            )
            
            // City and Country Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { 
                        city = it
                        cityError = null
                    },
                    label = { Text("City *") },
                    modifier = Modifier.weight(1f),
                    isError = cityError != null,
                    supportingText = cityError?.let { { Text(it) } }
                )
                
                OutlinedTextField(
                    value = country,
                    onValueChange = { 
                        country = it
                        countryError = null
                    },
                    label = { Text("Country *") },
                    modifier = Modifier.weight(1f),
                    isError = countryError != null,
                    supportingText = countryError?.let { { Text(it) } }
                )
            }
            
            // Contact Information Section
            Text(
                text = "Contact Information",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Phone
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Phone,
                        contentDescription = null
                    )
                }
            )
            
            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = null
                    )
                }
            )
            
            // Website
            OutlinedTextField(
                value = website,
                onValueChange = { website = it },
                label = { Text("Website") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Language,
                        contentDescription = null
                    )
                }
            )
            
            // Pricing Section
            Text(
                text = "Pricing & Rating",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Price and Currency Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = pricePerNight,
                    onValueChange = { 
                        pricePerNight = it
                        priceError = null
                    },
                    label = { Text("Price per Night *") },
                    modifier = Modifier.weight(1f),
                    isError = priceError != null,
                    supportingText = priceError?.let { { Text(it) } },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.AttachMoney,
                            contentDescription = null
                        )
                    }
                )
                
                // Currency dropdown
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = { },
                    modifier = Modifier.weight(0.5f)
                ) {
                    OutlinedTextField(
                        value = currency,
                        onValueChange = { currency = it },
                        label = { Text("Currency") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = false)
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = false,
                        onDismissRequest = { }
                    ) {
                        listOf("USD", "EUR", "GBP", "JPY", "CAD", "AUD").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = { currency = option }
                            )
                        }
                    }
                }
            }
            
            // Rating
            OutlinedTextField(
                value = rating,
                onValueChange = { 
                    rating = it
                    ratingError = null
                },
                label = { Text("Rating (0-5)") },
                modifier = Modifier.fillMaxWidth(),
                isError = ratingError != null,
                supportingText = ratingError?.let { { Text(it) } } ?: { Text("Leave empty for no rating") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null
                    )
                }
            )
            
            // Status Section
            Text(
                text = "Status",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Active Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Hotel",
                    fontSize = 16.sp
                )
                Switch(
                    checked = isActive,
                    onCheckedChange = { isActive = it }
                )
            }
            
            // Help text
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "💡 Tips:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Fields marked with * are required",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• Rating should be between 0 and 5",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• Inactive hotels won't appear to users",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Loading indicator
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
} 