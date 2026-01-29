package com.example.filmbox_front.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewResponse {
    @SerializedName("movie_id")
    private int movieId;
    @SerializedName("total_reviews")
    private int totalReviews;
    @SerializedName("preview")
    private List<Review> preview;

    // Getters y Setters
    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }

    public List<Review> getPreview() {
        return preview;
    }

    public void setPreview(List<Review> preview) {
        this.preview = preview;
    }

    public static class Review {
        @SerializedName("author")
        private String author;
        @SerializedName("rating")
        private float rating;
        @SerializedName("comment")
        private String comment;
        @SerializedName("date")
        private String date;

        // Getters y Setters
        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public float getRating() {
            return rating;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}

