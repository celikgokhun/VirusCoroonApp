package com.celik.gokhun.viruscoronapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import com.celik.gokhun.viruscoronapp.R;

public class PreservationActivity extends AppCompatActivity {

    WebView preservationWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preservation);

        preservationWebView = findViewById(R.id.webViewPreservation);
        preservationWebView.loadUrl(getString(R.string.prezervation_link));
    }
}
