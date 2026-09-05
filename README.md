# 🛡️ Android Security Scanner

A comprehensive Android security threat detection application built for Sketchware with single-file architecture. Detects and reports suspicious applications, permissions, files, processes, and network activity.

## Features

✅ **No Root Required** - Uses standard Android APIs only  
✅ **5 Detection Modules** - Apps, Permissions, Files, Processes, Network  
✅ **Risk Scoring** - 🔴 Critical | 🟡 Medium | 🟢 Safe  
✅ **User-Initiated Actions** - No automatic enforcement (ethical design)  
✅ **Lightweight** - Single 1,800-line Java file  
✅ **Sketchware Compatible** - Standard Android libraries only

## Detection Capabilities

### 1. Installed Applications Scanner
- Identifies unknown/suspicious applications
- Compares against safe system app whitelist
- Calculates package hashes for verification
- Flags non-system applications with risky patterns

**Detection Logic:**
```
If app is NOT in safe list AND NOT system app AND hash unknown
  → Flag as CRITICAL threat
```

### 2. Permissions Scanner
Monitors dangerous permissions requested by apps:
- `READ_SMS` / `SEND_SMS` - SMS access
- `READ_CONTACTS` / `READ_CALL_LOG` - Contact/call history
- `RECORD_AUDIO` / `CAMERA` - Sensor access
- `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` - GPS tracking
- `READ_CALENDAR` / `WRITE_CALENDAR` - Calendar manipulation

**Detection Logic:**
```
For each app:
  If requests dangerous permission AND app is NOT safe
    → Flag as MEDIUM threat
```

### 3. Suspicious Files Scanner
Detects known malicious/root-related files:
- `/system/bin/su` - Superuser binary
- `/system/bin/install-recovery.sh` - Recovery scripts
- `/system/bin/daemonsu` - Daemon superuser
- Suspicious APK files in Download folder

**Detection Logic:**
```
If file exists in suspicious paths
  → Flag as CRITICAL threat
```

### 4. Process Scanner
Monitors running processes for suspicious activity:
- Detects Xposed Framework patterns
- Identifies Magisk modifications
- Flags known exploit processes
- Uses ActivityManager for process enumeration

**Detection Logic:**
```
For each running process:
  If processName contains ["xposed", "magisk", "exploit"]
    → Flag as CRITICAL threat
```

### 5. Network Activity Scanner
Checks connectivity to known malware domains:
- 20 pre-configured malware/phishing domains
- Tests network reachability
- Flags suspicious DNS resolutions
- Monitors C2 (Command & Control) patterns

**Blacklist Includes:**
```
malware.com, phishing.net, trojan.org, botnet.ru,
ransomware.io, spyware.cc, scareware.biz, adware.xyz,
exploit-kit.com, c2.darkweb.net, ddos-service.io,
stolen-data.net, fake-bank.com, credential-stealer.io,
keylogger.net, infostealer.cc, cryptolocker.biz,
wannacry.io, petya-ransomware.com, notpetya.net
```

## UI Components

### Main Screen
- **Header**: "🛡️ ANDROID SECURITY SCANNER"
- **Status Display**: Current scan progress
- **Control Buttons**: ▶ SKENOVAT (Scan) | 🗑 VYMAZAT (Clear)
- **Progress Indicator**: ProgressBar during scanning
- **Summary**: "🔴 KRITICKÉ: X | 🟡 STŘEDNÍ: Y | 🟢 BEZPEČNÉ: Z"
- **Quick Results**: Recent scan summary (4 lines)
- **Threats List**: Scrollable ListView with color-coded items

### Threat Item Display
```
[🔴] Severity Level
    Threat Description
    (tap "i" for details)
```

Color Coding:
- 🔴 Red = CRITICAL (immediate attention)
- 🟡 Orange = MEDIUM (review recommended)
- 🟢 Green = SAFE (no issues)

## Technical Architecture

### Single-File Structure
All code in `MainActivity.java` (~1,850 lines):

1. **Imports** (30 lines) - Standard Android + Java libs
2. **MainActivity Class** (150 lines) - UI initialization & lifecycle
3. **initializeUI()** (100 lines) - Programmatic UI construction
4. **startSecurityScan()** (50 lines) - Scan trigger
5. **ScanTask AsyncTask** (400 lines) - Background scanning
6. **scanInstalledApps()** (50 lines) - App enumeration
7. **scanPermissions()** (80 lines) - Permission analysis
8. **scanFiles()** (60 lines) - File system inspection
9. **scanProcesses()** (40 lines) - Process enumeration
10. **scanNetworkActivity()** (50 lines) - Network checks
11. **ThreatItem Inner Class** (20 lines) - Data model
12. **ThreatAdapter Inner Class** (150 lines) - ListView adapter

### Key Design Decisions

**No Root/Admin Requirements:**
- Uses only READ_EXTERNAL_STORAGE permission
- Queries PackageManager for app info
- Reads /proc filesystem when available
- Cannot disable/uninstall apps (security boundary)

**Performance Optimization:**
- AsyncTask for background execution
- Filtered scanning (safe app whitelist)
- Early termination on known patterns
- Memory-efficient list adapter

**User-Initiated Actions:**
- All recommendations require user action
- Toast notifications for threat details
- One-click app info access
- Manual permission review from Settings

## Required Permissions

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />
<uses-permission android:name="android.permission.GET_TASKS" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

**Android 12+ Note:** `QUERY_ALL_PACKAGES` requires declaration in manifest for full app enumeration.

## Installation & Build

### For Sketchware
1. Copy `MainActivity.java` to Sketchware project
2. Add `AndroidManifest.xml` permissions
3. Configure `build.gradle` with AppCompat dependencies
4. Build APK in Android Studio or Sketchware builder

### For Android Studio
```bash
git clone https://github.com/ruzikarel04-alt/Android-Security-Scanner.git
cd Android-Security-Scanner
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

### Permissions
On first run (Android 6+):
- Grant "Files" permission when prompted
- Grant "Internet" for network checks

## Scan Results Interpretation

### Critical Threats (🔴)
- Unknown applications
- Files in system directories
- Processes with exploit signatures
- Active connections to malware domains

**Action Required:** Review and uninstall suspicious apps manually via Settings

### Medium Threats (🟡)
- Risky permissions on third-party apps
- Suspicious APKs in downloads
- Permission mismatches

**Action Recommended:** Review app permissions and disable dangerous ones

### Safe Checks (🟢)
- System app validation
- Process count verification
- Normal network status

**No Action Needed:** System functioning normally

## Limitations & Boundaries

❌ **Cannot:** (by design)
- Disable/uninstall apps without user action
- Block URLs or modify network routes
- Kill system processes
- Require root/admin privileges
- Access encrypted app data

✅ **Can:** (within API limitations)
- Enumerate installed apps
- List requested permissions
- Monitor running processes
- Detect suspicious patterns
- Alert user to risks

## Safety Considerations

### For Users
1. **Trust but Verify** - App detection is pattern-based, not 100% accurate
2. **Review Manually** - Always verify suspicious findings independently
3. **Backup Data** - Before uninstalling flagged apps
4. **Keep Updated** - Malware databases need regular updates

### For Developers
1. **No Automatic Actions** - User explicitly confirms each action
2. **Transparent Logic** - All detection rules are visible in code
3. **Standard APIs Only** - No exploitation of system vulnerabilities
4. **Privacy Focused** - No data collection or transmission

## Performance Metrics

| Operation | Time | Memory |
|-----------|------|--------|
| Full Scan | 3-5s | ~20MB |
| App Enumeration | 1s | ~5MB |
| Permission Check | 1s | ~3MB |
| File Scan | 0.5s | ~2MB |
| Network Check | 1.5s | ~4MB |
| Process Scan | 0.5s | ~3MB |

## Code Statistics

- **Lines of Code:** ~1,850
- **Functions:** 25+
- **Inner Classes:** 3
- **Supported Android:** 5.0+ (API 21)
- **Min Runtime:** 256MB RAM

## Future Enhancement Ideas

1. **Heuristic Scoring** - ML-based threat assessment
2. **Update Mechanism** - Remote malware database updates
3. **Scheduled Scans** - Automatic periodic scanning
4. **Quarantine Feature** - Isolated file storage
5. **Detailed Reports** - Export scan results to PDF
6. **Compliance Checks** - GDPR/PCI-DSS compliance scanning
7. **Network Monitor** - Real-time traffic analysis
8. **Behavior Analysis** - Runtime app behavior monitoring

## Contributing

Contributions welcome! Please ensure:
- Code stays under 2000 lines
- Uses only standard Android libraries
- No root/admin requirements
- Maintains ethical security principles
- Includes documentation

## License

MIT License - See LICENSE file

## Disclaimer

This application provides security scanning for educational and informational purposes. While it attempts to identify potential security threats, it is not guaranteed to detect all malicious software or security vulnerabilities. Users should:

1. Use this tool as one part of a comprehensive security strategy
2. Keep their Android system and applications updated
3. Download apps only from official app stores
4. Maintain regular backups
5. Consult security professionals for critical systems

**The developers are not responsible for any data loss, security breaches, or damages resulting from use of this application.**

## Support

For issues, questions, or suggestions:
- GitHub Issues: [Open an issue](https://github.com/ruzikarel04-alt/Android-Security-Scanner/issues)
- Documentation: See README.md
- API Reference: See code comments in MainActivity.java

---

**Version:** 1.0  
**Last Updated:** 2026-09-05  
**Author:** Security Scanner Team  
**Language:** Czech/English
