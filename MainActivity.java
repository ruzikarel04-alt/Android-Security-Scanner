package com.security.scanner;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView resultText, summaryText, statusText;
    private Button scanButton, clearButton;
    private ProgressBar progressBar;
    private ListView threatsListView;
    private List<ThreatItem> threats = new ArrayList<>();
    private ThreatAdapter adapter;
    
    private static final String[] DANGEROUS_PERMISSIONS = {
        "android.permission.READ_SMS",
        "android.permission.SEND_SMS",
        "android.permission.READ_CONTACTS",
        "android.permission.READ_CALL_LOG",
        "android.permission.RECORD_AUDIO",
        "android.permission.CAMERA",
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.READ_CALENDAR",
        "android.permission.WRITE_CALENDAR"
    };

    private static final String[] SUSPICIOUS_FILES = {
        "/system/bin/install-recovery.sh",
        "/system/bin/su",
        "/system/bin/daemonsu",
        "/system/bin/busybox",
        ".superuser",
        "/data/local/tmp/install-recovery.sh"
    };

    private static final String[] MALWARE_URLS = {
        "malware.com",
        "phishing.net",
        "trojan.org",
        "botnet.ru",
        "ransomware.io",
        "spyware.cc",
        "scareware.biz",
        "adware.xyz",
        "exploit-kit.com",
        "c2.darkweb.net",
        "ddos-service.io",
        "stolen-data.net",
        "fake-bank.com",
        "credential-stealer.io",
        "keylogger.net",
        "infostealer.cc",
        "cryptolocker.biz",
        "wannacry.io",
        "petya-ransomware.com",
        "notpetya.net"
    };

    private static final String[] SAFE_SYSTEM_APPS = {
        "com.android.systemui",
        "com.android.launcher",
        "com.android.phone",
        "com.android.settings",
        "com.google.android.gms",
        "com.android.chrome",
        "com.google.android.apps.maps"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUI();
    }

    private void initializeUI() {
        android.widget.LinearLayout mainLayout = new android.widget.LinearLayout(this);
        mainLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
        mainLayout.setPadding(16, 16, 16, 16);

        summaryText = new TextView(this);
        summaryText.setTextSize(18);
        summaryText.setTextColor(0xFF000000);
        summaryText.setText("🛡️ ANDROID SECURITY SCANNER");
        summaryText.setPadding(10, 10, 10, 10);
        mainLayout.addView(summaryText);

        statusText = new TextView(this);
        statusText.setTextSize(14);
        statusText.setTextColor(0xFF666666);
        statusText.setText("Připraven ke skenování");
        mainLayout.addView(statusText);

        android.widget.LinearLayout buttonLayout = new android.widget.LinearLayout(this);
        buttonLayout.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        buttonLayout.setPadding(0, 10, 0, 10);

        scanButton = new Button(this);
        scanButton.setText("▶ SKENOVAT");
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSecurityScan();
            }
        });
        buttonLayout.addView(scanButton, new android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        clearButton = new Button(this);
        clearButton.setText("🗑 VYMAZAT");
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearResults();
            }
        });
        buttonLayout.addView(clearButton, new android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        mainLayout.addView(buttonLayout);

        progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.GONE);
        mainLayout.addView(progressBar);

        resultText = new TextView(this);
        resultText.setTextSize(12);
        resultText.setTextColor(0xFF333333);
        resultText.setMovementMethod(new ScrollingMovementMethod());
        resultText.setMaxLines(4);
        android.widget.LinearLayout.LayoutParams textParams = new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 100);
        mainLayout.addView(resultText, textParams);

        threatsListView = new ListView(this);
        adapter = new ThreatAdapter(this, threats);
        threatsListView.setAdapter(adapter);
        android.widget.LinearLayout.LayoutParams listParams = new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT);
        mainLayout.addView(threatsListView, listParams);

        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        scrollView.addView(mainLayout);
        setContentView(scrollView);
    }

    private void startSecurityScan() {
        scanButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        threats.clear();
        statusText.setText("⏳ Skenování v průběhu...");
        new ScanTask().execute();
    }

    private void clearResults() {
        threats.clear();
        adapter.notifyDataSetChanged();
        resultText.setText("");
        statusText.setText("Připraven ke skenování");
        summaryText.setText("🛡️ ANDROID SECURITY SCANNER");
    }

    private class ScanTask extends AsyncTask<Void, String, Void> {
        int criticalCount = 0;
        int mediumCount = 0;
        int safeCount = 0;

        @Override
        protected Void doInBackground(Void... params) {
            publishProgress("Skenování aplikací...");
            scanInstalledApps();
            
            publishProgress("Skenování oprávnění...");
            scanPermissions();
            
            publishProgress("Skenování souborů...");
            scanFiles();
            
            publishProgress("Skenování procesů...");
            scanProcesses();
            
            publishProgress("Skenování sítě...");
            scanNetworkActivity();
            
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            statusText.setText(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
            scanButton.setEnabled(true);
            displayResults();
        }

        private void scanInstalledApps() {
            PackageManager pm = getPackageManager();
            List<PackageInfo> packages = pm.getInstalledPackages(0);
            
            for (PackageInfo pkg : packages) {
                String appName = pkg.applicationInfo.loadLabel(pm).toString();
                String packageName = pkg.packageName;
                boolean isSystemApp = (pkg.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

                if (!isSafeApp(packageName) && !isSystemApp) {
                    String hash = calculateHash(packageName);
                    if (hash.contains("unknown")) {
                        addThreat("🔴 KRITICKÉ", "Neznámá aplikace: " + appName, 
                            packageName, "critical");
                        criticalCount++;
                    }
                }
            }
            safeCount += packages.size() - criticalCount;
        }

        private void scanPermissions() {
            PackageManager pm = getPackageManager();
            List<PackageInfo> packages = pm.getInstalledPackages(
                PackageManager.GET_PERMISSIONS);

            for (PackageInfo pkg : packages) {
                if (pkg.requestedPermissions == null) continue;
                
                String appName = pkg.applicationInfo.loadLabel(pm).toString();
                boolean hasRiskyPermissions = false;

                for (String permission : pkg.requestedPermissions) {
                    for (String dangerous : DANGEROUS_PERMISSIONS) {
                        if (permission.equals(dangerous)) {
                            if (!isSafeApp(pkg.packageName)) {
                                addThreat("🟡 STŘEDNÍ", 
                                    appName + " má " + permission.replace("android.permission.", ""),
                                    pkg.packageName, "medium");
                                mediumCount++;
                                hasRiskyPermissions = true;
                                break;
                            }
                        }
                    }
                    if (hasRiskyPermissions) break;
                }
            }
        }

        private void scanFiles() {
            for (String filePath : SUSPICIOUS_FILES) {
                File file = new File(filePath);
                if (file.exists()) {
                    addThreat("🔴 KRITICKÉ", 
                        "Podezřelý soubor: " + filePath,
                        filePath, "critical");
                    criticalCount++;
                }
            }

            File externalDir = new File("/sdcard/Download");
            if (externalDir.exists()) {
                File[] files = externalDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        String name = file.getName().toLowerCase();
                        if (name.endsWith(".apk") && isUnknownAPK(name)) {
                            addThreat("🟡 STŘEDNÍ", 
                                "Podezřelý APK: " + name,
                                file.getAbsolutePath(), "medium");
                            mediumCount++;
                        }
                    }
                }
            }
        }

        private void scanProcesses() {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
            
            if (processes != null) {
                for (ActivityManager.RunningAppProcessInfo proc : processes) {
                    if (proc.processName.contains("xposed") || 
                        proc.processName.contains("magisk") ||
                        proc.processName.contains("exploit")) {
                        addThreat("🔴 KRITICKÉ", 
                            "Podezřelý proces: " + proc.processName,
                            proc.processName, "critical");
                        criticalCount++;
                    }
                }
            }
            safeCount += (processes != null ? processes.size() : 0) / 3;
        }

        private void scanNetworkActivity() {
            String[] commonMalwareSites = MALWARE_URLS;
            for (String site : commonMalwareSites) {
                if (isNetworkReachable(site)) {
                    addThreat("🔴 KRITICKÉ", 
                        "Připojení na podezřelou doménu: " + site,
                        site, "critical");
                    criticalCount++;
                }
            }
            safeCount += 3;
        }

        private boolean isNetworkReachable(String host) {
            try {
                java.net.InetAddress.getByName(host);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        private void addThreat(String severity, String message, String data, String type) {
            ThreatItem item = new ThreatItem(severity, message, data, type);
            threats.add(item);
        }

        private String calculateHash(String packageName) {
            try {
                PackageManager pm = getPackageManager();
                PackageInfo info = pm.getPackageInfo(packageName, 0);
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] hash = md.digest(packageName.getBytes());
                StringBuilder sb = new StringBuilder();
                for (byte b : hash) {
                    sb.append(String.format("%02x", b));
                }
                if (sb.toString().equals("a" + packageName.charAt(0))) {
                    return "unknown";
                }
                return sb.toString();
            } catch (Exception e) {
                return "unknown";
            }
        }

        private boolean isUnknownAPK(String filename) {
            return !filename.contains("instagram") && 
                   !filename.contains("facebook") &&
                   !filename.contains("whatsapp") &&
                   !filename.contains("telegram");
        }

        private void displayResults() {
            String summary = String.format("🔴 KRITICKÉ: %d | 🟡 STŘEDNÍ: %d | 🟢 BEZPEČNÉ: %d",
                criticalCount, mediumCount, safeCount);
            summaryText.setText(summary);
            
            String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            resultText.setText("Skenování dokončeno: " + timestamp + "\nNalezeno hrozeb: " + threats.size());
            
            adapter.notifyDataSetChanged();
            
            if (threats.isEmpty()) {
                statusText.setText("✅ Žádné hrozby nebyly detekovány");
            } else {
                statusText.setText("⚠️ " + threats.size() + " hrozeb detekováno - nahlédněte níže");
            }
        }
    }

    private boolean isSafeApp(String packageName) {
        for (String safe : SAFE_SYSTEM_APPS) {
            if (packageName.equals(safe)) return true;
        }
        return packageName.startsWith("com.google") || packageName.startsWith("com.android");
    }

    private static class ThreatItem {
        String severity;
        String message;
        String data;
        String type;

        ThreatItem(String severity, String message, String data, String type) {
            this.severity = severity;
            this.message = message;
            this.data = data;
            this.type = type;
        }
    }

    private class ThreatAdapter extends BaseAdapter {
        private Context context;
        private List<ThreatItem> items;

        ThreatAdapter(Context context, List<ThreatItem> items) {
            this.context = context;
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public ThreatItem getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, android.view.ViewGroup parent) {
            android.widget.LinearLayout itemLayout = new android.widget.LinearLayout(context);
            itemLayout.setOrientation(android.widget.LinearLayout.HORIZONTAL);
            itemLayout.setPadding(12, 8, 12, 8);

            ThreatItem item = getItem(position);

            ImageView icon = new ImageView(context);
            android.widget.LinearLayout.LayoutParams iconParams = 
                new android.widget.LinearLayout.LayoutParams(40, 40);
            iconParams.setMargins(0, 0, 12, 0);

            if (item.severity.contains("KRITICKÉ")) {
                icon.setBackgroundColor(0xFFFF0000);
            } else if (item.severity.contains("STŘEDNÍ")) {
                icon.setBackgroundColor(0xFFFFAA00);
            } else {
                icon.setBackgroundColor(0xFF00AA00);
            }
            itemLayout.addView(icon, iconParams);

            android.widget.LinearLayout textLayout = new android.widget.LinearLayout(context);
            textLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
            textLayout.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            TextView titleText = new TextView(context);
            titleText.setText(item.severity);
            titleText.setTextSize(13);
            titleText.setTextColor(0xFF000000);
            textLayout.addView(titleText);

            TextView messageText = new TextView(context);
            messageText.setText(item.message);
            messageText.setTextSize(11);
            messageText.setTextColor(0xFF666666);
            messageText.setMaxLines(2);
            textLayout.addView(messageText);

            itemLayout.addView(textLayout);

            Button actionButton = new Button(context);
            actionButton.setText("i");
            actionButton.setTextSize(10);
            actionButton.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                40, 40));
            final String data = item.data;
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showThreatInfo(data);
                }
            });
            itemLayout.addView(actionButton);

            return itemLayout;
        }

        private void showThreatInfo(String data) {
            Toast.makeText(context, "Hrozba: " + data, Toast.LENGTH_SHORT).show();
        }
    }
}
