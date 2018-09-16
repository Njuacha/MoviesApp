package com.example.android.movies.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movies.R;
import com.example.android.movies.adapter.ReviewsAdapter;
import com.example.android.movies.adapter.TrailersAdapter;
import com.example.android.movies.model.Movie;
import com.example.android.movies.model.Review;
import com.example.android.movies.model.Video;
import com.example.android.movies.viewmodel.DetailActivityViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.movies.activity.MainActivity.EXTRA_MOVIE;
import static com.example.android.movies.adapter.MainActivityAdapter.BASE_PATH;

public class DetailActivity extends AppCompatActivity implements TrailersAdapter.TrailerClickListener {

    // Declare and instantiate views
    @BindView((R.id.iv_poster)) ImageView mImageView;
    @BindView(R.id.tv_user_rating) TextView mUserRatingTv;
    @BindView(R.id.tv_release_date) TextView mReleaseDateTv;
    @BindView(R.id.tv_plot_synopsis) TextView mPlotSynopsisTv;
    @BindView(R.id.tv_title) TextView mTitleTv;
    @BindView(R.id.rv_trailers) RecyclerView mRvTrailers;
    @BindView(R.id.rv_reviews) RecyclerView mRvReviews;

    TrailersAdapter mTrailersAdapter;
    ReviewsAdapter mReviewsAdapter;


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
            Movie movie = intent.getParcelableExtra(EXTRA_MOVIE);
            // Set the image to the mImageView
            String path = BASE_PATH + movie.getPosterPath();

            Picasso.with(this).load(path).into(mImageView);
            // Set the title to the textView
            mTitleTv.setText(movie.getOriginalTitle());

            mUserRatingTv.setText(String.valueOf(movie.getUserRating()));
            mReleaseDateTv.setText(movie.getReleaseDate());
            mPlotSynopsisTv.setText(movie.getaPlotSynopsis());

            // Instantiate trailer and reviews adapters
            mTrailersAdapter = new TrailersAdapter(this, this);
            mReviewsAdapter = new ReviewsAdapter(this);

            // Set dividers for recycler views
            mRvTrailers.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            mRvReviews.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

            // Set adapters to respective rv's
            mRvTrailers.setAdapter(mTrailersAdapter);
            mRvReviews.setAdapter(mReviewsAdapter);

            setUpViewModel(movie.getId());


        }
    }

    private void setUpViewModel(int id) {
        DetailActivityViewModel viewModel = ViewModelProviders.of(this).get(DetailActivityViewModel.class);

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
        Uri youTubeUri = Uri.parse(baseUrl+videoKey);

        Intent intent = new Intent(Intent.ACTION_VIEW,youTubeUri);
        startActivity(intent);
    }
}
