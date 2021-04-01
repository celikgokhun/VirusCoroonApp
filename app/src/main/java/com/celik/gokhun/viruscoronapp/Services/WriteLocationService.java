package com.celik.gokhun.viruscoronapp.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.celik.gokhun.viruscoronapp.Activities.MainActivity;
import com.celik.gokhun.viruscoronapp.Activities.UserSignUpActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WriteLocationService extends Service {

    String longitude;
    String latitude;
    String userType;

    String userId;

    private BroadcastReceiver broadcastReceiverForLocation;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();

        userId = mAuth.getCurrentUser().getUid();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (broadcastReceiverForLocation == null)        {
            broadcastReceiverForLocation = new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context context, Intent intent) {


                    try
                    {
                        userType = UserSignUpActivity.sharedPreferences.getString("userType", "yok galiba");
                        latitude = intent.getExtras().get("LATITUDE").toString();
                        longitude = intent.getExtras().get("LONGITUDE").toString();
                    }
                    catch (NullPointerException npe)
                    {
                        npe.printStackTrace();
                    }


                    try {
                        saveDatabase();
                    }
                    catch (NullPointerException npe) {
                        //System.out.println("SAlakaakakaka   : "+ npe.getLocalizedMessage());
                    }

                }
            };
        }
        registerReceiver(broadcastReceiverForLocation, new IntentFilter("location_update"));
        return super.onStartCommand(intent, flags, startId);
    }


    public void saveDatabase() {
        try
        {
            if (isOnline() && !longitude.isEmpty() && !latitude.isEmpty() && !userId.isEmpty() && !userType.isEmpty())
            {
                //System.out.println("Longitude: "+longitude+"\n"+"Latitude: "+ latitude);
                //System.out.println("Id: "+userId);

                try
                {
                    Map<String, Object> user = new HashMap<>();
                    user.put("longitude", longitude);
                    user.put("latitude", latitude);
                    user.put("userType", userType);
                    user.put("userId", userId);

                    db.collection("USERS").document(userId)
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //System.out.println("DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //System.out.println("Error writing document"+ e.getLocalizedMessage());
                                }
                            });
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }

            }
        }
        catch (NullPointerException npe)
        {
            npe.printStackTrace();
        }

    }

    public boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
