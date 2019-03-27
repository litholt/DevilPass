package com.meglio.albuquerk.rubaink.devilpass;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LunchActivity extends AppCompatActivity {

    private final int  REQUEST_LOGIN = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent().setClass(LunchActivity.this, WelcomeActivity.class)
                        .setData(getIntent().getData()));
                finish();

            }
        }, 3000);
    }
}
