package com.example.android.movies.model;

import com.google.gson.annotations.SerializedName;

public class Video {
    @SerializedName("name")
    private String name;
    @SerializedName("key")
    private String key;
    @SerializedName("type")
    private String type;

    public Video(String name, String key, String type) {
        this.name = name;
        this.key = key;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }
}
