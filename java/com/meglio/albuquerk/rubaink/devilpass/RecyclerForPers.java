package com.meglio.albuquerk.rubaink.devilpass;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RecyclerForPers extends RecyclerView.Adapter
        <RecyclerForPers.MyViewHolder> {

    private Context mContext ;
    private List<Book> mData ;

    public RecyclerForPers(Context mContext, List<Book> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public RecyclerForPers.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.event_list,parent,false);
        return new RecyclerForPers.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerForPers.MyViewHolder holder, final int position) {

        holder.book_title.setText(mData.get(position).getTitle());
        holder.img_thumbnail.setImageResource(mData.get(position).getCategory());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = mData.get(position).getTitle();

                Intent evt = new Intent(mContext, GroupEventActivity.class);

                switch (type) {
                    case "Concert":
                        /*mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=com.jeu.rubaink.sportjeu")));*/
                        evt.putExtra("event","Concert");
                        mContext.startActivity(evt);
                        break;
                    case "Spectacle":
                        evt.putExtra("event","Spectacle");
                        mContext.startActivity(evt);
                        break;
                    case "Loisir":
                        evt.putExtra("event","Loisir");
                        mContext.startActivity(evt);

                        break;
                    case "Festival":
                        evt.putExtra("event","Festival");
                        mContext.startActivity(evt);

                        break;
                    case "Cinema":
                        evt.putExtra("event","Cinema");
                        mContext.startActivity(evt);

                        break;
                    case "Sport":
                        evt.putExtra("event","Sport");
                        mContext.startActivity(evt);

                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView book_title;
        ImageView img_thumbnail;
        CardView constraintLayout ;

        public MyViewHolder(View itemView) {
            super(itemView);

            book_title = itemView.findViewById(R.id.note) ;
            img_thumbnail = itemView.findViewById(R.id.imageView);
            constraintLayout =  itemView.findViewById(R.id.cl);
        }
    }
}
