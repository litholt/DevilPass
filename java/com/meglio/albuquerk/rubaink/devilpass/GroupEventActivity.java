package com.meglio.albuquerk.rubaink.devilpass;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GroupEventActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private String current_user_id;

    private RecyclerView event_list_view;
    private RecyclerView event_list_view2;

    private List<Evento> event_list;
    private List<Evento> event_list2;
    //private List<Users> user_list;

    private FirebaseAuth firebaseAuth;
    private RecyclerGroup eventRecyclerAdapter;
    private RecyclerGroup2 eventRecyclerAdapter2;

    private Boolean isFirstPageFirstLoad = true,isFirstPageFirstLoad2 = true,controlInCo = false;

    private SwipeRefreshLayout swipeRefreshLayout;
    private String group_name;
    private TextView eventname;

    private ImageView search,back;
    private TextView inMyCo;

    private Query firstQuery,firstQuery2;

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
        setContentView(R.layout.activity_group_event);

        back = findViewById(R.id.back);
        search= findViewById(R.id.event_search);
        eventname = findViewById(R.id.eventName);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        inMyCo = findViewById(R.id.in_my_country);

        group_name = getIntent().getStringExtra("event");
        eventname.setText(group_name);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        event_list = new ArrayList<>();
        event_list2 = new ArrayList<>();

        event_list_view = findViewById(R.id.recycler_event);
        event_list_view2 = findViewById(R.id.recycler_event2);

        eventRecyclerAdapter = new RecyclerGroup(event_list);
        event_list_view.setLayoutManager(new GridLayoutManager(this,3));
        event_list_view.setAdapter(eventRecyclerAdapter);
        event_list_view.setHasFixedSize(true);

        eventRecyclerAdapter2 = new RecyclerGroup2(event_list2);
        event_list_view2.setLayoutManager(new GridLayoutManager(this,3));
        event_list_view2.setAdapter(eventRecyclerAdapter2);
        event_list_view2.setHasFixedSize(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);

                        Intent nt = new Intent(GroupEventActivity.this,
                                GroupEventActivity.class);
                        nt.putExtra("event",group_name);
                        startActivity(nt);
                        finish();
                    }
                },2000);
            }
        });

        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(GroupEventActivity.this,
                        SearchActivity.class);
                startActivity(intent);
            }
        });

        controlEventType();

        //my country
        loadAllEvent();

        //In World
        alsoAllEvent();

        inMyCo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controlInCo){
                    Intent t = new Intent(GroupEventActivity.this,
                            SelectGroupActivity.class);startActivity(t); }
            }
        });
    }

    private void controlEventType() {
        //Know Event Name

        //firebaseFirestore.collection("Country/" + "code/" + code).document(newId).set(ap)
        //firebaseFirestore.collection("Country/" + code + "/" + groupe).document(newId).set(ap);

        switch (group_name) {
            case "Concert":
                firstQuery = firebaseFirestore.collection("CONCERT")
                        .orderBy("timestamp", Query.Direction.ASCENDING);break;
            case "Spectacle":
                firstQuery = firebaseFirestore.collection("SPECTACLES")
                        .orderBy("timestamp", Query.Direction.ASCENDING);break;
            case "Festival":
                firstQuery = firebaseFirestore.collection("FESTIVAL")
                        .orderBy("timestamp", Query.Direction.ASCENDING);break;
            case "Sport":
                firstQuery = firebaseFirestore.collection("SPORT")
                        .orderBy("timestamp", Query.Direction.ASCENDING);break;
            case "Cinema":
                firstQuery = firebaseFirestore.collection("CINEMA")
                        .orderBy("timestamp", Query.Direction.ASCENDING);break;
            case "Loisir":
                firstQuery = firebaseFirestore.collection("LOISIR")
                        .orderBy("timestamp", Query.Direction.ASCENDING);break;
        }
    }

    private void loadAllEvent() {

        firstQuery.addSnapshotListener(GroupEventActivity.this,  new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {

                    event_list.clear();
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String eventId = doc.getDocument().getId();
                            final Evento evento = doc.getDocument().toObject(Evento.class).withId(eventId);

                            if (isFirstPageFirstLoad) {
                                event_list.add(evento);
                            } else {
                                event_list.add(0, evento);
                            }
                            eventRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                    isFirstPageFirstLoad = false;
                }else {
                    controlInCo = true;
                    inMyCo.setText("In My Country (No Event , Add ?)");
                    //inMyCo.setTextColor(R.color.wallet_highlighted_text_holo_light);
                }
            }
        });
    }

    private void alsoAllEvent() {

        firstQuery2 = firebaseFirestore.collection("EVENTO")
                .orderBy("timestamp", Query.Direction.ASCENDING);

        firstQuery2.addSnapshotListener(GroupEventActivity.this,  new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {

                    event_list2.clear();
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String eventId = doc.getDocument().getId();
                            Evento evento = doc.getDocument().toObject(Evento.class).withId(eventId);

                            if (isFirstPageFirstLoad2) {
                                event_list2.add(evento);
                            } else {
                                event_list2.add(0, evento);
                            }
                            eventRecyclerAdapter2.notifyDataSetChanged();
                        }
                    }
                    isFirstPageFirstLoad2 = false;
                }
            }
        });
    }
}
/*String UserId = doc.getDocument().getString("user_id");
                            firebaseFirestore.collection("USERS").document(UserId).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()){
                                                Users user = task.getResult().toObject(Users.class);
                                                if (isFirstPageFirstLoad) {
                                                    user_list.add(user);
                                                    event_list.add(evento);
                                                } else {
                                                    user_list.add(0,user);
                                                    event_list.add(0, evento);
                                               }
                                                eventRecyclerAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });*/