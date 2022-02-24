package com.example.md4;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartActivity extends AppCompatActivity {
    TelephonyManager tm;
    public String imei;
    private Button login;
    private Button register;

    private static final String KEY_USER = "user";
    private static final String KEY_DEVICE = "device";

    ListView listView;
    TextView text;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        register = findViewById(R.id.btn_register);
        login = findViewById(R.id.btn_login);
        listView = findViewById(R.id.listview);
        text = findViewById(R.id.totalapp);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));


                Toast.makeText(StartActivity.this, imei, Toast.LENGTH_SHORT).show();


            }
        });





//        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        Network[] networks = connectivity.getAllNetworks();
//        for (int i = 0; i < networks.length; i++) {
//            NetworkCapabilities capabilities = connectivity.getNetworkCapabilities(networks[i]);
//            System.out.println("networks:" + networks);
//        }


//        getssid();



//        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        List<ScanResult> networkList = wifi.getScanResults();
//
////get current connected SSID for comparison to ScanResult
//        WifiInfo wi = wifi.getConnectionInfo();
//        String currentSSID = wi.getSSID();
//        System.out.println("|||||||||||||||||||||||||");
//
//        if (networkList != null) {
//            for (ScanResult network : networkList) {
//                //check if current connected SSID
//                if (currentSSID.equals(network.SSID)) {
//                    //get capabilities of current connection
//                    String capabilities = network.capabilities;
//                    Log.d(TAG, network.SSID + " capabilities : " + capabilities);
//
//
//                    if (capabilities.contains("WPA2")) {
//                        System.out.println("wpa2");
//                    } else if (capabilities.contains("WPA")) {
//                        System.out.println("wpa");
//                    } else if (capabilities.contains("WEP")) {
//                        System.out.println("wep");
//                    } else {
//                        System.out.println("smth");
//                    }
//                }
//            }
//        }

    }

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            startActivity(new Intent(StartActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

        }

    }

    public void getssid() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid = info.getSSID();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            int wifi = info.getWifiStandard();
            System.out.println("wifi:" + wifi);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        String mac = info.getMacAddress();
       int netId = info.getNetworkId();

        System.out.println("------------------------");
        System.out.println("ssid: " + ssid);
        System.out.println("net id: " + netId);
        System.out.println("mac id: " + mac);
        return;



    }



//    public void getallapps(View view) throws PackageManager.NameNotFoundException {
//        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
//        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//
//        // get list of all the apps installed(just name but returns very few)
//        List<ResolveInfo> ril = getPackageManager().queryIntentActivities(mainIntent, 0);
//        List<String> componentList = new ArrayList<String>();
//        String name = null;
//        int i = 0;
//
//        // get size of ril and create a list
//        String[] apps = new String[ril.size()];
//        for (ResolveInfo ri : ril) {
//            if (ri.activityInfo != null) {
//                // get package
//                Resources res = getPackageManager().getResourcesForApplication(ri.activityInfo.applicationInfo);
//                // if activity label res is found
//                if (ri.activityInfo.labelRes != 0) {
//                    name = res.getString(ri.activityInfo.labelRes);
//                } else {
//                    name = ri.activityInfo.applicationInfo.loadLabel(
//                            getPackageManager()).toString();
//                }
//                apps[i] = name;
//                i++;
//            }
//        }
//        // set all the apps name in list view
//        listView.setAdapter(new ArrayAdapter<String>(StartActivity.this, android.R.layout.simple_list_item_1, apps));
//        // write total count of apps available.
//        text.setText(ril.size() + " Apps are installed");
//    }



//    public void getallapps(View view) {
//        // get list of all the apps installed(full package name)
//        List<ApplicationInfo> infos = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
//        // create a list with sze of total number of apps
//        String[] apps = new String[infos.size()];
//        int i = 0;
//        // add all the app name in string list
//        for (ApplicationInfo info : infos) {
//            apps[i] = info.packageName;
//            i++;
//        }
//        // set all the apps name in list view
//        listView.setAdapter(new ArrayAdapter<String>(StartActivity.this, android.R.layout.simple_list_item_1, apps));
//        // write total count of apps available.
//        text.setText(infos.size() + " Apps are installed");
//    }
public void getallapps(View view) {
    // get list of all the apps installed just name
    List<PackageInfo> packList = getPackageManager().getInstalledPackages(0);
    String[] apps = new String[packList.size()];
    for (int i = 0; i < packList.size(); i++) {
        PackageInfo packInfo = packList.get(i);
        apps[i] = packInfo.applicationInfo.loadLabel(getPackageManager()).toString();
    }
    // set all the apps name in list view
    listView.setAdapter(new ArrayAdapter<String>(StartActivity.this, android.R.layout.simple_list_item_1, apps));
    // write total count of apps available.
    text.setText(packList.size() + " Apps are installed");
}





//    private void gettingImei() {
//        int permisI = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
//
//        if (permisI == PackageManager.PERMISSION_GRANTED) {
//            tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            imei = tm.getDeviceId();
//
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 123);
//        }
//    }



}