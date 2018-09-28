package com.example.android.movies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.android.movies.Database.AppDatabase;
import com.example.android.movies.R;
import com.example.android.movies.Repository;
import com.example.android.movies.model.Movie;
import com.example.android.movies.model.MovieResponse;
import com.example.android.movies.rest.ApiClient;
import com.example.android.movies.rest.ApiInterface;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class MainViewModel extends AndroidViewModel {


    private MutableLiveData<List<Movie>> movies ;
    private LiveData<List<Movie>> favoriteMovies;
    private final Context mContext;
    private boolean favorite = false;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mContext = this.getApplication();
    }

    public LiveData<List<Movie>> getMovies(String prefChoice){

        if(prefChoice.equals(mContext.getString(R.string.favorite_value))){

            // Set variable favorite to true to indicate that this a favorite movies
            favorite = true;

            return Repository.getFavoriteMovies(mContext,favoriteMovies);
        }else{

            // Set the variable favorite to false to indicate that these are not favorite movies
            favorite = false;

            return Repository.getMoviesFromApi(mContext,prefChoice,movies);
        }

    }

    public boolean isFavorite() {
        return favorite;
    }
}
