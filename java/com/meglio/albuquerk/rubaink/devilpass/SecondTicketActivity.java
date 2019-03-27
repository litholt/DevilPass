package com.meglio.albuquerk.rubaink.devilpass;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SecondTicketActivity extends AppCompatActivity {

    private ImageView back,help;
    private TextView eventTitle;
    private String inf,place,image;
    private Button suivant;
    private EditText web,email,phone;
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
        setContentView(R.layout.activity_second_ticket);

        web = findViewById(R.id.web);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.num);

        suivant = findViewById(R.id.button);

        inf = getIntent().getStringExtra("event");
        image = getIntent().getStringExtra("image");

        final Intent concertIntent = new Intent(
                SecondTicketActivity.this, SecondLocActivity.class);
        suivant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String eM = email.getText().toString();
                final String wE = web.getText().toString();
                final String pH = phone.getText().toString();

                if( !TextUtils.isEmpty(eM) || !TextUtils.isEmpty(wE)){

                    concertIntent.putExtra("event",inf);
                    concertIntent.putExtra("type","bb");
                    concertIntent.putExtra("image",image);

                    concertIntent.putExtra("web",wE);
                    concertIntent.putExtra("phone",pH);
                    concertIntent.putExtra("email",eM);

                    startActivity(concertIntent);
                }else {
                    Toast.makeText(SecondTicketActivity.this, "Make at least two choices",
                            Toast.LENGTH_LONG).show();
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