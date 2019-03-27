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

public class SecondInfoActivity extends AppCompatActivity implements View.OnClickListener{


    private String inf,image_st,type;
    private EditText selectDate,selectTime,selectName
            ,selectWeb,selectLieu,selectDescription,selectInfo,selectSponsor;

    private int mYear, mMonth, mDay, mHour, mMinute;
    private ImageView back;
    private Uri selectImage = null;
    private TextView eventTitle;
    private Button createEvento;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private DatabaseReference mDatabase,mPosts;

    private String web,phone,email;

    private Bitmap compressedImageFile;
    private String user_id;

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
        setContentView(R.layout.activity_second_info);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        imageView = findViewById(R.id.image_event);

        la = getIntent().getStringExtra("lat");
        lo = getIntent().getStringExtra("long");
        ad = getIntent().getStringExtra("addr");
        code = getIntent().getStringExtra("code").toUpperCase();
        co = getIntent().getStringExtra("co");
        ci = getIntent().getStringExtra("ci");

        inf = getIntent().getStringExtra("event");
        image_st = getIntent().getStringExtra("image");
        selectImage = Uri.parse(image_st);

        if (!SecondInfoActivity.this.isFinishing()){
            Glide.with(SecondInfoActivity.this).load(selectImage).into(imageView);
        }


        web = getIntent().getStringExtra("web");
        phone = getIntent().getStringExtra("phone");
        email = getIntent().getStringExtra("email");
        type = getIntent().getStringExtra("type");

        selectDate= findViewById(R.id.date);
        selectTime= findViewById(R.id.time);
        selectWeb= findViewById(R.id.urlweb);
        selectLieu= findViewById(R.id.lieu);
        selectInfo = findViewById(R.id.info);
        selectSponsor = findViewById(R.id.spons);
        selectDescription= findViewById(R.id.description);

        createEvento = findViewById(R.id.evento);
        selectName = findViewById(R.id.eEventName);
        progressBar = findViewById(R.id.progressBar);

        back= findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
                final String eventoInfo = selectInfo.getText().toString();
                final String eventoSpons = selectSponsor.getText().toString();
                final String eventoDescription = selectDescription.getText().toString();

                if(!TextUtils.isEmpty(eventoDate)
                        && !TextUtils.isEmpty(eventoTime)
                        && !TextUtils.isEmpty(eventoName)
                        && !TextUtils.isEmpty(eventoWeb)
                        && !TextUtils.isEmpty(eventoLieu)
                        && !TextUtils.isEmpty(eventoInfo)
                        && !TextUtils.isEmpty(eventoDescription)
                        && selectImage != null){ //uriImage

                    progressBar.setVisibility(View.VISIBLE);
                    createEventoSiEnable();

                    final String randomName = UUID.randomUUID().toString();

                    File newImageFile = new File(selectImage.getPath());
                    try {

                        compressedImageFile = new Compressor(SecondInfoActivity.this)
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

                                        compressedImageFile = new Compressor(SecondInfoActivity.this)
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
                                                            postMap.put("lieu", eventoLieu);
                                                            postMap.put("sponsor", eventoSpons);
                                                            postMap.put("info", eventoInfo);
                                                            postMap.put("description", eventoDescription);
                                                            postMap.put("user_id", user_id);
                                                            postMap.put("group", inf);
                                                            postMap.put("type", type);
                                                            postMap.put("timestamp", FieldValue.serverTimestamp());
                                                            postMap.put("address", ad);
                                                            postMap.put("code", code);
                                                            //postMap.put("country", co);
                                                            //postMap.put("city", ci);

                                                            postMap.put("bfone", phone);
                                                            postMap.put("bmail", email);
                                                            postMap.put("bweb", web);

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
                                                                                                            //4 COUNTRY
                                                                                                            firebaseFirestore.collection("Country/" + "code/" + code).document(newId).set(ap)
                                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onSuccess(Void aVoid) {

                                                                                                                            //4'
                                                                                                                            firebaseFirestore.collection("Country/" + code + "/" + groupe)
                                                                                                                                    .document(newId).set(ap);

                                                                                                                            //EVENT PLACE AND PRICE
                                                                                                                            final Map<String, Object> postMap = new HashMap<>();
                                                                                                                            postMap.put("timestamp", FieldValue.serverTimestamp());
                                                                                                                            postMap.put("user_id", user_id);
                                                                                                                            postMap.put("bfone", phone);
                                                                                                                            postMap.put("bmail", email);
                                                                                                                            postMap.put("bweb", web);
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
                                                                                    }); }})
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {

                                                                            progressBar.setVisibility(View.INVISIBLE);
                                                                            createEventoNoEnable();
                                                                            Toast.makeText(SecondInfoActivity.this, "Error", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(SecondInfoActivity.this, "Name empty", Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(eventoDate)){
                    Toast.makeText(SecondInfoActivity.this, "Date empty", Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(eventoTime)){
                    Toast.makeText(SecondInfoActivity.this, "Time empty", Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(eventoWeb)){
                    Toast.makeText(SecondInfoActivity.this, "Web empty", Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(eventoDescription)){
                    Toast.makeText(SecondInfoActivity.this, "Description empty", Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(eventoTime)){
                    Toast.makeText(SecondInfoActivity.this, "Time empty", Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(eventoLieu)){
                    Toast.makeText(SecondInfoActivity.this, "Luogo empty", Toast.LENGTH_LONG).show();
                }
                else if (TextUtils.isEmpty(eventoInfo)){
                    Toast.makeText(SecondInfoActivity.this, "Information empty", Toast.LENGTH_LONG).show();
                }
                else if (selectImage == null){
                    Toast.makeText(SecondInfoActivity.this, "No Imagine", Toast.LENGTH_LONG).show();
                }
            }

        });
    }//End Create

    private void success() {
        progressBar.setVisibility(View.INVISIBLE);
        createEventoNoEnable();

        Intent nt = new Intent(SecondInfoActivity.this, MainActivity.class);
        Toast.makeText(SecondInfoActivity.this, "*** Success , Event Added ***",Toast.LENGTH_LONG).show();
        startActivity(nt);
        finish();
    }

    private void fail(){
        progressBar.setVisibility(View.INVISIBLE);
        createEventoNoEnable();

        Intent nt = new Intent(SecondInfoActivity.this, MainActivity.class);
        Toast.makeText(SecondInfoActivity.this, "Event not Added",Toast.LENGTH_LONG).show();
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

public class SecondInfoActivity extends AppCompatActivity implements View.OnClickListener{


    private String inf,image_st,type;
    private EditText selectDate,selectTime,selectName
            ,selectWeb,selectLieu,selectDescription,selectInfo,selectSponsor;

    private int mYear, mMonth, mDay, mHour, mMinute;
    private ImageView back;
    private Uri selectImage = null;
    private TextView eventTitle;
    private Button createEvento;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private DatabaseReference mDatabase,mPosts;

    private String web,phone,email;

    private Bitmap compressedImageFile;
    private String user_id;

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
        setContentView(R.layout.activity_second_info);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        imageView = findViewById(R.id.image_event);

        la = getIntent().getStringExtra("lat");
        lo = getIntent().getStringExtra("long");
        ad = getIntent().getStringExtra("addr");
        co = getIntent().getStringExtra("co");
        ci = getIntent().getStringExtra("ci");

        inf = getIntent().getStringExtra("event");
        image_st = getIntent().getStringExtra("image");
        selectImage = Uri.parse(image_st);
        Glide.with(SecondInfoActivity.this).load(selectImage).into(imageView);

        web = getIntent().getStringExtra("web");
        phone = getIntent().getStringExtra("phone");
        email = getIntent().getStringExtra("email");
        type = getIntent().getStringExtra("type");

        selectDate= findViewById(R.id.date);
        selectTime= findViewById(R.id.time);
        selectWeb= findViewById(R.id.urlweb);
        selectLieu= findViewById(R.id.lieu);
        selectInfo = findViewById(R.id.info);
        selectSponsor = findViewById(R.id.spons);
        selectDescription= findViewById(R.id.description);

        createEvento = findViewById(R.id.evento);
        selectName = findViewById(R.id.eEventName);
        progressBar = findViewById(R.id.progressBar);

        back= findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
                final String eventoInfo = selectInfo.getText().toString();
                final String eventoSpons = selectSponsor.getText().toString();
                final String eventoDescription = selectDescription.getText().toString();

                if(!TextUtils.isEmpty(eventoDate)
                        && !TextUtils.isEmpty(eventoTime)
                        && !TextUtils.isEmpty(eventoName)
                        && !TextUtils.isEmpty(eventoWeb)
                        && !TextUtils.isEmpty(eventoLieu)
                        && !TextUtils.isEmpty(eventoInfo)
                        && !TextUtils.isEmpty(eventoDescription)
                        && selectImage != null){ //uriImage

                    progressBar.setVisibility(View.VISIBLE);
                    createEventoSiEnable();

                    final String randomName = UUID.randomUUID().toString();

                    File newImageFile = new File(selectImage.getPath());
                    try {

                        compressedImageFile = new Compressor(SecondInfoActivity.this)
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

                                        compressedImageFile = new Compressor(SecondInfoActivity.this)
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
                                                            postMap.put("lieu", eventoLieu);
                                                            postMap.put("sponsor", eventoSpons);
                                                            postMap.put("info", eventoInfo);
                                                            postMap.put("description", eventoDescription);
                                                            postMap.put("user_id", user_id);
                                                            postMap.put("group", inf);
                                                            postMap.put("type", type);
                                                            postMap.put("timestamp", FieldValue.serverTimestamp());
                                                            postMap.put("address", ad);
                                                            postMap.put("country", co);
                                                            postMap.put("city", ci);

                                                            postMap.put("bfone", phone);
                                                            postMap.put("bmail", email);
                                                            postMap.put("bweb", web);

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
                                                                            firebaseFirestore.collection("EVENTLOCATION").document(newId).set(postMap)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {

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
                                                                                                                firebaseFirestore.collection("Country/" + co).document(newId).set(likeMap);

                                                                                                                //EVENT PLACE AND PRICE
                                                                                                                final Map<String, Object> postMap = new HashMap<>();
                                                                                                                postMap.put("timestamp", FieldValue.serverTimestamp());
                                                                                                                postMap.put("user_id", user_id);
                                                                                                                postMap.put("bfone", phone);
                                                                                                                postMap.put("bmail", email);
                                                                                                                postMap.put("bweb", web);
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
                                                                                                                                        SecondInfoActivity.this,
                                                                                                                                        MainActivity.class);
                                                                                                                                Toast.makeText(SecondInfoActivity.this,
                                                                                                                                        "*** Success , Event Added ***",
                                                                                                                                        Toast.LENGTH_LONG).show();

                                                                                                                                startActivity(mainIntent);
                                                                                                                                finish();
                                                                                                                            }
                                                                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                                                                    @Override
                                                                                                                    public void onFailure(@NonNull Exception e) {

                                                                                                                    }
                                                                                                                }) ;
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {

                                                                                }
                                                                            });
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {

                                                                            progressBar.setVisibility(View.INVISIBLE);
                                                                            createEventoNoEnable();
                                                                            Toast.makeText(SecondInfoActivity.this, "(Error):" + e, Toast.LENGTH_LONG).show();
                                                                            createEvento.setEnabled(false);

                                                                        }
                                                                    });
                                                        }
                                                    } ) ;
                                        }
                                    } ) .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SecondInfoActivity.this, "(Error)" + e, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            });
                        }
                    });
                }else if (TextUtils.isEmpty(eventoName)){
                    Toast.makeText(SecondInfoActivity.this, "Name empty", Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(eventoDate)){
                    Toast.makeText(SecondInfoActivity.this, "Date empty", Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(eventoTime)){
                    Toast.makeText(SecondInfoActivity.this, "Time empty", Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(eventoWeb)){
                    Toast.makeText(SecondInfoActivity.this, "Web empty", Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(eventoDescription)){
                    Toast.makeText(SecondInfoActivity.this, "Description empty", Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(eventoTime)){
                    Toast.makeText(SecondInfoActivity.this, "Time empty", Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(eventoLieu)){
                    Toast.makeText(SecondInfoActivity.this, "Luogo empty", Toast.LENGTH_LONG).show();
                }
                else if (TextUtils.isEmpty(eventoInfo)){
                    Toast.makeText(SecondInfoActivity.this, "Information empty", Toast.LENGTH_LONG).show();
                }
                else if (selectImage == null){
                    Toast.makeText(SecondInfoActivity.this, "No Imagine", Toast.LENGTH_LONG).show();
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