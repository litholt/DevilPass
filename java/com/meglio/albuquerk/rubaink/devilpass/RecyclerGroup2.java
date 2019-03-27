package com.meglio.albuquerk.rubaink.devilpass;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RecyclerGroup2 extends RecyclerView.Adapter<RecyclerGroup2.ViewHolder> {

    public List<Evento> event_list;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String type;

    public RecyclerGroup2(List<Evento> event_list){
        this.event_list = event_list;
    }

    @Override
    public RecyclerGroup2.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_home, parent,
                false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new RecyclerGroup2.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerGroup2.ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        String event_user_id = event_list.get(position).getUser_id();

        final String eventoId = event_list.get(position).EventoId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore.collection("EVENTO").document(eventoId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    if(task.getResult().exists()){

                        String image = task.getResult().getString("imageUrl");
                        String imageTh = task.getResult().getString("imageThumb");
                        String day = task.getResult().getString("day");
                        String nam = task.getResult().getString("name");
                        String p1 = task.getResult().getString("p1");
                        String typ = task.getResult().getString("type");
                        type = typ;

                        holder.setEventImage(image, imageTh);
                        holder.setEventText(nam,day,p1);
                    }
                } else {
                    Toast.makeText(context,"(Error)" , Toast.LENGTH_LONG).show();
                }
            }
        });

        /*if (event_user_id.equals(currentUserId)){
        }*/

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type.equals("aa")){

                    Intent evIntent = new Intent(context, ScrollingActivity.class);
                    evIntent.putExtra("eventid",eventoId);
                    context.startActivity(evIntent);
                }else if (type.equals("bb")){

                    Intent evIntent = new Intent(context, Scrolling2Activity.class);
                    evIntent.putExtra("eventid",eventoId);
                    context.startActivity(evIntent);
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

            eventname = mView.findViewById(R.id.note);
            eventDate = mView.findViewById(R.id.eventdate);
            eventprice = mView.findViewById(R.id.eventprice);

            eventname.setText(name);
            eventDate.setText(day);
            if (price==null){
                eventprice.setText("");
            }else
                eventprice.setText(price + " Euro");

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