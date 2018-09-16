package com.example.android.movies.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.movies.model.Review;
import com.example.android.movies.model.ReviewResponse;
import com.example.android.movies.model.Video;
import com.example.android.movies.model.VideoResponse;
import com.example.android.movies.rest.ApiClient;
import com.example.android.movies.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivityViewModel extends ViewModel{
    private MutableLiveData<List<Video>> trailers ;
    private MutableLiveData<List<Review>> reviews ;

    public LiveData<List<Video>> loadTrailers (int id){
        // Get the Trailer videos from api
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<VideoResponse> call = apiService.getVideos(id);

        call.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {

                //Iterate through the videos and remove those which are not trailers
                List<Video> videos = response.body().getResults();
                List<Video> trailerVideos = new ArrayList<>();
                for(Video video: videos ){
                    if(video.getType().equals("Trailer")){
                        trailerVideos.add(video);
                    }
                }

                trailers.postValue(trailerVideos);
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {

            }
        });

        return trailers;
    }

    public LiveData<List<Review>> loadReviews(int id){
        // Get the Trailer videos from api
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
}
