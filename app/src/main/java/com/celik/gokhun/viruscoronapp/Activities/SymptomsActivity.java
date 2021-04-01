package com.celik.gokhun.viruscoronapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import com.celik.gokhun.viruscoronapp.R;


public class SymptomsActivity extends AppCompatActivity {

    WebView symptomsWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms);

        symptomsWebView = findViewById(R.id.webViewSymptoms);
        symptomsWebView.loadUrl(getString(R.string.symptom_link));

    }
}
