package com.example.hotelbookingsystem.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class SupportRequest(
    val id: String,
    val type: String, // "contact" or "issue"
    val subject: String,
    val message: String,
    val userEmail: String,
    val timestamp: Long,
    val status: String = "pending" // "pending", "in_progress", "resolved"
)

data class IssueReport(
    val id: String,
    val issueType: String,
    val description: String,
    val userEmail: String,
    val timestamp: Long,
    val status: String = "pending"
)

class SupportViewModel : ViewModel() {
    
    private val _supportRequests = MutableStateFlow<List<SupportRequest>>(emptyList())
    val supportRequests: StateFlow<List<SupportRequest>> = _supportRequests.asStateFlow()
    
    private val _issueReports = MutableStateFlow<List<IssueReport>>(emptyList())
    val issueReports: StateFlow<List<IssueReport>> = _issueReports.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    fun submitContactRequest(subject: String, message: String, userEmail: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                // Simulate network delay
                kotlinx.coroutines.delay(1000)
                
                val request = SupportRequest(
                    id = "support_${System.currentTimeMillis()}",
                    type = "contact",
                    subject = subject,
                    message = message,
                    userEmail = userEmail,
                    timestamp = System.currentTimeMillis()
                )
                
                // Add to local list (in real app, this would be sent to server)
                val currentRequests = _supportRequests.value.toMutableList()
                currentRequests.add(request)
                _supportRequests.value = currentRequests
                
                _successMessage.value = "Your message has been sent successfully! We'll get back to you within 2 hours."
                
                println("Support request submitted: $request")
                
            } catch (e: Exception) {
                _errorMessage.value = "Failed to send message: ${e.message ?: "Unknown error"}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun submitIssueReport(issueType: String, description: String, userEmail: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                // Simulate network delay
                kotlinx.coroutines.delay(1000)
                
                val report = IssueReport(
                    id = "issue_${System.currentTimeMillis()}",
                    issueType = issueType,
                    description = description,
                    userEmail = userEmail,
                    timestamp = System.currentTimeMillis()
                )
                
                // Add to local list (in real app, this would be sent to server)
                val currentReports = _issueReports.value.toMutableList()
                currentReports.add(report)
                _issueReports.value = currentReports
                
                _successMessage.value = "Issue report submitted successfully! Our team will investigate and get back to you soon."
                
                println("Issue report submitted: $report")
                
            } catch (e: Exception) {
                _errorMessage.value = "Failed to submit issue report: ${e.message ?: "Unknown error"}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun getSupportStatistics(): Map<String, Int> {
        val requests = _supportRequests.value
        val reports = _issueReports.value
        
        return mapOf(
            "total_requests" to requests.size,
            "pending_requests" to requests.count { it.status == "pending" },
            "resolved_requests" to requests.count { it.status == "resolved" },
            "total_issues" to reports.size,
            "pending_issues" to reports.count { it.status == "pending" },
            "resolved_issues" to reports.count { it.status == "resolved" }
        )
    }
    
    fun getRecentRequests(limit: Int = 5): List<SupportRequest> {
        return _supportRequests.value
            .sortedByDescending { it.timestamp }
            .take(limit)
    }
    
    fun getRecentIssues(limit: Int = 5): List<IssueReport> {
        return _issueReports.value
            .sortedByDescending { it.timestamp }
            .take(limit)
    }
    
    fun clearSuccessMessage() {
        _successMessage.value = null
    }
    
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
    
    fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val formatter = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
        return formatter.format(date)
    }
} 