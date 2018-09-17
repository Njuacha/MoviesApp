package com.example.android.movies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.example.android.movies.R;
import com.example.android.movies.model.Movie;
import com.example.android.movies.model.MovieResponse;
import com.example.android.movies.rest.ApiClient;
import com.example.android.movies.rest.ApiInterface;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class MainViewModel extends AndroidViewModel {

    private static final String DEFAULT_VALUE = "-1";
    private MutableLiveData<List<Movie>> movies ;
    private Context mContext;
    private String mPrefChoice = DEFAULT_VALUE;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mContext = this.getApplication();
    }

    public LiveData<List<Movie>> getMovies(String prefChoice){
        /*
          This condition is satisfied when onCreate is first called in main Or when our preference changes
        */
        if(movies == null || (prefChoice != mPrefChoice)) {

            // If it the case of onCreate being called for the first time then movies must be instantiated
            if( movies == null){
                movies = new MutableLiveData<List<Movie>>();
            }

        /*
            Create a connection to the API i.e create client object. And map the client object to the interface which is service object
        */
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            // Perform an HTTP request using the service object just created depending on the sort choice of user
            Call<MovieResponse> call;
            // If there is no pref available for whatever mysterious reason, then return null
            if( prefChoice.equals(mContext.getString(R.string.top_rated_value))){
                call = apiService.getTopRatedMovies();
            }else if( prefChoice.equals(mContext.getString(R.string.most_popular_value))){
                call = apiService.getMostPopularMovies();
            }else {
                return null;
            }

            try {
                Response<MovieResponse> response = call.execute();
                movies.postValue(response.body().getResults());
            } catch (IOException e) {
                e.printStackTrace();
            }
            /*
             Else we are not loading for the first time
             nor has pref choice changed so we return already cached movies
            */
        }

        return movies;
    }

}
