package com.example.android.movies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.android.movies.AppExecutors;
import com.example.android.movies.Database.AppDatabase;
import com.example.android.movies.Repository;
import com.example.android.movies.model.Review;
import com.example.android.movies.model.ReviewResponse;
import com.example.android.movies.model.ReviewWithId;
import com.example.android.movies.model.TrailerVideoWithId;
import com.example.android.movies.model.Video;
import com.example.android.movies.model.VideoResponse;
import com.example.android.movies.rest.ApiClient;
import com.example.android.movies.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivityViewModel extends AndroidViewModel{
    public static final String TRAILER_TYPE = "Trailer";
    private MutableLiveData<List<Video>> trailers ;
    private MutableLiveData<List<Review>> reviews ;
    private boolean favorite = false;
    private final Context context;

    public DetailActivityViewModel(@NonNull Application application) {
        super(application);
        context = this.getApplication();
    }

    public LiveData<List<Video>> getTrailers(int id) {
        if(trailers == null) {
            trailers = new MutableLiveData<List<Video>>();
            Repository.loadTrailers(context,id,favorite,trailers);
        }
        return trailers;
    }

    public LiveData<List<Review>> getReviews(int id) {
        if(reviews == null){
            reviews = new MutableLiveData<List<Review>>();
            Repository.loadReviews(context,id,favorite,reviews);
        }
        return reviews;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

}
