package com.meglio.albuquerk.rubaink.devilpass;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class PersFrag extends Fragment {

    List<Book> lstBook ;
    private CardView cardView1,cardView2,cardView3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pers, container, false);

        cardView1 = view.findViewById(R.id.cardv1);
        cardView2 = view.findViewById(R.id.cardv2);
        cardView3 = view.findViewById(R.id.cardv3);

        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nt = new Intent(getActivity(), SelectGroupActivity.class);startActivity(nt);
            }
        });
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nt = new Intent(getActivity(), TrensitionActivity.class);startActivity(nt);
            }
        });
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nt = new Intent(getActivity(), PostsActivity.class);startActivity(nt);
            }
        });

        lstBook = new ArrayList<>();
        lstBook.add(new Book("Concert" ,R.drawable.a));
        lstBook.add(new Book("Spectacle",R.drawable.b));
        lstBook.add(new Book("Loisir",R.drawable.c));
        lstBook.add(new Book("Festival",R.drawable.b));
        lstBook.add(new Book("Sport",R.drawable.d));
        lstBook.add(new Book("Cinema",R.drawable.a));

        RecyclerView myrv = view.findViewById(R.id.recycler_event);
        RecyclerForPers myAdapter = new RecyclerForPers(container.getContext(),lstBook);
        myrv.setLayoutManager(new GridLayoutManager(container.getContext(),3));
        myrv.setAdapter(myAdapter);
        myrv.setHasFixedSize(true);

        return view;
    }
}
