package com.example.android.movies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.movies.Database.AppDatabase;
import com.example.android.movies.model.Movie;
import com.example.android.movies.model.MovieResponse;
import com.example.android.movies.model.Review;
import com.example.android.movies.model.ReviewResponse;
import com.example.android.movies.model.ReviewWithId;
import com.example.android.movies.model.TrailerVideoWithId;
import com.example.android.movies.model.Video;
import com.example.android.movies.model.VideoResponse;
import com.example.android.movies.rest.ApiClient;
import com.example.android.movies.rest.ApiInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class Repository {
    public static final String TRAILER_TYPE = "Trailer";
    private static final String DEFAULT_VALUE = "-1";



    public static String saveImageInFile(Context context, String originalTitle, ImageView imageView) {
        // Get Bitmap from image
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();

        if(drawable == null){
            return null;
        }

        Bitmap bitmap = drawable.getBitmap();

        // Declare File out put stream to be used to write to a file
        FileOutputStream fileOutputStream;
        File directory = context.getFilesDir();

        File file = new File(directory, originalTitle);

        try {

            fileOutputStream = context.openFileOutput(file.getName(), MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return String.valueOf(Uri.fromFile(file));
    }

    public static void removePicFrmInternalMemory(String posterPath) {
        if(posterPath != null){
            File file = new File(Uri.parse(posterPath).getPath());
            file.delete();
        }
    }

    public static void removeFromDatabase(Context context,Movie movie) {
        AppDatabase db = AppDatabase.getDatabaseInstance(context);

        db.movieDoa().deleteMovie(movie);
        db.trailerDoa().deleteTrailersForMovie(movie.getId());
        db.reviewDao().deleteReviewsForMovie(movie.getId());
    }

    public static void insertMovieInDatabase(Context context,Movie movie) {
        AppDatabase.getDatabaseInstance(context).movieDoa().insertMovie(movie);
    }

    public static void insertReviewsInDatabase(Context context, List<ReviewWithId> reviewsWithVideoId) {
        if (reviewsWithVideoId.size() != 0) {
            AppDatabase.getDatabaseInstance(context).reviewDao().insertReviews(reviewsWithVideoId);
        }
    }

    public static void insertTrailersInDatabase(Context context, List<TrailerVideoWithId> trailerVideosWithId) {
        if (trailerVideosWithId.size() != 0) {
            AppDatabase.getDatabaseInstance(context).trailerDoa().insertTrailers(trailerVideosWithId);
        }
    }

    public static boolean isFavorite(Context context, int id){
        Movie movie = AppDatabase.getDatabaseInstance(context).movieDoa().getMovie(id);
        return movie != null;
    }

    public static void loadTrailers(final Context context, final int id, boolean favorite, final MutableLiveData<List<Video>> trailers){
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
    }


    public static void loadReviews(final Context context, final int id, boolean favorite, final MutableLiveData<List<Review>> reviews){
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
    }

   public static LiveData<List<Movie>> getFavoriteMovies(Context context, LiveData<List<Movie>> favoriteMovies){
       if(favoriteMovies == null) {
           favoriteMovies = AppDatabase.getDatabaseInstance(context).movieDoa().loadFavorites();
       }
       return favoriteMovies;
   }

   public static LiveData<List<Movie>> getMoviesFromApi(Context context,String prefChoice, MutableLiveData<List<Movie>> movies){
       String mPrefChoice = DEFAULT_VALUE;
       if(movies == null || (prefChoice != mPrefChoice)) {

           // If it the case of onCreate being called for the first time then movies must be instantiated
           if( movies == null){
               movies = new MutableLiveData<>();
           }

        /*
            Create a connection to the API i.e create client object. And map the client object to the interface which is service object
        */
           ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
           // Perform an HTTP request using the service object just created depending on the sort choice of user
           Call<MovieResponse> call;
           // If there is no pref available for whatever mysterious reason, then return null
           if( prefChoice.equals(context.getString(R.string.top_rated_value))){
               call = apiService.getTopRatedMovies();
           }else if( prefChoice.equals(context.getString(R.string.most_popular_value))){
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
