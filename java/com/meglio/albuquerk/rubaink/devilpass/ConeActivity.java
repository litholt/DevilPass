package com.meglio.albuquerk.rubaink.devilpass;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ConeActivity extends AppCompatActivity {

    private ImageView event_image,back;
    private String inf_evId,inf_nam,inf_ima,inf_th,inf_jh,inf_li,inf_des,inf_name_place,inf_price,loge;
    private TextView eName,eDes,eDay,eLoc,ePrice,ePlaceDes,eCondition;
    private Button payment;
    private Spinner eSpinner;
    private ProgressBar progressBar;

    private String compteAdmin,compteClient;
    private String nbr1,nbr2,nbr3,nbr4,
            numberBVendu,restEco1;

    private double restEconomic ,comptAdmin, comptClient;
    private int billetVendu ;
    private String user_id;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String loge_e_billet = "0";
    private String totalPlace;

    //Ctrl+O
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase)); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Ubuntu-R.ttf")
                .setFontAttrId(R.attr.fontPath).build());//USD EUR
        setContentView(R.layout.activity_cone);

        progressBar = findViewById(R.id.progressBar);
        payment = findViewById(R.id.ePayer);
        eSpinner = findViewById(R.id.eSpinner);

        event_image = findViewById(R.id.image);
        eName = findViewById(R.id.eName);
        eDes = findViewById(R.id.eDesc);
        eDay = findViewById(R.id.eDay);
        eLoc = findViewById(R.id.eLoc);
        ePrice = findViewById(R.id.ePrice);
        ePlaceDes = findViewById(R.id.ePlaceDes);
        eCondition = findViewById(R.id.eCondition);

        loge = getIntent().getStringExtra("loge");
        inf_evId = getIntent().getStringExtra("eventId");
        inf_nam = getIntent().getStringExtra("eventNa");
        inf_ima = getIntent().getStringExtra("eventIm");
        inf_th = getIntent().getStringExtra("eventTh");
        inf_jh = getIntent().getStringExtra("eventDa");
        inf_li = getIntent().getStringExtra("eventLi");
        inf_des = getIntent().getStringExtra("eventDe");
        inf_price = getIntent().getStringExtra("eventPrice");
        inf_name_place = getIntent().getStringExtra("eventNP");

        Glide.with(ConeActivity.this).load(inf_ima).thumbnail(
                Glide.with(ConeActivity.this).load(inf_th)).into(event_image);
        eName.setText(inf_nam);
        eDes.setText(inf_des);
        eDay.setText(inf_jh);
        eLoc.setText(inf_li);
        ePrice.setText(inf_price  + " Euro");
        ePlaceDes.setText(inf_name_place);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (loge) {
                    case "1":
                        progressBar.setVisibility(View.VISIBLE);
                        createEventoSiEnable();
                        //for (int i = 0 ; i < eBillet + 1 ; i++){

                        firebaseFirestore.collection("EVENTO2").document(inf_evId).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().exists()) {

                                                totalPlace = task.getResult().getString("t1");
                                                numberBVendu = task.getResult().getString("nBv1");
                                                compteAdmin = task.getResult().getString("cAdmin");
                                                compteClient = task.getResult().getString("cClient");

                                                billetVendu = Integer.parseInt(numberBVendu);
                                                int totalsPlace = Integer.parseInt(totalPlace);
                                                final double prixB = Double.parseDouble(inf_price);

                                                comptAdmin = Double.parseDouble(compteAdmin);
                                                comptClient = Double.parseDouble(compteClient);

                                                if ((billetVendu < totalsPlace)) {

                                                    billetVendu += 1;
                                                    restEconomic = prixB - transfEc(prixB);
                                                    comptClient += restEconomic;
                                                    comptAdmin+= transfEc(prixB);

                                                    //Update Map
                                                    final Map<String, Object> po = new HashMap<>();
                                                    po.put("nBv1", String.valueOf(billetVendu));
                                                    po.put("rEc1", String.valueOf(restEconomic));
                                                    po.put("cAdmin", String.valueOf(comptAdmin));
                                                    po.put("cClient", String.valueOf(comptClient));
                                                    po.put("timestamp", FieldValue.serverTimestamp());

                                                    //1'
                                                    firebaseFirestore.collection("EVENTO2").document(inf_evId).update(po)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    //Create NEW ID PASS IN EVENT

                                                                    final Map<String, Object> ap = new HashMap<>();
                                                                    ap.put("number", String.valueOf(billetVendu));
                                                                    ap.put("loge", loge);
                                                                    ap.put("price", String.valueOf(prixB));
                                                                    ap.put("user_id", user_id);
                                                                    ap.put("timestamp", FieldValue.serverTimestamp());
                                                                    //2'
                                                                    firebaseFirestore.collection("EVENTO2/" + inf_evId + "/PASS").add(ap)
                                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                                @Override
                                                                                public void onSuccess(DocumentReference documentReference) {
                                                                                    //Get NEW PASS FOR USER REFERENCE
                                                                                    final String newId = documentReference.getId();

                                                                                    final Map<String, Object> ap = new HashMap<>();
                                                                                    ap.put("image", inf_ima);
                                                                                    ap.put("imageTh", inf_th);
                                                                                    ap.put("day", inf_jh);
                                                                                    ap.put("name", inf_nam);
                                                                                    ap.put("number", String.valueOf(billetVendu));
                                                                                    ap.put("loge", loge);
                                                                                    ap.put("eventId", inf_evId);
                                                                                    ap.put("price", String.valueOf(prixB));
                                                                                    ap.put("timestamp", FieldValue.serverTimestamp());
                                                                                    //3'   Add Pass of User
                                                                                    firebaseFirestore.collection("USERS/" + user_id + "/PASS")
                                                                                            .document(newId).set(ap)
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {
                                                                                                    success();
                                                                                                }
                                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            Toast.makeText(ConeActivity.this, "" + e,
                                                                                                    Toast.LENGTH_LONG).show();
                                                                                        }
                                                                                    });
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    fail();
                                                                                }
                                                                            });
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            fail();
                                                        }
                                                    });
                                                } else {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(ConeActivity.this, "(Tous les billets ont ete achete)",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        } else {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(ConeActivity.this, "(Error)", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });//}

                        break;
                    case "2":
                        progressBar.setVisibility(View.VISIBLE);
                        createEventoSiEnable();
                        //for (int i = 0 ; i < eBillet + 1 ; i++){

                        firebaseFirestore.collection("EVENTO2").document(inf_evId).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (Objects.requireNonNull(task.getResult()).exists()) {

                                                totalPlace = task.getResult().getString("t2");
                                                numberBVendu = task.getResult().getString("nBv2");
                                                compteAdmin = task.getResult().getString("cAdmin");
                                                compteClient = task.getResult().getString("cClient");

                                                billetVendu = stringToInt(numberBVendu);
                                                int totalsPlace = stringToInt(totalPlace);
                                                final double prixB = stringToDouble(inf_price);

                                                comptAdmin = stringToDouble(compteAdmin);
                                                comptClient = stringToDouble(compteClient);

                                                if ((billetVendu < totalsPlace)) {

                                                    billetVendu += 1;
                                                    restEconomic = prixB - transfEc(prixB);
                                                    comptClient += restEconomic;
                                                    comptAdmin+= transfEc(prixB);

                                                    //Update Map
                                                    final Map<String, Object> po = new HashMap<>();
                                                    po.put("nBv2", String.valueOf(billetVendu));
                                                    po.put("rEc2", String.valueOf(restEconomic));
                                                    po.put("cAdmin", String.valueOf(comptAdmin));
                                                    po.put("cClient", String.valueOf(comptClient));
                                                    po.put("timestamp", FieldValue.serverTimestamp());

                                                    //1'
                                                    firebaseFirestore.collection("EVENTO2").document(inf_evId).update(po)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    //Create NEW ID PASS IN EVENT

                                                                    final Map<String, Object> ap = new HashMap<>();
                                                                    ap.put("number", String.valueOf(billetVendu));
                                                                    ap.put("loge", loge);
                                                                    ap.put("price", String.valueOf(prixB));
                                                                    ap.put("user_id", user_id);
                                                                    ap.put("timestamp", FieldValue.serverTimestamp());
                                                                    //2'
                                                                    firebaseFirestore.collection("EVENTO2/" + inf_evId + "/PASS").add(ap)
                                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                                @Override
                                                                                public void onSuccess(DocumentReference documentReference) {
                                                                                    //Get NEW PASS FOR USER REFERENCE
                                                                                    final String newId = documentReference.getId();

                                                                                    final Map<String, Object> ap = new HashMap<>();
                                                                                    ap.put("image", inf_ima);
                                                                                    ap.put("imageTh", inf_th);
                                                                                    ap.put("day", inf_jh);
                                                                                    ap.put("name", inf_nam);
                                                                                    ap.put("number", String.valueOf(billetVendu));
                                                                                    ap.put("loge", loge);
                                                                                    ap.put("eventId", inf_evId);
                                                                                    ap.put("price", String.valueOf(prixB));
                                                                                    ap.put("timestamp", FieldValue.serverTimestamp());
                                                                                    //3'   Add Pass of User
                                                                                    firebaseFirestore.collection("USERS/" + user_id + "/PASS")
                                                                                            .document(newId).set(ap)
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {
                                                                                                    success();
                                                                                                }
                                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            Toast.makeText(ConeActivity.this, "" + e,
                                                                                                    Toast.LENGTH_LONG).show();
                                                                                        }
                                                                                    });
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    fail();
                                                                                }
                                                                            });
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            fail();
                                                        }
                                                    });
                                                } else {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(ConeActivity.this, "(Tous les billets ont ete achete)",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        } else {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(ConeActivity.this, "(Error)", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });//}

                        break;
                    case "3":
                        progressBar.setVisibility(View.VISIBLE);
                        createEventoSiEnable();
                        //for (int i = 0 ; i < eBillet + 1 ; i++){

                        firebaseFirestore.collection("EVENTO2").document(inf_evId).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (Objects.requireNonNull(task.getResult()).exists()) {

                                                totalPlace = task.getResult().getString("t3");
                                                numberBVendu = task.getResult().getString("nBv3");
                                                compteAdmin = task.getResult().getString("cAdmin");
                                                compteClient = task.getResult().getString("cClient");

                                                billetVendu = stringToInt(numberBVendu);
                                                int totalsPlace = stringToInt(totalPlace);
                                                final double prixB = stringToDouble(inf_price);

                                                comptAdmin = stringToDouble(compteAdmin);
                                                comptClient = stringToDouble(compteClient);

                                                if ((billetVendu < totalsPlace)) {

                                                    billetVendu += 1;
                                                    restEconomic = prixB - transfEc(prixB);
                                                    comptClient += restEconomic;
                                                    comptAdmin += transfEc(prixB);

                                                    //Update Map
                                                    final Map<String, Object> po = new HashMap<>();
                                                    po.put("nBv3", String.valueOf(billetVendu));
                                                    po.put("rEc3", String.valueOf(restEconomic));
                                                    po.put("cAdmin", String.valueOf(comptAdmin));
                                                    po.put("cClient", String.valueOf(comptClient));
                                                    po.put("timestamp", FieldValue.serverTimestamp());

                                                    //1'
                                                    firebaseFirestore.collection("EVENTO2").document(inf_evId).update(po)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    //Create NEW ID PASS IN EVENT

                                                                    final Map<String, Object> ap = new HashMap<>();
                                                                    ap.put("number", String.valueOf(billetVendu));
                                                                    ap.put("loge", loge);
                                                                    ap.put("price", String.valueOf(prixB));
                                                                    ap.put("user_id", user_id);
                                                                    ap.put("timestamp", FieldValue.serverTimestamp());
                                                                    //2'
                                                                    firebaseFirestore.collection("EVENTO2/" + inf_evId + "/PASS").add(ap)
                                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                                @Override
                                                                                public void onSuccess(DocumentReference documentReference) {
                                                                                    //Get NEW PASS FOR USER REFERENCE
                                                                                    final String newId = documentReference.getId();

                                                                                    final Map<String, Object> ap = new HashMap<>();
                                                                                    ap.put("image", inf_ima);
                                                                                    ap.put("imageTh", inf_th);
                                                                                    ap.put("day", inf_jh);
                                                                                    ap.put("name", inf_nam);
                                                                                    ap.put("number", String.valueOf(billetVendu));
                                                                                    ap.put("loge", loge);
                                                                                    ap.put("eventId", inf_evId);
                                                                                    ap.put("price", String.valueOf(prixB));
                                                                                    ap.put("timestamp", FieldValue.serverTimestamp());
                                                                                    //3'   Add Pass of User
                                                                                    firebaseFirestore.collection("USERS/" + user_id + "/PASS")
                                                                                            .document(newId).set(ap)
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {
                                                                                                    success();
                                                                                                }
                                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            Toast.makeText(ConeActivity.this, "" + e,
                                                                                                    Toast.LENGTH_LONG).show();
                                                                                        }
                                                                                    });
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    fail();
                                                                                }
                                                                            });
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            fail();
                                                        }
                                                    });
                                                } else {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(ConeActivity.this, "(Tous les billets ont ete achete)",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        } else {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(ConeActivity.this, "(Error)", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });//}

                        break;
                    case "4":
                        progressBar.setVisibility(View.VISIBLE);
                        createEventoSiEnable();
                        //for (int i = 0 ; i < eBillet + 1 ; i++){

                        firebaseFirestore.collection("EVENTO2").document(inf_evId).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (Objects.requireNonNull(task.getResult()).exists()) {

                                                totalPlace = task.getResult().getString("t4");
                                                numberBVendu = task.getResult().getString("nBv4");
                                                compteAdmin = task.getResult().getString("cAdmin");
                                                compteClient = task.getResult().getString("cClient");

                                                billetVendu = stringToInt(numberBVendu);
                                                int totalsPlace = stringToInt(totalPlace);
                                                final double prixB = stringToDouble(inf_price);

                                                comptAdmin = stringToDouble(compteAdmin);
                                                comptClient = stringToDouble(compteClient);

                                                if ((billetVendu < totalsPlace)) {

                                                    billetVendu += 1;
                                                    restEconomic = prixB - transfEc(prixB);
                                                    comptClient += restEconomic;
                                                    comptAdmin += transfEc(prixB);

                                                    //Update Map
                                                    final Map<String, Object> po = new HashMap<>();
                                                    po.put("nBv4", String.valueOf(billetVendu));
                                                    po.put("rEc4", String.valueOf(restEconomic));
                                                    po.put("cAdmin", String.valueOf(comptAdmin));
                                                    po.put("cClient", String.valueOf(comptClient));
                                                    po.put("timestamp", FieldValue.serverTimestamp());

                                                    //1'
                                                    firebaseFirestore.collection("EVENTO2").document(inf_evId).update(po)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    //Create NEW ID PASS IN EVENT

                                                                    final Map<String, Object> ap = new HashMap<>();
                                                                    ap.put("number", String.valueOf(billetVendu));
                                                                    ap.put("loge", loge);
                                                                    ap.put("price", String.valueOf(prixB));
                                                                    ap.put("user_id", user_id);
                                                                    ap.put("timestamp", FieldValue.serverTimestamp());
                                                                    //2'
                                                                    firebaseFirestore.collection("EVENTO2/" + inf_evId + "/PASS").add(ap)
                                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                                @Override
                                                                                public void onSuccess(DocumentReference documentReference) {
                                                                                    //Get NEW PASS FOR USER REFERENCE
                                                                                    final String newId = documentReference.getId();

                                                                                    final Map<String, Object> ap = new HashMap<>();
                                                                                    ap.put("image", inf_ima);
                                                                                    ap.put("name", inf_nam);
                                                                                    ap.put("imageTh", inf_th);
                                                                                    ap.put("day", inf_jh);
                                                                                    ap.put("number", String.valueOf(billetVendu));
                                                                                    ap.put("loge", loge);
                                                                                    ap.put("eventId", inf_evId);
                                                                                    ap.put("price", String.valueOf(prixB));
                                                                                    ap.put("timestamp", FieldValue.serverTimestamp());
                                                                                    //3'   Add Pass of User
                                                                                    firebaseFirestore.collection("USERS/" + user_id + "/PASS")
                                                                                            .document(newId).set(ap)
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {
                                                                                                    success();
                                                                                                }
                                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            Toast.makeText(ConeActivity.this, "" + e,
                                                                                                    Toast.LENGTH_LONG).show();
                                                                                        }
                                                                                    });
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    fail();
                                                                                }
                                                                            });
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            fail();
                                                        }
                                                    });
                                                } else {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(ConeActivity.this, "(Tous les billets ont ete achete)",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        } else {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(ConeActivity.this, "(Error)", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });//}

                        break;
                }
            }
        });
    }

    private void createEventoNoEnable() {
        payment.setEnabled(true);
        payment.setClickable(true);
    }

    private void createEventoSiEnable() {
        payment.setEnabled(false);
        payment.setClickable(false);
    }

    private void success() {

        progressBar.setVisibility(View.INVISIBLE);
        createEventoNoEnable();

        Intent nt = new Intent(ConeActivity.this, TicketActivity.class);
        Toast.makeText(ConeActivity.this,"(Felicitation pour Ton Devil'Pass)" ,
                Toast.LENGTH_LONG).show();
        startActivity(nt);
        finish();
    }

    private void fail(){
        progressBar.setVisibility(View.INVISIBLE);
        createEventoNoEnable();

        /*Intent nt = new Intent(ConeActivity.this, TicketActivity.class);
        Toast.makeText(ConeActivity.this, "Probleme",Toast.LENGTH_LONG).show();
        startActivity(nt);
        finish();*/
    }
    private String trsf(String amount) {
        int amoun = Integer.parseInt(amount);
        amoun = amoun * 5;
        String str = Integer.toString(amoun);
        return str;
    }

    private int stringToInt(String amount) {
        return Integer.parseInt(amount);
    }

    private Double stringToDouble(String amount) {
        return Double.parseDouble(amount);
    }

    private double transfEc(double amount) {
        return amount*0.1;
    }

    private String transfString(double amount) {
        return String.valueOf(amount);
    }

    private String transf(String a,String amount) {
        int medail = Integer.parseInt(a); //STRING TO INTEGER
        int amoun = Integer.parseInt(amount);
        amoun = amoun * 5;
        medail = medail + amoun;
        String str = Integer.toString(medail);//INTEGER TO STRING
        return str;
    }
}