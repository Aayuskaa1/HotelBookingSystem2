# 📱 Android Project Commands Guide

## ❌ **Don't Use These (Node.js/npm commands):**
```bash
npm install
npm start
npm run build
npm test
```

## ✅ **Use These Instead (Android/Gradle commands):**

### **🔨 Building the Project:**
```bash
# Build debug version
./gradlew assembleDebug

# Clean and rebuild
./gradlew clean assembleDebug

# Build release version
./gradlew assembleRelease
```

### **📱 Installing on Device:**
```bash
# Install debug version on connected device/emulator
./gradlew installDebug

# Install release version
./gradlew installRelease
```

### **🧪 Testing:**
```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Run all tests
./gradlew check
```

### **🔍 Debugging:**
```bash
# Show all available tasks
./gradlew tasks

# Show project dependencies
./gradlew dependencies

# Show build info
./gradlew properties
```

### **🧹 Cleaning:**
```bash
# Clean build files
./gradlew clean

# Clean and rebuild
./gradlew clean build
```

## 🎯 **Quick Commands for Your Hotel Booking App:**

### **1. Build and Install:**
```bash
./gradlew clean assembleDebug installDebug
```

### **2. Just Build (for testing):**
```bash
./gradlew assembleDebug
```

### **3. Check for Errors:**
```bash
./gradlew build --stacktrace
```

## 📋 **Project Structure (Android vs Node.js):**

| Android Project | Node.js Project |
|-----------------|-----------------|
| `build.gradle.kts` | `package.json` |
| `gradlew` | `npm` |
| `app/` directory | `src/` directory |
| `AndroidManifest.xml` | `index.js` |
| Gradle dependencies | npm dependencies |

## 🚀 **Next Steps:**

1. **Build the app**: `./gradlew assembleDebug`
2. **Install on device**: `./gradlew installDebug`
3. **Test Firebase connection**: Use the test button in the app
4. **Check logs**: Use Android Studio Logcat

## 💡 **Pro Tips:**

- **Always use `./gradlew`** instead of `gradle` (uses the project's Gradle wrapper)
- **Clean before major changes**: `./gradlew clean`
- **Use `--stacktrace`** for detailed error information
- **Check `gradle.properties`** for project configuration

---

**Remember**: This is an Android project, so use Gradle commands, not npm commands! 🎯 