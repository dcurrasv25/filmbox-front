package com.example.filmbox_front;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.filmbox_front.api.ApiClient;
import com.example.filmbox_front.api.FilmBoxService;
import com.example.filmbox_front.models.Film;
import com.example.filmbox_front.models.ReviewRequest;
import com.example.filmbox_front.models.ReviewResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG = "MovieDetailActivity";
    private int movieId;

    private ImageView movieHeaderImage;
    private TextView movieTitle;
    private TextView movieYearDirectorLabel;
    private TextView movieDirectorName;
    private Button trailerButton;
    private ImageView moviePosterThumbnail;
    private TextView movieDescription;
    private TextView ratingValue;
    private LinearLayout starRatingLayout;
    private Button watchedButton;
    private Button watchlistButton;
    private Button favoritesButton;
    private EditText writeReviewEditText;
    private LinearLayout reviewsContainer;
    private Button allReviewsButton;

    private FilmBoxService filmBoxService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        filmBoxService = ApiClient.getService();

        // Inicializar vistas
        movieHeaderImage = findViewById(R.id.movie_header_image);
        movieTitle = findViewById(R.id.movie_title);
        movieYearDirectorLabel = findViewById(R.id.movie_year_director_label);
        movieDirectorName = findViewById(R.id.movie_director_name);
        trailerButton = findViewById(R.id.trailer_button);
        moviePosterThumbnail = findViewById(R.id.movie_poster_thumbnail);
        movieDescription = findViewById(R.id.movie_description);
        ratingValue = findViewById(R.id.rating_value);
        starRatingLayout = findViewById(R.id.star_rating_layout);
        watchedButton = findViewById(R.id.watched_button);
        watchlistButton = findViewById(R.id.watchlist_button);
        favoritesButton = findViewById(R.id.favorites_button);
        writeReviewEditText = findViewById(R.id.write_review_edit_text);
        reviewsContainer = findViewById(R.id.reviews_container);
        allReviewsButton = findViewById(R.id.all_reviews_button);

        // Obtener el ID de la película del Intent
        movieId = getIntent().getIntExtra("movie_id", -1);
        if (movieId != -1) {
            loadMovieDetail(movieId);
            loadMovieReviews(movieId, false);
        } else {
            Toast.makeText(this, "Error: ID de película no proporcionado", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Configurar listeners para los botones de acción
        watchedButton.setOnClickListener(v -> toggleWatchedStatus(movieId));
        watchlistButton.setOnClickListener(v -> toggleWishlistStatus(movieId));
        favoritesButton.setOnClickListener(v -> toggleFavoriteStatus(movieId));

        // Configurar listener para el botón de enviar reseña
        writeReviewEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                postReview(movieId, writeReviewEditText.getText().toString(), 3.0f); // Asume una calificación de 3.0 por ahora
                return true;
            }
            return false;
        });

        // Configurar listener para el botón "ALL REVIEWS"
        allReviewsButton.setOnClickListener(v -> {
            // Aquí podrías iniciar una nueva actividad para mostrar todas las reseñas
            Toast.makeText(MovieDetailActivity.this, "Mostrar todas las reseñas", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadMovieDetail(int movieId) {
        filmBoxService.getMovieDetail(movieId).enqueue(new Callback<Film>() {
            @Override
            public void onResponse(Call<Film> call, Response<Film> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Film film = response.body();
                    updateUIWithFilmDetails(film);
                } else {
                    Toast.makeText(MovieDetailActivity.this, "Error al cargar detalles de la película", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error al cargar detalles de la película: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Film> call, Throwable t) {
                Toast.makeText(MovieDetailActivity.this, "Error de red al cargar detalles", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error de red al cargar detalles de la película", t);
            }
        });
    }

    private void updateUIWithFilmDetails(Film film) {
        movieTitle.setText(film.getTitle());
        movieYearDirectorLabel.setText(film.getYear() + " · DIRECTED BY");
        movieDirectorName.setText(film.getDirector());
        movieDescription.setText(film.getDescription());
        ratingValue.setText(String.valueOf(film.getRating()));

        // Cargar imágenes con Picasso
        if (film.getImageUrl() != null && !film.getImageUrl().isEmpty()) {
            Picasso.get().load(film.getImageUrl()).into(movieHeaderImage);
            Picasso.get().load(film.getImageUrl()).into(moviePosterThumbnail);
        }

        // Actualizar estrellas de calificación
        updateStarRating(film.getRating());
    }

    private void updateStarRating(float rating) {
        starRatingLayout.removeAllViews();
        int numStars = (int) (rating);
        boolean hasHalfStar = (rating - numStars) >= 0.5;

        for (int i = 0; i < 5; i++) {
            ImageView star = new ImageView(this);
            star.setLayoutParams(new LinearLayout.LayoutParams(24, 24));
            if (i < numStars) {
                star.setImageResource(android.R.drawable.btn_star_big_on);
            } else if (hasHalfStar && i == numStars) {
                // No hay un drawable de media estrella por defecto, se podría usar uno personalizado
                star.setImageResource(android.R.drawable.btn_star_big_on); // Usar estrella completa por simplicidad
            } else {
                star.setImageResource(android.R.drawable.btn_star_big_off);
            }
            star.setColorFilter(getResources().getColor(R.color.yellow_star)); // Necesitarás definir este color
            starRatingLayout.addView(star);
        }
    }

    private void loadMovieReviews(int movieId, boolean allReviews) {
        filmBoxService.getMovieReviews(movieId, allReviews).enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ReviewResponse reviewResponse = response.body();
                    updateUIWithReviews(reviewResponse.getPreview());
                } else {
                    Toast.makeText(MovieDetailActivity.this, "Error al cargar reseñas", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error al cargar reseñas: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                Toast.makeText(MovieDetailActivity.this, "Error de red al cargar reseñas", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error de red al cargar reseñas", t);
            }
        });
    }

    private void updateUIWithReviews(List<ReviewResponse.Review> reviews) {
        reviewsContainer.removeAllViews(); // Limpiar reseñas existentes
        for (ReviewResponse.Review review : reviews) {
            // Inflar el layout de una reseña individual y rellenar los datos
            // Esto es un ejemplo básico, deberías crear un layout XML para una reseña individual
            TextView reviewTextView = new TextView(this);
            reviewTextView.setText(review.getAuthor() + ": " + review.getComment() + " (" + review.getRating() + ")");
            reviewsContainer.addView(reviewTextView);
        }
    }

    private void postReview(int movieId, String comment, float rating) {
        ReviewRequest reviewRequest = new ReviewRequest(rating, comment);
        filmBoxService.postMovieReview(movieId, reviewRequest).enqueue(new Callback<ReviewResponse.Review>() {
            @Override
            public void onResponse(Call<ReviewResponse.Review> call, Response<ReviewResponse.Review> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(MovieDetailActivity.this, "Reseña enviada con éxito", Toast.LENGTH_SHORT).show();
                    writeReviewEditText.setText("");
                    loadMovieReviews(movieId, false); // Recargar reseñas
                } else {
                    Toast.makeText(MovieDetailActivity.this, "Error al enviar reseña", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error al enviar reseña: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse.Review> call, Throwable t) {
                Toast.makeText(MovieDetailActivity.this, "Error de red al enviar reseña", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error de red al enviar reseña", t);
            }
        });
    }

    private void toggleWatchedStatus(int movieId) {
        // Lógica para marcar/desmarcar como vista
        Toast.makeText(this, "Toggle Watched para película " + movieId, Toast.LENGTH_SHORT).show();
    }

    private void toggleWishlistStatus(int movieId) {
        // Lógica para añadir/quitar de la lista de deseos
        Toast.makeText(this, "Toggle Wishlist para película " + movieId, Toast.LENGTH_SHORT).show();
    }

    private void toggleFavoriteStatus(int movieId) {
        // Lógica para añadir/quitar de favoritos
        Toast.makeText(this, "Toggle Favorites para película " + movieId, Toast.LENGTH_SHORT).show();
    }
}
