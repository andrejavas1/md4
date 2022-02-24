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


//        getssid();



    }

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            startActivity(new Intent(StartActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

        }

    }


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


}