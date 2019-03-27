package com.meglio.albuquerk.rubaink.devilpass;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
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
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SearchActivity extends AppCompatActivity {

    private ImageView back;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String current_user_id;

    private RecyclerView event_list_view;
    private List<Evento> event_list;
    private List<Users> user_list;

    private FirebaseAuth firebaseAuth;
    private RecycleForHome eventRecyclerAdapter;

    private Boolean isFirstPageFirstLoad = true;

    private TextView noSearch,aucun;
    private TextInputEditText editText;
    private ImageView searchBtn,my_pass,home;
    private ConstraintLayout rootLayout;
    private android.widget.SearchView searchView;

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
        setContentView(R.layout.activity_search);

        noSearch = findViewById(R.id.no_search);
        aucun = findViewById(R.id.aucun);
        searchView = findViewById(R.id.search_bar);

        rootLayout = findViewById(R.id.rootlayout);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        event_list = new ArrayList<>();
        user_list = new ArrayList<>();

        event_list_view = findViewById(R.id.recycler_event);

        firebaseAuth = FirebaseAuth.getInstance();

        eventRecyclerAdapter = new RecycleForHome(event_list, user_list);
        event_list_view.setLayoutManager(new GridLayoutManager(this,3));
        event_list_view.setAdapter(eventRecyclerAdapter);
        event_list_view.setHasFixedSize(true);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                clear();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()){
                    clear();
                    searchview(newText.toLowerCase().trim());
                }else
                    clear();
                return true;
            }
        });
    }

    private void clear() {
        event_list.clear();
        user_list.clear();
        eventRecyclerAdapter.notifyDataSetChanged();
    }

    private void searchview(final String edit) {

        Query firstQuery = firebaseFirestore.collection("EVENTO")
                .orderBy("lower", Query.Direction.ASCENDING)
                .startAt(edit)
                .endAt(edit + "\uf8ff");

        noSearch.setText("");
        aucun.setVisibility(View.INVISIBLE);
        firstQuery.addSnapshotListener(SearchActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String eventId = doc.getDocument().getId();
                            final Evento evento = doc.getDocument().toObject(Evento.class).withId(eventId);

                            String UserId = doc.getDocument().getString("user_id");
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
                                    });
                        }
                    }
                }else{
                    noSearch.setText(edit);
                    aucun.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}