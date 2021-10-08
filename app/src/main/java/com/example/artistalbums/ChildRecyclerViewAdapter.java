package com.example.artistalbums;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class ChildRecyclerViewAdapter extends RecyclerView.Adapter<ChildRecyclerViewAdapter.MyViewHolder> {
    ArrayList<ChildModel> childModelArrayList;
    Context context;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    public ChildRecyclerViewAdapter(ArrayList<ChildModel> childModelArrayList, Context context) {
        this.childModelArrayList = childModelArrayList;
        this.context = context;

        sharedpreferences = context.getSharedPreferences("" + R.string.app_name, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView albumImage;
        public TextView albumName;

        public MyViewHolder(View itemView) {
            super(itemView);
            albumImage = itemView.findViewById(R.id.album_image);
            albumName = itemView.findViewById(R.id.album_name);

        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_recyclerview_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder  holder, int position) {
        ChildModel albumDetails = childModelArrayList.get(position);

        Picasso.get()
                .load(albumDetails.getAlbumImage())
                .into(holder.albumImage);
        holder.albumName.setText(albumDetails.getAlbumName());

        if(sharedpreferences.getString("1","1").equals("1")) {
            String album = albumDetails.getArtist() + "/" + albumDetails.getAlbumName();
            Set<String> set = sharedpreferences.getStringSet(album, null);
            ArrayList<String> albumItems = new ArrayList<>(set);
            Collections.sort(albumItems);
            String topic = albumItems.get(0);
            keepString("topic", topic);
            keepString("subject", album);
            keepString("image", albumDetails.getAlbumImage());
            keepString("url", sharedpreferences.getString(album + "/" + topic, ""));
            keepString("album",album);
            Intent Bintent = new Intent("UI");
            Bintent.putExtra("key", "enable");
            LocalBroadcastManager.getInstance(context).sendBroadcast(Bintent);

            keepString("1","0");
        }


        holder.itemView.setOnClickListener(view -> {
            ChildModel currentItem = childModelArrayList.get(holder.getAdapterPosition());
            keepString("album", currentItem.getArtist()+"/"+currentItem.getAlbumName());

            Intent Bintent = new Intent("UI");
            Bintent.putExtra("key", "visibility");
            LocalBroadcastManager.getInstance(context).sendBroadcast(Bintent);

        });
    }

    @Override
    public int getItemCount() {
        return childModelArrayList.size();
    }

    private void keepString(String keyStr1, String valueStr1) {
        editor.putString(keyStr1, valueStr1);
        editor.apply();
    }
}
