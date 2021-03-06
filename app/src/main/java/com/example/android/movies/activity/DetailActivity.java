package com.example.android.movies.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movies.Database.AppDatabase;
import com.example.android.movies.R;
import com.example.android.movies.Repository;
import com.example.android.movies.adapter.ReviewsAdapter;
import com.example.android.movies.adapter.TrailersAdapter;
import com.example.android.movies.model.Movie;
import com.example.android.movies.model.Review;
import com.example.android.movies.model.ReviewWithId;
import com.example.android.movies.model.TrailerVideoWithId;
import com.example.android.movies.model.Video;
import com.example.android.movies.viewmodel.DetailActivityViewModel;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.movies.activity.MainActivity.EXTRA_FAVORITE;
import static com.example.android.movies.activity.MainActivity.EXTRA_MOVIE;

public class DetailActivity extends AppCompatActivity implements TrailersAdapter.TrailerClickListener {

    // Declare and instantiate views
    @BindView((R.id.iv_poster))
    ImageView mImageView;
    @BindView(R.id.tv_release_date)
    TextView mReleaseDateTv;
    @BindView(R.id.tv_plot_synopsis)
    TextView mPlotSynopsisTv;
    @BindView(R.id.tv_title)
    TextView mTitleTv;
    @BindView(R.id.rv_trailers)
    RecyclerView mRvTrailers;
    @BindView(R.id.rv_reviews)
    RecyclerView mRvReviews;
    @BindView(R.id.btn_favorite)
    Button mFavoriteBtn;
    @BindView(R.id.rating_bar)
    RatingBar mRatingBar;
    @BindView(R.id.pb)
    ProgressBar mProgressBar;
    @BindView(R.id.no_trailer)
    TextView trailerTv;
    @BindView(R.id.no_review)
    TextView reviewTv;


    private TrailersAdapter mTrailersAdapter;
    private ReviewsAdapter mReviewsAdapter;
    private Movie mMovie;
    private AppDatabase mDb;
    private boolean mFavorite = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_MOVIE)) {
            // Get the movie object which was passed
            mMovie = intent.getParcelableExtra(EXTRA_MOVIE);
            // Set the image to the mImageView
            String path = mMovie.getPosterPath();

            Picasso.with(this).load(path).into(mImageView);
            // Set the title to the textView
            mTitleTv.setText(mMovie.getOriginalTitle());

            mRatingBar.setRating(mMovie.getUserRating()/(float)2);
            mReleaseDateTv.setText(mMovie.getReleaseDate());
            mPlotSynopsisTv.setText(mMovie.getAPlotSynopsis());

            // Instantiate trailer and reviews adapters
            mTrailersAdapter = new TrailersAdapter(this, this);
            mReviewsAdapter = new ReviewsAdapter(this);

            mDb = AppDatabase.getDatabaseInstance(this);

            // Set dividers for recycler views
            mRvTrailers.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            mRvReviews.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            // Set adapters to respective rv's

            mRvTrailers.setAdapter(mTrailersAdapter);
            mRvReviews.setAdapter(mReviewsAdapter);
            setUpData();

        }
    }

    private void  setUpData() {

        new AsyncTask<Void,Void, Boolean>(){

            @Override
            protected Boolean doInBackground(Void... voids) {
                // Get the boolean value from main activity that depicts
                // if the sort order is now favorite movies or not in main activity
                boolean favorite = getIntent().getBooleanExtra(EXTRA_FAVORITE, false);
                if(favorite){
                    // Because the sort order is favorite movies we can assume that this is a favorite movie
                    return true;
                }
                else{
                    // if the sort order is not favorite movies we have to check if the movie is a favorite
                    // By verifying if there is a movie like this in the favorite list in database
                    return Repository.isFavorite(getApplicationContext(),mMovie.getId());
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {

                mFavoriteBtn.setVisibility(View.VISIBLE);
                mFavorite = aBoolean;
                // If it is a favorite movie we change text of button to "Unmark this favorite"
                if(mFavorite){
                    mFavoriteBtn.setText(R.string.unmark_this_favorite);
                }

                setUpViewModel(mMovie.getId());
            }

        }.execute();

    }

    private void setUpViewModel(int id) {
        final DetailActivityViewModel viewModel = ViewModelProviders.of(this).get(DetailActivityViewModel.class);
        viewModel.setFavorite(mFavorite);
        // Set up the trailers
        viewModel.getTrailers(id).observe(this, new Observer<List<Video>>() {
            @Override
            public void onChanged(@Nullable List<Video> videos) {

                mTrailersAdapter.setVideos(videos);

                if (videos.size() == 0){
                    trailerTv.setVisibility(View.VISIBLE);
                }else{
                    trailerTv.setVisibility(View.INVISIBLE);
                }

            }

        });


        // Set up the reviews
        viewModel.getReviews(id).observe(this, new Observer<List<Review>>() {
            @Override
            public void onChanged(@Nullable List<Review> reviews) {

                mReviewsAdapter.setReviews(reviews);

                if(reviews.size() == 0){
                    reviewTv.setVisibility(View.VISIBLE);
                }else{
                    reviewTv.setVisibility(View.INVISIBLE);
                }

            }
        });

    }

    @Override
    public void onTrailerClicked(String videoKey) {
        String baseUrl = getString(R.string.youtube_base_url);
        Uri youTubeUri = Uri.parse(baseUrl + videoKey);

        Intent intent = new Intent(Intent.ACTION_VIEW, youTubeUri);
        startActivity(intent);
    }

    public void onFavoriteButtonClicked(View view) {
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPreExecute() {
                mProgressBar.setVisibility(View.VISIBLE);
                mFavoriteBtn.setClickable(false);
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                if(mFavorite){
                    Repository.removeFromDatabase(getApplicationContext(),mMovie);
                    Repository.removePicFrmInternalMemory(mMovie.getPosterPath());
                }else {
                    String uri = Repository.saveImageInFile(getApplicationContext(),mMovie.getOriginalTitle(),mImageView);
                    Movie movie = createANewMovieObject(uri);
                    saveInDatabase(movie);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                if(mFavorite){
                    Toast.makeText(getBaseContext(),"Removed From Favorites",Toast.LENGTH_LONG).show();
                    mFavoriteBtn.setText(getString(R.string.mark_as_favorite));
                }else{
                    Toast.makeText(getBaseContext(),"Added to Favorites",Toast.LENGTH_LONG).show();
                    mFavoriteBtn.setText(getString(R.string.unmark_this_favorite));
                }
                // Toggle the favorite state.True to false or vise versa
                mFavorite = !mFavorite;
                mFavoriteBtn.setClickable(true);
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }.execute();

    }

    private void saveInDatabase(final Movie movie) {
        Repository.insertMovieInDatabase(getApplicationContext(), movie);
        Repository.insertTrailersInDatabase(getApplicationContext(),constructListOfTrailerVideoWithId(mMovie.getId()));
        Repository.insertReviewsInDatabase(getApplicationContext(),constructListOfReviewWithVideoId(mMovie.getId()));
    }

    private List<ReviewWithId> constructListOfReviewWithVideoId(int id) {
        List<ReviewWithId> reviewsWithId = new ArrayList<>();

        for (Review review : mReviewsAdapter.getReviews()) {
            reviewsWithId.add(new ReviewWithId(id, review));
        }

        return reviewsWithId;
    }

    private List<TrailerVideoWithId> constructListOfTrailerVideoWithId(int id) {
        List<TrailerVideoWithId> trailerVideosWithId = new ArrayList<>();
        
        for (Video video : mTrailersAdapter.getVideos()) {
            trailerVideosWithId.add(new TrailerVideoWithId(id, video));
        }

        return trailerVideosWithId;
    }

    private Movie createANewMovieObject(String uri) {
        // Take the existing movie object and modify the path to the uri of image file
        Movie movie = mMovie;
        movie.setPosterPath(uri);
        return movie;
    }


}
