# Network Error Troubleshooting Guide

## 🔧 **Fixed Issues:**

### ✅ **1. Added Internet Permissions**
```xml
<!-- Added to AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### ✅ **2. Firebase Initialization**
```kotlin
// Added to MainActivity.kt
FirebaseApp.initializeApp(this)
```

### ✅ **3. Enhanced Error Handling**
- Better error messages for different network issues
- Detailed logging for debugging
- Timeout and connection failure detection

## 🚨 **Common Network Error Solutions:**

### **1. "Network error. Please check your connection"**

#### **Check Your Device:**
- ✅ **WiFi/Data**: Ensure you have internet connection
- ✅ **Airplane Mode**: Turn off airplane mode
- ✅ **Network Settings**: Check if WiFi/data is enabled

#### **Check Firebase Console:**
1. **Go to Firebase Console**: https://console.firebase.google.com/
2. **Select your project**: `hotelbookingsystem-8d382`
3. **Check Authentication**: 
   - Go to Authentication → Sign-in method
   - Ensure "Email/Password" is enabled
4. **Check Project Status**: Make sure project is active

### **2. "Connection timeout. Please try again"**

#### **Possible Causes:**
- Slow internet connection
- Firebase service temporarily unavailable
- Device network issues

#### **Solutions:**
- ✅ **Wait and retry**: Try again in a few minutes
- ✅ **Check internet speed**: Use a speed test
- ✅ **Switch networks**: Try different WiFi or mobile data

### **3. "Failed to connect to server"**

#### **Firebase Configuration Check:**
1. **Verify google-services.json**:
   ```json
   {
     "project_id": "hotelbookingsystem-8d382",
     "api_key": "AIzaSyBZJPYMshXIJ50YjjADhh6ZsRUaGGgcijY"
   }
   ```

2. **Check Package Name**:
   - Ensure package name matches: `com.example.hotelbookingsystem`
   - Check in `app/build.gradle.kts` and `google-services.json`

### **4. "No account found with this email"**

#### **Create Admin User in Firebase:**
1. **Firebase Console** → **Authentication** → **Users**
2. **Click "Add User"**
3. **Enter admin credentials**:
   - Email: `admin@hotelbooking.com`
   - Password: `admin123456`
4. **Click "Add user"**

## 🔍 **Debug Steps:**

### **1. Check Logs in Android Studio:**
1. **Open Logcat** in Android Studio
2. **Filter by tag**: `AuthViewModel`
3. **Look for these logs**:
   ```
   AuthViewModel: User logged in: Email: admin@hotelbooking.com, Role: ADMIN, isAdmin: true
   AuthViewModel: Login error: [specific error message]
   ```

### **2. Test Network Connection:**
```bash
# Test internet connectivity
ping google.com

# Test Firebase connectivity
ping firebase.google.com
```

### **3. Verify Firebase Project:**
1. **Project ID**: `hotelbookingsystem-8d382`
2. **API Key**: `AIzaSyBZJPYMshXIJ50YjjADhh6ZsRUaGGgcijY`
3. **Authentication**: Email/Password enabled

## 📱 **Device-Specific Solutions:**

### **Android Emulator:**
- ✅ **Check emulator internet**: Settings → Network & Internet
- ✅ **Restart emulator**: Sometimes network gets stuck
- ✅ **Use host network**: Emulator settings → Network

### **Physical Device:**
- ✅ **Check mobile data**: Ensure data is enabled
- ✅ **Check WiFi**: Connect to stable WiFi network
- ✅ **Restart device**: Sometimes clears network issues

## 🛠 **Advanced Troubleshooting:**

### **1. Clear App Data:**
1. **Settings** → **Apps** → **Hotel Booking System**
2. **Storage** → **Clear Data**
3. **Restart app**

### **2. Reinstall App:**
1. **Uninstall app**
2. **Clean build**: `./gradlew clean`
3. **Rebuild**: `./gradlew assembleDebug`
4. **Reinstall app**

### **3. Check Firebase Rules:**
1. **Firebase Console** → **Authentication** → **Rules**
2. **Ensure rules allow authentication**:
   ```javascript
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /{document=**} {
         allow read, write: if request.auth != null;
       }
     }
   }
   ```

## ✅ **Success Checklist:**

### **Before Testing:**
- ✅ Internet permissions added to AndroidManifest.xml
- ✅ Firebase initialized in MainActivity
- ✅ Admin user created in Firebase Console
- ✅ Device has internet connection
- ✅ App is freshly installed

### **Expected Behavior:**
- ✅ Login screen loads without errors
- ✅ Admin credentials accepted
- ✅ Redirected to Admin Dashboard
- ✅ No network error messages

## 🆘 **Still Having Issues?**

### **Contact Support:**
1. **Check logs** in Android Studio Logcat
2. **Note exact error message**
3. **Include device/emulator info**
4. **Share Firebase project status**

### **Alternative Testing:**
1. **Try different admin email**: `administrator@hotelbooking.com`
2. **Test on different device/emulator**
3. **Use mobile data instead of WiFi**
4. **Wait 5-10 minutes and retry**

---

**Note**: Most network errors are resolved by adding internet permissions and proper Firebase initialization. If issues persist, check your internet connection and Firebase project status. 