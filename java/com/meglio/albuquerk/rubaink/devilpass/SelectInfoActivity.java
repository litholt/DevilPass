package com.meglio.albuquerk.rubaink.devilpass;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SelectInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private String inf,image_st,type;
    private EditText selectDate,selectTime,selectName
            ,selectWeb,selectLieu,selectDescription,selectInfo,selectSponsor;

    private int mYear, mMonth, mDay, mHour, mMinute;
    private ImageView image,back;
    private Uri selectImage = null;
    private TextView eventTitle;
    private Button createEvento;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private DatabaseReference mDatabase,mPosts;

    private String zone1,prix1,desc1,total1,zone2,prix2
            ,desc2,total2,zone3,prix3,desc3,total3,zone4,prix4,desc4,total4;

    private String reste1 = "0",reste2 = "0",reste3 = "0",reste4 = "0",//reste economique apres prelevement = reste
            nombreBilletvendu1 = "0",nombreBilletvendu2 = "0",nombreBilletvendu3 = "0",nombreBilletvendu4 = "0";

    private String compteClient = "0",compteAdmin = "0";
    private Bitmap compressedImageFile;
    private String user_id,place;

    private ImageView imageView;
    private ProgressBar progressBar;

    private  String lo,la,ad,ci,co,code;

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
        setContentView(R.layout.activity_select_info);

        la = getIntent().getStringExtra("lat");
        lo = getIntent().getStringExtra("long");
        ad = getIntent().getStringExtra("addr");
        co = getIntent().getStringExtra("co");
        code = getIntent().getStringExtra("code").toUpperCase();
        ci = getIntent().getStringExtra("ci");

        inf = getIntent().getStringExtra("event");
        imageView = findViewById(R.id.image_event);
        image_st = getIntent().getStringExtra("image");
        selectImage = Uri.parse(image_st);

        if (!SelectInfoActivity.this.isFinishing()){
            Glide.with(SelectInfoActivity.this).load(selectImage).into(imageView);
        }

        zone1 = getIntent().getStringExtra("z1");
        prix1 = getIntent().getStringExtra("p1");
        desc1 = getIntent().getStringExtra("d1");
        total1 = getIntent().getStringExtra("t1");

        zone2 = getIntent().getStringExtra("z2");
        prix2 = getIntent().getStringExtra("p2");
        desc2 = getIntent().getStringExtra("d2");
        total2 = getIntent().getStringExtra("t2");

        zone3 = getIntent().getStringExtra("z3");
        prix3 = getIntent().getStringExtra("p3");
        desc3 = getIntent().getStringExtra("d3");
        total3 = getIntent().getStringExtra("t3");

        zone4 = getIntent().getStringExtra("z4");
        prix4 = getIntent().getStringExtra("p4");
        desc4 = getIntent().getStringExtra("d4");
        total4 = getIntent().getStringExtra("t4");

        place = getIntent().getStringExtra("place");
        type = getIntent().getStringExtra("type");

        image = findViewById(R.id.image);

        selectDate=(EditText)findViewById(R.id.date);
        selectTime=(EditText)findViewById(R.id.time);
        selectWeb=(EditText)findViewById(R.id.urlweb);
        selectLieu=(EditText)findViewById(R.id.lieu);
        selectInfo = findViewById(R.id.info);
        selectSponsor= findViewById(R.id.spons);
        selectDescription=(EditText)findViewById(R.id.description);

        createEvento = findViewById(R.id.evento);
        selectName = findViewById(R.id.eEventName);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        back= findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //eventTitle = findViewById(R.id.concert);
        //eventTitle.setText(inf + " Information");

        selectDate.setOnClickListener(this);
        selectTime.setOnClickListener(this);

        createEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String eventoDate = selectDate.getText().toString();
                final String eventoTime = selectTime.getText().toString();
                final String eventoName = selectName.getText().toString();
                final String eventoWeb = selectWeb.getText().toString();
                final String eventoLieu = selectLieu.getText().toString();
                final String eventoSpons = selectSponsor.getText().toString();
                final String eventoInfo = selectInfo.getText().toString();
                final String eventoDescription = selectDescription.getText().toString();

                if(!TextUtils.isEmpty(eventoDate)
                        && !TextUtils.isEmpty(eventoTime)
                        && !TextUtils.isEmpty(eventoName)
                        && !TextUtils.isEmpty(eventoWeb)
                        && !TextUtils.isEmpty(eventoLieu)
                        && !TextUtils.isEmpty(eventoInfo)
                        && !TextUtils.isEmpty(eventoDescription)
                        && !TextUtils.isEmpty(zone1)
                        && selectImage != null){ //uriImage

                    progressBar.setVisibility(View.VISIBLE);
                    createEventoSiEnable();

                    final String randomName = UUID.randomUUID().toString();

                    File newImageFile = new File(selectImage.getPath());
                    try {

                        compressedImageFile = new Compressor(SelectInfoActivity.this)
                                .setMaxHeight(520)//720
                                .setMaxWidth(520)//
                                .setQuality(40)//50
                                .compressToBitmap(newImageFile);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageData = baos.toByteArray();

                    // PHOTO UPLOAD
                    UploadTask filePath = storageReference.child("EventoImmagine").child(randomName + ".jpg").putBytes(imageData);
                    filePath.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUri = uri.toString();
                                    File newThumbFile = new File(selectImage.getPath());
                                    try {
                                        compressedImageFile = new Compressor(SelectInfoActivity.this)
                                                .setMaxHeight(50)
                                                .setMaxWidth(50)
                                                .setQuality(1)
                                                .compressToBitmap(newThumbFile);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] thumbData = baos.toByteArray();

                                    UploadTask uploadTask = storageReference.child("EventoImmagine/thumbs")
                                            .child(randomName + ".jpg").putBytes(thumbData);
                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            //getUri
                                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener
                                                    (new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String downloadthumbUri = uri.toString();
                                                    final Map<String, Object> postMap = new HashMap<>();

                                                    postMap.put("imageUrl", downloadUri);
                                                    postMap.put("imageThumb", downloadthumbUri);
                                                    postMap.put("name", eventoName);
                                                    postMap.put("lower", eventoName.toLowerCase());
                                                    postMap.put("time", eventoTime);postMap.put("day", eventoDate);
                                                    postMap.put("web", eventoWeb);
                                                    postMap.put("sponsor", eventoSpons);
                                                    postMap.put("lieu", eventoLieu);
                                                    postMap.put("info", eventoInfo);
                                                    postMap.put("description", eventoDescription);
                                                    postMap.put("user_id", user_id);
                                                    postMap.put("group", inf);
                                                    postMap.put("loge", place);
                                                    postMap.put("type", type);
                                                    postMap.put("timestamp", FieldValue.serverTimestamp());
                                                    postMap.put("address", ad);
                                                    postMap.put("code", code);
                                                    //postMap.put("country", co);
                                                   // postMap.put("city", ci);
                                                    if (zone1 != null){
                                                        postMap.put("z1", zone1);
                                                        postMap.put("p1", prix1);
                                                        postMap.put("d1", desc1);
                                                    }
                                                    if (zone2 != null){
                                                        postMap.put("z2", zone2);
                                                        postMap.put("p2", prix2);
                                                        postMap.put("d2", desc2);
                                                    }
                                                    if (zone3 != null){
                                                        postMap.put("z3", zone3);
                                                        postMap.put("d3", desc3);
                                                        postMap.put("p3", prix3);
                                                    }
                                                    if (zone4 != null){
                                                        postMap.put("p4", prix4);
                                                        postMap.put("d4", desc4);
                                                        postMap.put("z4", zone4);
                                                    }
                                                    //1'
                                                    firebaseFirestore.collection("EVENTO").add(postMap)
                                                            .addOnSuccessListener( new OnSuccessListener<DocumentReference>() {
                                                                @Override
                                                                public void onSuccess(DocumentReference documentReference) {

                                                                    //ID OF EVENT
                                                                    final String newId = documentReference.getId();

                                                                    //2' FOR MAPS EVENT
                                                                    Map<String, Object> postMap = new HashMap<>();
                                                                    postMap.put("latitude", Double.parseDouble(la));
                                                                    postMap.put("longitude", Double.parseDouble(lo));
                                                                    postMap.put("address", ad);
                                                                    postMap.put("name", eventoName);
                                                                    postMap.put("code", code);
                                                                    postMap.put("user_id", user_id);

                                                                    firebaseFirestore.collection("EVENTLOCATION").document(newId).set(postMap)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {

                                                                                    final String groupe = inf.toUpperCase();

                                                                                    //3' GROUPS
                                                                                    final Map<String, Object> ap = new HashMap<>();
                                                                                    ap.put("timestamp", FieldValue.serverTimestamp());
                                                                                    ap.put("user_id", user_id);
                                                                                    firebaseFirestore.collection("Groupe/" + "Event/" + groupe).document(newId).set(ap)
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {

                                                                                                    //4' COUNTRY
                                                                                                    firebaseFirestore.collection("Country/" + "code/" + code).document(newId).set(ap)
                                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onSuccess(Void aVoid) {

                                                                                                                    //4''
                                                                                                                    firebaseFirestore.collection("Country/" + code + "/" + groupe).document(newId).set(ap);

                                                                                                                    //EVENT PLACE AND PRICE
                                                                                                                    final Map<String, Object> postMap = new HashMap<>();
                                                                                                                    postMap.put("timestamp", FieldValue.serverTimestamp());
                                                                                                                    postMap.put("user_id", user_id);
                                                                                                                    if (zone1 != null){
                                                                                                                        postMap.put("nBv1", nombreBilletvendu1);//Nombre de place vendu
                                                                                                                        postMap.put("rEc1", reste1);//Reste economique apres
                                                                                                                        postMap.put("t1", total1);
                                                                                                                    }
                                                                                                                    if (zone2 != null){
                                                                                                                        postMap.put("nBv2", nombreBilletvendu2);
                                                                                                                        postMap.put("rEc2", reste2);
                                                                                                                        postMap.put("t2", total2);
                                                                                                                    }
                                                                                                                    if (zone3 != null){
                                                                                                                        postMap.put("nBv3", nombreBilletvendu3);
                                                                                                                        postMap.put("rEc3", reste3);
                                                                                                                        postMap.put("t3", total3);
                                                                                                                    }
                                                                                                                    if (zone4 != null){
                                                                                                                        postMap.put("nBv4", nombreBilletvendu4);
                                                                                                                        postMap.put("rEc4", reste4);
                                                                                                                        postMap.put("t4", total4);
                                                                                                                    }
                                                                                                                    postMap.put("cAdmin", compteAdmin);
                                                                                                                    postMap.put("cClient", compteClient);
                                                                                                                    //5'
                                                                                                                    firebaseFirestore.collection("EVENTO2").document(newId).set(postMap)
                                                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onSuccess(Void aVoid) {

                                                                                                                                    //6' EVENT AT CURRENT USERS
                                                                                                                                    final Map<String, Object> ap2 = new HashMap<>();
                                                                                                                                    ap2.put("timestamp", FieldValue.serverTimestamp());
                                                                                                                                    firebaseFirestore.collection("USERS/" + user_id + "/EVENTO")
                                                                                                                                            .document(newId).set(ap2)
                                                                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                                @Override
                                                                                                                                                public void onSuccess(Void aVoid) {

                                                                                                                                                    //7' POSTS FINAL
                                                                                                                                                    firebaseFirestore.collection("POSTS")
                                                                                                                                                            .document(newId).set(ap)
                                                                                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                                                @Override
                                                                                                                                                                public void onSuccess(Void aVoid) {
                                                                                                                                                                    success();
                                                                                                                                                                }
                                                                                                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                                                                                        @Override
                                                                                                                                                        public void onFailure(@NonNull Exception e) { fail(); }}); }
                                                                                                                                            })
                                                                                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                                                                        @Override
                                                                                                                                        public void onFailure(@NonNull Exception e) { fail(); }
                                                                                                                                    }) ; }
                                                                                                                            })
                                                                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                                                        @Override
                                                                                                                        public void onFailure(@NonNull Exception e) { fail(); }
                                                                                                                    }); }
                                                                                                            })
                                                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                                        @Override
                                                                                                        public void onFailure(@NonNull Exception e) { fail(); }
                                                                                                    }); }
                                                                                            })
                                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) { fail(); }}); }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) { fail(); }
                                                                    });
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                    createEventoNoEnable();
                                                                    Toast.makeText(SelectInfoActivity.this, "Error", Toast.LENGTH_LONG).show();
                                                                    createEvento.setEnabled(false);

                                                                }
                                                            });
                                                }
                                            } ) ;
                                        }
                                    } ) .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            fail();
                                        }
                                    });
                                }
                            });
                        }
                    });
                }else if (TextUtils.isEmpty(eventoName)){
                    Toast.makeText(SelectInfoActivity.this, "Name empty", Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(eventoDate)){
                    Toast.makeText(SelectInfoActivity.this, "Date empty", Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(eventoTime)){
                    Toast.makeText(SelectInfoActivity.this, "Time empty", Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(eventoWeb)){
                    Toast.makeText(SelectInfoActivity.this, "Web empty", Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(eventoDescription)){
                    Toast.makeText(SelectInfoActivity.this, "Description empty", Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(eventoTime)){
                    Toast.makeText(SelectInfoActivity.this, "Time empty", Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(eventoLieu)){
                    Toast.makeText(SelectInfoActivity.this, "Luogo empty", Toast.LENGTH_LONG).show();
                }
                else if (TextUtils.isEmpty(eventoInfo)){
                    Toast.makeText(SelectInfoActivity.this, "Information empty", Toast.LENGTH_LONG).show();
                }
                else if (selectImage == null){
                    Toast.makeText(SelectInfoActivity.this, "No Imagine", Toast.LENGTH_LONG).show();
                }
            }

        });
    }//End Create

    private void success() {
        progressBar.setVisibility(View.INVISIBLE);
        createEventoNoEnable();

        Intent nt = new Intent(SelectInfoActivity.this, MainActivity.class);
        Toast.makeText(SelectInfoActivity.this, "*** Success , Event Added ***",Toast.LENGTH_LONG).show();
        startActivity(nt);
        finish();
    }

    private void fail(){
        progressBar.setVisibility(View.INVISIBLE);
        createEventoNoEnable();

        Intent nt = new Intent(SelectInfoActivity.this, MainActivity.class);
        Toast.makeText(SelectInfoActivity.this, "Event not Added",Toast.LENGTH_LONG).show();
        startActivity(nt);
        finish();
    }

    private void createEventoNoEnable() {
        createEvento.setEnabled(true);
        createEvento.setClickable(true);
    }

    private void createEventoSiEnable() {
        createEvento.setEnabled(false);
        createEvento.setClickable(false);
    }

    @Override
    public void onClick(View view) {

        if (view == selectDate) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            selectDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (view == selectTime) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            selectTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }
}
/*
package com.meglio.albuquerk.rubaink.devilpass;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SelectInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private String inf,image_st,type;
    private EditText selectDate,selectTime,selectName
            ,selectWeb,selectLieu,selectDescription,selectInfo,selectSponsor;

    private int mYear, mMonth, mDay, mHour, mMinute;
    private ImageView image,back;
    private Uri selectImage = null;
    private TextView eventTitle;
    private Button createEvento;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private DatabaseReference mDatabase,mPosts;

    private String zone1,prix1,desc1,total1,zone2,prix2
            ,desc2,total2,zone3,prix3,desc3,total3,zone4,prix4,desc4,total4;

    private String reste1 = "0",reste2 = "0",reste3 = "0",reste4 = "0",//reste economique apres prelevement = reste
            nombreBilletvendu1 = "0",nombreBilletvendu2 = "0",nombreBilletvendu3 = "0",nombreBilletvendu4 = "0";

    private String compteClient = "0",compteAdmin = "0";
    private Bitmap compressedImageFile;
    private String user_id,place;

    private ImageView imageView;
    private ProgressBar progressBar;

    private  String lo,la,ad,ci,co;

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
        setContentView(R.layout.activity_select_info);

        la = getIntent().getStringExtra("lat");
        lo = getIntent().getStringExtra("long");
        ad = getIntent().getStringExtra("addr");
        co = getIntent().getStringExtra("co");
        ci = getIntent().getStringExtra("ci");

        inf = getIntent().getStringExtra("event");
        imageView = findViewById(R.id.image_event);
        image_st = getIntent().getStringExtra("image");
        selectImage = Uri.parse(image_st);

        Glide.with(SelectInfoActivity.this).load(selectImage).into(imageView);

        zone1 = getIntent().getStringExtra("z1");
        prix1 = getIntent().getStringExtra("p1");
        desc1 = getIntent().getStringExtra("d1");
        total1 = getIntent().getStringExtra("t1");

        zone2 = getIntent().getStringExtra("z2");
        prix2 = getIntent().getStringExtra("p2");
        desc2 = getIntent().getStringExtra("d2");
        total2 = getIntent().getStringExtra("t2");

        zone3 = getIntent().getStringExtra("z3");
        prix3 = getIntent().getStringExtra("p3");
        desc3 = getIntent().getStringExtra("d3");
        total3 = getIntent().getStringExtra("t3");

        zone4 = getIntent().getStringExtra("z4");
        prix4 = getIntent().getStringExtra("p4");
        desc4 = getIntent().getStringExtra("d4");
        total4 = getIntent().getStringExtra("t4");

        place = getIntent().getStringExtra("place");
        type = getIntent().getStringExtra("type");

        image = findViewById(R.id.image);

        selectDate=(EditText)findViewById(R.id.date);
        selectTime=(EditText)findViewById(R.id.time);
        selectWeb=(EditText)findViewById(R.id.urlweb);
        selectLieu=(EditText)findViewById(R.id.lieu);
        selectInfo = findViewById(R.id.info);
        selectSponsor= findViewById(R.id.spons);
        selectDescription=(EditText)findViewById(R.id.description);

        createEvento = findViewById(R.id.evento);
        selectName = findViewById(R.id.eEventName);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        back= findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //eventTitle = findViewById(R.id.concert);
        //eventTitle.setText(inf + " Information");

        selectDate.setOnClickListener(this);
        selectTime.setOnClickListener(this);

        createEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String eventoDate = selectDate.getText().toString();
                final String eventoTime = selectTime.getText().toString();
                final String eventoName = selectName.getText().toString();
                final String eventoWeb = selectWeb.getText().toString();
                final String eventoLieu = selectLieu.getText().toString();
                final String eventoSpons = selectSponsor.getText().toString();
                final String eventoInfo = selectInfo.getText().toString();
                final String eventoDescription = selectDescription.getText().toString();

                if(!TextUtils.isEmpty(eventoDate)
                        && !TextUtils.isEmpty(eventoTime)
                        && !TextUtils.isEmpty(eventoName)
                        && !TextUtils.isEmpty(eventoWeb)
                        && !TextUtils.isEmpty(eventoLieu)
                        && !TextUtils.isEmpty(eventoInfo)
                        && !TextUtils.isEmpty(eventoDescription)
                        && !TextUtils.isEmpty(zone1)
                        && selectImage != null){ //uriImage

                    progressBar.setVisibility(View.VISIBLE);
                    createEventoSiEnable();

                    final String randomName = UUID.randomUUID().toString();

                    File newImageFile = new File(selectImage.getPath());
                    try {

                        compressedImageFile = new Compressor(SelectInfoActivity.this)
                                .setMaxHeight(520)//720
                                .setMaxWidth(520)//
                                .setQuality(40)//50
                                .compressToBitmap(newImageFile);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageData = baos.toByteArray();

                    // PHOTO UPLOAD
                    UploadTask filePath = storageReference.child("EventoImmagine").child(randomName + ".jpg").putBytes(imageData);

                    filePath.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUri = uri.toString();

                                    File newThumbFile = new File(selectImage.getPath());
                                    try {

                                        compressedImageFile = new Compressor(SelectInfoActivity.this)
                                                .setMaxHeight(50)
                                                .setMaxWidth(50)
                                                .setQuality(1)
                                                .compressToBitmap(newThumbFile);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] thumbData = baos.toByteArray();

                                    UploadTask uploadTask = storageReference.child("EventoImmagine/thumbs")
                                            .child(randomName + ".jpg").putBytes(thumbData);

                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            //getUri
                                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener
                                                    (new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String downloadthumbUri = uri.toString();

                                                    final Map<String, Object> postMap = new HashMap<>();

                                                    postMap.put("imageUrl", downloadUri);
                                                    postMap.put("imageThumb", downloadthumbUri);
                                                    postMap.put("name", eventoName);
                                                    postMap.put("lower", eventoName.toLowerCase());
                                                    postMap.put("time", eventoTime);
                                                    postMap.put("day", eventoDate);
                                                    postMap.put("web", eventoWeb);
                                                    postMap.put("sponsor", eventoSpons);
                                                    postMap.put("lieu", eventoLieu);
                                                    postMap.put("info", eventoInfo);
                                                    postMap.put("description", eventoDescription);
                                                    postMap.put("user_id", user_id);
                                                    postMap.put("group", inf);
                                                    postMap.put("loge", place);
                                                    postMap.put("type", type);
                                                    postMap.put("timestamp", FieldValue.serverTimestamp());
                                                    postMap.put("address", ad);
                                                    postMap.put("country", co);
                                                    postMap.put("city", ci);
                                                    if (zone1 != null){
                                                        postMap.put("z1", zone1);
                                                        postMap.put("p1", prix1);
                                                        postMap.put("d1", desc1);
                                                        postMap.put("t1", total1);
                                                    }
                                                    if (zone2 != null){
                                                        postMap.put("z2", zone2);
                                                        postMap.put("p2", prix2);
                                                        postMap.put("d2", desc2);
                                                        postMap.put("t2", total2);
                                                    }
                                                    if (zone3 != null){
                                                        postMap.put("z3", zone3);
                                                        postMap.put("d3", desc3);
                                                        postMap.put("p3", prix3);
                                                        postMap.put("t3", total3);
                                                    }
                                                    if (zone4 != null){
                                                        postMap.put("p4", prix4);
                                                        postMap.put("d4", desc4);
                                                        postMap.put("z4", zone4);
                                                        postMap.put("t4", total4);
                                                    }
                                                    firebaseFirestore.collection("EVENTO").add(postMap)
                                                            .addOnSuccessListener( new OnSuccessListener<DocumentReference>() {
                                                                @Override
                                                                public void onSuccess(DocumentReference documentReference) {

                                                                    //ID OF EVENT
                                                                    final String newId = documentReference.getId();

                                                                    //FOR MAPS EVENT
                                                                    Map<String, Object> postMap = new HashMap<>();
                                                                    postMap.put("latitude", Double.parseDouble(la));
                                                                    postMap.put("longitude", Double.parseDouble(lo));
                                                                    postMap.put("address", ad);
                                                                    postMap.put("country", co);
                                                                    postMap.put("city", ci);
                                                                    postMap.put("eventid", newId);
                                                                    postMap.put("user_id", user_id);
                                                                    firebaseFirestore.collection("EVENTLOCATION").document(newId).set(postMap);

                                                                    //CREATE EVENT GROUP & PRICE
                                                                    final String ev = inf.toUpperCase();
                                                                    firebaseFirestore.collection(ev).document(newId).get()
                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                    if(!task.getResult().exists()){

                                                                                        //GROUP
                                                                                        Map<String, Object> likeMap = new HashMap<>();
                                                                                        likeMap.put("timestamp", FieldValue.serverTimestamp());
                                                                                        likeMap.put("user_id", user_id);
                                                                                        firebaseFirestore.collection(ev).document(newId).set(likeMap);
                                                                                        firebaseFirestore.collection(co).document(newId).set(likeMap);

                                                                                        //EVENT PLACE AND PRICE
                                                                                        final Map<String, Object> postMap = new HashMap<>();
                                                                                        postMap.put("timestamp", FieldValue.serverTimestamp());
                                                                                        postMap.put("user_id", user_id);
                                                                                        if (zone1 != null){
                                                                                            postMap.put("nBv1", nombreBilletvendu1);//Nombre de place vendu
                                                                                            postMap.put("rEc1", reste1);//Reste economique apres
                                                                                            // deduction du pourcentage de DP
                                                                                        }
                                                                                        if (zone2 != null){
                                                                                            postMap.put("nBv2", nombreBilletvendu2);
                                                                                            postMap.put("rEc2", reste2);
                                                                                        }
                                                                                        if (zone3 != null){
                                                                                            postMap.put("nBv3", nombreBilletvendu3);
                                                                                            postMap.put("rEc3", reste3);
                                                                                        }
                                                                                        if (zone4 != null){
                                                                                            postMap.put("nBv4", nombreBilletvendu4);
                                                                                            postMap.put("rEc4", reste4);
                                                                                        }
                                                                                        postMap.put("cAdmin", compteAdmin);
                                                                                        postMap.put("cClient", compteClient);
                                                                                        firebaseFirestore.collection("EVENTO2").document(newId).set(postMap);

                                                                                        //SIGNAL EVENT AT USERS
                                                                                        firebaseFirestore.collection("USERS/" + user_id + "/EVENTO")
                                                                                                .document(newId).set(likeMap)
                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(Void aVoid) {
                                                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                                                        createEventoNoEnable();
                                                                                                        Intent mainIntent = new Intent(
                                                                                                                SelectInfoActivity.this,
                                                                                                                MainActivity.class);
                                                                                                        Toast.makeText(SelectInfoActivity.this,
                                                                                                                "*** Success , Event Added ***",
                                                                                                                Toast.LENGTH_LONG).show();

                                                                                                        startActivity(mainIntent);
                                                                                                        finish();
                                                                                                    }
                                                                                                }) ;
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                    createEventoNoEnable();
                                                                    Toast.makeText(SelectInfoActivity.this, "(Error):" + e, Toast.LENGTH_LONG).show();
                                                                    createEvento.setEnabled(false);

                                                                }
                                                            });
                                                }
                                            } ) ;
                                        }
                                    } ) .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SelectInfoActivity.this, "(Error)" + e, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            });
                        }
                    });
                }else if (TextUtils.isEmpty(eventoName)){
                    Toast.makeText(SelectInfoActivity.this, "Name empty", Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(eventoDate)){
                    Toast.makeText(SelectInfoActivity.this, "Date empty", Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(eventoTime)){
                    Toast.makeText(SelectInfoActivity.this, "Time empty", Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(eventoWeb)){
                    Toast.makeText(SelectInfoActivity.this, "Web empty", Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(eventoDescription)){
                    Toast.makeText(SelectInfoActivity.this, "Description empty", Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(eventoTime)){
                    Toast.makeText(SelectInfoActivity.this, "Time empty", Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(eventoLieu)){
                    Toast.makeText(SelectInfoActivity.this, "Luogo empty", Toast.LENGTH_LONG).show();
                }
                else if (TextUtils.isEmpty(eventoInfo)){
                    Toast.makeText(SelectInfoActivity.this, "Information empty", Toast.LENGTH_LONG).show();
                }
                else if (selectImage == null){
                    Toast.makeText(SelectInfoActivity.this, "No Imagine", Toast.LENGTH_LONG).show();
                }
            }

        });
    }//End Create

    private void createEventoNoEnable() {
        createEvento.setEnabled(true);
        createEvento.setClickable(true);
    }

    private void createEventoSiEnable() {
        createEvento.setEnabled(false);
        createEvento.setClickable(false);
    }

    @Override
    public void onClick(View view) {

        if (view == selectDate) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            selectDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (view == selectTime) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            selectTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }
}
 */