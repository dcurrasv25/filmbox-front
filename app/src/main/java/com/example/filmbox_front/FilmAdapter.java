package com.example.filmbox_front;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.FilmViewHolder> {
    private List<Film> mList;
    private Context context;

    private ApiService apiService;

    public FilmAdapter(List<Film> mList, Context context, ApiService apiService) {
        this.mList = mList;
        this.context = context;
        this.apiService = apiService;
    }

    @NonNull
    @Override
    public FilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_film, parent, false);
        return new FilmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmViewHolder holder, int position) {
        Film film = mList.get(position);

        holder.tvMovieTitle.setText(film.getTitle());
        holder.tvMovieYear.setText(String.valueOf(film.getYear()));

        Glide.with(context)
                .load(film.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.imgFilm);

        // Estado inicial de la estrella
        holder.imgFavorite.setImageResource(film.isFavorite() ?
                android.R.drawable.btn_star_big_on :
                android.R.drawable.btn_star_big_off);

        holder.imgFavorite.setOnClickListener(v -> {
            int movieId = film.getId();
            boolean newState = !film.isFavorite();

            // 1. Cambio visual instantáneo (Optimismo)
            film.setFavorite(newState);
            holder.imgFavorite.setImageResource(newState ?
                    android.R.drawable.btn_star_big_on :
                    android.R.drawable.btn_star_big_off);

            // 2. Llamada real a Django según vuestro urls.py
            // La ruta es: favorites/<int:movie_id>
            Call<Void> call;
            if (newState) {
                // Llama al método PUT de vuestra FavoriteFilmView
                call = apiService.addFavorite(movieId);
            } else {
                // Llama al método DELETE de vuestra FavoriteFilmView
                call = apiService.removeFavorite(movieId);
            }

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!response.isSuccessful()) {
                        // Si el servidor falla (ej. error 403), revertimos el cambio
                        film.setFavorite(!newState);
                        holder.imgFavorite.setImageResource(!newState ?
                                android.R.drawable.btn_star_big_on :
                                android.R.drawable.btn_star_big_off);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // Si no hay internet, revertimos el cambio
                    film.setFavorite(!newState);
                    holder.imgFavorite.setImageResource(!newState ?
                            android.R.drawable.btn_star_big_on :
                            android.R.drawable.btn_star_big_off);
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public static class FilmViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieTitle, tvMovieYear;
        ImageView imgFilm, imgFavorite;

        public FilmViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFavorite = itemView.findViewById(R.id.imgFavorite);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvMovieYear = itemView.findViewById(R.id.tvMovieYear);
            imgFilm = itemView.findViewById(R.id.imgFilm);
        }
    }
}