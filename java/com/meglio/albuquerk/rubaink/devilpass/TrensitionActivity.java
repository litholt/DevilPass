package com.meglio.albuquerk.rubaink.devilpass;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TrensitionActivity extends AppCompatActivity {

    private CardView cardView1,cardView2;

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

        setContentView(R.layout.activity_trensition);

        cardView1 = findViewById(R.id.cardv1);
        cardView2 = findViewById(R.id.cardv2);

        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nt = new Intent(TrensitionActivity.this, SecondActivity.class);
                startActivity(nt);
            }
        });
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nt = new Intent(TrensitionActivity.this, SelectGroupActivity.class);
                startActivity(nt);
            }
        });
    }
}
