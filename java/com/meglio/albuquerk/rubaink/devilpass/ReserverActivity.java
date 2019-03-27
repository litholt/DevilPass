package com.meglio.albuquerk.rubaink.devilpass;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ReserverActivity extends AppCompatActivity {

    private String inf_evId,inf_nam,inf_ima,inf_th,inf_jh,inf_li,inf_des;
    private String inf_name_place1,inf_name_place2,inf_name_place3,inf_name_place4;
    private String inf_price1,inf_price2,inf_price3,inf_price4;
    private TextView evName;
    private FirebaseFirestore firebaseFirestore;
    private TextView zone1,prix1,loge1,zone2,prix2,loge2,zone3,prix3,loge3,zone4,prix4,loge4;
    private ImageView back;
    private CardView reserve1,reserve2,reserve3,reserve4;

    //Ctrl+O
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase)); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Ubuntu-R.ttf")
                .setFontAttrId(R.attr.fontPath).build());//USD EUR
        setContentView(R.layout.activity_reserver);

        firebaseFirestore = FirebaseFirestore.getInstance();
        evName = findViewById(R.id.eventName);
        evName.setText(inf_nam);

        zone1 = findViewById(R.id.zone1);zone2 = findViewById(R.id.zone2);
        zone3 = findViewById(R.id.zone3);zone4 = findViewById(R.id.zone4);

        prix1 = findViewById(R.id.prix1);prix2 = findViewById(R.id.prix2);
        prix3 = findViewById(R.id.prix3);prix4 = findViewById(R.id.prix4);

        reserve1 = findViewById(R.id.card1);reserve2 = findViewById(R.id.card2);
        reserve3 = findViewById(R.id.card3);reserve4 = findViewById(R.id.card4);

        inf_evId = getIntent().getStringExtra("eventId");
        inf_nam = getIntent().getStringExtra("eventNa");
        inf_ima = getIntent().getStringExtra("eventIm");
        inf_th = getIntent().getStringExtra("eventTh");
        inf_jh = getIntent().getStringExtra("eventDa");
        inf_li = getIntent().getStringExtra("eventLi");
        inf_des = getIntent().getStringExtra("eventDe");

        firebaseFirestore.collection("EVENTO").document(inf_evId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    //String name = task.getResult().getString("name");
                    //String user_id = task.getResult().getString("user_id");

                    String nombreLoge = Objects.requireNonNull(task.getResult()).getString("loge");

                    if (nombreLoge != null){
                        switch (nombreLoge) {
                            case "4": {
                                reserve4.setVisibility(View.VISIBLE);
                                reserve3.setVisibility(View.VISIBLE);
                                reserve2.setVisibility(View.VISIBLE);
                                reserve1.setVisibility(View.VISIBLE);

                                String z1 = task.getResult().getString("z1");
                                String p1 = task.getResult().getString("p1");
                                String d1 = task.getResult().getString("d1");//desc1.setText("-Loge 1 : " + d1);
                                zone1.setText(z1 + " : " + d1);
                                prix1.setText(p1 + " Euro");
                                inf_price1 = p1;
                                inf_name_place1 = z1 + " : " + d1;

                                String z2 = task.getResult().getString("z2");
                                String p2 = task.getResult().getString("p2");
                                String d2 = task.getResult().getString("d2");//desc2.setText("-Loge 2 : " + d2);
                                zone2.setText(z2 + " : " + d2);
                                prix2.setText(p2 + " Euro");
                                inf_price2 = p2;
                                inf_name_place2 = z2 + " : " + d2;

                                String z3 = task.getResult().getString("z3");
                                String p3 = task.getResult().getString("p3");
                                String d3 = task.getResult().getString("d3");//desc3.setText("-Loge 3 : " + d3);
                                zone3.setText(z3 + " : " + d3);
                                prix3.setText(p3 + " Euro");
                                inf_price3 = p3;
                                inf_name_place3 = z3 + " : " + d3;

                                String z4 = task.getResult().getString("z4");
                                String p4 = task.getResult().getString("p4");
                                String d4 = task.getResult().getString("d4");//desc4.setText("-Loge 4 : " + d4);
                                zone4.setText(z4 + " : " + d4);
                                prix4.setText(p4 + " Euro");
                                inf_price4 = p4;
                                inf_name_place4 = z4 + " : " + d4;

                                break;
                            }
                            case "3": {
                                reserve3.setVisibility(View.VISIBLE);
                                reserve2.setVisibility(View.VISIBLE);
                                reserve1.setVisibility(View.VISIBLE);

                                String z1 = task.getResult().getString("z1");
                                String p1 = task.getResult().getString("p1");
                                String d1 = task.getResult().getString("d1");//desc1.setText("-Loge 1 : " + d1);
                                zone1.setText(z1 + " : " + d1);
                                prix1.setText(p1 + " Euro");
                                inf_price1 = p1 ;
                                inf_name_place1 = z1 + " : " + d1;

                                String z2 = task.getResult().getString("z2");
                                String p2 = task.getResult().getString("p2");
                                String d2 = task.getResult().getString("d2");//desc2.setText("-Loge 2 : " + d2);
                                zone2.setText(z2 + " : " + d2);
                                prix2.setText(p2 + " Euro");
                                inf_price2 = p2;
                                inf_name_place2 = z2 + " : " + d2;

                                String z3 = task.getResult().getString("z3");
                                String p3 = task.getResult().getString("p3");
                                String d3 = task.getResult().getString("d3");//desc3.setText("-Loge 3 : " + d3);
                                zone3.setText(z3 + " : " + d3);
                                prix3.setText(p3 + " Euro");
                                inf_price3 = p3;
                                inf_name_place3 = z3 + " : " + d3;

                                break;
                            }
                            case "2": {
                                reserve1.setVisibility(View.VISIBLE);
                                reserve2.setVisibility(View.VISIBLE);

                                String z1 = task.getResult().getString("z1");
                                String p1 = task.getResult().getString("p1");
                                String d1 = task.getResult().getString("d1");//desc1.setText("-Loge 1 : " + d1);
                                zone1.setText(z1 + " : " + d1);
                                prix1.setText(p1 + " Euro");
                                inf_price1 = p1;
                                inf_name_place1 = z1 + " : " + d1;

                                String z2 = task.getResult().getString("z2");
                                String p2 = task.getResult().getString("p2");
                                String d2 = task.getResult().getString("d2");//desc2.setText("-Loge 2 : " + d2);
                                zone2.setText(z2 + " : " + d2);
                                prix2.setText(p2 + " Euro");
                                inf_price2 = p2;
                                inf_name_place2 = z2 + " : " + d2;

                                break;
                            }
                            case "1": {
                                reserve1.setVisibility(View.VISIBLE);

                                String z1 = task.getResult().getString("z1");
                                String p1 = task.getResult().getString("p1");
                                String d1 = task.getResult().getString("d1");//desc1.setText("-Loge 1 : " + d1);
                                zone1.setText(z1 + " : " + d1);
                                prix1.setText(p1 + " Euro");
                                inf_price1 = p1;
                                inf_name_place1 = z1 + " : " + d1;

                                break;
                            }
                        }
                    }

                } else {
                    Toast.makeText(ReserverActivity.this,"(Error)" , Toast.LENGTH_LONG).show();
                }
            }
        });

        reserve1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent t = new Intent(ReserverActivity.this, ConeActivity.class);

                t.putExtra("eventId",inf_evId);
                t.putExtra("eventNa",inf_nam);
                t.putExtra("eventIm",inf_ima);
                t.putExtra("eventTh",inf_th);
                t.putExtra("eventDa",inf_jh);
                t.putExtra("eventLi",inf_li);
                t.putExtra("eventDe",inf_des);
                t.putExtra("eventPrice",inf_price1);
                t.putExtra("eventNP",inf_name_place1);
                t.putExtra("loge","1");
                startActivity(t);
            }
        });
        reserve2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent t = new Intent(ReserverActivity.this, ConeActivity.class);

                t.putExtra("eventId",inf_evId);
                t.putExtra("eventNa",inf_nam);
                t.putExtra("eventIm",inf_ima);
                t.putExtra("eventTh",inf_th);
                t.putExtra("eventDa",inf_jh);
                t.putExtra("eventLi",inf_li);
                t.putExtra("eventDe",inf_des);
                t.putExtra("eventPrice",inf_price2);
                t.putExtra("eventNP",inf_name_place2);
                t.putExtra("loge","2");
                startActivity(t);
            }
        });
        reserve3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent t = new Intent(ReserverActivity.this, ConeActivity.class);

                t.putExtra("eventId",inf_evId);
                t.putExtra("eventNa",inf_nam);
                t.putExtra("eventIm",inf_ima);
                t.putExtra("eventTh",inf_th);
                t.putExtra("eventDa",inf_jh);
                t.putExtra("eventLi",inf_li);
                t.putExtra("eventDe",inf_des);
                t.putExtra("eventPrice",inf_price3);
                t.putExtra("eventNP",inf_name_place3);
                t.putExtra("loge","3");
                startActivity(t);
            }
        });
        reserve4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent t = new Intent(ReserverActivity.this, ConeActivity.class);

                t.putExtra("eventId",inf_evId);
                t.putExtra("eventNa",inf_nam);
                t.putExtra("eventIm",inf_ima);
                t.putExtra("eventTh",inf_th);
                t.putExtra("eventDa",inf_jh);
                t.putExtra("eventLi",inf_li);
                t.putExtra("eventDe",inf_des);
                t.putExtra("eventPrice",inf_price4);
                t.putExtra("eventNP",inf_name_place4);
                t.putExtra("loge","4");
                startActivity(t);
            }
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
