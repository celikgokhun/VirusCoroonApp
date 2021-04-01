package com.celik.gokhun.viruscoronapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.celik.gokhun.viruscoronapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserSignUpActivity extends AppCompatActivity
{
    RadioGroup userTypeRadioGroup;
    String userType = null;
    String userId = null;

    Button signUpButton;
    FirebaseAuth mAuth;

    public static SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);
        signUpButton = findViewById(R.id.signUpBtn);


        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();

        sharedPreferences = this.getSharedPreferences("com.celik.gokhun.viruscoronapp",Context.MODE_PRIVATE);


        try
        {
            if (!mAuth.getCurrentUser().getUid().isEmpty())
            {
                startActivity(new Intent(UserSignUpActivity.this, MainActivity.class));
            }
        }
        catch (NullPointerException npe)
        {
            npe.printStackTrace();
        }

        userTypeRadioGroup = findViewById(R.id.userTypeRad);
        userTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radioHealthy:
                        userType = "healthy";
                        break;
                    case R.id.radioInfected:
                        userType = "infected";
                        break;
                    case R.id.radioOver:
                        userType = "over";
                        break;
                    case R.id.radioSuspected:
                        userType = "suspected";
                        break;

                    case R.id.radioHighRisk:
                        userType = "highRisk";
                        break;
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        signUpButton.setClickable(true);
    }

    public void saveDatabase(View view) {
        try {
            if (isOnline() && !userType.isEmpty()) {
                signUpButton.setClickable(false);
                ////add user type
                sharedPreferences.edit().putString("userType", userType).apply();

                mAuth.signInAnonymously()
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //Sign in success, update UI with the signed-in user's information
                                    //System.out.println("signInAnonymously:success");
                                    startActivity(new Intent(UserSignUpActivity.this, MainActivity.class));

                                }
                                else
                                {
                                    System.out.println("signInAnonymously:failure");
                                }
                            }
                        });
            }
            else {
                Toast.makeText(getApplicationContext(),"Lütfen internet bağlantınızı kontrol edin ve durumlardan birini işaretleyin !", Toast.LENGTH_LONG).show();
            }
        }catch (NullPointerException npe)
        {
            npe.printStackTrace();
        }

    }

    public void jumpToSymptoms(View view)
    {
        if (isOnline())
        {
            startActivity(new Intent(UserSignUpActivity.this, SymptomsActivity.class));
        }

    }

    public void jumpToRiskGroup(View view)
    {
        if (isOnline())
        {
            startActivity(new Intent(UserSignUpActivity.this, RiskGroupActivity.class));
        }

    }

    public void jumpToPreservation(View view)
    {
        if (isOnline())
        {
            startActivity(new Intent(UserSignUpActivity.this, PreservationActivity.class));
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
