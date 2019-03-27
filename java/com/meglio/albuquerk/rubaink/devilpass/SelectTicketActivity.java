package com.meglio.albuquerk.rubaink.devilpass;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SelectTicketActivity extends AppCompatActivity {

    private ImageView back,help;
    private TextView eventTitle;
    private String inf,place,image;
    private CardView suivant;
    private TextInputEditText zone1,prix1,desc1,total1,zone2,prix2
            ,desc2,total2,zone3,prix3,desc3,total3,zone4,prix4,desc4,total4;

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
                //@font/unifrakturmaguntia
                .setDefaultFontPath("fonts/Ubuntu-R.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_select_ticket);

        //eventTitle = findViewById(R.id.concert);

        zone1 = findViewById(R.id.z1);
        zone2 = findViewById(R.id.z2);
        zone3 = findViewById(R.id.z3);
        zone4 = findViewById(R.id.z4);

        prix1 = findViewById(R.id.p1);
        prix2 = findViewById(R.id.p2);
        prix3 = findViewById(R.id.p3);
        prix4 = findViewById(R.id.p4);

        desc1 = findViewById(R.id.d1);
        desc2 = findViewById(R.id.d2);
        desc3 = findViewById(R.id.d3);
        desc4 = findViewById(R.id.d4);

        total1 = findViewById(R.id.t1);
        total2 = findViewById(R.id.t2);
        total3 = findViewById(R.id.t3);
        total4 = findViewById(R.id.t4);

        /*help = findViewById(R.id.floatingActionHelp);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelpDialog();
            }
        });*/
        suivant = findViewById(R.id.suivant);

        inf = getIntent().getStringExtra("event");
        image = getIntent().getStringExtra("image");
        //eventTitle.setText(inf);

        final Intent concertIntent = new Intent(
                SelectTicketActivity.this, SelectLocatActivity.class);
        suivant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String z1 = zone1.getText().toString();
                final String z2 = zone2.getText().toString();
                final String z3 = zone3.getText().toString();
                final String z4 = zone4.getText().toString();

                final String p1 = prix1.getText().toString();
                final String p2 = prix2.getText().toString();
                final String p3 = prix3.getText().toString();
                final String p4 = prix4.getText().toString();

                final String d1 = desc1.getText().toString();
                final String d2 = desc2.getText().toString();
                final String d3 = desc3.getText().toString();
                final String d4 = desc4.getText().toString();

                final String t1 = total1.getText().toString();
                final String t2 = total2.getText().toString();
                final String t3 = total3.getText().toString();
                final String t4 = total4.getText().toString();

                if( !TextUtils.isEmpty(z1) && !TextUtils.isEmpty(p1) && !TextUtils.isEmpty(d1) && !TextUtils.isEmpty(t1)
                        && !TextUtils.isEmpty(z2) && !TextUtils.isEmpty(p2) && !TextUtils.isEmpty(d2) && !TextUtils.isEmpty(t2)
                        && !TextUtils.isEmpty(z3) && !TextUtils.isEmpty(p3) && !TextUtils.isEmpty(d3) && !TextUtils.isEmpty(t3)
                        && !TextUtils.isEmpty(z4) && !TextUtils.isEmpty(p4) && !TextUtils.isEmpty(d4) && !TextUtils.isEmpty(t4) ){

                    concertIntent.putExtra("event",inf);
                    concertIntent.putExtra("place","4");
                    concertIntent.putExtra("image",image);
                    concertIntent.putExtra("type","aa");

                    concertIntent.putExtra("z1",z1);
                    concertIntent.putExtra("p1",p1);
                    concertIntent.putExtra("d1",d1);
                    concertIntent.putExtra("t1",t1);

                    concertIntent.putExtra("z2",z2);
                    concertIntent.putExtra("p2",p2);
                    concertIntent.putExtra("d2",d2);
                    concertIntent.putExtra("t2",t2);

                    concertIntent.putExtra("z3",z3);
                    concertIntent.putExtra("p3",p3);
                    concertIntent.putExtra("d3",d3);
                    concertIntent.putExtra("t3",t3);

                    concertIntent.putExtra("z4",z4);
                    concertIntent.putExtra("p4",p4);
                    concertIntent.putExtra("d4",d4);
                    concertIntent.putExtra("t4",t4);

                    startActivity(concertIntent);

                }else if (!TextUtils.isEmpty(z1) && !TextUtils.isEmpty(p1) && !TextUtils.isEmpty(d1) && !TextUtils.isEmpty(t1)
                        && !TextUtils.isEmpty(z2) && !TextUtils.isEmpty(p2) && !TextUtils.isEmpty(d2) && !TextUtils.isEmpty(t2)
                        && !TextUtils.isEmpty(z3) && !TextUtils.isEmpty(p3) && !TextUtils.isEmpty(d3) && !TextUtils.isEmpty(t3)
                        && TextUtils.isEmpty(z4) && TextUtils.isEmpty(p4) && TextUtils.isEmpty(d4) && TextUtils.isEmpty(t4)) {

                    concertIntent.putExtra("event",inf);
                    concertIntent.putExtra("place","3");
                    concertIntent.putExtra("image",image);
                    concertIntent.putExtra("type","aa");

                    concertIntent.putExtra("z1",z1);
                    concertIntent.putExtra("p1",p1);
                    concertIntent.putExtra("d1",d1);
                    concertIntent.putExtra("t1",t1);

                    concertIntent.putExtra("z2",z2);
                    concertIntent.putExtra("p2",p2);
                    concertIntent.putExtra("d2",d2);
                    concertIntent.putExtra("t2",t2);

                    concertIntent.putExtra("z3",z3);
                    concertIntent.putExtra("p3",p3);
                    concertIntent.putExtra("d3",d3);
                    concertIntent.putExtra("t3",t3);

                    startActivity(concertIntent);
                }
                else if (!TextUtils.isEmpty(z1) && !TextUtils.isEmpty(p1) && !TextUtils.isEmpty(d1) && !TextUtils.isEmpty(t1)
                        && !TextUtils.isEmpty(z2) && !TextUtils.isEmpty(p2) && !TextUtils.isEmpty(d2) && !TextUtils.isEmpty(t2)
                        && TextUtils.isEmpty(z3) && TextUtils.isEmpty(p3) && TextUtils.isEmpty(d3) && TextUtils.isEmpty(t3)
                        && TextUtils.isEmpty(z4) && TextUtils.isEmpty(p4) && TextUtils.isEmpty(d4) && TextUtils.isEmpty(t4)) {

                    concertIntent.putExtra("event",inf);
                    concertIntent.putExtra("place","2");
                    concertIntent.putExtra("image",image);
                    concertIntent.putExtra("type","aa");

                    concertIntent.putExtra("z1",z1);
                    concertIntent.putExtra("p1",p1);
                    concertIntent.putExtra("d1",d1);
                    concertIntent.putExtra("t1",t1);

                    concertIntent.putExtra("z2",z2);
                    concertIntent.putExtra("p2",p2);
                    concertIntent.putExtra("d2",d2);
                    concertIntent.putExtra("t2",t2);

                    startActivity(concertIntent);

                }else if (!TextUtils.isEmpty(z1) && !TextUtils.isEmpty(p1) && !TextUtils.isEmpty(d1) && !TextUtils.isEmpty(t1)
                        && TextUtils.isEmpty(z2) && TextUtils.isEmpty(p2) && TextUtils.isEmpty(d2) && TextUtils.isEmpty(t2)
                        && TextUtils.isEmpty(z3) && TextUtils.isEmpty(p3) && TextUtils.isEmpty(d3) && TextUtils.isEmpty(t3)
                        && TextUtils.isEmpty(z4) && TextUtils.isEmpty(p4) && TextUtils.isEmpty(d4) && TextUtils.isEmpty(t4)) {

                    concertIntent.putExtra("event",inf);
                    concertIntent.putExtra("place","1");
                    concertIntent.putExtra("image",image);
                    concertIntent.putExtra("type","aa");

                    concertIntent.putExtra("z1",z1);
                    concertIntent.putExtra("p1",p1);
                    concertIntent.putExtra("d1",d1);
                    concertIntent.putExtra("t1",t1);

                    startActivity(concertIntent);
                }else {
                    Toast.makeText(SelectTicketActivity.this, "Completer", Toast.LENGTH_LONG).show();
                }
            }
        });
        back= findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    /*
    private void showHelpDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("LES RESERVATIONS");
        dialog.setMessage("Fixer vos reservations et prix");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.nav2,null);

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
