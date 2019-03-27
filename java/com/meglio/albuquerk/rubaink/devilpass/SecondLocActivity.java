package com.meglio.albuquerk.rubaink.devilpass;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SecondLocActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    SharedPreferences sharedPreferences;
    int locationCount = 0;
    private Button button;

    private String latit = "";
    private String longit = "";
    private String address = "";
    private String city = "";
    private String country = "",c_code;
    private FirebaseFirestore firebaseFirestore;

    private String image_st,inf,type;
    // Write a message to the database
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;


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
        setContentView(R.layout.activity_second_loc);

        image_st = getIntent().getStringExtra("image");
        inf = getIntent().getStringExtra("event");
        type = getIntent().getStringExtra("type");

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        } else { // Google Play Services are available

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        firebaseFirestore = FirebaseFirestore.getInstance();

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!latit.equals("")) && !longit.equals("")){

                    Intent intent = new Intent(SecondLocActivity.this,
                            SecondInfoActivity.class);

                    intent.putExtra("lat",latit);
                    intent.putExtra("long",longit);
                    intent.putExtra("addr",address);
                    intent.putExtra("ci",city);
                    intent.putExtra("code",c_code);
                    intent.putExtra("co",country);

                    intent.putExtra("event",inf);
                    intent.putExtra("type", type);
                    intent.putExtra("image",image_st);

                    intent.putExtra("web", getIntent().getStringExtra("web"));
                    intent.putExtra("phone", getIntent().getStringExtra("phone"));
                    intent.putExtra("email", getIntent().getStringExtra("email"));

                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        // Enabling MyLocation Layer of Google Map

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.clear();
        googleMap.setMyLocationEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(false);
        googleMap.setBuildingsEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        //googleMap.clear();

        sharedPreferences = getSharedPreferences("location", 0);
        locationCount = sharedPreferences.getInt("locationCount", 0);
        String zoom = sharedPreferences.getString("zoom", "0");

        if(locationCount!=0){
            String lat = "";
            String lng = "";

            for(int i=0;i<locationCount;i++){
                lat = sharedPreferences.getString("lat"+i,"0");
                lng = sharedPreferences.getString("lng"+i,"0");
                drawMarker(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)) , googleMap);
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLng
                    (new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(Float.parseFloat(zoom)));
        }

        //ON CLICK
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //googleMap.clear();
                longitude_latitude (latLng,googleMap);
            }
        });
        //LONG CLICK
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                googleMap.clear();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                locationCount=0;
            }
        });

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                //googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));

                LatLng position = place.getLatLng();
                longitude_latitude (position,googleMap);
            }

            @Override
            public void onError(Status status) {
            }
        });
    }

    private void longitude_latitude(LatLng latLng,GoogleMap googleMap) {
        locationCount++;
        //googleMap.clear();

        double latitude = latLng.latitude;
        double longitude = latLng.longitude;

        Geocoder geocoder ;
        List<Address> addresses;
        geocoder = new Geocoder(getApplicationContext(),Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude,longitude,1);
            if (addresses != null && addresses.size() > 0){

                address = addresses.get(0).getAddressLine(0);
                city = addresses.get(0).getLocality();
                country = addresses.get(0).getCountryName();
                c_code = addresses.get(0).getCountryCode();
            }
        }catch (IOException e){
        }

        latit = Double.toString(latitude);
        longit = Double.toString(longitude);

        drawMarker(latLng,googleMap);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lat"+ Integer.toString((locationCount-1)), Double.toString(latLng.latitude));
        editor.putString("lng"+ Integer.toString((locationCount-1)), Double.toString(latLng.longitude));
        editor.putInt("locationCount", locationCount);
        editor.putString("zoom", Float.toString(googleMap.getCameraPosition().zoom));
        editor.commit();

        Toast.makeText(getBaseContext(), "Event Location is added to the Map", Toast.LENGTH_LONG).show();
        //Toast.makeText(getBaseContext(), "long: "+ longitude + " ,lat: "+ latitude,
        // Toast.LENGTH_LONG).show();
    }

    private void drawMarker(LatLng latLng,GoogleMap googleMap) {
        //googleMap.clear();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        googleMap.addMarker(markerOptions);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}