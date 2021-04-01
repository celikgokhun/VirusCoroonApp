package com.celik.gokhun.viruscoronapp.Activities;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.celik.gokhun.viruscoronapp.R;
import com.celik.gokhun.viruscoronapp.Services.LocationService;
import com.celik.gokhun.viruscoronapp.Services.VibrationService;
import com.celik.gokhun.viruscoronapp.Services.WriteLocationService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {


    private static final int PERMISSION_REQUEST_CODE = 1;
    private BroadcastReceiver broadcastReceiver;
    public String longitude;
    public String latitude;

    public String userId;

    boolean autoFind;

    String fsLongitude;
    String fsLatitude;
    String fsUserType;
    String fsUserId;

    private FirebaseFirestore firebaseFirestore;
    ArrayList<String> longitudeFromFB;
    ArrayList<String> latitudeFromFB;
    ArrayList<String> userTypeFromFB;
    ArrayList<String> userIdFromFB;

    private GoogleMap mMap;

    private ArrayList <MarkerOptions> options = new ArrayList<>();

    FirebaseAuth mAuth;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        floatingActionButton = findViewById(R.id.fab);

        autoFind = false;

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        longitudeFromFB = new ArrayList<>();
        latitudeFromFB = new ArrayList<>();
        userTypeFromFB = new ArrayList<>();
        userIdFromFB = new ArrayList<>();

        firebaseFirestore = FirebaseFirestore.getInstance();

        requestLocationPermissions();
        startLocation();
        startDatabase();

        //startVibration();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    /// longitude and latitude is here
                    longitude = intent.getExtras().get("LONGITUDE").toString();
                    latitude = intent.getExtras().get("LATITUDE").toString();
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        CollectionReference collectionReference = firebaseFirestore.collection("USERS");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                googleMap.clear();

                if (e != null) {
                    System.out.println("bos amk " +e.getLocalizedMessage());
                }

                if (queryDocumentSnapshots != null)
                {

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments())
                    {
                        Map<String,Object> data = snapshot.getData();

                        //Casting
                        fsLongitude = (String) data.get("longitude");
                        fsLatitude = (String) data.get("latitude");
                        fsUserType = (String) data.get("userType");
                        fsUserId = (String) data.get("userId");

                        longitudeFromFB.add(fsLongitude);
                        latitudeFromFB.add(fsLatitude);
                        userTypeFromFB.add(fsUserType);
                        userIdFromFB.add(fsUserId);

                        try
                        {
                            if(!fsUserType.isEmpty() && !fsLongitude.isEmpty() && !fsLatitude.isEmpty()) {
                            /*
                            System.out.println("DATA GELDİ    : " +fsLatitude);
                            System.out.println("DATA GELDİ    : " +fsLongitude);
                            System.out.println("DATA GELDİ    : " +fsUserType);
                            System.out.println("DATA GELDİ    : " +fsUserId);
                            System.out.println("-------------------------------------");

                             */

                                try
                                {
                                    options.clear();
                                    if (fsUserType.equals("infected") && !userId.equals(fsUserId) && distance(Double.parseDouble(latitude), Double.parseDouble(longitude), Double.parseDouble(fsLatitude), Double.parseDouble(fsLongitude))<=0.2)
                                    {
                                        System.out.println("titriyooom");
                                        startVibration();
                                    }
                                    else
                                    {
                                        stopVibration();
                                    }

                                    if (fsUserType.equals("healthy"))
                                    {
                                        options.add(new MarkerOptions()
                                                .position(new LatLng(Double.parseDouble(fsLatitude), Double.parseDouble(fsLongitude)))
                                                .icon(bitmapDescriptorFromVector(MainActivity.this, R.drawable.healthy))
                                                .title(fsUserId));
                                    }
                                    else if (fsUserType.equals("infected"))
                                    {
                                        options.add(new MarkerOptions()
                                                .position(new LatLng(Double.parseDouble(fsLatitude), Double.parseDouble(fsLongitude)))
                                                .icon(bitmapDescriptorFromVector(MainActivity.this, R.drawable.infected))
                                                .title(fsUserId));
                                    }
                                    else if (fsUserType.equals("suspected"))
                                    {
                                        options.add(new MarkerOptions()
                                                .position(new LatLng(Double.parseDouble(fsLatitude), Double.parseDouble(fsLongitude)))
                                                .icon(bitmapDescriptorFromVector(MainActivity.this, R.drawable.suspected))
                                                .title(fsUserId));
                                    }
                                    else if (fsUserType.equals("over"))
                                    {
                                        options.add(new MarkerOptions()
                                                .position(new LatLng(Double.parseDouble(fsLatitude), Double.parseDouble(fsLongitude)))
                                                .icon(bitmapDescriptorFromVector(MainActivity.this, R.drawable.over))
                                                .title(fsUserId));
                                    }

                                    else if (fsUserType.equals("highRisk"))
                                    {
                                        options.add(new MarkerOptions()
                                                .position(new LatLng(Double.parseDouble(fsLatitude), Double.parseDouble(fsLongitude)))
                                                .icon(bitmapDescriptorFromVector(MainActivity.this, R.drawable.high_risk))
                                                .title(fsUserId));
                                    }

                                }
                                catch (NullPointerException npe)
                                {
                                    npe.printStackTrace();
                                }

                                for (int i = 0; i < options.size(); i++)
                                {
                                    final int finalI = i;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (autoFind)
                                            {
                                                googleMap.addMarker(options.get(finalI));
                                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)), 25));
                                            }
                                            else
                                            {
                                                googleMap.addMarker(options.get(finalI));
                                                //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)), 15));
                                            }
                                        }
                                    });
                                }
                            }
                        }
                        catch (NullPointerException npe)
                        {
                            npe.printStackTrace();
                        }
                    }
                }
            }

        });
    }

    public void findMyLocation(View view)
    {
        if (autoFind)
        {
            floatingActionButton.setImageResource(R.drawable.my_location);

            autoFind =false;
        }
        else
        {
            floatingActionButton.setImageResource(R.drawable.here);
            autoFind = true;
        }
        System.out.println("bastık amk     :" +autoFind);
    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void startLocation(){
        startService(new Intent(MainActivity.this, LocationService.class));
    }

    public void startDatabase(){
        startService(new Intent(MainActivity.this, WriteLocationService.class));
    }

    public void startVibration(){
        startService(new Intent(MainActivity.this, VibrationService.class));
    }

    public void stopVibration(){
        stopService(new Intent(MainActivity.this, VibrationService.class));
    }

    private void requestLocationPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) & ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)  )
        {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Permission needed").setMessage("This permission needed because of this and that").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
                }
            }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.dismiss();
                }
            })
                    .create().show();
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    public boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
