package com.meglio.albuquerk.rubaink.devilpass;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UserEvActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_user_ev);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Toast.makeText(UserEvActivity.this, "Country " +
                getUserCountry(this), Toast.LENGTH_LONG).show();

    }
    public static String getUserCountry(Context context){
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();

            if ((simCountry != null) && (simCountry.length() == 2)){

                return myCountry(simCountry.toUpperCase(Locale.US));

            }else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA){
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2){

                    return myCountry(networkCountry.toLowerCase(Locale.US));
                }
            }
        }catch (Exception e){ }
        return null;
    }

    private static String myCountry(String s) {
        String[] country = {"Afghanistan","Albania","Algeria","Andorra","Angola","Anguilla","Antigua & Barbuda"
                ,"Argentina","Armenia","Australia","Austria","Azerbaijan","Bahamas","Bahrain","Bangladesh"
                ,"Barbados","Belarus","Belgium","Belize","Benin","Bermuda","Bhutan","Bolivia","Bosnia & Herzegovina","Botswana"
                ,"Brazil","Brunei Darussalam","Bulgaria","Burkina Faso","Myanmar/Burma","Burundi","Cambodia","Cameroon"
                ,"Canada","Cape Verde","Cayman Islands","Central African Republic","Chad","Chile","China","Colombia","Comoros"
                ,"Congo","Costa Rica","Croatia","Cuba","Cyprus","Czech Republic","Democratic Republic of the Congo","Denmark"
                ,"Djibouti","Dominican Republic","Dominica","Ecuador","Egypt","El Salvador","Equatorial Guinea","Eritrea"
                ,"Estonia","Ethiopia","Fiji","Finland","France","French Guiana","Gabon","Gambia","Georgia","Germany","Ghana"
                ,"Great Britain","Greece","Grenada","Guadeloupe","Guatemala","Guinea","Guinea-Bissau","Guyana","Haiti","Honduras"
                ,"Hungary","Iceland","India","Indonesia","Iran","Iraq","Israel and the Occupied Territories","Italy","Ivory Coast (Cote d'Ivoire)"
                ,"Jamaica","Japan","Jordan","Kazakhstan","Kenya","Kosovo","Kuwait","Kyrgyz Republic (Kyrgyzstan)","Laos","Latvia","Lebanon"
                ,"Lesotho","Liberia","Libya","Liechtenstein","Lithuania","Luxembourg","Republic of Macedonia","Madagascar","Malawi"
                ,"Malaysia","Maldives","Mali","Malta","Martinique","Mauritania","Mauritius","Mayotte","Mexico","Moldova, Republic of Monaco"
                ,"Mongolia","Montenegro","Montserrat","Morocco","Mozambique","Namibia","Nepal","Netherlands","New Zealand","Nicaragua"
                ,"Niger","Nigeria","Korea, Democratic Republic of (North Korea)","Norway","Oman","Pacific Islands","Pakistan","Panama"
                ,"Papua New Guinea","Paraguay","Peru","Philippines","Poland","Portugal","Puerto Rico","Qatar","Reunion","Romania"
                ,"Russian Federation","Rwanda","Saint Kitts and Nevis","Saint Lucia","Saint Vincent's & Grenadines","Samoa","Sao Tome and Principe"
                ,"Saudi Arabia","Senegal","Serbia","Seychelles","Sierra Leone","Singapore","Slovak Republic ","Slovenia","Solomon Islands"
                ,"Somalia","South Africa","Korea, Republic of (South Korea)","South Sudan","Spain","Sri Lanka","Sudan","Suriname","Swaziland"
                ,"Sweden","Switzerland","Syria","Tajikistan","Tanzania","Thailand","Timor Leste","Togo","Trinidad & Tobago","Tunisia"
                ,"Turkey","Turkmenistan","Turks & Caicos Islands","Uganda","Ukraine","United Arab Emirates","United States of America (USA)"
                ,"Uruguay","Uzbekistan","Venezuela","Vietnam","Virgin Islands (UK)","Virgin Islands (US)","Yemen","Zambia","Zimbabwe"};

        if (s.equals("af")){
            s = country[0].toLowerCase();
        }else if (s.equals("al")){
            s = country[1].toLowerCase();
        }else if (s.equals("it")){
            s = ("Italy").toLowerCase();
        }

        return s;
    }
}
