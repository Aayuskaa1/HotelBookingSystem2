# Hotel Search Guide

## Problem: No Hotels Showing in Search

If you're not seeing any hotels in the hotel search screen, this is likely because:

1. **Firebase Firestore is empty** - No hotels have been added to the database yet
2. **Network connectivity issues** - Unable to connect to Firebase
3. **Firebase configuration issues** - Firebase not properly configured

## Solutions

### Solution 1: Load Mock Data (Recommended for Testing)

1. Navigate to the **Hotel Search** screen
2. Tap the **Data Usage** icon (📊) in the top-right corner
3. This will load 3 sample hotels from mock data:
   - Grand Plaza Hotel (New York)
   - Seaside Resort (Miami) 
   - Mountain Lodge (Denver)

### Solution 2: Populate Firebase with Sample Data

1. Navigate to the **Hotel Search** screen
2. Tap the **Cloud Upload** icon (☁️) in the top-right corner
3. This will add the sample hotels to your Firebase Firestore database
4. The hotels will then be available for all users

### Solution 3: Add Hotels Manually (Admin Only)

1. Login as an admin user
2. Navigate to **Hotel Management**
3. Tap **Add Hotel** to create new hotels
4. Fill in the hotel details and save

## Sample Hotels Available

The app includes 3 sample hotels in mock data:

| Hotel Name | Location | Price/Night | Rating |
|------------|----------|-------------|---------|
| Grand Plaza Hotel | New York, USA | $299.99 | 4.8 ⭐ |
| Seaside Resort | Miami, USA | $199.99 | 4.6 ⭐ |
| Mountain Lodge | Denver, USA | $149.99 | 4.4 ⭐ |

## Features

- **Search by name, city, or country**
- **Filter by city**
- **Filter by price range**
- **Real-time search results**
- **Hotel details and booking**

## Troubleshooting

### Still No Hotels After Loading Mock Data?

1. Check the console logs for error messages
2. Verify Firebase configuration in `google-services.json`
3. Ensure internet connectivity
4. Try refreshing the screen

### Firebase Connection Issues?

1. Verify Firebase project is set up correctly
2. Check Firebase console for any errors
3. Ensure Firestore rules allow read access
4. Verify API keys are correct

## Technical Details

- **Primary Data Source**: Firebase Firestore
- **Fallback Data Source**: Mock Data (for testing)
- **Repository Pattern**: FirebaseHotelRepository + MockHotelRepository
- **State Management**: Kotlin Flow + ViewModel
- **UI Framework**: Jetpack Compose

## Development Notes

The app automatically falls back to mock data when:
- Firebase returns an empty list
- Firebase connection fails
- Any other Firebase-related errors

This ensures users always have access to sample data for testing the app functionality. 