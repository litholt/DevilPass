package com.meglio.albuquerk.rubaink.devilpass;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import id.zelory.compressor.Compressor;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NextRegActivity extends AppCompatActivity {

    private Button confirmSetup;
    private String email,pass,user_denario,user_country;
    private TextInputEditText username,user_phone;
    private ImageView user_image,current_setting;

    private String user_id ;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private Uri mainImageURI = null;
    private Boolean isChanged=false,found;
    private Bitmap compressedImageFile;
    private DatabaseReference mDatabase;
    private ProgressBar progressBar;

    private String deleteImage;
    private ConstraintLayout rootLayout;
    private FloatingActionButton select_tof;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_reg);

            confirmSetup = findViewById(R.id.btn_user_info);
            select_tof = findViewById(R.id.select_tof);
            current_setting = findViewById(R.id.current_setting);
            rootLayout = findViewById(R.id.rootlayout);

            user_image = findViewById(R.id.user_image);
            username = findViewById(R.id.user_name);
            progressBar = findViewById(R.id.eProgressBar);

            firebaseAuth = FirebaseAuth.getInstance();
            user_id = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
            firebaseFirestore = FirebaseFirestore.getInstance();
            storageReference = FirebaseStorage.getInstance().getReference();

            email = getIntent().getStringExtra("email");
            pass = getIntent().getStringExtra("pass");

            final String cc = getUserCountry(NextRegActivity.this);
            user_denario = null;

            //Add info
            firebaseFirestore.collection("USERS").document(user_id).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                if(Objects.requireNonNull(task.getResult()).exists()){

                                    String name = task.getResult().getString("name");
                                    String image = task.getResult().getString("image");

                                    pass = task.getResult().getString("pass");
                                    email = task.getResult().getString("email");
                                    user_denario = task.getResult().getString("compt");

                                    mainImageURI = Uri.parse(image);
                                    username.setText(name);
                                    deleteImage = image;

                                    if (!NextRegActivity.this.isFinishing()){
                                        Glide.with(NextRegActivity.this).load(image).into(user_image);
                                    }
                                }
                            }
                        }
                    });

            //setupImage
            select_tof.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if(ContextCompat.checkSelfPermission(SetupActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager
                                .PERMISSION_GRANTED){
                            Toast.makeText(SetupActivity.this, "Permission Denied",
                                    Toast.LENGTH_LONG).show();
                            ActivityCompat.requestPermissions(SetupActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    1);
                        } else { BringImagePicker(); }
                    } else { BringImagePicker(); } }});

            confirmSetup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    progressBar.setVisibility(View.VISIBLE);
                    createEventoSiEnable();

                    final String user_name = Objects.requireNonNull(username.getText()).toString();
                    //final String user_fone = user_phone.getText().toString();
                    //final String user_phonecode = countryCodeAndroid;
                    //final String user_genre = mSex;
                    //final String user_country_code = user_country;
                    final String user_pass = pass;
                    final String user_email = email;
                    final String user_compt = user_denario;

                    if (!TextUtils.isEmpty(user_name) && mainImageURI != null) {

                        if (isChanged) {

                            final String randomName = UUID.randomUUID().toString();

                            user_id = firebaseAuth.getCurrentUser().getUid();
                            File newImageFile = new File(mainImageURI.getPath());
                            try {

                                compressedImageFile = new Compressor(SetupActivity.this)
                                        .setMaxHeight(200)
                                        .setMaxWidth(200)
                                        .setQuality(75)
                                        .compressToBitmap(newImageFile);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] thumbData = baos.toByteArray();

                            UploadTask image_path = storageReference.child("profile_images")
                                    .child(randomName + ".jpg").putBytes(thumbData);

                            image_path.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(
                                            new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    //String downloadbUri = uri.toString();

                                                    storeFirestore(uri, user_name/*,user_fone,
                                                        user_genre ,user_phonecode*/,
                                                            user_pass,cc, user_email,user_compt);
                                                    deleteOldImage();
                                                }
                                            });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(SetupActivity.this, "(IMAGE Error)" ,
                                            Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    createEventoNoEnable();
                                }
                            }) ;
                        } else {
                            storeFirestore(null, user_name,/*user_fone,
                                user_genre,user_phonecode,*/
                                    user_pass,cc, user_email,user_compt);

                        }
                    }else if (TextUtils.isEmpty(user_name)){
                        Snackbar.make(rootLayout,"Your name.",
                                Snackbar.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        createEventoNoEnable();
                    }
                    else if (mainImageURI == null){
                        Snackbar.make(rootLayout,"Image Error",
                                Snackbar.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        createEventoNoEnable();
                    }
                }
            });
        }

        private void deleteOldImage() {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference desertRef = storageRef.child(deleteImage);
            Toast.makeText(SetupActivity.this,
                    ""+deleteImage, Toast.LENGTH_LONG).show();
            desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(SetupActivity.this,
                            "Oklm", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SetupActivity.this,
                            "Oklm1", Toast.LENGTH_LONG).show();

                }
            });
        }

        private void createEventoNoEnable() {
            confirmSetup.setEnabled(true);
            confirmSetup.setClickable(true);
        }

        private void createEventoSiEnable() {
            confirmSetup.setEnabled(false);
            confirmSetup.setClickable(false);
        }

        public static String getUserCountry(Context context){
            try {
                final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                final String simCountry = tm.getSimCountryIso();

                if ((simCountry != null) && (simCountry.length() == 2)){

                    return simCountry.toUpperCase(Locale.US);

                }else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA){
                    String networkCountry = tm.getNetworkCountryIso();
                    if (networkCountry != null && networkCountry.length() == 2){

                        return networkCountry.toUpperCase(Locale.US);
                    }
                }
            }catch (Exception e){ }
            return null;
        }


        private void storeFirestore( final Uri downloaduri, final String user_name,
                                 /*final String user_fone,final String user_genre,
                                 final String user_phonecode,*/final String user_pass
                                ,final String user_country_code,
        final String user_email,final String user_compt) {

            Uri download_uri;

            if(downloaduri != null) {
                download_uri = downloaduri;
            } else {
                download_uri = mainImageURI;
            }

            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

            final Map<String, Object> userMap = new HashMap<>();

            userMap.put("image", download_uri.toString());
            userMap.put("name", user_name);
            //userMap.put("phone", user_fone);
            //userMap.put("genre", user_genre);
            //userMap.put("country_code", user_phonecode);
            userMap.put("country", user_country_code);
            userMap.put("pass", user_pass);
            userMap.put("email", user_email);
            userMap.put("compt", user_compt);
            userMap.put("timestamp", FieldValue.serverTimestamp());

            firebaseFirestore.collection("USERS").document(user_id).set(userMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firebaseFirestore.collection("USERS").document(user_id).set(userMap);

                                mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){

                                            progressBar.setVisibility(View.VISIBLE);
                                            createEventoNoEnable();

                                            Snackbar.make(rootLayout,"Your information are updated.",
                                                    Snackbar.LENGTH_LONG).show();
                                            Intent mt = new Intent(SetupActivity.this, MainActivity.class);
                                            startActivity(mt);
                                            finish();
                                        }
                                        else{ progressBar.setVisibility(View.INVISIBLE);
                                            createEventoNoEnable();

                                            Snackbar.make(rootLayout,"Your information aren't updated.",
                                                    Snackbar.LENGTH_LONG).show();
                                            Intent mt = new Intent(SetupActivity.this, MainActivity.class);
                                            startActivity(mt);
                                            finish(); }
                                    }
                                });
                            } else {
                                String error = Objects.requireNonNull(task.getException()).getMessage();
                                Snackbar.make(rootLayout,"Failed: " + error,Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }

        private void BringImagePicker() {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {

                    mainImageURI = result.getUri();
                    Glide.with(SetupActivity.this).load(mainImageURI).into(user_image);

                    isChanged = true;

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                    Exception error = result.getError();
                    Snackbar.make(rootLayout,"Failed: " + error,Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        }
    }