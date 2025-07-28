# 🔍 Error Diagnosis Guide

## 🚨 **Step-by-Step Error Diagnosis:**

### **1. Install the Updated App**
```bash
# Clean and rebuild
./gradlew clean
./gradlew assembleDebug
```

### **2. Check Android Studio Logcat**
1. **Open Android Studio**
2. **Go to View → Tool Windows → Logcat**
3. **Filter by tags**: `FirebaseTest`, `AuthViewModel`, `MainActivity`
4. **Look for these specific logs**:

#### **✅ Good Logs (Firebase Working):**
```
MainActivity: Firebase Configuration:
- Project ID: hotelbookingsystem-8d382
- API Key: AIzaSyBZJP...
FirebaseTest: Firebase Auth instance created successfully
```

#### **❌ Error Logs (Firebase Issues):**
```
FirebaseTest: Firebase connection failed: [error message]
AuthViewModel: Login error: [specific error]
```

### **3. Use the Test Button**
1. **Launch the app**
2. **Go to Login screen**
3. **Click "Test Firebase Connection" button**
4. **Check the result message**:
   - **Green text** = Firebase working ✅
   - **Red text** = Firebase error ❌

### **4. Common Error Messages & Solutions:**

| Error Message | Cause | Solution |
|---------------|-------|----------|
| **"No account found with this email"** | Admin user not created | Create admin in Firebase Console |
| **"Network error - Check internet connection"** | No internet permission | Already fixed in AndroidManifest.xml |
| **"Invalid Firebase API key"** | Wrong API key | Check google-services.json |
| **"Firebase project not found"** | Wrong project ID | Verify project ID in Firebase Console |
| **"Failed to connect to Firebase"** | Firebase not initialized | Already fixed in MainActivity |

## 🔧 **Firebase Console Setup:**

### **1. Enable Authentication:**
1. **Go to**: https://console.firebase.google.com/
2. **Select project**: `hotelbookingsystem-8d382`
3. **Click**: Authentication → Sign-in method
4. **Enable**: Email/Password provider
5. **Save changes**

### **2. Create Admin User:**
1. **Go to**: Authentication → Users
2. **Click**: "Add User"
3. **Enter**:
   - Email: `admin@hotelbooking.com`
   - Password: `admin123456`
4. **Click**: "Add user"

### **3. Verify Project Settings:**
1. **Go to**: Project Settings (gear icon)
2. **Check**:
   - Project ID: `hotelbookingsystem-8d382`
   - API Key: `AIzaSyBZJPYMshXIJ50YjjADhh6ZsRUaGGgcijY`
   - App ID: `1:949928374160:android:571989a7ba2ec89dc8cd15`

## 📱 **Device Testing:**

### **Android Emulator:**
1. **Check internet**: Settings → Network & Internet
2. **Restart emulator** if needed
3. **Clear app data**: Settings → Apps → Hotel Booking System → Clear Data

### **Physical Device:**
1. **Enable WiFi/mobile data**
2. **Check airplane mode is OFF**
3. **Try different network** if possible

## 🛠 **Advanced Debugging:**

### **1. Check Firebase Rules:**
```javascript
// In Firebase Console → Authentication → Rules
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

### **2. Verify google-services.json:**
```json
{
  "project_info": {
    "project_id": "hotelbookingsystem-8d382"
  },
  "client": [
    {
      "api_key": [
        {
          "current_key": "AIzaSyBZJPYMshXIJ50YjjADhh6ZsRUaGGgcijY"
        }
      ]
    }
  ]
}
```

### **3. Check Package Name:**
- **build.gradle.kts**: `applicationId = "com.example.hotelbookingsystem"`
- **google-services.json**: Should match this package name

## 🎯 **Quick Fix Checklist:**

### **Before Testing:**
- ✅ **App rebuilt** with latest changes
- ✅ **Firebase Authentication enabled** in console
- ✅ **Admin user created** in Firebase
- ✅ **Internet connection** available
- ✅ **App permissions** granted

### **Test Steps:**
1. **Launch app** → Should show splash screen
2. **Login screen** → Should appear after splash
3. **Click "Test Firebase Connection"** → Should show result
4. **Try admin login** → Should redirect to admin dashboard

## 🆘 **Still Getting Errors?**

### **Please Share:**
1. **Exact error message** from the app
2. **Logcat output** from Android Studio
3. **Test button result** (green/red text)
4. **Device/emulator** you're using

### **Common Issues:**
- **Firebase project inactive** → Check Firebase Console
- **Wrong credentials** → Verify admin email/password
- **Network timeout** → Try again in a few minutes
- **App cache issues** → Clear app data and reinstall

---

**Note**: The test button will help identify the exact Firebase issue. Click it and share the result message for specific troubleshooting. 