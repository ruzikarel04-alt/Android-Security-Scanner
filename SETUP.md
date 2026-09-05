# Setup & Installation Guide

## Quick Start (5 minutes)

### Prerequisites
- Android Studio 4.0+ or Sketchware Pro
- JDK 8 or higher
- Android SDK 21+ (Android 5.0 Lollipop minimum)
- 500MB disk space

### Method 1: Android Studio

1. **Clone Repository**
```bash
git clone https://github.com/ruzikarel04-alt/Android-Security-Scanner.git
cd Android-Security-Scanner
```

2. **Open in Android Studio**
   - File → Open → Select project folder
   - Let Gradle sync complete

3. **Grant Permissions**
   - Review `AndroidManifest.xml`
   - Permissions auto-requested on Android 6+ first run

4. **Build & Run**
```bash
./gradlew assembleDebug        # Build debug APK
./gradlew installDebug         # Install on device/emulator
```

5. **APK Location**
   - `app/build/outputs/apk/debug/app-debug.apk`

### Method 2: Sketchware Pro

1. **Create New Project**
   - Project Name: "Security Scanner"
   - Package: com.security.scanner
   - Min SDK: 21 (Android 5.0)

2. **Import MainActivity.java**
   - Copy `MainActivity.java` content
   - Paste into Sketchware Java Editor
   - Auto-format code

3. **Add Permissions**
   - AndroidManifest.xml → Permissions tab
   - Check: READ_EXTERNAL_STORAGE, INTERNET, QUERY_ALL_PACKAGES, GET_TASKS, ACCESS_NETWORK_STATE

4. **Add Dependencies**
   - Add AppCompat library (v1.6.1)
   - Add ConstraintLayout (2.1.4)

5. **Build APK**
   - Menu → Build APK
   - Wait for compilation
   - APK saved to `/storage/emulated/0/Sketchware/apk/`

### Method 3: Direct APK Installation

If you have pre-built APK:

```bash
adb install Android-Security-Scanner-v1.0.apk
```

## Android Version Compatibility

| Android Version | Minimum API | Status | Notes |
|-----------------|-------------|--------|-------|
| 5.0 Lollipop | 21 | ✅ Full | Full app enumeration |
| 6.0 Marshmallow | 23 | ✅ Full | Runtime permissions |
| 7.0 Nougat | 24 | ✅ Full | File access scoped |
| 8.0 Oreo | 26 | ✅ Full | Background limits |
| 9.0 Pie | 28 | ✅ Full | Package visibility |
| 10 Q | 29 | ✅ Full | Scoped storage |
| 11 R | 30 | ✅ Full | Full compatibility |
| 12 S | 31 | ⚠️ Limited | QUERY_ALL_PACKAGES required |
| 13 T | 33 | ⚠️ Limited | Stricter permissions |
| 14 U | 34 | ⚠️ Limited | Beta support |

## Permission Requests on First Run

**Android 6.0+** will prompt for runtime permissions:

```
┌─────────────────────────────────┐
│ Android Security Scanner        │
├─────────────────────────────────┤
│ Allow access to:                │
│ ☑ Photos, Media & Files        │
│ ☑ Device Calendar              │
│ ☑ Location                     │
│ ☑ Phone Calls                  │
│ ☑ Messages                     │
│ ☑ Contacts                     │
│ ☑ Camera                       │
│ ☑ Microphone                   │
├─────────────────────────────────┤
│ [ALLOW]           [DENY]        │
└─────────────────────────────────┘
```

**Grant all permissions** for full scanning capability. Denying permissions will limit detection accuracy.

## Post-Installation Setup

### 1. Initial Configuration
- Launch app
- Allow permission requests
- App is ready to scan

### 2. First Scan
- Tap "▶ SKENOVAT" button
- Wait 3-5 seconds for full system scan
- Review results

### 3. Threat Review
- 🔴 Critical: Requires immediate review
- 🟡 Medium: Review when convenient
- 🟢 Safe: No action needed

## Updating the App

### Manual Update
1. Download latest APK from GitHub Releases
2. Uninstall current version
3. Install new APK
4. Existing scan data is preserved

### In-App Updates (Future)
Currently not implemented. Check GitHub releases regularly.

## Troubleshooting

### Issue: "Permission Denied" on startup

**Solution:**
```
Settings → Apps → Security Scanner → Permissions
→ Enable: Files, Location, Phone
```

### Issue: Blank app screen

**Solution:**
- Force close: `adb shell am force-stop com.security.scanner`
- Clear app cache: Settings → Apps → Security Scanner → Storage → Clear Cache
- Reinstall app

### Issue: Scan doesn't complete

**Solution:**
- Ensure device has 20MB+ free RAM
- Close other apps
- Disable Low Power Mode
- Try scanning again

### Issue: "Unknown Application" appears for legit apps

**Solution:**
- This is normal for third-party apps not in whitelist
- Verify app authenticity in Play Store
- Cross-reference with manual app inspection

### Issue: Network check fails

**Solution:**
- Ensure internet permission is granted
- Check device WiFi/cellular connection
- Malware domain checks require internet access
- WiFi-only devices: Network scan skipped automatically

## Device Requirements

### Minimum Specifications
- **Device:** Android phone or tablet
- **OS:** Android 5.0+ (API 21)
- **RAM:** 256MB minimum, 512MB+ recommended
- **Storage:** 5MB free space
- **Processor:** ARMv7 or higher

### Recommended Specifications
- **Device:** Modern smartphone
- **OS:** Android 10 or higher
- **RAM:** 2GB or more
- **Storage:** 50MB free space
- **Processor:** ARMv8 (64-bit)

## Uninstallation

### Option 1: System Settings
```
Settings → Apps & Notifications 
→ App info → Security Scanner 
→ Uninstall
```

### Option 2: ADB Command
```bash
adb uninstall com.security.scanner
```

### Option 3: Sketchware
- Sketchware → Projects → Security Scanner → Delete

## Performance Optimization

### Speed Up Scanning
1. Close unnecessary apps before scanning
2. Disable WiFi during local file scans
3. Disable antivirus apps temporarily (they slow scanning)
4. Ensure device has 20%+ battery

### Reduce Memory Usage
1. Restart device before scan
2. Clear app cache regularly
3. Update system to latest version
4. Remove rarely used apps

## FAQ

**Q: Is the app safe?**  
A: Yes. Uses only read-only Android APIs. No system modification.

**Q: Does it require root?**  
A: No. Works without root/admin privileges.

**Q: Can it delete files?**  
A: Only with explicit user confirmation in Settings app.

**Q: Can it disable apps?**  
A: No. User must disable manually from Settings.

**Q: Does it collect data?**  
A: No. All scanning happens locally. No data transmission.

**Q: Can I share scan results?**  
A: Yes. Screenshot results or export via Settings app.

**Q: How often should I scan?**  
A: Weekly recommended. Daily for security-sensitive users.

**Q: Does battery drain?**  
A: Minimal impact. 3-5 second scan uses <1% battery.

## Support & Feedback

**Issues:**  
→ [GitHub Issues](https://github.com/ruzikarel04-alt/Android-Security-Scanner/issues)

**Questions:**  
→ Check README.md or code comments

**Contributions:**  
→ Pull requests welcome!

---

**Last Updated:** 2026-09-05  
**Version:** 1.0  
**Maintained By:** Security Scanner Team
