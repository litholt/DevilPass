package com.meglio.albuquerk.rubaink.devilpass;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DashFrag extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener{

    //private SupportMapFragment mapFragment;
    private PlaceAutocompleteFragment autocompleteFragment;
    private GoogleMap mMap;
    private Marker mLoc;
    private FirebaseFirestore firebaseFirestore;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String eventoId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dash, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (view == null)
            view = inflater.inflate(R.layout.fragment_dash, container, false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {

                            final LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions()
                                    .position(sydney)
                                    .title("me")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_flash)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        }else
                        {
                            Toast.makeText(getContext(), "Active your location", Toast.LENGTH_LONG).show();
                            LatLng sydney = new LatLng(40.7241659999999, -74.0102282);
                            mMap.addMarker(new MarkerOptions()
                                    .position(sydney)
                                    .title("NY")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_flash)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12.0f));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "" + e, Toast.LENGTH_LONG).show();
            }
        });

        SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.place_autocom);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));
            }
            @Override
            public void onError(Status status) {
            }
        });

        final List<Marker> markerList = new ArrayList<>();

        firebaseFirestore = FirebaseFirestore.getInstance();
        Query firstQuery = firebaseFirestore.collection("EVENTLOCATION");
        firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        final String loc_id = doc.getDocument().getId();
                        //Double evento = doc.getDocument().toObject(Double.class);

                        firebaseFirestore.collection("EVENTLOCATION").document(loc_id).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().exists()) {
                                                eventoId = loc_id;

                                                Double lat = task.getResult().getDouble("latitude");
                                                Double lon = task.getResult().getDouble("longitude");
                                                String name = task.getResult().getString("name");

                                                LatLng LOCA = new LatLng(lat, lon);
                                                mLoc = mMap.addMarker(new MarkerOptions().position(LOCA).title(name)
                                                        .icon(BitmapDescriptorFactory
                                                                .fromResource(R.drawable.ic_flash_red)));
                                                mLoc.setTag(0);
                                                markerList.add(mLoc);
                                            } } else { Toast.makeText(getActivity(), "Error",
                                                    Toast.LENGTH_LONG).show(); } }}); } } }});

        mMap.setOnMarkerClickListener(this); //register our click listner !
        for (Marker m : markerList) {

            LatLng latLng = new LatLng(m.getPosition().latitude, m.getPosition().longitude);
            mMap.addMarker(new MarkerOptions().position(latLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 2));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Integer clickCount = (Integer) marker.getTag();
        if (clickCount != null) {

            clickCount = clickCount + 1 ;
            if (clickCount >= 2) {
                Intent evIntent = new Intent(getActivity(), ScrollingActivity.class);
                evIntent.putExtra("eventid",eventoId);
                getActivity().startActivity(evIntent);
            } }
        return false;
    }

   /*@Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(viewChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(, savedInstanceState);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }*/

        /*final LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions()
                        .position(sydney)
                        .title("sydney")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_flash)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
/*
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
            getChildFragmentManager().beginTransaction().remove(mapFragment).commit();
    }
*/
}
