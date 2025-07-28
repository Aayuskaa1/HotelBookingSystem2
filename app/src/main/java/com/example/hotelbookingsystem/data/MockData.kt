package com.example.hotelbookingsystem.data

import com.example.hotelbookingsystem.model.*

object MockData {
    
    // Mock Hotels
    val mockHotels = listOf(
        Hotel(
            id = "hotel_1",
            name = "Grand Plaza Hotel",
            description = "Luxury 5-star hotel in the heart of the city",
            address = "123 Main Street, Downtown",
            city = "New York",
            country = "USA",
            rating = 4.8f,
            pricePerNight = 299.99,
            currency = "USD",
            amenities = listOf("WiFi", "Pool", "Spa", "Restaurant", "Gym"),
            images = listOf("https://example.com/hotel1.jpg"),
            isActive = true,
            createdAt = System.currentTimeMillis() - 86400000 * 30, // 30 days ago
            updatedAt = System.currentTimeMillis()
        ),
        Hotel(
            id = "hotel_2",
            name = "Seaside Resort",
            description = "Beautiful beachfront resort with ocean views",
            address = "456 Beach Road, Coastal Area",
            city = "Miami",
            country = "USA",
            rating = 4.6f,
            pricePerNight = 199.99,
            currency = "USD",
            amenities = listOf("WiFi", "Beach Access", "Pool", "Restaurant", "Bar"),
            images = listOf("https://example.com/hotel2.jpg"),
            isActive = true,
            createdAt = System.currentTimeMillis() - 86400000 * 25,
            updatedAt = System.currentTimeMillis()
        ),
        Hotel(
            id = "hotel_3",
            name = "Mountain Lodge",
            description = "Cozy mountain retreat with scenic views",
            address = "789 Mountain Trail, Alpine Valley",
            city = "Denver",
            country = "USA",
            rating = 4.4f,
            pricePerNight = 149.99,
            currency = "USD",
            amenities = listOf("WiFi", "Fireplace", "Hiking Trails", "Restaurant"),
            images = listOf("https://example.com/hotel3.jpg"),
            isActive = true,
            createdAt = System.currentTimeMillis() - 86400000 * 20,
            updatedAt = System.currentTimeMillis()
        )
    )
    
    // Mock Users
    val mockUsers = listOf(
        User(
            id = "user_1",
            email = "john.doe@example.com",
            displayName = "John Doe",
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "+1-555-0123",
            dateOfBirth = 631152000000, // 1990-01-01
            address = "123 User Street",
            city = "New York",
            country = "USA",
            userRole = UserRole.USER,
            isActive = true,
            isEmailVerified = true,
            totalBookings = 3,
            totalSpent = 899.97,
            createdAt = System.currentTimeMillis() - 86400000 * 60,
            updatedAt = System.currentTimeMillis()
        ),
        User(
            id = "user_2",
            email = "jane.smith@example.com",
            displayName = "Jane Smith",
            firstName = "Jane",
            lastName = "Smith",
            phoneNumber = "+1-555-0456",
            dateOfBirth = 662688000000, // 1991-01-01
            address = "456 User Avenue",
            city = "Los Angeles",
            country = "USA",
            userRole = UserRole.USER,
            isActive = true,
            isEmailVerified = true,
            totalBookings = 2,
            totalSpent = 399.98,
            createdAt = System.currentTimeMillis() - 86400000 * 45,
            updatedAt = System.currentTimeMillis()
        ),
        User(
            id = "user_3",
            email = "mike.wilson@example.com",
            displayName = "Mike Wilson",
            firstName = "Mike",
            lastName = "Wilson",
            phoneNumber = "+1-555-0789",
            dateOfBirth = 694224000000, // 1992-01-01
            address = "789 User Boulevard",
            city = "Chicago",
            country = "USA",
            userRole = UserRole.USER,
            isActive = true,
            isEmailVerified = false,
            totalBookings = 1,
            totalSpent = 199.99,
            createdAt = System.currentTimeMillis() - 86400000 * 30,
            updatedAt = System.currentTimeMillis()
        )
    )
    
    // Mock Bookings
    val mockBookings = listOf(
        Booking(
            id = "booking_1",
            userId = "user_1",
            userEmail = "john.doe@example.com",
            userName = "John Doe",
            hotelId = "hotel_1",
            hotelName = "Grand Plaza Hotel",
            checkInDate = System.currentTimeMillis() + 86400000 * 7, // 7 days from now
            checkOutDate = System.currentTimeMillis() + 86400000 * 10, // 10 days from now
            numberOfGuests = 2,
            numberOfRooms = 1,
            totalAmount = 899.97,
            currency = "USD",
            bookingStatus = BookingStatus.CONFIRMED,
            paymentStatus = PaymentStatus.PAID,
            specialRequests = "Late check-in preferred",
            createdAt = System.currentTimeMillis() - 86400000 * 5,
            updatedAt = System.currentTimeMillis()
        ),
        Booking(
            id = "booking_2",
            userId = "user_2",
            userEmail = "jane.smith@example.com",
            userName = "Jane Smith",
            hotelId = "hotel_2",
            hotelName = "Seaside Resort",
            checkInDate = System.currentTimeMillis() + 86400000 * 14,
            checkOutDate = System.currentTimeMillis() + 86400000 * 16,
            numberOfGuests = 1,
            numberOfRooms = 1,
            totalAmount = 399.98,
            currency = "USD",
            bookingStatus = BookingStatus.CONFIRMED,
            paymentStatus = PaymentStatus.PAID,
            specialRequests = "Ocean view room",
            createdAt = System.currentTimeMillis() - 86400000 * 3,
            updatedAt = System.currentTimeMillis()
        ),
        Booking(
            id = "booking_3",
            userId = "user_3",
            userEmail = "mike.wilson@example.com",
            userName = "Mike Wilson",
            hotelId = "hotel_3",
            hotelName = "Mountain Lodge",
            checkInDate = System.currentTimeMillis() + 86400000 * 21,
            checkOutDate = System.currentTimeMillis() + 86400000 * 23,
            numberOfGuests = 3,
            numberOfRooms = 2,
            totalAmount = 599.97,
            currency = "USD",
            bookingStatus = BookingStatus.PENDING,
            paymentStatus = PaymentStatus.PENDING,
            specialRequests = "Family room with mountain view",
            createdAt = System.currentTimeMillis() - 86400000 * 1,
            updatedAt = System.currentTimeMillis()
        ),
        Booking(
            id = "booking_4",
            userId = "user_1",
            userEmail = "john.doe@example.com",
            userName = "John Doe",
            hotelId = "hotel_2",
            hotelName = "Seaside Resort",
            checkInDate = System.currentTimeMillis() - 86400000 * 30, // Past booking
            checkOutDate = System.currentTimeMillis() - 86400000 * 28,
            numberOfGuests = 2,
            numberOfRooms = 1,
            totalAmount = 399.98,
            currency = "USD",
            bookingStatus = BookingStatus.COMPLETED,
            paymentStatus = PaymentStatus.PAID,
            specialRequests = "Early check-in",
            createdAt = System.currentTimeMillis() - 86400000 * 35,
            updatedAt = System.currentTimeMillis()
        )
    )
} 