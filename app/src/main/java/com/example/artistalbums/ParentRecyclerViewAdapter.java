package com.example.artistalbums;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class ParentRecyclerViewAdapter extends RecyclerView.Adapter<ParentRecyclerViewAdapter.MyViewHolder> {
    ArrayList<ChildModel> arrayList;
    ArrayList<ParentModel> parentModelArrayList;
    Context context;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    public ParentRecyclerViewAdapter(ArrayList<ParentModel> parentModelArrayList, MainActivity mainActivity) {
        this.parentModelArrayList=parentModelArrayList;
        this.context=mainActivity;

        sharedpreferences = context.getSharedPreferences("" + R.string.app_name, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parent_recyclerview_items, parent, false);
        return new MyViewHolder(view);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView artist;
        public RecyclerView childRecyclerView;

        public MyViewHolder(View itemView) {
            super(itemView);

            artist = itemView.findViewById(R.id.artist);
            childRecyclerView = itemView.findViewById(R.id.Child_RV);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ParentModel artist = parentModelArrayList.get(position);
        holder.artist.setText(artist.getArtist());

        LinearLayoutManager parentLayoutManager = new LinearLayoutManager(holder.childRecyclerView.getContext(),LinearLayoutManager.HORIZONTAL,false);
        holder.childRecyclerView.setLayoutManager(parentLayoutManager);

        arrayList = new ArrayList<>();
        ChildRecyclerViewAdapter childRecyclerViewAdapter = new ChildRecyclerViewAdapter(arrayList, holder.childRecyclerView.getContext());
        holder.childRecyclerView.setAdapter(childRecyclerViewAdapter);

        Set<String> albumsSet = sharedpreferences.getStringSet(artist.getArtist(),null);
        ArrayList<String> albums = new ArrayList<>(albumsSet);
        Collections.sort(albums);
        for(String album : albums){
            String albumImage = sharedpreferences.getString(artist.getArtist()+"/"+album+"/image","");
            arrayList.add(new ChildModel(artist.getArtist(), album, albumImage));
        }
    }

    @Override
    public int getItemCount() {
        return parentModelArrayList.size();
    }
}