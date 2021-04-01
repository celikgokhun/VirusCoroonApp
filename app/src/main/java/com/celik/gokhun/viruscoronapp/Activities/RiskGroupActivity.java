package com.celik.gokhun.viruscoronapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import com.celik.gokhun.viruscoronapp.R;

public class RiskGroupActivity extends AppCompatActivity {

    WebView riskGroupWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risk_group);

        riskGroupWebView = findViewById(R.id.webViewRiskGroup);
        riskGroupWebView.loadUrl(getString(R.string.risk_grup_link));
    }
}
