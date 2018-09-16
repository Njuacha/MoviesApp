package com.example.android.movies.model;

import com.google.gson.annotations.SerializedName;

public class Video {
    @SerializedName("name")
    private String name;
    @SerializedName("key")
    private String key;
    @SerializedName("type")
    private String type;


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
