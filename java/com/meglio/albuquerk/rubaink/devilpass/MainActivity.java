package com.meglio.albuquerk.rubaink.devilpass;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private CircleImageView eUserImage;
    private TextView userName,userEmail;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private String current_user_id,cc;
    private Boolean isSecond,draw;

    //Ctrl+O
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase)); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Before setContent
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Ubuntu-R.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Sky'Pass");

        if(mAuth.getCurrentUser() != null) {

            current_user_id = mAuth.getCurrentUser().getUid();
            firebaseFirestore = FirebaseFirestore.getInstance();
            cc = getUserCountry(MainActivity.this);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            View hearderView = navigationView.getHeaderView(0);
            eUserImage = hearderView.findViewById(R.id.imageView);
            userEmail = hearderView.findViewById(R.id.user_email);
            userName = hearderView.findViewById(R.id.user_name);
            navigationInfoUser();

            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            bottomNav.setOnNavigationItemSelectedListener(navListener);

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFrag()).commit();
            }
        }else sendToLogin();
    }

    private void navigationInfoUser() {

        firebaseFirestore.collection("USERS").document(current_user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (Objects.requireNonNull(task.getResult()).exists()) {

                                String name = task.getResult().getString("name");
                                String email = task.getResult().getString("email");
                                String image = task.getResult().getString("image");
                                String ccode = task.getResult().getString("country");

                                userName.setText(name);
                                userEmail.setText(email);
                                if (!MainActivity.this.isFinishing())
                                    Glide.with(MainActivity.this).load(image).into(eUserImage);

                                if (name == null || email == null || image == null ||ccode == null)
                                    sendToSetup();

                                if (ccode!= null && !ccode.equals(cc)){
                                    final Map<String, Object> p = new HashMap<>();
                                    p.put("country",cc);
                                    firebaseFirestore.collection("USERS").document(current_user_id)
                                            .update(p); }

                            }else sendToSetup();
                        }else sendToSetup();
                    }
                });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            selectedFragment = new HomeFrag();
                            break;
                        case R.id.navigation_dashboard:
                            selectedFragment = new DashFrag();
                            break;
                        case R.id.navigation_notifications:
                            selectedFragment = new PersFrag();
                            break;
                    }
                    assert selectedFragment != null;
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed(); }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            return true;
        }
        if (id == R.id.action_search) {
            Intent nt = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(nt);
            return true;
        }
        if (id == R.id.action_scan) {
            Intent nt = new Intent(MainActivity.this, XcanActivity.class);
            startActivity(nt);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent nt = new Intent(MainActivity.this, SetupActivity.class);
            startActivity(nt);

        } else if (id == R.id.nav_gallery) {
            Intent nt = new Intent(MainActivity.this, MyPassActivity.class);//nt.putExtra("type","post");
            startActivity(nt);

        } else if (id == R.id.nav_slideshow) {
            Intent nt = new Intent(MainActivity.this, PostsActivity.class);
            startActivity(nt);

        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.meglio.albuquerk.rubaink.devilpass")));

        } else if (id == R.id.nav_share) {
            Intent t = new Intent(MainActivity.this, ModifyPActivity.class);
            startActivity(t);

        } else if (id == R.id.nav_about) {
            Intent nt = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(nt);
        }
        else if (id == R.id.maj) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LunchActivity.class);
        startActivity(loginIntent);
        finish();
    }
    private void sendToSetup() {
        Toast.makeText(MainActivity.this, "Complete your information", Toast.LENGTH_LONG).show();
        Intent nt = new Intent(MainActivity.this, SetupActivity.class);
        startActivity(nt);
        finish();
    }
}
