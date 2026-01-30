package com.example.filmbox_front;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private static final String PREFS = "search_prefs";
    private static final String KEY_RECENTS = "recents";
    private static final int MAX_RECENTS = 10;

    // Ajusta estos dos hasta que quede perfecto con tu Figma
    private static final int TOP_MARGIN_EXPLORE_DP = 2;   // como estaba
    private static final int TOP_MARGIN_FOCUS_DP   = 50;  // cuando clicas (baja)

    private enum Mode { EXPLORE, RECENTS, RESULTS }
    private Mode mode = Mode.EXPLORE;

    public SearchFragment() {
        super(R.layout.fragment_search);
    }

    private int dp(int value) {
        return (int) (value * requireContext().getResources().getDisplayMetrics().density);
    }

    // -------- Recents helpers --------
    private List<String> loadRecents() {
        SharedPreferences sp = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String raw = sp.getString(KEY_RECENTS, "");
        if (raw == null || raw.trim().isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(raw.split("\\|\\|")));
    }

    private void saveRecents(List<String> recents) {
        SharedPreferences sp = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String raw = String.join("||", recents);
        sp.edit().putString(KEY_RECENTS, raw).apply();
    }

    private void addRecent(String q) {
        q = q.trim();
        if (q.isEmpty()) return;

        List<String> recents = loadRecents();
        recents.remove(q);
        recents.add(0, q);

        if (recents.size() > MAX_RECENTS) {
            recents = recents.subList(0, MAX_RECENTS);
        }
        saveRecents(recents);
    }
    // --------------------------------

    private void setMode(
            Mode newMode,
            TextView tvBuscar,
            ImageView ivSearchIcon,
            TextView tvExplorar, RecyclerView rvCategories,
            TextView tvRecientes, RecyclerView rvRecientes,
            ChipGroup chipGroup, RecyclerView rvResults,
            RecentAdapter recentAdapter,
            EditText etSearch,
            View divider,
            View searchBox
    ) {
        mode = newMode;

        boolean showExplore = (newMode == Mode.EXPLORE);
        boolean showRecents = (newMode == Mode.RECENTS);
        boolean showResults = (newMode == Mode.RESULTS);

        // 1) “Buscar” solo en explore
        tvBuscar.setVisibility(showExplore ? View.VISIBLE : View.GONE);

        // 2) Icono lupa ↔ flecha
        if (showExplore) {
            ivSearchIcon.setImageResource(R.drawable.ic_search_small);
            ivSearchIcon.setOnClickListener(null);
        } else {
            ivSearchIcon.setImageResource(R.drawable.ic_back_small);
            ivSearchIcon.setOnClickListener(v -> {
                etSearch.setText("");
                etSearch.clearFocus();
                setMode(Mode.EXPLORE, tvBuscar, ivSearchIcon, tvExplorar, rvCategories,
                        tvRecientes, rvRecientes, chipGroup, rvResults, recentAdapter, etSearch, divider, searchBox);
            });
        }

        // 3) Secciones
        tvExplorar.setVisibility(showExplore ? View.VISIBLE : View.GONE);
        rvCategories.setVisibility(showExplore ? View.VISIBLE : View.GONE);

        tvRecientes.setVisibility(showRecents ? View.VISIBLE : View.GONE);
        rvRecientes.setVisibility(showRecents ? View.VISIBLE : View.GONE);

        chipGroup.setVisibility(showResults ? View.VISIBLE : View.GONE);
        rvResults.setVisibility(showResults ? View.VISIBLE : View.GONE);

        // Divider: en recents lo oculto (se ve más limpio)
        divider.setVisibility(showRecents ? View.GONE : View.VISIBLE);

        // 4) MOVER SOLO AL CLICAR (recents/results)
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) searchBox.getLayoutParams();
        lp.topMargin = dp(showExplore ? TOP_MARGIN_EXPLORE_DP : TOP_MARGIN_FOCUS_DP);
        searchBox.setLayoutParams(lp);

        if (showRecents) {
            recentAdapter.setItems(loadRecents());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Views
        TextView tvBuscar = view.findViewById(R.id.tvBuscar);
        ImageView ivSearchIcon = view.findViewById(R.id.ivSearchIcon);
        EditText etSearch = view.findViewById(R.id.etSearch);

        View searchBox = view.findViewById(R.id.searchBox);
        View divider = view.findViewById(R.id.dividerSearchExplorar);

        TextView tvExplorar = view.findViewById(R.id.tvExplorar);
        RecyclerView rvCategories = view.findViewById(R.id.rvCategories);

        TextView tvRecientes = view.findViewById(R.id.tvRecientes);
        RecyclerView rvRecientes = view.findViewById(R.id.rvRecientes);

        ChipGroup chipGroup = view.findViewById(R.id.chipGroup);
        Chip chipMovies = view.findViewById(R.id.chipMovies);
        Chip chipUsers = view.findViewById(R.id.chipUsers);

        RecyclerView rvResults = view.findViewById(R.id.rvResults);

        // Categories
        rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 2));
        CategoryAdapter categoryAdapter = new CategoryAdapter();
        rvCategories.setAdapter(categoryAdapter);

        // Recents
        RecentAdapter recentAdapter = new RecentAdapter();
        rvRecientes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecientes.setAdapter(recentAdapter);

        // Results (movies/users)
        MovieResultAdapter movieAdapter = new MovieResultAdapter();
        UserResultAdapter userAdapter = new UserResultAdapter();

        rvResults.setLayoutManager(new LinearLayoutManager(getContext()));
        rvResults.setAdapter(movieAdapter);

        // Back del móvil
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (mode != Mode.EXPLORE) {
                            etSearch.setText("");
                            etSearch.clearFocus();
                            setMode(Mode.EXPLORE, tvBuscar, ivSearchIcon, tvExplorar, rvCategories,
                                    tvRecientes, rvRecientes, chipGroup, rvResults, recentAdapter, etSearch, divider, searchBox);
                        } else {
                            setEnabled(false);
                            requireActivity().onBackPressed();
                        }
                    }
                }
        );

        // Estado inicial
        setMode(Mode.EXPLORE, tvBuscar, ivSearchIcon, tvExplorar, rvCategories,
                tvRecientes, rvRecientes, chipGroup, rvResults, recentAdapter, etSearch, divider, searchBox);

        // Carga categorías
        RetrofitClient.api().getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryAdapter.setItems(response.body());
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });

        // Click/focus en barra: si está vacía → recientes
        etSearch.setOnClickListener(v -> {
            if (etSearch.getText().toString().trim().isEmpty()) {
                setMode(Mode.RECENTS, tvBuscar, ivSearchIcon, tvExplorar, rvCategories,
                        tvRecientes, rvRecientes, chipGroup, rvResults, recentAdapter, etSearch, divider, searchBox);
            }
        });

        etSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && etSearch.getText().toString().trim().isEmpty()) {
                setMode(Mode.RECENTS, tvBuscar, ivSearchIcon, tvExplorar, rvCategories,
                        tvRecientes, rvRecientes, chipGroup, rvResults, recentAdapter, etSearch, divider, searchBox);
            }
        });

        // Click en reciente
        recentAdapter.setOnRecentClickListener(query -> {
            etSearch.setText(query);
            etSearch.setSelection(query.length());
            addRecent(query);

            if (chipUsers.isChecked()) {
                rvResults.setAdapter(userAdapter);
                doUserSearch(query, userAdapter, tvBuscar, ivSearchIcon, tvExplorar, rvCategories,
                        tvRecientes, rvRecientes, chipGroup, rvResults, recentAdapter, etSearch, divider, searchBox);
            } else {
                chipMovies.setChecked(true);
                rvResults.setAdapter(movieAdapter);
                doMovieSearch(query, movieAdapter, tvBuscar, ivSearchIcon, tvExplorar, rvCategories,
                        tvRecientes, rvRecientes, chipGroup, rvResults, recentAdapter, etSearch, divider, searchBox);
            }
        });

        // Chips: alterna movies/users
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String q = etSearch.getText().toString().trim();
            if (mode != Mode.RESULTS || q.isEmpty()) return;

            if (checkedId == R.id.chipMovies) {
                rvResults.setAdapter(movieAdapter);
                doMovieSearch(q, movieAdapter, tvBuscar, ivSearchIcon, tvExplorar, rvCategories,
                        tvRecientes, rvRecientes, chipGroup, rvResults, recentAdapter, etSearch, divider, searchBox);
            } else if (checkedId == R.id.chipUsers) {
                rvResults.setAdapter(userAdapter);
                doUserSearch(q, userAdapter, tvBuscar, ivSearchIcon, tvExplorar, rvCategories,
                        tvRecientes, rvRecientes, chipGroup, rvResults, recentAdapter, etSearch, divider, searchBox);
            }
        });

        // Mientras escribe: si vacío → recientes
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String q = s.toString().trim();
                if (q.isEmpty()) {
                    setMode(Mode.RECENTS, tvBuscar, ivSearchIcon, tvExplorar, rvCategories,
                            tvRecientes, rvRecientes, chipGroup, rvResults, recentAdapter, etSearch, divider, searchBox);
                }
            }
        });

        // Enter/Search del teclado
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            String q = etSearch.getText().toString().trim();
            if (q.isEmpty()) return false;

            addRecent(q);

            if (chipUsers.isChecked()) {
                rvResults.setAdapter(userAdapter);
                doUserSearch(q, userAdapter, tvBuscar, ivSearchIcon, tvExplorar, rvCategories,
                        tvRecientes, rvRecientes, chipGroup, rvResults, recentAdapter, etSearch, divider, searchBox);
            } else {
                chipMovies.setChecked(true);
                rvResults.setAdapter(movieAdapter);
                doMovieSearch(q, movieAdapter, tvBuscar, ivSearchIcon, tvExplorar, rvCategories,
                        tvRecientes, rvRecientes, chipGroup, rvResults, recentAdapter, etSearch, divider, searchBox);
            }
            return false;
        });
    }

    private void doMovieSearch(
            String query,
            MovieResultAdapter movieAdapter,
            TextView tvBuscar,
            ImageView ivSearchIcon,
            TextView tvExplorar, RecyclerView rvCategories,
            TextView tvRecientes, RecyclerView rvRecientes,
            ChipGroup chipGroup, RecyclerView rvResults,
            RecentAdapter recentAdapter,
            EditText etSearch,
            View divider,
            View searchBox
    ) {
        setMode(Mode.RESULTS, tvBuscar, ivSearchIcon, tvExplorar, rvCategories,
                tvRecientes, rvRecientes, chipGroup, rvResults, recentAdapter, etSearch, divider, searchBox);

        RetrofitClient.api().searchMovies(query).enqueue(new Callback<List<FilmLite>>() {
            @Override
            public void onResponse(@NonNull Call<List<FilmLite>> call, @NonNull Response<List<FilmLite>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    movieAdapter.setItems(response.body());
                } else {
                    movieAdapter.setItems(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<FilmLite>> call, @NonNull Throwable t) {
                t.printStackTrace();
                movieAdapter.setItems(new ArrayList<>());
            }
        });
    }

    private void doUserSearch(
            String query,
            UserResultAdapter userAdapter,
            TextView tvBuscar,
            ImageView ivSearchIcon,
            TextView tvExplorar, RecyclerView rvCategories,
            TextView tvRecientes, RecyclerView rvRecientes,
            ChipGroup chipGroup, RecyclerView rvResults,
            RecentAdapter recentAdapter,
            EditText etSearch,
            View divider,
            View searchBox
    ) {
        setMode(Mode.RESULTS, tvBuscar, ivSearchIcon, tvExplorar, rvCategories,
                tvRecientes, rvRecientes, chipGroup, rvResults, recentAdapter, etSearch, divider, searchBox);

        RetrofitClient.api().searchUsers(query).enqueue(new Callback<List<UserLite>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserLite>> call, @NonNull Response<List<UserLite>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userAdapter.setItems(response.body());
                } else {
                    userAdapter.setItems(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserLite>> call, @NonNull Throwable t) {
                t.printStackTrace();
                userAdapter.setItems(new ArrayList<>());
            }
        });
    }
}
