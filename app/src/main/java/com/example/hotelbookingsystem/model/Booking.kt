package com.example.hotelbookingsystem.model

import java.util.UUID

data class Booking(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val userEmail: String = "",
    val userName: String = "",
    val hotelId: String = "",
    val hotelName: String = "",
    val checkInDate: Long = 0L,
    val checkOutDate: Long = 0L,
    val numberOfGuests: Int = 1,
    val numberOfRooms: Int = 1,
    val totalAmount: Double = 0.0,
    val currency: String = "USD",
    val bookingStatus: BookingStatus = BookingStatus.PENDING,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val specialRequests: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "userId" to userId,
            "userEmail" to userEmail,
            "userName" to userName,
            "hotelId" to hotelId,
            "hotelName" to hotelName,
            "checkInDate" to checkInDate,
            "checkOutDate" to checkOutDate,
            "numberOfGuests" to numberOfGuests,
            "numberOfRooms" to numberOfRooms,
            "totalAmount" to totalAmount,
            "currency" to currency,
            "bookingStatus" to bookingStatus.name,
            "paymentStatus" to paymentStatus.name,
            "specialRequests" to specialRequests,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
    
    companion object {
        fun fromMap(map: Map<String, Any>): Booking {
            return Booking(
                id = map["id"] as? String ?: "",
                userId = map["userId"] as? String ?: "",
                userEmail = map["userEmail"] as? String ?: "",
                userName = map["userName"] as? String ?: "",
                hotelId = map["hotelId"] as? String ?: "",
                hotelName = map["hotelName"] as? String ?: "",
                checkInDate = (map["checkInDate"] as? Number)?.toLong() ?: 0L,
                checkOutDate = (map["checkOutDate"] as? Number)?.toLong() ?: 0L,
                numberOfGuests = (map["numberOfGuests"] as? Number)?.toInt() ?: 1,
                numberOfRooms = (map["numberOfRooms"] as? Number)?.toInt() ?: 1,
                totalAmount = (map["totalAmount"] as? Number)?.toDouble() ?: 0.0,
                currency = map["currency"] as? String ?: "USD",
                bookingStatus = BookingStatus.valueOf(map["bookingStatus"] as? String ?: BookingStatus.PENDING.name),
                paymentStatus = PaymentStatus.valueOf(map["paymentStatus"] as? String ?: PaymentStatus.PENDING.name),
                specialRequests = map["specialRequests"] as? String ?: "",
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }
    }
}

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED,
    NO_SHOW
}

enum class PaymentStatus {
    PENDING,
    PAID,
    FAILED,
    REFUNDED
} 