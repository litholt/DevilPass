package com.meglio.albuquerk.rubaink.devilpass;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SecondImageActivity extends AppCompatActivity {

    private Uri selectImage = null;
    private FloatingActionButton floatingActionCamera;
    private ImageView imageView,back;
    private CardView suivant;
    private String inf;

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

        setContentView(R.layout.activity_select_image);

        floatingActionCamera = findViewById(R.id.floatingActionCamera);
        imageView = findViewById(R.id.image_event);
        suivant = findViewById(R.id.suivant);
        inf = getIntent().getStringExtra("event");

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        floatingActionCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(SecondImageActivity.this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(SecondImageActivity.this, "Permission denied",
                                Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(SecondImageActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        BringImagePicker();
                    }
                } else {
                    BringImagePicker();
                }
            }
        });

        suivant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectImage != null){
                    String mainImage = String.valueOf(selectImage);
                    Intent nt = new Intent(SecondImageActivity.this,
                            SecondTicketActivity.class);
                    nt.putExtra("image",mainImage);
                    nt.putExtra("event",inf);
                    startActivity(nt);
                }else
                    Toast.makeText(SecondImageActivity.this, "Select event image",
                            Toast.LENGTH_LONG).show();
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

                selectImage = result.getUri();
                if (!SecondImageActivity.this.isFinishing()){
                    Glide.with(SecondImageActivity.this).load(selectImage).into(imageView);
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(SecondImageActivity.this, "(Error): " + error,
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
