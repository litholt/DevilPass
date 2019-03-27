package com.meglio.albuquerk.rubaink.devilpass;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MyPassActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private String current_user_id;

    private RecyclerView event_list_view;
    private List<Evento> event_list;

    private FirebaseAuth firebaseAuth;
    private RecyclerMyPass eventRecyclerAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private Boolean isFirstPageFirstLoad = true;
    private ImageView back;
    private Query firstQuery;

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
        setContentView(R.layout.activity_my_pass);

        back = findViewById(R.id.back);
        //swipeRefreshLayout = findViewById(R.id.swiperefresh);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        current_user_id = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        event_list = new ArrayList<>();
        event_list_view = findViewById(R.id.recycler_event);

        eventRecyclerAdapter = new RecyclerMyPass(event_list);
        event_list_view.setLayoutManager(new GridLayoutManager(this,3));
        event_list_view.setAdapter(eventRecyclerAdapter);
        event_list_view.setHasFixedSize(true);

        /*swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);

                        Intent nt = new Intent(MyPassActivity.this, PostsActivity.class);
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
                getResources().getColor(android.R.color.holo_red_light));*/

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firstQuery = firebaseFirestore.collection("USERS/" + current_user_id + "/PASS")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        loadAllEvent();
    }

    private void loadAllEvent() {

        firstQuery.addSnapshotListener(MyPassActivity.this,  new EventListener<QuerySnapshot>() {
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
                }
            }
        });
    }
}