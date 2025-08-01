package com.example.hotelbookingsystem.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.hotelbookingsystem.R
// Firebase utilities removed for offline testing
// import com.example.hotelbookingsystem.utils.FirebaseSetupChecker
// import com.example.hotelbookingsystem.utils.FirebaseConnectionTest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onSignUpClick: () -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    val scope = rememberCoroutineScope()
    var helpText by remember { mutableStateOf<String?>(null) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    // Debug: Log when LoginScreen is displayed
    LaunchedEffect(Unit) {
        println("LoginScreen displayed - user must enter credentials")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Hotel Logo Image
        Image(
            painter = painterResource(id = R.drawable.image),
            contentDescription = "Hotel Logo",
            modifier = Modifier.size(150.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Title
        Text(
            text = "Hotel Booking System",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Help text
        Text(
            text = "Enter your credentials to login",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Password"
                )
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Error Message
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Login Button
        Button(
            onClick = { onLoginClick(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty(),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = "Sign In",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Sign Up Link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Don't have an account? ",
                color = Color.Gray
            )
            TextButton(onClick = onSignUpClick) {
                Text(
                    text = "Sign Up",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Help buttons for network error - removed for offline testing
        // if (errorMessage?.contains("Network error") == true || errorMessage?.contains("network") == true) {
        //     // Firebase Connection Test Button
        //     OutlinedButton(
        //         onClick = {
        //             scope.launch {
        //                 helpText = FirebaseConnectionTest.testFirebaseConnection()
        //             }
        //         },
        //         modifier = Modifier.fillMaxWidth()
        //     ) {
        //         Text("🔧 Test Firebase Connection")
        //     }
        //     
        //     Spacer(modifier = Modifier.height(8.dp))
        //     
        //     // Admin Login Test Button
        //     OutlinedButton(
        //         onClick = {
        //             scope.launch {
        //                 helpText = FirebaseSetupChecker.testAdminLogin()
        //             }
        //         },
        //         modifier = Modifier.fillMaxWidth()
        //     ) {
        //         Text("👤 Test Admin Login")
        //     }
        //     
        //     if (helpText != null) {
        //         Spacer(modifier = Modifier.height(8.dp))
        //                 Text(
        //                     text = helpText!!,
        //                     color = if (helpText!!.startsWith("✅")) Color.Green else Color.Red,
        //                     fontSize = 12.sp,
        //                     modifier = Modifier.fillMaxWidth()
        //                 )
        //     }
        // }
        
        // Connection test button removed for offline testing
        // if (errorMessage == null) {
        //     Spacer(modifier = Modifier.height(16.dp))
        //     
        //     OutlinedButton(
        //         onClick = {
        //             scope.launch {
        //                 helpText = FirebaseConnectionTest.testFirebaseConnection()
        //             }
        //         },
        //         modifier = Modifier.fillMaxWidth(),
        //         colors = ButtonDefaults.outlinedButtonColors(
        //             contentColor = MaterialTheme.colorScheme.secondary
        //         )
        //     ) {
        //         Text("🔧 Test Firebase Connection")
        //     }
        //     
        //     if (helpText != null) {
        //         Spacer(modifier = Modifier.height(8.dp))
        //         Text(
        //             text = helpText!!,
        //             color = if (helpText!!.startsWith("✅")) Color.Green else Color.Red,
        //             fontSize = 12.sp,
        //             modifier = Modifier.fillMaxWidth()
        //         )
        //     }
        // }
    }
} 