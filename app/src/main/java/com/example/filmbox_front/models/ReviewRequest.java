package com.example.filmbox_front.models;

import com.google.gson.annotations.SerializedName;

public class ReviewRequest {
    @SerializedName("rating")
    private float rating;
    @SerializedName("comment")
    private String comment;

    public ReviewRequest(float rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    // Getters y Setters
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
}

