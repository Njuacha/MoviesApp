package com.example.android.movies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hubert on 8/27/18.
 */

public class MovieResponse {
    @SerializedName("page")
    private int page;
    @SerializedName("total_results")
    private int totalResults;
    @SerializedName("total_pages")
    private int totalPages;
    @SerializedName("results")
    private List<Movie> results;


    public List<Movie> getResults() {
        return results;
    }

}
