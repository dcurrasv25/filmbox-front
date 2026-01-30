package com.example.filmbox_front;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MovieResultAdapter extends RecyclerView.Adapter<MovieResultAdapter.VH> {

    private final List<FilmLite> items = new ArrayList<>();

    public void setItems(List<FilmLite> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_result, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        FilmLite m = items.get(position);
        h.title.setText(m.title);
        h.meta.setText(m.year + " • " + m.duration + " min");

        Glide.with(h.itemView.getContext())
                .load(m.image_url)
                .centerCrop()
                .into(h.poster);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title, meta;

        VH(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.imgPoster);
            title = itemView.findViewById(R.id.tvTitle);
            meta = itemView.findViewById(R.id.tvMeta);
        }
    }
}
