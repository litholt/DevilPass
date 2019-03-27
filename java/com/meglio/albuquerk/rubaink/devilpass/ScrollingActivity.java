package com.meglio.albuquerk.rubaink.devilpass;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ScrollingActivity extends AppCompatActivity {

    private ImageView event_image,back;
    private FirebaseFirestore firebaseFirestore;
    private String evId,image,imageTh,namee,lieu,description,jour_h;
    private TextView event_name;
    private TextView user_name;
    private CircleImageView user_image;
    private String current_user_id;
    private FirebaseAuth firebaseAuth;
    private ImageView eventImage;
    private TextView evDescript,evLieu,evSite,evInfo,
            evDay,evSponsor,evName,groupE;
    private Button reserver;
    private CardView el1,el2,el3,el4,el5,el6,el7,el8;
    private ImageView imvQrCode;

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
        setContentView(R.layout.activity_scrolling);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        current_user_id = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        el1 = findViewById(R.id.element1);
        el2 = findViewById(R.id.element2);
        el3 = findViewById(R.id.element3);
        el4 = findViewById(R.id.element4);
        el5 = findViewById(R.id.element5);
        el6 = findViewById(R.id.element6);
        el7 = findViewById(R.id.element7);
        el8 = findViewById(R.id.element8);

        user_image = findViewById(R.id.eUserImage);
        user_name = findViewById(R.id.eUserName);
        evDay = findViewById(R.id.day);
        evName = findViewById(R.id.nameEv);
        evDescript = findViewById(R.id.description);
        evLieu = findViewById(R.id.lieu);
        evSponsor = findViewById(R.id.sponsEv);
        evSite = findViewById(R.id.event_site);
        evInfo = findViewById(R.id.info);
        groupE = findViewById(R.id.group);
        reserver = findViewById(R.id.reserver);
        imvQrCode = findViewById(R.id.eCode);

        event_image = findViewById(R.id.event_image_scroll);
        event_name = findViewById(R.id.eventName);

        firebaseFirestore = FirebaseFirestore.getInstance();
        evId = getIntent().getStringExtra("eventid");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        /*final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");// HH:mm:ss
        final Date dateSistem = new Date();
        String dateEvent = task.getResult().getString("day") + " "
                                       + task.getResult().getString("time");
                                try {
                                    Date d1 = dateFormat.parse(task.getResult().getString("day"));
                                    Toast.makeText(EventViewActivity.this,""+ dateSistem , Toast.LENGTH_LONG).show();
                                    if (d1.compareTo(dateSistem) > 0){
                                        Toast.makeText(EventViewActivity.this,"Futur" , Toast.LENGTH_LONG).show();
                                    }else if (d1.compareTo(dateSistem) < 0){
                                        Toast.makeText(EventViewActivity.this,"Deja" , Toast.LENGTH_LONG).show();
                                    }else
                                        Toast.makeText(EventViewActivity.this,"Aujourd'hui" , Toast.LENGTH_LONG).show();
                                } catch (ParseException e) {
                                    Toast.makeText(EventViewActivity.this,"Error " + e , Toast.LENGTH_LONG).show();
                                }*/

        firebaseFirestore.collection("EVENTO").document(evId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if(Objects.requireNonNull(task.getResult()).exists()){

                                image = task.getResult().getString("imageUrl");
                                imageTh = task.getResult().getString("imageThumb");
                                String user = task.getResult().getString("user_id");
                                namee = task.getResult().getString("name");
                                String time = task.getResult().getString("time");
                                String day = task.getResult().getString("day");jour_h = day + " " + time;
                                lieu = task.getResult().getString("lieu");

                                String spons = task.getResult().getString("sponsor");
                                String info = task.getResult().getString("info");
                                final String web = task.getResult().getString("web");
                                description = task.getResult().getString("description");
                                String group = task.getResult().getString("group");

                                groupE.setText("Name of " + group);
                                evName.setText(namee);
                                evDay.setText(day + " at " + time);
                                evLieu.setText(lieu);

                                if (spons!=null) evSponsor.setText("Sky'Pass , " + spons);
                                else evSponsor.setText("Sky'pass ");

                                evDescript.setText(description);
                                evInfo.setText(info);
                                evSite.setText(web);

                                if (!ScrollingActivity.this.isFinishing()){
                                    Glide.with(ScrollingActivity.this).load(image).thumbnail(
                                            Glide.with(ScrollingActivity.this).load(imageTh)).into(event_image);

                                }

                                event_name.setText(namee);
                                if (user != null){
                                    firebaseFirestore.collection("USERS").document(user).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> itask) {
                                                    if(task.isSuccessful()){
                                                        if(task.getResult().exists()){

                                                            String image = Objects.requireNonNull
                                                                    (itask.getResult()).getString("image");
                                                            String name = itask.getResult().getString("name");

                                                            user_name.setText(name);

                                                            if (!ScrollingActivity.this.isFinishing())
                                                                Glide.with(ScrollingActivity.this).load(image)
                                                                        .into(user_image);
                                                        }
                                                    } else {
                                                        Toast.makeText(ScrollingActivity.this,"(Error)"
                                                                , Toast.LENGTH_LONG).show(); } }});
                                }

                                el1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                                el2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent nt = new Intent(ScrollingActivity.this,
                                                WebActivity.class);
                                        startActivity(nt);
                                    }
                                });
                                el3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });

                                el4.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        firebaseFirestore.collection("EVENTLOCATION").document(evId).get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            if(Objects.requireNonNull(task.getResult()).exists()){
                                                                Double lat,lon;
                                                                Intent nt = new Intent(ScrollingActivity.this,
                                                                        EventLocActivity.class);
                                                                lat = task.getResult().getDouble("latitude");
                                                                lon = task.getResult().getDouble("longitude");
                                                                if (lat != null && lon != null){
                                                                    nt.putExtra("lat",Double.toString(lat));
                                                                    nt.putExtra("lon",Double.toString(lon));
                                                                    nt.putExtra("name",namee); }
                                                                startActivity(nt);  } }}}); }
                                });
                                el5.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                                el6.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent nt = new Intent(ScrollingActivity.this,
                                                WebActivity.class);
                                        nt.putExtra("lien",web);
                                        nt.putExtra("name",namee);
                                        startActivity(nt);
                                    }
                                });
                                el7.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                                el8.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                            }
                        } else {
                            Toast.makeText(ScrollingActivity.this,"(Error)" , Toast.LENGTH_LONG).show();
                        }
                    }
                });

        reserver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent t = new Intent(ScrollingActivity.this, ReserverActivity.class);
                //String image,imageTh,namee,lieu,description;

                t.putExtra("eventId",evId);
                t.putExtra("eventNa",namee);
                t.putExtra("eventIm",image);
                t.putExtra("eventTh",imageTh);
                t.putExtra("eventDa",jour_h);
                t.putExtra("eventLi",lieu);
                t.putExtra("eventDe",description);

                startActivity(t);
            }
        });

        back= findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if (evId != null){
            Bitmap bitmap = null;
            try {
                bitmap = textToImage(evId, 500, 500);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            imvQrCode.setImageBitmap(bitmap);
        }
    }

    private Bitmap textToImage(String text, int width, int height) throws WriterException, NullPointerException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE,
                    width, height, null);
        } catch (IllegalArgumentException Illegalargumentexception) {
            return null;
        }

        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        int colorWhite = 0xFFFFFFFF;
        int colorBlack = 0xFF000000;

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;
            for (int x = 0; x < bitMatrixWidth; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ? colorBlack : colorWhite;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, width, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

}
