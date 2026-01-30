package com.example.filmbox_front;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Film {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("image_url")
    private String image_url;

    @SerializedName("year")
    private int year;

    // Django envía objetos Category completos, no solo IDs
    @SerializedName(value = "categories", alternate = {"categorias"})
    private List<Category> categories;

    private boolean isFavorite;

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return image_url;
    }

    public int getYear() {
        return year;
    }

    // Extrae solo los IDs de las categorías
    public List<Integer> getCategoriasIds() {
        if (categories == null) return new ArrayList<>();

        List<Integer> ids = new ArrayList<>();
        for (Category cat : categories) {
            ids.add(cat.getId());
        }
        return ids;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String image_url) {
        this.image_url = image_url;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}