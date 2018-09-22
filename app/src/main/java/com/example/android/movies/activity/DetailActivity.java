package com.example.android.movies.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movies.AppExecutors;
import com.example.android.movies.Database.AppDatabase;
import com.example.android.movies.R;
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
    @BindView(R.id.tv_user_rating)
    TextView mUserRatingTv;
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

            mUserRatingTv.setText(String.valueOf(mMovie.getUserRating()));
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

            setUpViewModel(mMovie.getId());


        }
    }

    private void setUpViewModel(int id) {
        final DetailActivityViewModel viewModel = ViewModelProviders.of(this).get(DetailActivityViewModel.class);
        viewModel.setFavorite(ismFavorite());
        // Set up the trailers
        viewModel.getTrailers(id).observe(this, new Observer<List<Video>>() {
            @Override
            public void onChanged(@Nullable List<Video> videos) {
                mTrailersAdapter.setVideos(videos);
            }
        });

        // Set up the reviews
        viewModel.getReviews(id).observe(this, new Observer<List<Review>>() {
            @Override
            public void onChanged(@Nullable List<Review> reviews) {
                mReviewsAdapter.setReviews(reviews);
            }
        });
    }

    @Override
    public void onTrailerClicked(String videoKey) {
        String baseUrl = "https://www.youtube.com/watch?v=";
        Uri youTubeUri = Uri.parse(baseUrl + videoKey);

        Intent intent = new Intent(Intent.ACTION_VIEW, youTubeUri);
        startActivity(intent);
    }

    public void onFavoriteButtonClicked(View view) {
        String uri = saveImageInFile(mMovie.getOriginalTitle());
        Movie movie = createANewMovieObject(uri);

        saveInDatabase(movie);
    }

    private void saveInDatabase(final Movie movie) {
        AppExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                insertMovieInDatabase(movie);
                insertTrailersInDatabase(movie.getId());
                insertReviewsInDatabase(movie.getId());
            }
        });

    }

    private void insertReviewsInDatabase(int id) {
        List<ReviewWithId> reviewsWithVideoId = constructListOfReviewWithVideoId(id);
        if (reviewsWithVideoId.size() != 0) {
            mDb.reviewDao().insertReviews(reviewsWithVideoId);
        }
    }

    private void insertTrailersInDatabase(int id) {
        List<TrailerVideoWithId> trailerVideosWithId = constructListOfTrailerVideoWithId(id);
        if (trailerVideosWithId.size() != 0) {
            mDb.trailerDoa().insertTrailers(trailerVideosWithId);
        }
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

    private void insertMovieInDatabase(Movie movie) {
        mDb.movieDoa().insertMovie(movie);
    }

    private Movie createANewMovieObject(String uri) {
        // Take the existing movie object and modify the path to the uri of image file
        Movie movie = mMovie;
        movie.setPosterPath(uri);
        return movie;
    }

    private String saveImageInFile(String originalTitle) {
        // Get Bitmap from image
        Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        // Declare File out put stream to be used to write to a file
        FileOutputStream fileOutputStream = null;
        File directory = getApplicationContext().getFilesDir();
        File file = new File(directory, originalTitle);

        try {

            fileOutputStream = openFileOutput(file.getName(), MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return String.valueOf(Uri.fromFile(file));
    }

    private boolean ismFavorite() {
        mFavorite = getIntent().getBooleanExtra(EXTRA_FAVORITE, false);
        return mFavorite;
    }
}
