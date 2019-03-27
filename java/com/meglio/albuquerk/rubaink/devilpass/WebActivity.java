package com.meglio.albuquerk.rubaink.devilpass;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WebActivity extends AppCompatActivity {

    private WebView webview;
    private TextView name;
    private String lien;
    private String sp_name;
    private ImageView back;

    @Override
    public void onBackPressed() {
        if(webview.canGoBack()){
            webview.goBack();
        }else {
            super.onBackPressed();
        }
    }

    //Ctrl+O
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Before setContent
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Ubuntu-R.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_web);

        webview = findViewById(R.id.webview);
        name = findViewById(R.id.eventName);
        lien = getIntent().getStringExtra("lien");
        sp_name = getIntent().getStringExtra("name");

        if (lien != null && sp_name != null){

            name.setText(sp_name);
            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webview.setWebViewClient(new WebViewClient());
            webview.loadUrl(lien);
        }

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}