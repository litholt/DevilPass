package com.meglio.albuquerk.rubaink.devilpass;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RecycleForHome extends RecyclerView.Adapter
        <RecycleForHome.ViewHolder> {

    public List<Evento> event_list;
    public List<Users> user_list;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public RecycleForHome(List<Evento> event_list, List<Users> user_list){
        this.event_list = event_list;
        this.user_list = user_list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_home, parent,
                false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final String eventoId = event_list.get(position).EventoId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();
        String event_user_id = event_list.get(position).getUser_id();

        final String eventType = event_list.get(position).getType();

        if (eventType.equals("aa")){

            final String eventname = event_list.get(position).getName();
            final String eventday = event_list.get(position).getDay();
            final String eventprice = event_list.get(position).getP1();//getP1()
            holder.setEventText(eventname,eventday,eventprice);

            final String image_url = event_list.get(position).getImageUrl();
            final String thumbUri = event_list.get(position).getImageThumb();
            holder.setEventImage(image_url, thumbUri);

        }else if (eventType.equals("bb")){

            final String eventname = event_list.get(position).getName();
            final String eventday = event_list.get(position).getDay();
            holder.setEventText(eventname,eventday,"ras");

            final String image_url = event_list.get(position).getImageUrl();
            final String thumbUri = event_list.get(position).getImageThumb();
            holder.setEventImage(image_url, thumbUri);
        }

        if (event_user_id.equals(currentUserId)){
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventType.equals("aa")){

                    Intent t = new Intent(context, ScrollingActivity.class);
                    t.putExtra("eventid",eventoId);
                    context.startActivity(t);
                }else if (eventType.equals("bb")){

                    Intent nt = new Intent(context, Scrolling2Activity.class);
                    nt.putExtra("eventid",eventoId);
                    context.startActivity(nt);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return event_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView eventname,eventDate,eventprice;
        private ImageView eventImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setEventText(String name,String day,String price){

            eventname= mView.findViewById(R.id.note);
            eventDate= mView.findViewById(R.id.eventdate);
            eventprice= mView.findViewById(R.id.eventprice);

            eventname.setText(name);
            eventDate.setText(day);
            if (!price.equals("ras")){
                eventprice.setText(price + " Euro");
            }
        }

        public void setEventImage(String downloadUri, String thumbUri){

            eventImage = mView.findViewById(R.id.image);
            if (downloadUri != null && thumbUri != null)
                Glide.with(context).load(downloadUri).thumbnail(
                        Glide.with(context).load(thumbUri)
                ).into(eventImage);
        }
    }
}