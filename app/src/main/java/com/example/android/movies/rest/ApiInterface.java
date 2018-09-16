package com.example.android.movies.rest;

import com.example.android.movies.model.MovieResponse;
import com.example.android.movies.model.ReviewResponse;
import com.example.android.movies.model.VideoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hubert on 8/27/18.
 */

public interface ApiInterface {

    @GET("top_rated?api_key=35562acca65b30345f1dad4fbcdae45d")
    Call<MovieResponse> getTopRatedMovies();

    @GET("popular?api_key=35562acca65b30345f1dad4fbcdae45d")
    Call<MovieResponse> getMostPopularMovies();

    @GET("{id}/videos?api_key=35562acca65b30345f1dad4fbcdae45d")
    Call<VideoResponse> getVideos(@Path("id") int id);

    @GET("{id}/reviews?api_key=35562acca65b30345f1dad4fbcdae45d")
    Call<ReviewResponse> getReviews(@Path("id") int id);




}
