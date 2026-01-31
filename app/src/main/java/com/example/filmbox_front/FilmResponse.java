package com.example.filmbox_front;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FilmResponse {

    @SerializedName("id")
    public int id;

    @SerializedName("title")
    public String title;

    @SerializedName("year")
    public int year;

    @SerializedName("duration")
    public int duration;

    @SerializedName("director")
    public String director;

    @SerializedName("description")
    public String description;

    @SerializedName("image_url")
    public String image_url;

    @SerializedName("film_url")
    public String film_url;

    @SerializedName("trailer_url")
    public String trailer_url;

    @SerializedName("categorias")
    public List<Category> categorias;

    public static class Category {
        @SerializedName("id")
        public int id;

        @SerializedName("title")
        public String title;

        @SerializedName("image_url")
        public String image_url;
    }
}
