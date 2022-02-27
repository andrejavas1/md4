package com.example.md4;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity<ActivityUtil> extends AppCompatActivity {

    private static final String TAG = "MyActivity";
    private Button logout;
    TelephonyManager tm;
    public String imei;
    ListView listView;
    TextView text;
    private static final String KEY_USER = "user";
    private static final String KEY_DEVICE = "device";


    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new FetchCategoryTask().execute();


        logout = findViewById(R.id.logout);
//        add = findViewById(R.id.add);


        listView = findViewById(R.id.listview);
        text = findViewById(R.id.totalapp);
        logout = findViewById(R.id.logout);


        putUserToDB();


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Atsijungta", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, StartActivity.class));
            }
        });


        final PackageManager pm = getPackageManager();
        final List<ApplicationInfo> installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        final int installedApps1 = getApplicationInfo().category;
        for (ApplicationInfo app : installedApps) {

            StringBuffer permissions = new StringBuffer();

            try {
                PackageInfo packageInfo = pm.getPackageInfo(app.packageName, PackageManager.GET_PERMISSIONS);

                String[] requestedPermissions = packageInfo.requestedPermissions;
                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        permissions.append(requestedPermissions[i] + "\n");
                    }

                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        nustatLygi();

    }

    private void nustatLygi() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String[] lygis = new String[1];
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference docRef = db.collection("User").document(mUser.getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Log.d("Saugumo lygis", document.getString("saugumoLygis")); //Print the name
                        lygis[0] = document.getString("saugumoLygis");
                        System.out.println("-------");
                        System.out.println(lygis[0]);
                    } else {
                        Log.d(TAG, "Saugumo lygis nepriskirtas");
                    }
                } else {
                    Log.d(TAG, "nepavyko ", task.getException());
                }

            }

        });
    }


    private void putUserToDB() {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, Object> id = new HashMap<>();
        id.put("device id", getIMEIDeviceId(MainActivity.this));

        db.collection("User").document(mUser.getEmail()).
                collection("Device").document(Build.MODEL).set(id)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "klaida pridedant ID");
                    }
                });
    }

    private class FetchCategoryTask extends AsyncTask<String, Void, Void> {

        private final String TAG = FetchCategoryTask.class.getSimpleName();
        private PackageManager pm;
        Map<String, ArrayList<String>> multiValueMap = new HashMap<String, ArrayList<String>>();

        @Override
        protected Void doInBackground(String... errors) {
            String category;
            String GoogleConst = "https://play.google.com/store/apps/details?id=";
            pm = getPackageManager();
            int i = 0;
            Map<String, Object> app = new HashMap<>();
            Map<String, Object> appAll = new HashMap<>();



            List<ApplicationInfo> infos = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
            // create a list with sze of total number of apps
            String[] apps = new String[infos.size()];

//             add all the app name in string list
            for (ApplicationInfo info : infos) {
                apps[i] = info.packageName;
                if ((infos.get(i).flags & ApplicationInfo.FLAG_SYSTEM) == 0) {


                    String query_url = GoogleConst + info.packageName; //"com.spotify.music"; //info.packageName;  //GOOGLE_URL + packageInfo.packageName;
                    Log.i(TAG, query_url);
                    category = getCategory(query_url);

                    app.put("ID: ", i);
                    app.put("Programėlė: ", info.packageName);
                    app.put("Kategorija: ", category);
//
//                    appAll.put("ID: "+ i + " Programėlė: ", info.packageName);
//                    appAll.put("ID: "+ i + " Kategorija: ", category);


                    if (multiValueMap.containsKey(category)) {
                        multiValueMap.get(category).add(info.packageName);
                    } else {
                        multiValueMap.put(category, new ArrayList<String>());
                        multiValueMap.get(category).add(info.packageName);
                    }


                }
                i++;
            }
            Log.d("CATEGORY", app.toString());
            Log.d("CATEGORYmulti", multiValueMap.toString());
            Log.d("CATEGORYmulti1", String.valueOf(multiValueMap.size()));
            System.out.println(multiValueMap.get("Social").toString());



//            System.out.println(app.toString());

            return null;
        }

        public String getCategory(String query_url) {

            pm = getPackageManager();
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            Iterator<ApplicationInfo> iterator = packages.iterator();
            ApplicationInfo packageInfo = iterator.next();
            List<ApplicationInfo> infos = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
            // create a list with sze of total number of apps
            String[] apps = new String[infos.size()];
//            for (ApplicationInfo info : infos) {
            int i = 0;

//                apps[i] = info.packageName;

            try {
                Document doc = Jsoup.connect(query_url).get();
                //Elements link = doc.select("a[class=\"hrTbp R8zArc\"]");
                Elements link = doc.select("a[class=\"hrTbp R8zArc\"]");
                return link.get(1).text();
            } catch (Exception e) {
                Log.e("DOc", e.toString());
            }

            return "Default app";
        }

//        @Override
//        protected void onPostExecute(Void unused) {
//            super.onPostExecute(unused);
//            MainActivity.this.checkLevel();
//        }
    }

//    private void checkLevel() {
//        System.out.println(new FetchCategoryTask().multiValueMap.get("Social").toString());
//        System.out.println("]]]]]]");
//    }


    private void appinfo() {
        PackageManager p = this.getPackageManager();
        final List<PackageInfo> appinstall = p.getInstalledPackages(PackageManager.GET_PERMISSIONS | PackageManager.GET_RECEIVERS |
                PackageManager.GET_SERVICES | PackageManager.GET_PROVIDERS | PackageManager.GET_SIGNING_CERTIFICATES);
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        for (PackageInfo pInfo : appinstall) {
            //PermissionInfo[] permission=pInfo.permissions;
            String[] reqPermission = pInfo.requestedPermissions;
            ServiceInfo[] services = pInfo.services;
            ProviderInfo[] providers = pInfo.providers;


            int versionCode = pInfo.versionCode;

            Map<String, Object> appName = new HashMap<>();

            //   Log.d("versionCode-package ", Integer.toString(versionCode));
            // Log.d("versionCode-package ", String.valueOf(pInfo.applicationInfo));
            // Log.d("Installed Applications", pInfo.applicationInfo
            //         .loadLabel(p).toString());
            // Log.d("Installed Applications", String.valueOf(pInfo.splitNames));

            List<PackageInfo> packList = getPackageManager().getInstalledPackages(0);
            for (int i = 0; i < packList.size(); i++) {
                PackageInfo packInfo = packList.get(i);
                if ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0
                        & (packInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                    String appName1 = packInfo.applicationInfo.dataDir;
                    Log.e("App № " + Integer.toString(i), appName1);
                }

                ApplicationInfo appinf = getApplicationInfo();
                //     Log.d("Installed dir", pInfo.packageName);

                Map<String, Object> appDir = new HashMap<>();
                //    appDir.put("Programėlės direktorija:", pInfo.applicationInfo.dataDir);

                db.collection("User").document(mUser.getEmail()).
                        collection("Device").document(android.os.Build.MODEL).collection("applications")
                        .document(pInfo.packageName).set(appDir)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "klaida pridedant aplikacijas");
                            }
                        });

            }


            //adding packagenames to db
            appName.put("Programele:", pInfo.applicationInfo
                    .loadLabel(p).toString());

            db.collection("User").document(mUser.getEmail()).
                    collection("Device").document(android.os.Build.MODEL).collection("applications").document(pInfo.packageName).set(appName)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "klaida pridedant aplikacijas");
                        }
                    });

//            long pirmoIrasData = pInfo.firstInstallTime;

            //creating Date from millisecond
//            Date currentDate = new Date(pirmoIrasData);

            DateFormat df = new SimpleDateFormat("yy:MM:dd:HH:mm:ss");

            //Converting milliseconds to Date using Calendar
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(pInfo.firstInstallTime);

            //Addind first install date to db
            Log.d("first install", String.valueOf(pInfo.firstInstallTime));
            Map<String, Object> appFirstInstall = new HashMap<>();
            appFirstInstall.put("Programėlė įrašyta:", df.format(pInfo.firstInstallTime));

            db.collection("User").document(mUser.getEmail()).
                    collection("Device").document(android.os.Build.MODEL).collection("applications")
                    .document(pInfo.packageName).update(appFirstInstall)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "klaida pridedant aplikacijas");
                        }
                    });

            Log.d("last update", String.valueOf(pInfo.lastUpdateTime));


            //last update time to db
            Map<String, Object> appLastUpdate = new HashMap<>();
            appLastUpdate.put("Programėlė atnaujinta:", df.format(pInfo.lastUpdateTime));

            db.collection("User").document(mUser.getEmail()).
                    collection("Device").document(android.os.Build.MODEL).collection("applications")
                    .document(pInfo.packageName).update(appLastUpdate)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "klaida pridedant aplikacijas");
                        }
                    });

            //adding permissions to db
            ArrayList<String> cars = new ArrayList<String>();

            Map<String, Object> appPerm = new HashMap<>();

            if (reqPermission != null)
                for (int i = 0; i < reqPermission.length; i++)

                    cars.add(reqPermission[i]);
            appPerm.put("Leidimai", cars);

            db.collection("User").document(mUser.getEmail()).
                    collection("Device").document(android.os.Build.MODEL).collection("applications")
                    .document(pInfo.packageName).update(appPerm)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "klaida pridedant aplikacijas");
                        }
                    });

            if (reqPermission != null)
                for (int i = 0; i < reqPermission.length; i++)
                    Log.d("permission list", reqPermission[i]);


        }


    }


    public static String getIMEIDeviceId(Context context) {

        String deviceId;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return "";
                }
            }
            assert mTelephony != null;
            if (mTelephony.getDeviceId() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    deviceId = mTelephony.getImei();
                } else {
                    deviceId = mTelephony.getDeviceId();
                }
            } else {
                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        }
        Log.d("deviceId", deviceId);
        return deviceId;
    }


    public void getallapps(View view) {

        // get list of all the apps installed(full package name)
        List<ApplicationInfo> infos = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        // create a list with sze of total number of apps
        String[] apps = new String[infos.size()];
        int i = 0;
        // add all the app name in string list
        for (ApplicationInfo info : infos) {
            apps[i] = info.packageName;
            i++;
        }
        // set all the apps name in list view
        listView.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, apps));
        // write total count of apps available.
        text.setText(infos.size() + " Apps are installed");

        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.md4", PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("MY KEY HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


    }

    public void writeAllappsToDb() {
        // get list of all the apps installed just name
        List<PackageInfo> packList = getPackageManager().getInstalledPackages(0);
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        String[] apps = new String[packList.size()];
        for (int i = 0; i < packList.size(); i++) {
            PackageInfo packInfo = packList.get(i);
            apps[i] = packInfo.applicationInfo.loadLabel(getPackageManager()).toString();


            Map<String, Object> appsdb = new HashMap<>();
            appsdb.put("device id", apps);
            db.collection("User").document(mUser.getEmail()).
                    collection("Device").document(android.os.Build.MODEL).collection("Apps").document().set(appsdb);
        }
    }


}
