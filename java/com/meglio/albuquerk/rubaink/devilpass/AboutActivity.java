package com.meglio.albuquerk.rubaink.devilpass;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mehdi.sakout.aboutpage.Element adsElement = new mehdi.sakout.aboutpage.Element();
        adsElement.setTitle("Sky'Pass");

        View aboutPage =new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.image_placeholder)
                .setDescription("Sky'Pass version 1.0.1 Find Event, Enjoy yourself")
                .addItem(new mehdi.sakout.aboutpage.Element().setTitle("Version 1.0.1"))
                .addItem(adsElement)
                .addGroup("Developed by Albuquerk Meglio Rubain")
                .addWebsite("http://www.google.com")
                .addPlayStore("")
                .addEmail("skypass@gmail.com")
                .addFacebook("")
                .addInstagram("")
                .addTwitter("")
                .addYoutube("")
                .addItem(createCopyright())
                .create();

        setContentView(aboutPage);
    }

    private Element createCopyright() {
        Element copyright = new Element();
        final String copyrightString = String.format("Copyright %d by Sky'Pass",
                Calendar.getInstance().get(Calendar.YEAR)) ;
        copyright.setTitle(copyrightString);
        copyright.setIcon(R.drawable.flag_italy);
        copyright.setGravity(Gravity.CENTER);
        copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutActivity.this,copyrightString,Toast.LENGTH_SHORT)
                        .show();
            }
        });
        return copyright;
    }
}
