package com.example.filmbox_front;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

public class MainCategoryAdapter extends RecyclerView.Adapter<MainCategoryAdapter.ViewHolder> {
    private static final String TAG = "MainCategoryAdapter";
    private static final int MAX_FILMS_PREVIEW = 9;

    private List<Category> categories;
    private Map<Integer, List<Film>> filmsByCat;
    private Context context;

    private ApiService apiService;

    public MainCategoryAdapter(List<Category> categories, Map<Integer, List<Film>> filmsByCat, Context context, ApiService apiService) {
        this.categories = categories;
        this.filmsByCat = filmsByCat;
        this.context = context;
        this.apiService = apiService;

        Log.d(TAG, "Adapter creado con " + categories.size() + " categorías");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_category_row, parent, false);
        Log.d(TAG, "ViewHolder creado");
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category cat = categories.get(position);
        String title = cat.getTitle();

        Log.d(TAG, "Binding posición " + position + ": " + title + " (ID: " + cat.getId() + ")");

        if (title == null || title.isEmpty()) {
            Log.w(TAG, "ADVERTENCIA: Título vacío o null en posición " + position);
            holder.tvTitle.setText("Sin título");
        } else {
            holder.tvTitle.setText(title);
        }

        List<Film> allFilms = filmsByCat.get(cat.getId());
        if (allFilms != null && !allFilms.isEmpty()) {
            Log.d(TAG, "Total de películas en " + title + ": " + allFilms.size());

            List<Film> filmsToShow = allFilms.size() > MAX_FILMS_PREVIEW
                    ? allFilms.subList(0, MAX_FILMS_PREVIEW)
                    : allFilms;

            holder.rvFilms.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.rvFilms.setAdapter(new FilmAdapter(filmsToShow, context, apiService));

            if (allFilms.size() > MAX_FILMS_PREVIEW) {
                holder.btnViewAll.setVisibility(View.VISIBLE);
                holder.btnViewAll.setText("Ver todas (" + allFilms.size() + ")");

                holder.btnViewAll.setOnClickListener(v -> {
                    holder.rvFilms.setAdapter(new FilmAdapter(allFilms, context, apiService));
                    holder.btnViewAll.setVisibility(View.GONE);
                    Log.d(TAG, "Mostrando todas las películas de " + title);
                });
            } else {
                holder.btnViewAll.setVisibility(View.GONE);
            }
        } else {
            Log.w(TAG, "No hay películas para la categoría " + title);
            holder.btnViewAll.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        int count = categories.size();
        Log.d(TAG, "getItemCount: " + count);
        return count;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        RecyclerView rvFilms;
        TextView btnViewAll;

        ViewHolder(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvCategoryTitle);
            rvFilms = v.findViewById(R.id.rvFilmsInCategory);
            btnViewAll = v.findViewById(R.id.btnViewAll);

            if (tvTitle == null) {
                Log.e(TAG, "ERROR: No se pudo encontrar tvCategoryTitle en el layout!");
            }
            if (rvFilms == null) {
                Log.e(TAG, "ERROR: No se pudo encontrar rvFilmsInCategory en el layout!");
            }
            if (btnViewAll == null) {
                Log.e(TAG, "ERROR: No se pudo encontrar btnViewAll en el layout!");
            }
        }
    }
}