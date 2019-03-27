package com.meglio.albuquerk.rubaink.devilpass;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ForgetActivity extends AppCompatActivity {

    private EditText forgetEmailText;
    private Button sendBtn;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private ImageView back;

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
        setContentView(R.layout.activity_forget);

        mAuth = FirebaseAuth.getInstance();

        forgetEmailText = findViewById(R.id.email);
        sendBtn = findViewById(R.id.email_sign_in_button);
        progressBar = findViewById(R.id.login_progress);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fEmail =  forgetEmailText.getText().toString();

                if(!TextUtils.isEmpty(fEmail)){

                    progressBar.setVisibility(View.VISIBLE);
                    sendBtn.setEnabled(false);

                    mAuth.sendPasswordResetEmail(fEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                finish();
                                Toast.makeText(ForgetActivity.this,
                                        "Password send to your email", Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(ForgetActivity.this,
                                        task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
