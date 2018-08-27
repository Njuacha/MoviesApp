package com.example.android.movies.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hubert on 8/27/18.
 */

public class ApiClient {

    public static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if(retrofit == null){

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
