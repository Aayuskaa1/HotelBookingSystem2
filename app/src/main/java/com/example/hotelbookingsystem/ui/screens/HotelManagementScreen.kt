package com.example.hotelbookingsystem.ui.screens

import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hotelbookingsystem.model.Hotel
import com.example.hotelbookingsystem.viewmodel.HotelViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelManagementScreen(
    onBackClick: () -> Unit,
    onAddHotelClick: () -> Unit,
    onEditHotelClick: (Hotel) -> Unit,
    hotelViewModel: HotelViewModel
) {
    val hotels by hotelViewModel.hotels.collectAsState()
    
    // Filter user-generated hotels (not mock data)
    val userGeneratedHotels = hotels.filter { hotel ->
        // Mock hotels have IDs like "hotel_1", "hotel_2", etc.
        // User-generated hotels have UUID-based IDs
        !hotel.id.startsWith("hotel_")
    }
    
    val isLoading by hotelViewModel.isLoading.collectAsState()
    val errorMessage by hotelViewModel.errorMessage.collectAsState()
    val successMessage by hotelViewModel.successMessage.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf<Hotel?>(null) }
    var showBulkActions by remember { mutableStateOf(false) }
    var selectedHotels by remember { mutableStateOf(setOf<String>()) }
    
    // Load hotels on first launch and refresh when screen becomes active
    LaunchedEffect(Unit) {
        println("HotelManagementScreen: Loading hotels...")
        hotelViewModel.loadHotels()
    }
    
    // Refresh hotels when returning to this screen
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(500) // Delay to ensure proper loading after navigation
        println("HotelManagementScreen: Refreshing hotels after navigation...")
        hotelViewModel.loadHotels()
    }
    
    // Clear messages after showing
    LaunchedEffect(errorMessage, successMessage) {
        if (errorMessage != null || successMessage != null) {
            kotlinx.coroutines.delay(3000)
            hotelViewModel.clearMessages()
        }
    }
    
    // Auto-refresh when success message is shown (indicating hotel was added/updated)
    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            kotlinx.coroutines.delay(1000) // Wait a bit for the message to show
            println("HotelManagementScreen: Auto-refreshing after success message")
            hotelViewModel.loadHotels()
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
                    text = "User Hotels Management",
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
                // Refresh button
                IconButton(onClick = { 
                    println("HotelManagementScreen: Manual refresh triggered")
                    hotelViewModel.loadHotels() 
                }) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Refresh"
                    )
                }
                
                if (selectedHotels.isNotEmpty()) {
                    IconButton(onClick = { showBulkActions = true }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Bulk Actions"
                        )
                    }
                } else {
                    IconButton(onClick = onAddHotelClick) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Hotel"
                        )
                    }
                }
            }
        )
        
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { 
                searchQuery = it
                hotelViewModel.searchHotels(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search hotels...") },
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
                        hotelViewModel.loadHotels()
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
        
        // Stats Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                title = "Total Hotels",
                value = hotels.size.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Active Hotels",
                value = hotels.count { it.isActive }.toString(),
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Success",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Hotel Operation Successful",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 14.sp
                    )
                }
            }
            println("HotelManagementScreen: Success message displayed: $message")
        }
        
        // Hotels List
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (userGeneratedHotels.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Business,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No hotels found" else "No hotels added yet",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                    if (searchQuery.isEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onAddHotelClick) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add First Hotel")
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(userGeneratedHotels) { hotel ->
                    HotelCard(
                        hotel = hotel,
                        isSelected = selectedHotels.contains(hotel.id),
                        onEditClick = { onEditHotelClick(hotel) },
                        onDeleteClick = { showDeleteDialog = hotel },
                        onToggleStatus = { isActive ->
                            hotelViewModel.toggleHotelStatus(hotel.id, isActive)
                        },
                        onSelectionChanged = { isSelected ->
                            selectedHotels = if (isSelected) {
                                selectedHotels + hotel.id
                            } else {
                                selectedHotels - hotel.id
                            }
                        }
                    )
                }
            }
        }
    }
    
    // Delete Confirmation Dialog
    showDeleteDialog?.let { hotel ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Hotel") },
            text = { Text("Are you sure you want to delete '${hotel.name}'? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        hotelViewModel.deleteHotel(hotel.id)
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
    
    // Bulk Actions Dialog
    if (showBulkActions) {
        AlertDialog(
            onDismissRequest = { showBulkActions = false },
            title = { Text("Bulk Actions") },
            text = { Text("Select an action to perform on ${selectedHotels.size} selected hotel(s)") },
            confirmButton = {
                Column {
                    TextButton(
                        onClick = {
                            selectedHotels.forEach { hotelId ->
                                hotelViewModel.toggleHotelStatus(hotelId, true)
                            }
                            selectedHotels = emptySet()
                            showBulkActions = false
                        }
                    ) {
                        Text("Activate All")
                    }
                    TextButton(
                        onClick = {
                            selectedHotels.forEach { hotelId ->
                                hotelViewModel.toggleHotelStatus(hotelId, false)
                            }
                            selectedHotels = emptySet()
                            showBulkActions = false
                        }
                    ) {
                        Text("Deactivate All")
                    }
                    TextButton(
                        onClick = {
                            selectedHotels.forEach { hotelId ->
                                hotelViewModel.deleteHotel(hotelId)
                            }
                            selectedHotels = emptySet()
                            showBulkActions = false
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete All")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    selectedHotels = emptySet()
                    showBulkActions = false 
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun StatCard(
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
fun HotelCard(
    hotel: Hotel,
    isSelected: Boolean = false,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleStatus: (Boolean) -> Unit,
    onSelectionChanged: (Boolean) -> Unit = {}
) {
    // Check if hotel was added recently (within last 5 minutes)
    val isRecentlyAdded = remember(hotel.id) {
        val currentTime = System.currentTimeMillis()
        val fiveMinutesAgo = currentTime - (5 * 60 * 1000)
        // For mock data, we'll consider hotels with recent IDs as "new"
        hotel.id.contains("hotel_") && hotel.id.length > 10
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (hotel.isActive) 
                MaterialTheme.colorScheme.surface 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with name and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Selection checkbox
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = onSelectionChanged
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = hotel.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        // New badge for recently added hotels
                        if (isRecentlyAdded) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.height(20.dp)
                            ) {
                                Text(
                                    text = "NEW",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "${hotel.city}, ${hotel.country}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Status toggle
                Switch(
                    checked = hotel.isActive,
                    onCheckedChange = onToggleStatus
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Description
            if (hotel.description.isNotEmpty()) {
                Text(
                    text = hotel.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Price
                Text(
                    text = "${hotel.currency} ${NumberFormat.getNumberInstance().format(hotel.pricePerNight)}/night",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Rating
                if (hotel.rating > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFFFFD700)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", hotel.rating),
                            fontSize = 14.sp
                        )
                    }
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