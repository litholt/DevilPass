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
import java.util.Objects;

public class RecyclerMyPass extends RecyclerView.Adapter<RecyclerMyPass.ViewHolder> {

    public List<Evento> event_list;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String image,imageTh,nam,day;

    public RecyclerMyPass(List<Evento> event_list){
        this.event_list = event_list;
    }

    @Override
    public RecyclerMyPass.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_home, parent,
                false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        return new RecyclerMyPass.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerMyPass.ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

        final String passId = event_list.get(position).EventoId;
        final String currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        firebaseFirestore.collection("USERS/"+ currentUserId + "/PASS").document(passId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(Objects.requireNonNull(task.getResult()).exists()){

                        image = task.getResult().getString("image");
                        imageTh = task.getResult().getString("imageTh");
                        day = task.getResult().getString("day");
                        nam = task.getResult().getString("name");

                        holder.setEventImage(image, imageTh);
                        holder.setEventText(nam,day);
                    }
                } else {
                    Toast.makeText(context,"(Error)" , Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent evIntent = new Intent(context, TicketActivity.class);
                evIntent.putExtra("eventId",passId);
                evIntent.putExtra("image",image);
                evIntent.putExtra("imageTh",imageTh);
                evIntent.putExtra("eName",nam);
                evIntent.putExtra("day",day);
                context.startActivity(evIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return event_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView eventname,eventDate;
        private ImageView eventImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setEventText(String name,String day/*,String price*/){

            eventname = mView.findViewById(R.id.note);
            eventDate = mView.findViewById(R.id.eventdate);
            //eventprice = mView.findViewById(R.id.eventprice);

            eventname.setText(name);
            eventDate.setText(day);
            /*if (price == null){
                eventprice.setText("");
            }else
                eventprice.setText(price + " Euro");*/
        }

        public void setEventImage(String downloadUri, String thumbUri){
            eventImage = mView.findViewById(R.id.image);

            if (downloadUri != null && thumbUri != null){
                Glide.with(context).load(downloadUri).thumbnail(
                        Glide.with(context).load(thumbUri)
                ).into(eventImage);
            }
        }
    }
}