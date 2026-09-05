# Detection Rules & Threat Database

## Overview

This document describes all threat detection rules, heuristics, and databases used by Android Security Scanner. Rules are categorized by severity level and detection module.

## Severity Levels

### 🔴 CRITICAL (Immediate Action Required)
- **Score:** 90-100 points
- **Action:** Review and remove immediately
- **Examples:** Known malware, system exploits, root binaries

### 🟡 MEDIUM (Review Recommended)
- **Score:** 50-89 points
- **Action:** Review within 24 hours
- **Examples:** Risky permissions, suspicious files, unknown APKs

### 🟢 SAFE (No Action Needed)
- **Score:** 0-49 points
- **Action:** None
- **Examples:** System apps, trusted publishers, normal processes

---

## Module 1: Application Scanner

### Detection Rules

#### Rule 1.1: Unknown Third-Party Applications
```
Condition:
  - Application NOT in SAFE_SYSTEM_APPS whitelist
  - Application is NOT a system app (FLAG_SYSTEM not set)
  - Package hash cannot be verified

Severity: 🔴 CRITICAL
Score: 95
Action: User reviews and uninstalls manually
```

#### Rule 1.2: Suspicious Package Names
```
Condition:
  - Package name contains suspicious keywords:
    * "hack", "crack", "warez", "mod"
    * "trojan", "malware", "virus", "adware"
    * "spy", "track", "monitor", "bot"

Severity: 🟡 MEDIUM
Score: 70
Action: Verify app authenticity in Play Store
```

#### Rule 1.3: Modified System Applications
```
Condition:
  - Application claims to be system app
  - Package hash differs from baseline
  - Installation path outside /system

Severity: 🔴 CRITICAL
Score: 90
Action: Review in Settings → Apps
```

#### Rule 1.4: Recently Installed Applications
```
Condition:
  - Installation date within last 24 hours
  - Not from Google Play Store
  - Developer unverified

Severity: 🟡 MEDIUM
Score: 65
Action: Monitor behavior for 48 hours
```

### Safe System Apps Whitelist

```java
com.android.systemui
com.android.launcher
com.android.launcher3
com.android.phone
com.android.contacts
com.android.dialer
com.android.settings
com.android.mms
com.android.messaging
com.android.bluetooth
com.android.deskclock
com.android.calculator2
com.google.android.gms
com.google.android.gsf
com.google.android.apps.maps
com.google.android.apps.docs
com.google.android.apps.docs.editors.slides
com.google.android.apps.docs.editors.sheets
com.google.android.apps.docs.editors.docs
com.android.chrome
com.google.android.youtube
com.google.android.apps.maps
com.google.android.apps.photos
com.google.android.gms.maps
```

---

## Module 2: Permissions Scanner

### Dangerous Permissions Monitored

#### High-Risk Category (READ_SMS, SEND_SMS)
```
Severity: 🔴 CRITICAL when:
  - Non-system app requests both READ_SMS and SEND_SMS
  - App has no legitimate SMS functionality
  - Combined with CALL_LOG permissions

Severity: 🟡 MEDIUM when:
  - Legitimate SMS app (known publisher) requests SMS permissions
  - Single SMS permission (either READ or SEND)
```

#### Location Tracking (ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
```
Severity: 🔴 CRITICAL when:
  - Combined with INTERNET + background execution
  - No visible location feature in app
  - Continuous location tracking via GPS

Severity: 🟡 MEDIUM when:
  - Maps/navigation app with location permission
  - Single location permission without INTERNET
```

#### Sensor Access (CAMERA, RECORD_AUDIO)
```
Severity: 🔴 CRITICAL when:
  - Camera/audio permission without corresponding app feature
  - Background microphone access detected
  - Permissions enabled on app with no UI

Severity: 🟡 MEDIUM when:
  - Camera app with RECORD_AUDIO permission
  - Voice recorder with CAMERA permission
```

#### Contact & Calendar Access (READ_CONTACTS, READ_CALL_LOG, READ_WRITE_CALENDAR)
```
Severity: 🔴 CRITICAL when:
  - Game or utility app requests contact permissions
  - Multiple personal data permissions combined
  - Permissions with INTERNET access

Severity: 🟡 MEDIUM when:
  - Messaging app with READ_CONTACTS
  - Calendar app with READ_CONTACTS
```

### Complete Dangerous Permissions List

```
1. android.permission.READ_SMS
2. android.permission.SEND_SMS
3. android.permission.READ_MMS
4. android.permission.SEND_MMS
5. android.permission.READ_CONTACTS
6. android.permission.WRITE_CONTACTS
7. android.permission.READ_CALL_LOG
8. android.permission.WRITE_CALL_LOG
9. android.permission.RECORD_AUDIO
10. android.permission.ACCESS_FINE_LOCATION
11. android.permission.ACCESS_COARSE_LOCATION
12. android.permission.ACCESS_BACKGROUND_LOCATION
13. android.permission.CAMERA
14. android.permission.READ_CALENDAR
15. android.permission.WRITE_CALENDAR
16. android.permission.READ_EXTERNAL_STORAGE
17. android.permission.WRITE_EXTERNAL_STORAGE
18. android.permission.ACCESS_MEDIA_LOCATION
```

---

## Module 3: File Scanner

### Suspicious File Paths

#### System Binary Exploits
```
Paths Monitored:
  /system/bin/su                    - Superuser binary
  /system/bin/daemonsu              - Daemon superuser
  /system/bin/busybox               - BusyBox (shell access)
  /system/bin/install-recovery.sh   - Recovery scripts
  /system/bin/sh                    - Shell (when modified)
  /system/xbin/su                   - Alternative superuser
  /data/local/tmp/install-recovery.sh
  /data/local/tmp/su
  /data/local/tmp/busybox

Severity: 🔴 CRITICAL
Score: 100
Reason: Root access exploitation
```

#### Superuser Management Apps
```
Paths Monitored:
  /.superuser                       - Superuser marker
  /system/app/Superuser.apk
  /system/app/SuperSU.apk
  /data/data/com.noshufou.android.su
  /data/data/com.thirdparty.superuser
  /data/data/eu.chainfire.supersu

Severity: 🔴 CRITICAL
Score: 95
Reason: Root management framework
```

#### Suspicious Downloaded APKs
```
Patterns Detected:
  /sdcard/Download/*.apk
  /sdcard/Android/data/*.apk
  /cache/*.apk
  /data/cache/*.apk

Severity: 🟡 MEDIUM
Score: 70
Reason: Unauthorized APK sources
```

#### Modified System Files
```
Patterns Detected:
  /system/app/*.apk (with modified hash)
  /system/priv-app/*.apk (with modified hash)
  /vendor/lib/*.so (with modified hash)

Severity: 🔴 CRITICAL
Score: 90
Reason: System tampering
```

---

## Module 4: Process Scanner

### Suspicious Process Patterns

#### Rooting Framework Processes
```
Process Names:
  - Contains "xposed"         → Xposed Framework
  - Contains "magisk"         → Magisk root manager
  - Contains "riru"           → Riru framework
  - Contains "supersu"        → SuperSU daemon
  - Contains "daemonsu"       → Daemon superuser
  - Contains "su_daemon"      → SU daemon process

Severity: 🔴 CRITICAL
Score: 95
Detection: ActivityManager.getRunningAppProcesses()
```

#### Exploit Detection
```
Process Names:
  - Contains "exploit"        → Known exploits
  - Contains "poc"            → Proof-of-concept malware
  - Contains "backdoor"       → Backdoor access
  - Contains "injectable"     → Code injection framework

Severity: 🔴 CRITICAL
Score: 90
```

#### Suspicious System Processes
```
Process Names:
  - "com.android.hidden" (fake system process)
  - "com.system.updater" (fake system update)
  - "com.google.android.fake" (fake Google process)
  - Processes with spaces in name (invalid)

Severity: 🟡 MEDIUM
Score: 75
```

#### CPU Consumption Anomalies
```
Condition:
  - Single process uses >80% CPU constantly
  - Process PID alternates frequently (process respawning)
  - Process uses >500MB RAM (non-game app)

Severity: 🟡 MEDIUM
Score: 70
Action: Monitor process behavior
```

---

## Module 5: Network Activity Scanner

### Malware Domain Blacklist

#### Command & Control Servers (C2)
```
c2.darkweb.net
botnet.ru
ddos-service.io
botmaster.cc
exploit-kit.com
backdoor-access.net
```

#### Ransomware Domains
```
ransomware.io
cryptolocker.biz
wannacry.io
petya-ransomware.com
notpetya.net
grayware.io
```

#### Phishing & Credential Stealing
```
phishing.net
fake-bank.com
credential-stealer.io
keylogger.net
infostealer.cc
stealer-panel.net
```

#### Spyware & Adware
```
spyware.cc
scareware.biz
adware.xyz
stolen-data.net
tracking-network.net
```

#### Malware Distribution
```
malware.com
trojan.org
malware-distribution.net
payload-server.io
malware-hosting.net
```

### Detection Logic

```java
Detection Procedure:
1. For each blacklist domain:
   - Attempt InetAddress.getByName(domain)
   - Measure response time
   
2. If connection successful:
   - Severity: 🔴 CRITICAL
   - Score: 95
   - Action: Alert user immediately
   
3. If domain resolves but connection fails:
   - Likely blocked by firewall
   - Severity: 🟡 MEDIUM
   - Score: 65
   
4. If domain unresolvable:
   - Severity: 🟢 SAFE
   - Score: 0 (no threat detected)
```

### Network Anomaly Detection

```
High-Risk Indicators:
  - DNS resolution to private IP (127.0.0.1, 192.168.x.x)
  - Rapid DNS queries (>100/sec)
  - Connection attempts to port 1-1024 (reserved)
  - Outbound connections on suspicious ports:
    * 6667 (IRC botnet)
    * 4444 (Blaster worm)
    * 5900 (VNC remote access)
    * 31337 (Back Orifice)

Severity: 🟡 MEDIUM
Score: 70
Note: Requires network monitoring permissions
```

---

## Combined Scoring Algorithm

### Threat Score Calculation

```
Base Score = 0

For each detected threat:
  Score += threat_severity_points
  
Multipliers:
  - Multiple threats from same app: x1.5
  - Critical + Medium combined: x1.3
  - Repeated offender (scanned before): x1.2

Final Score = min(Score * Multipliers, 100)

Severity Classification:
  0-49    → 🟢 SAFE
  50-89   → 🟡 MEDIUM
  90-100  → 🔴 CRITICAL
```

### Example Calculations

**Example 1: Suspicious App with Multiple Risk Factors**
```
App: "FreeGame.apk"
Detected Threats:
  - Unknown app (base): +40
  - READ_SMS permission: +30
  - READ_CONTACTS permission: +20
  - Recorded in previous scan: x1.2 multiplier
  
Score = (40 + 30 + 20) × 1.2 = 108 → capped at 100
Final: 🔴 CRITICAL
```

**Example 2: Legitimate App with Single Permission**
```
App: "Messaging App"
Detected Threats:
  - READ_SMS permission (legitimate): +20
  - Verified publisher: -10
  - System app: -5
  
Score = (20 - 10 - 5) = 5
Final: 🟢 SAFE
```

---

## False Positive Mitigation

### Known False Positives

1. **Magisk on Rooted Devices**
   - User may have legitimate rooting for customization
   - App developers may use Magisk Module API
   - Flagged but user can whitelist

2. **Location Apps**
   - Maps, navigation, weather require location access
   - Combined with internet is normal
   - Whitelisted when from Play Store

3. **Firmware Update Files**
   - Download folder may contain recovery images
   - Detected as "suspicious APK"
   - Safe if from official manufacturer

### Whitelist Management

```
Hard Whitelist (Cannot override):
  - Official Google apps
  - Official Samsung/LG/etc system apps
  - AOSP core applications

Soft Whitelist (User can modify):
  - User-approved apps
  - Apps from verified publishers
  - Apps verified multiple times
```

---

## Rule Updates & Maintenance

### Quarterly Updates

1. **Domain Blacklist** - Add new malware C2 servers
2. **File Signatures** - Update MD5 hashes
3. **Permission Heuristics** - Refine risk scoring
4. **Process Patterns** - Add new exploit detection

### Reporting New Threats

To report a threat not detected:
1. Open GitHub Issue
2. Include: App name, package name, threat type
3. Attach: Screenshot, scan logs
4. Rule will be evaluated and added if confirmed

---

## Testing Detection Rules

### Manual Test Cases

```
Test 1: Unknown App Detection
  Install: Random unsigned APK from internet
  Expected: 🔴 CRITICAL threat alert
  
Test 2: Permission Detection
  Install: App with READ_SMS permission
  Expected: 🟡 MEDIUM threat alert
  
Test 3: File Detection
  Create: /data/local/tmp/test.sh
  Expected: 🔴 CRITICAL threat alert (if readable)
  
Test 4: Network Detection
  Connect to WiFi with DNS sinkhole
  Expected: Network check may skip (no internet)
  
Test 5: Process Detection
  Run: am start with xposed module
  Expected: 🔴 CRITICAL process threat (if running)
```

---

**Last Updated:** 2026-09-05  
**Rules Version:** 1.0  
**Malware Database:** 50+ patterns  
**Maintained By:** Security Scanner Team
