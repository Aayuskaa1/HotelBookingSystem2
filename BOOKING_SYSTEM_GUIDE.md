# Booking System Guide

## How Bookings Work

### Creating a Booking

1. **Search for Hotels**: Navigate to the Hotel Search screen and find a hotel you want to book
2. **Select Hotel**: Tap on a hotel card to view details and book
3. **Fill Booking Form**: 
   - Enter check-in and check-out dates
   - Specify number of guests
   - Provide guest information (pre-filled with your profile data)
   - Add any special requests
4. **Review & Confirm**: Check the total price and tap "Book Now"
5. **Success**: You'll see a success message confirming your booking

### Booking Appears in "My Bookings"

After creating a booking, it will automatically appear in your "My Bookings" section because:

- **User Association**: Each booking is linked to your user account via your user ID
- **Automatic Filtering**: The "My Bookings" screen only shows bookings belonging to the current user
- **Real-time Updates**: The booking list refreshes automatically when new bookings are added

## "My Bookings" Features

### View Your Bookings
- **All Bookings**: See all your past and current bookings
- **Booking Status**: Each booking shows its current status:
  - 🟡 **PENDING**: Awaiting confirmation
  - 🟢 **CONFIRMED**: Booking confirmed and active
  - 🔴 **CANCELLED**: Booking cancelled
  - 🔵 **COMPLETED**: Stay completed
  - 🔴 **NO_SHOW**: Didn't show up

### Search & Filter
- **Search**: Find bookings by hotel name or guest name
- **Status Filter**: Filter by booking status (Confirmed, Pending, etc.)
- **Statistics**: View total bookings and active bookings count

### Booking Actions
- **View Details**: Tap on any booking to see full details
- **Cancel Booking**: Cancel confirmed or pending bookings
- **Payment Status**: See payment status for each booking

## Booking Information

Each booking includes:
- **Hotel Name & Location**
- **Check-in & Check-out Dates**
- **Number of Guests**
- **Total Amount**
- **Booking Status**
- **Payment Status**
- **Special Requests** (if any)

## Technical Implementation

### User-Specific Bookings
- **User ID Association**: Bookings are created with the current user's ID
- **Email Matching**: Bookings are also matched by user email for compatibility
- **Filtered Display**: Only user's own bookings are shown in "My Bookings"

### Data Flow
1. **Booking Creation**: User fills form → Booking created with user ID
2. **Storage**: Booking saved to repository with user association
3. **Display**: "My Bookings" filters and shows only user's bookings
4. **Updates**: Real-time updates when booking status changes

### Repository Pattern
- **MockBookingRepository**: Handles booking data operations
- **User Filtering**: `getBookingsByUser(userId)` function
- **Status Management**: Update booking and payment status
- **Search & Filter**: Find bookings by various criteria

## Troubleshooting

### Booking Not Appearing in "My Bookings"?
1. **Check User Login**: Ensure you're logged in with the correct account
2. **Refresh Screen**: Pull to refresh or navigate back and forth
3. **Check Success Message**: Verify the booking was created successfully
4. **User ID Match**: Ensure the booking was created with your user ID

### Can't Create Booking?
1. **Fill Required Fields**: All marked fields (*) must be completed
2. **Valid Dates**: Check-in must be before check-out
3. **Valid Guest Count**: Number of guests must be at least 1
4. **Network Connection**: Ensure stable internet connection

### Booking Status Issues?
1. **Pending Status**: New bookings start as "PENDING"
2. **Admin Confirmation**: Admins can change booking status
3. **Payment Status**: Separate from booking status
4. **Cancellation**: Can cancel confirmed or pending bookings

## Best Practices

### For Users
- **Complete Profile**: Fill in your profile information for faster booking
- **Check Dates**: Verify check-in/check-out dates carefully
- **Special Requests**: Add any special requirements in the notes
- **Review Details**: Double-check all information before confirming

### For Admins
- **Monitor Bookings**: Check new bookings regularly
- **Update Status**: Confirm or cancel bookings promptly
- **User Support**: Help users with booking issues
- **Data Management**: Maintain booking data integrity

## Future Enhancements

- **Booking History**: Detailed booking timeline
- **Modify Bookings**: Allow users to change booking details
- **Payment Integration**: Real payment processing
- **Email Notifications**: Booking confirmations and updates
- **Calendar Integration**: Sync with user's calendar
- **Review System**: Rate and review completed stays 