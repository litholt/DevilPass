package com.meglio.albuquerk.rubaink.devilpass;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegisterActivity extends AppCompatActivity {

    private EditText reg_email_field;
    private EditText reg_pass_field;
    private EditText reg_confirm_pass_field;
    private Button reg_btn;
    private ProgressBar reg_progress;
    private FirebaseAuth mAuth;
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
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        reg_email_field = findViewById(R.id.email);
        reg_pass_field = findViewById(R.id.password);
        reg_confirm_pass_field = findViewById(R.id.password_conf);
        reg_btn = findViewById(R.id.email_sign_in_button);
        reg_progress = findViewById(R.id.login_progress);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = reg_email_field.getText().toString();
                String pass = reg_pass_field.getText().toString();
                final String confirm_pass = reg_confirm_pass_field.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confirm_pass)){

                    if(pass.equals(confirm_pass)){

                        reg_progress.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    Notification();

                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    if (currentUser != null){

                                        currentUser.sendEmailVerification()
                                                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){

                                                            Toast.makeText(RegisterActivity.this,
                                                                    "Verification email send to "+email , Toast.LENGTH_LONG).show();

                                                            Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
                                                            setupIntent.putExtra("email",email);
                                                            setupIntent.putExtra("pass",confirm_pass);
                                                            startActivity(setupIntent);
                                                            finish();
                                                        }
                                                    }
                                                });
                                    }
                                } else {

                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "(Error) " + errorMessage,
                                            Toast.LENGTH_LONG).show();
                                    reg_progress.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    } else {
                        Toast.makeText(RegisterActivity.this, "Confirm Password and Password Field doesn't match.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void Notification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        //.setSmallIcon(R.drawable.bcb)
                        .setContentTitle("Sky'Pass")
                        .setContentText("Hello Welcome")
                        .setColor(ContextCompat.getColor(RegisterActivity.this,R.color.colorPrimary))
                        .setColor(Color.RED)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

            sendToMain();
        }
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
/*
mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){

                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    currentUser.sendEmailVerification()
                                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    //reg_btn.setText("Confirm Your Email");
                                                    reg_btn.setEnabled(true);

                                                    if (task.isSuccessful()){

                                                        Toast.makeText(RegisterActivity.this, getString(R.string.verification_sms_send) + email, Toast.LENGTH_LONG).show();
                                                    }else {
                                                        String errorMessage = task.getException().getMessage();
                                                        Toast.makeText(RegisterActivity.this, getString(R.string.erreur) + errorMessage, Toast.LENGTH_LONG).show();
                                                    }
                                                    reg_progress.setVisibility(View.INVISIBLE);
                                                }
                                            });
                                    Intent setupIntent = new Intent(RegisterActivity.this, Login2Activity.class);
                                    setupIntent.putExtra("verification",getString(R.string.confirm_messge_send_to_your_mail));
                                    RegisterActivity.this.startActivity(setupIntent);
                                    finish();

                                } else {

                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, R.string.erreur + errorMessage, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
 */