package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());

        String url = getIntent().getStringExtra("url"); // 인텐트에서 "url" 가져오기
        if (url != null) {
            Log.d("WebViewActivity", "Loading URL: " + url);
            webView.loadUrl(url);
        } else {
            Log.e("WebViewActivity", "URL is null");
        }
    }
}
