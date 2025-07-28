# Admin Dashboard Setup Guide

## 🔐 Setting Up Admin Credentials

### 1. Firebase Console Setup

1. **Go to Firebase Console**: https://console.firebase.google.com/
2. **Select your project**: HotelBookingSystem2
3. **Navigate to Authentication**: In the left sidebar, click "Authentication"
4. **Go to Users tab**: Click on the "Users" tab

### 2. Create Admin User

#### Option A: Create Admin User in Firebase Console
1. Click **"Add User"** button
2. Enter admin email: `admin@hotelbooking.com` (or any email with "admin" in it)
3. Enter password: `admin123456` (or your preferred password)
4. Click **"Add user"**

#### Option B: Use Email Pattern
Any email containing these keywords will be recognized as admin:
- `admin@...`
- `administrator@...`
- `manager@...`
- `superuser@...`
- `admin.anything@...`
- `anything@admin.com`

### 3. Pre-configured Admin Emails

The system automatically recognizes these admin emails:
- `admin@hotelbooking.com`
- `administrator@hotelbooking.com`
- `manager@hotelbooking.com`
- `admin@gmail.com`
- `administrator@gmail.com`

## 🧪 Testing Admin Access

### 1. Using the App
1. **Launch the app**
2. **Go to Login screen** (after splash)
3. **Enter admin credentials**:
   - Email: `admin@hotelbooking.com`
   - Password: `admin123456`
4. **Click "Sign In"**
5. **You should be redirected to Admin Dashboard**

### 2. Expected Behavior
- ✅ **Admin users** → **Admin Dashboard**
- ✅ **Regular users** → **User Dashboard**
- ✅ **Role detection** based on email patterns

## 🔍 Debugging

### Check Logs
The app logs role detection information. Look for:
```
AuthViewModel: User logged in: Email: admin@hotelbooking.com, Role: ADMIN, isAdmin: true
```

### Role Detection Logic
```kotlin
// Admin detection patterns:
email.contains("admin")           // admin@example.com
email.contains("administrator")   // administrator@example.com
email.contains("manager")         // manager@example.com
email.contains("superuser")       // superuser@example.com
email.startsWith("admin.")        // admin.user@example.com
email.endsWith("@admin.com")      // user@admin.com
```

## 🎯 Admin Dashboard Features

### Statistics Overview
- Total Hotels: 12
- Active Bookings: 45
- Total Users: 156
- Revenue: $12.5K

### Quick Actions
- **Manage Hotels**: Add, edit, remove hotels
- **Manage Bookings**: View and manage all bookings
- **Manage Users**: View and manage user accounts
- **View Reports**: Generate system reports

### Professional Interface
- Clean, card-based design
- Admin-specific branding
- Comprehensive management tools

## 🚨 Troubleshooting

### If Admin Dashboard Doesn't Show:

1. **Check Email Pattern**: Make sure your admin email contains "admin", "administrator", or "manager"
2. **Check Logs**: Look for role detection logs in Android Studio
3. **Verify Firebase**: Ensure the user exists in Firebase Authentication
4. **Clear App Data**: Try clearing app data and logging in again

### Common Issues:

| Issue | Solution |
|-------|----------|
| Still showing User Dashboard | Check email contains admin keywords |
| Login fails | Verify credentials in Firebase Console |
| No role detection logs | Check Android Studio Logcat |

## 📝 Customization

### Add More Admin Emails
Edit `app/src/main/java/com/example/hotelbookingsystem/utils/UserRole.kt`:

```kotlin
private val adminEmails = setOf(
    "admin@hotelbooking.com",
    "your-admin@yourdomain.com",  // Add your admin emails here
    "manager@yourcompany.com"
)
```

### Change Role Detection Logic
Modify the `getUserRole()` function to match your requirements.

## ✅ Success Indicators

When admin access is working correctly:
- ✅ Login with admin credentials
- ✅ Redirected to Admin Dashboard
- ✅ See admin-specific statistics
- ✅ Access to management tools
- ✅ Professional admin interface

---

**Note**: The role detection is based on email patterns. Make sure your admin users have appropriate email addresses that match the detection logic. 