package com.example.hotelbookingsystem.ui.screens

import androidx.compose.foundation.background
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
fun UserHotelSearchScreen(
    onBackClick: () -> Unit,
    onHotelClick: (Hotel) -> Unit,
    hotelViewModel: HotelViewModel
) {
    val hotels by hotelViewModel.hotels.collectAsState()
    val isLoading by hotelViewModel.isLoading.collectAsState()
    val errorMessage by hotelViewModel.errorMessage.collectAsState()
    
    // Debug: Log when hotels change
    LaunchedEffect(hotels) {
        println("UserHotelSearchScreen: Hotels updated - ${hotels.size} hotels available")
        hotels.forEach { hotel ->
            println("UserHotelSearchScreen: Hotel: ${hotel.name} (ID: ${hotel.id})")
        }
    }
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf("") }
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    
    // Load hotels on first launch and refresh when screen becomes active
    LaunchedEffect(Unit) {
        println("UserHotelSearchScreen: Loading hotels on first launch...")
        hotelViewModel.loadHotels()
    }
    
    // Refresh hotels when returning to this screen
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(500) // Delay to ensure proper loading after navigation
        println("UserHotelSearchScreen: Refreshing hotels after navigation...")
        hotelViewModel.loadHotels()
    }
    
    // Filter hotels based on search and filters
    val filteredHotels = remember(hotels, searchQuery, selectedCity, minPrice, maxPrice) {
        println("UserHotelSearchScreen: Filtering ${hotels.size} hotels")
        hotels.forEach { hotel ->
            println("UserHotelSearchScreen: Checking hotel: ${hotel.name} - Active: ${hotel.isActive}")
        }
        
        hotels.filter { hotel ->
            val matchesSearch = searchQuery.isEmpty() || 
                hotel.name.contains(searchQuery, ignoreCase = true) ||
                hotel.city.contains(searchQuery, ignoreCase = true) ||
                hotel.country.contains(searchQuery, ignoreCase = true)
            
            val matchesCity = selectedCity.isEmpty() || hotel.city.equals(selectedCity, ignoreCase = true)
            
            val minPriceValue = minPrice.toDoubleOrNull() ?: 0.0
            val maxPriceValue = maxPrice.toDoubleOrNull() ?: Double.MAX_VALUE
            val matchesPrice = hotel.pricePerNight >= minPriceValue && hotel.pricePerNight <= maxPriceValue
            
            val isActive = hotel.isActive
            
            val shouldInclude = matchesSearch && matchesCity && matchesPrice && isActive
            
            if (!shouldInclude) {
                println("UserHotelSearchScreen: Hotel ${hotel.name} filtered out - Search: $matchesSearch, City: $matchesCity, Price: $matchesPrice, Active: $isActive")
            }
            
            shouldInclude
        }.also { filtered ->
            println("UserHotelSearchScreen: Filtered to ${filtered.size} hotels")
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
                    text = "Search Hotels",
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
                    hotelViewModel.loadHotels() 
                }) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Refresh"
                    )
                }
                
                IconButton(onClick = { showFilters = !showFilters }) {
                    Icon(
                        imageVector = Icons.Filled.FilterList,
                        contentDescription = "Filters"
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
            placeholder = { Text("Search hotels by name, city, or country...") },
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
        
        // Filters
        if (showFilters) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Filters",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // City Filter
                    OutlinedTextField(
                        value = selectedCity,
                        onValueChange = { selectedCity = it },
                        label = { Text("City") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    // Price Range
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = minPrice,
                            onValueChange = { minPrice = it },
                            label = { Text("Min Price") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = maxPrice,
                            onValueChange = { maxPrice = it },
                            label = { Text("Max Price") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    
                    // Clear Filters
                    TextButton(
                        onClick = {
                            selectedCity = ""
                            minPrice = ""
                            maxPrice = ""
                        }
                    ) {
                        Text("Clear Filters")
                    }
                }
            }
        }
        
        // Results Count
        Text(
            text = "${filteredHotels.size} hotels found",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        // Hotels List
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (filteredHotels.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty() || selectedCity.isNotEmpty() || minPrice.isNotEmpty() || maxPrice.isNotEmpty()) {
                            "No hotels match your search criteria"
                        } else {
                            "No hotels available"
                        },
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
                items(filteredHotels) { hotel ->
                    UserHotelCard(
                        hotel = hotel,
                        onClick = { onHotelClick(hotel) }
                    )
                }
            }
        }
    }
}

@Composable
fun UserHotelCard(
    hotel: Hotel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with name and rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = hotel.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${hotel.city}, ${hotel.country}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
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
                
                // Book Now Button
                Button(
                    onClick = onClick,
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Book Now", fontSize = 12.sp)
                }
            }
        }
    }
} 