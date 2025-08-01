package com.example.hotelbookingsystem.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbookingsystem.ui.components.BottomNavigationBar
import com.example.hotelbookingsystem.ui.components.BottomNavItem

@Composable
fun MainScreen(
    currentUser: com.example.hotelbookingsystem.model.MockFirebaseUser?,
    onLogout: () -> Unit,
    onNavigateToBookings: () -> Unit,
    onNavigateToPayments: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var currentRoute by remember { mutableStateOf("bookings") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD)) // Light blue background like in the image
    ) {
        // Main content area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            when (currentRoute) {
                "bookings" -> BookingsContent(
                    currentUser = currentUser,
                    onNavigateToBookings = onNavigateToBookings
                )
                "payments" -> PaymentsContent(
                    currentUser = currentUser,
                    onNavigateToPayments = onNavigateToPayments
                )
                "settings" -> SettingsContent(
                    currentUser = currentUser,
                    onLogout = onLogout,
                    onNavigateToSettings = onNavigateToSettings
                )
            }
        }
        
        // Bottom Navigation Bar
        BottomNavigationBar(
            currentRoute = currentRoute,
            onNavigate = { route ->
                currentRoute = route
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun BookingsContent(
    currentUser: com.example.hotelbookingsystem.model.MockFirebaseUser?,
    onNavigateToBookings: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = "Bookings",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Bookings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Manage your hotel reservations",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onNavigateToBookings,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("View Bookings")
        }
    }
}

@Composable
fun PaymentsContent(
    currentUser: com.example.hotelbookingsystem.model.MockFirebaseUser?,
    onNavigateToPayments: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Payment,
            contentDescription = "Payments",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Payments",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Manage your payment methods and transactions",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onNavigateToPayments,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CreditCard,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("View Payments")
        }
    }
}

@Composable
fun SettingsContent(
    currentUser: com.example.hotelbookingsystem.model.MockFirebaseUser?,
    onLogout: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Customize your app preferences",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onNavigateToSettings,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Open Settings")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout")
        }
    }
} 