package com.example.filmbox_front.models;

import com.google.gson.annotations.SerializedName;

public class Film {
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("year")
    private int year;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("length")
    private String length;
    @SerializedName("director")
    private String director;
    @SerializedName("description")
    private String description;
    @SerializedName("rating")
    private float rating;

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}