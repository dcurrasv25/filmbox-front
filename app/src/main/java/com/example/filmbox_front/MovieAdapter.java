package com.example.filmbox_front;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<String> movieImages; // URLs de las portadas
    private Context context;
    private OnMovieClickListener listener;

    public interface OnMovieClickListener {
        void onMovieClick(int position);
    }

    public MovieAdapter(Context context, List<String> movieImages, OnMovieClickListener listener) {
        this.context = context;
        this.movieImages = movieImages;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        if (position < 0 || position >= movieImages.size()) return;
        String url = movieImages.get(position);
        if (url != null && !url.isEmpty()) {
            Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .fit()
                    .centerCrop()
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_launcher_foreground);
        }
        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onMovieClick(position));
        }
    }




    @Override
    public int getItemCount() {
        return movieImages.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgPosterGrid);
        }
    }

    public void updateData(List<String> newImages) {
        this.movieImages.clear();
        if (newImages != null) {
            this.movieImages.addAll(newImages);
        }
        notifyDataSetChanged();
    }

}



