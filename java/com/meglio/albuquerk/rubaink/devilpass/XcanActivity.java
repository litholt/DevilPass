package com.meglio.albuquerk.rubaink.devilpass;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class XcanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView zXingScannerView;
    private FirebaseFirestore firebaseFirestore;
    private ImageView back,scanne;

    //Ctrl+O
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Ubuntu-R.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_xcan);

        back = findViewById(R.id.back);
        scanne = findViewById(R.id.scan_);

        scanne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zXingScannerView =new ZXingScannerView(getApplicationContext());
                setContentView(zXingScannerView);
                zXingScannerView.setResultHandler(XcanActivity.this);
                zXingScannerView.startCamera();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void handleResult(final com.google.zxing.Result result) {
        //Toast.makeText(XcanActivity.this,"" + result.getText(), Toast.LENGTH_LONG).show();

        firebaseFirestore.collection("EVENTO").document(result.getText())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(Objects.requireNonNull(task.getResult()).exists()){

                        String type = task.getResult().getString("type");
                        if (type != null && type.equals("aa")){

                            Intent nt = new Intent(XcanActivity.this, ScrollingActivity.class);
                            nt.putExtra("eventid",result.getText());
                            startActivity(nt);

                        }else if (type != null && type.equals("bb")){

                            Intent t = new Intent(XcanActivity.this, Scrolling2Activity.class);
                            t.putExtra("eventid",result.getText());
                            startActivity(t); }
                    }else
                        Toast.makeText(XcanActivity.this,"(Error1)" , Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(XcanActivity.this,"(Error2)" , Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(XcanActivity.this,"(Error3)" + e, Toast.LENGTH_LONG).show();
            }
        });

        //zXingScannerView.resumeCameraPreview( this);
    }
    /*@Override
    protected void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
    }*/

    public void scan(View view) {

        zXingScannerView =new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
    }
}
