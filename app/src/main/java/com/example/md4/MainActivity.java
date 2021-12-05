package com.example.md4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.example.md4.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    TelephonyManager tm;
    String imei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        gettingImei();
        Toast.makeText(MainActivity.this, imei, Toast.LENGTH_LONG).show();
    }

    private void gettingImei() {
        int permisI = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permisI == PackageManager.PERMISSION_GRANTED) {
            tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            imei = tm.getDeviceId().toString();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 123);
        }
    }
}