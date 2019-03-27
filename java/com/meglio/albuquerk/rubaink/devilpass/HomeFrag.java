package com.meglio.albuquerk.rubaink.devilpass;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
import java.util.Objects;

public class HomeFrag extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private String current_user_id;

    private RecyclerView event_list_view;
    private List<Evento> event_list;
    private List<Users> user_list;

    private FirebaseAuth mAuth;
    private Boolean isFirstPageFirstLoad = true;

    private RecycleForHome eventRecyclerAdapter;
    private Query firstQuery;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Begin Chip
        ChipGroup choiceChipGroup = view.findViewById(R.id.choice_chip_group);
        choiceChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, @IdRes int i) {

                Chip chip = chipGroup.findViewById(i);
                if (chip != null) { Toast.makeText(getActivity(),""+ chip.getChipText(),
                        Toast.LENGTH_LONG).show();
                }
            }
        });//End Chip

        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);

                        Intent ntent = new Intent(getActivity(), MainActivity.class);
                        startActivity(ntent);
                    }
                },4000);
            }
        });
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light));

        mAuth = FirebaseAuth.getInstance();
        current_user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        event_list = new ArrayList<>();
        user_list = new ArrayList<>();

        event_list_view = view.findViewById(R.id.recycler_event);
        eventRecyclerAdapter = new RecycleForHome(event_list, user_list);
        event_list_view.setLayoutManager(new GridLayoutManager(getActivity(),3));
        event_list_view.setAdapter(eventRecyclerAdapter);
        event_list_view.setHasFixedSize(true);

        if(mAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();
            firstQuery = firebaseFirestore.collection("EVENTO")
                    .orderBy("timestamp", Query.Direction.ASCENDING);
            loadEvents();
        }
        return view;
    }

    public void loadEvents(){

        firstQuery.addSnapshotListener(Objects.requireNonNull(getActivity()),  new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {

                    event_list.clear();
                    user_list.clear();

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String eventId = doc.getDocument().getId();
                            final Evento evento = doc.getDocument().toObject(Evento.class).withId(eventId);

                            String UserId = doc.getDocument().getString("user_id");
                            assert UserId != null;
                            firebaseFirestore.collection("USERS").document(UserId).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()){

                                                Users user = Objects.requireNonNull(task.getResult()).toObject(Users.class);

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
                    isFirstPageFirstLoad = false;
                }
            }
        });
    }
}