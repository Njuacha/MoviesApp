package com.example.android.movies;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static com.example.android.movies.MainActivity.EXTRA_MOVIE;
import static com.example.android.movies.MainActivityAdapter.BASE_PATH;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Declare and instantiate views
        ImageView mImageView = findViewById(R.id.iv_poster);
        TextView mUserRatingTv = findViewById(R.id.tv_user_rating);
        TextView mReleaseDateTv = findViewById(R.id.tv_release_date);
        TextView mPlotSynopsisTv = findViewById(R.id.tv_plot_synopsis);
        TextView mTitleTv = findViewById(R.id.tv_title);

        Intent intent = getIntent();
        if ( intent != null && intent.hasExtra(EXTRA_MOVIE)){
            // Get the movie object which was passed
            Movie movie = intent.getParcelableExtra(EXTRA_MOVIE);
            // Set the image to the mImageView
            String path = BASE_PATH + movie.getMovie_poster_path();
            Picasso.with(this).load(path).into(mImageView);
            // Set the title to the textView

                mTitleTv.setText(movie.getOriginal_title());

                mUserRatingTv.setText(String.valueOf(movie.getUser_rating()));
                mReleaseDateTv.setText(movie.getReleaseDate());
                mPlotSynopsisTv.setText(movie.getA_plot_synopsis());


        }
    }
}
