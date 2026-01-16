package com.example.hotelbookingsystem.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(
    onBackClick: () -> Unit,
    onContactSupport: () -> Unit = {},
    onReportIssue: () -> Unit = {},
    supportViewModel: com.example.hotelbookingsystem.viewmodel.SupportViewModel = viewModel()
) {
    var selectedFAQ by remember { mutableStateOf<FAQ?>(null) }
    var showContactDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    
    val isLoading by supportViewModel.isLoading.collectAsState()
    val successMessage by supportViewModel.successMessage.collectAsState()
    val errorMessage by supportViewModel.errorMessage.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        TopAppBar(
            title = {
                Text(
                    text = "Help & Support",
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
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quick Actions
            item {
                Text(
                    text = "Quick Actions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Quick Action Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        title = "Contact Support",
                        description = "Get in touch with our support team",
                        icon = Icons.Filled.Support,
                        onClick = { showContactDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                    
                    QuickActionCard(
                        title = "Report Issue",
                        description = "Report a bug or problem",
                        icon = Icons.Filled.BugReport,
                        onClick = { showReportDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Support Information
            item {
                SupportInfoCard()
            }
            
            // FAQs Section
            item {
                Text(
                    text = "Frequently Asked Questions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // FAQ Items
            items(faqs) { faq ->
                FAQCard(
                    faq = faq,
                    isExpanded = selectedFAQ == faq,
                    onToggle = {
                        selectedFAQ = if (selectedFAQ == faq) null else faq
                    }
                )
            }
            
            // Contact Information
            item {
                Text(
                    text = "Contact Information",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                ContactInfoCard()
            }
            
            // App Information
            item {
                Text(
                    text = "App Information",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                AppInfoCard()
            }
        }
    }
    
    // Success Message
    if (successMessage != null) {
        AlertDialog(
            onDismissRequest = { supportViewModel.clearSuccessMessage() },
            title = { Text("Success") },
            text = { Text(successMessage!!) },
            confirmButton = {
                TextButton(onClick = { supportViewModel.clearSuccessMessage() }) {
                    Text("OK")
                }
            }
        )
    }
    
    // Error Message
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { supportViewModel.clearErrorMessage() },
            title = { Text("Error") },
            text = { Text(errorMessage!!) },
            confirmButton = {
                TextButton(onClick = { supportViewModel.clearErrorMessage() }) {
                    Text("OK")
                }
            }
        )
    }
    
    // Loading Dialog
    if (isLoading) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Processing...") },
            text = { 
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            },
            confirmButton = { }
        )
    }
    
    // Contact Support Dialog
    if (showContactDialog) {
        ContactSupportDialog(
            onDismiss = { showContactDialog = false },
            onSendMessage = { subject, message ->
                supportViewModel.submitContactRequest(subject, message, "user@example.com")
                showContactDialog = false
            }
        )
    }
    
    // Report Issue Dialog
    if (showReportDialog) {
        ReportIssueDialog(
            onDismiss = { showReportDialog = false },
            onSubmitReport = { issue, description ->
                supportViewModel.submitIssueReport(issue, description, "user@example.com")
                showReportDialog = false
            }
        )
    }
}

@Composable
fun QuickActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SupportInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Need Help?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Our support team is available 24/7 to help you with any questions or issues you may have.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun FAQCard(
    faq: FAQ,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                )
            }
            
            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = faq.answer,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ContactInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            ContactInfoRow(
                icon = Icons.Filled.Email,
                title = "Email",
                value = "support@hotelbooking.com"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ContactInfoRow(
                icon = Icons.Filled.Phone,
                title = "Phone",
                value = "+1 (555) 123-4567"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ContactInfoRow(
                icon = Icons.AutoMirrored.Filled.Chat,
                title = "Live Chat",
                value = "Available 24/7"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ContactInfoRow(
                icon = Icons.Filled.Schedule,
                title = "Response Time",
                value = "Within 2 hours"
            )
        }
    }
}

@Composable
fun ContactInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AppInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            AppInfoRow("App Version", "1.0.0")
            AppInfoRow("Build Number", "2024.12.01")
            AppInfoRow("Last Updated", SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date()))
            AppInfoRow("Platform", "Android")
        }
    }
}

@Composable
fun AppInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ContactSupportDialog(
    onDismiss: () -> Unit,
    onSendMessage: (String, String) -> Unit
) {
    var message by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Contact Support") },
        text = {
            Column {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    maxLines = 6
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSendMessage(subject, message) },
                enabled = message.isNotBlank() && subject.isNotBlank()
            ) {
                Text("Send")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ReportIssueDialog(
    onDismiss: () -> Unit,
    onSubmitReport: (String, String) -> Unit
) {
    var issueType by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report Issue") },
        text = {
            Column {
                OutlinedTextField(
                    value = issueType,
                    onValueChange = { issueType = it },
                    label = { Text("Issue Type") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g., Booking Problem, App Crash, etc.") }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    maxLines = 6,
                    placeholder = { Text("Please describe the issue in detail...") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSubmitReport(issueType, description) },
                enabled = issueType.isNotBlank() && description.isNotBlank()
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// FAQ Data
data class FAQ(
    val question: String,
    val answer: String
)

val faqs = listOf(
    FAQ(
        "How do I book a hotel?",
        "To book a hotel, go to the 'Search Hotels' section, browse available hotels, select your preferred hotel, choose your dates and number of guests, fill in your details, and click 'Book Now'."
    ),
    FAQ(
        "Can I cancel my booking?",
        "Yes, you can cancel your booking from the 'My Bookings' section. Select the booking you want to cancel and click the 'Cancel' button. Note that cancellation policies may vary by hotel."
    ),
    FAQ(
        "How do I update my profile?",
        "Go to 'My Profile' from the user dashboard, edit your information, and click 'Save Changes' to update your profile details."
    ),
    FAQ(
        "What payment methods are accepted?",
        "We accept major credit cards (Visa, MasterCard, American Express), debit cards, and digital wallets. Payment is processed securely through our payment partners."
    ),
    FAQ(
        "How do I contact customer support?",
        "You can contact our support team through email at support@hotelbooking.com, phone at +1 (555) 123-4567, or use the live chat feature available 24/7."
    ),
    FAQ(
        "Is my personal information secure?",
        "Yes, we take your privacy and security seriously. All personal information is encrypted and stored securely. We never share your data with third parties without your consent."
    ),
    FAQ(
        "What if I have a problem with my booking?",
        "If you encounter any issues with your booking, please contact our support team immediately. We'll work to resolve the issue as quickly as possible."
    ),
    FAQ(
        "Can I modify my booking after confirmation?",
        "Booking modifications depend on the hotel's policy. Contact our support team to check if modifications are possible for your specific booking."
    )
) 