package com.meglio.albuquerk.rubaink.devilpass;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SecondActivity extends AppCompatActivity {

    private CardView concert ,spectacle ,sport ,festival ,loisir ,cinema ;
    private ImageView back;
    private FloatingActionButton help;

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
        setContentView(R.layout.activity_second);

        concert = findViewById(R.id.concert);
        spectacle = findViewById(R.id.spectacle);
        sport = findViewById(R.id.sport);
        cinema = findViewById(R.id.cinema);
        loisir = findViewById(R.id.loisir);
        festival = findViewById(R.id.festival);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        help = findViewById(R.id.floatingActionHelp);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*showHelpDialog()*/;
            }
        });

        final Intent eventIntent = new Intent(SecondActivity.this,
                SecondImageActivity.class);

        concert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventIntent.putExtra("event","Concert");
                startActivity(eventIntent);
            }
        });
        spectacle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventIntent.putExtra("event","Spectacle");
                startActivity(eventIntent);
            }
        });
        festival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventIntent.putExtra("event","Festival");
                startActivity(eventIntent);
            }
        });
        sport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventIntent.putExtra("event","Sport");
                startActivity(eventIntent);
            }
        });
        cinema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventIntent.putExtra("event","Cinema");
                startActivity(eventIntent);
            }
        });
        loisir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventIntent.putExtra("event","Loisir");
                startActivity(eventIntent);
            }
        });
    }

    /*
    private void showHelpDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("HELP");
        dialog.setMessage("Aide pour la categorie");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.nav1,null);

        dialog.setView(register_layout);

        //set button
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    */
}