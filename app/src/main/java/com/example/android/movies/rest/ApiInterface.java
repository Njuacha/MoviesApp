package com.example.android.movies.rest;

import com.example.android.movies.model.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by hubert on 8/27/18.
 */

public interface ApiInterface {

    @GET("top_rated")
    Call<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("popular")
    Call<MovieResponse> getMostPopularMovies(@Query("api_key") String apiKey);


}
