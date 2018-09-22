package com.example.android.movies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.movies.AppExecutors;
import com.example.android.movies.Database.AppDatabase;
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
    private Context context;

    public DetailActivityViewModel(@NonNull Application application) {
        super(application);
        context = this.getApplication();
    }

    public LiveData<List<Video>> loadTrailers (final int id){
        if(favorite){
            AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    List<Video> trailer2 = new ArrayList<>();
                    List<TrailerVideoWithId> trailerVideosWithId = AppDatabase.getDatabaseInstance(context).trailerDoa().getTrailerVideosWithId(id);

                    for(TrailerVideoWithId trailerVideoWithId: trailerVideosWithId){
                        trailer2.add(trailerVideoWithId.getVideo());
                    }

                    trailers.postValue(trailer2);

                }
            });

        }else {
            // Get the Trailer videos from api
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<VideoResponse> call = apiService.getVideos(id);

            call.enqueue(new Callback<VideoResponse>() {
                @Override
                public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {

                    //Iterate through the videos and remove those which are not trailers
                    List<Video> videos = response.body().getResults();
                    List<Video> trailerVideos = new ArrayList<>();
                    for (Video video : videos) {
                        if (video.getType().equals(TRAILER_TYPE)) {
                            trailerVideos.add(video);
                        }
                    }

                    trailers.postValue(trailerVideos);
                }

                @Override
                public void onFailure(Call<VideoResponse> call, Throwable t) {

                }
            });
        }

        return trailers;
    }

    public LiveData<List<Review>> loadReviews(final int id){
       if(favorite){

           AppExecutors.getsInstance().diskIO().execute(new Runnable() {
               @Override
               public void run() {
                   List<Review> reviews2 = new ArrayList<>();
                   List<ReviewWithId> reviewsWithId = AppDatabase.getDatabaseInstance(context).reviewDao().getReviewsWithId(id);

                   for(ReviewWithId reviewWithId: reviewsWithId){
                       reviews2.add(reviewWithId.getReview());
                   }

                   reviews.postValue(reviews2);

               }
           });

       }else{

           ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

           Call<ReviewResponse> call = apiService.getReviews(id);

           call.enqueue(new Callback<ReviewResponse>() {
               @Override
               public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                   reviews.postValue(response.body().getResults());
               }

               @Override
               public void onFailure(Call<ReviewResponse> call, Throwable t) {

               }
           });
       }


        return reviews;
    }

    public LiveData<List<Video>> getTrailers(int id) {
        if(trailers == null) {
            trailers = new MutableLiveData<List<Video>>();
            loadTrailers(id);
        }
        return trailers;
    }

    public LiveData<List<Review>> getReviews(int id) {
        if(reviews == null){
            reviews = new MutableLiveData<List<Review>>();
            loadReviews(id);
        }
        return reviews;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

}
