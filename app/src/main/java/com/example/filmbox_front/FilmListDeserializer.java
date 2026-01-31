package com.example.filmbox_front;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Permite que la API devuelva la lista de películas como array directo
 * <code>[ {...}, {...} ]</code> o dentro de un objeto con claves "data", "results" o "films".
 * Deserializa cada elemento como FilmResponse para evitar recursión infinita.
 */
public class FilmListDeserializer implements JsonDeserializer<List<FilmResponse>> {

    @Override
    public List<FilmResponse> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        if (json == null || json.isJsonNull()) {
            return new ArrayList<>();
        }
        JsonArray array = null;
        if (json.isJsonArray()) {
            array = json.getAsJsonArray();
        } else if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();
            JsonElement data = obj.get("data");
            if (data == null) data = obj.get("results");
            if (data == null) data = obj.get("films");
            if (data != null && data.isJsonArray()) {
                array = data.getAsJsonArray();
            }
        }
        if (array == null) {
            return new ArrayList<>();
        }
        List<FilmResponse> list = new ArrayList<>(array.size());
        for (JsonElement element : array) {
            list.add(context.deserialize(element, FilmResponse.class));
        }
        return list;
    }
}
