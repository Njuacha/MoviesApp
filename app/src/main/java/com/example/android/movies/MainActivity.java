package com.example.android.movies;


import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>, MainActivityAdapter.ImageClickListerner, SharedPreferences.OnSharedPreferenceChangeListener{
    public static final String EXTRA_MOVIE = "movie extra";
    private static final String MOVIE_URL_EXTRA = "movies url";

    @BindView(R.id.recycler_view) RecyclerView mRv;
    @BindView(R.id.pb)  ProgressBar mLoadingIndicator;
    @BindView(R.id.error_tv) TextView mErrorTv;

    private MainActivityAdapter mAdapter;
    private static final int MOVIE_LOADER_ID = 24;
    private final String URL_MOST_POPULAR_MOVIES = "http://api.themoviedb.org/3/movie/popular?api_key=35562acca65b30345f1dad4fbcdae45d";
    private final String URL_TOP_RATED_MOVIES = "http://api.themoviedb.org/3/movie/top_rated?api_key=35562acca65b30345f1dad4fbcdae45d";
    private final String[] URLS = { URL_MOST_POPULAR_MOVIES,URL_TOP_RATED_MOVIES};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mAdapter = new MainActivityAdapter(this, this);

        mRv.setLayoutManager(new GridLayoutManager(this, 2));
        mRv.setAdapter(mAdapter);

        int choice = getPreferredSortOder();

        // Use the choice made to get the url to use
        String url = URLS[choice];

        // Put the url gotten into a bundle
        Bundle moviesBundle = new Bundle();
        moviesBundle.putString(MOVIE_URL_EXTRA,url);

        // Attach the bundle to moviesLoader
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID,moviesBundle,  this);

    }

    private int getPreferredSortOder() {
        // Declare and instantiate the sharedPreferences object
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortValue = sharedPreferences.getString(getString(R.string.sort_order_key)
                ,getString(R.string.default_sort_order));

        // Register the sharedPreferences to the OnSharedPreferenceChangeListener of this activity
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        return Integer.parseInt(sortValue);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movies,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onImagedClicked(Movie movie) {
        startActivity(new Intent(MainActivity.this,DetailActivity.class).putExtra(EXTRA_MOVIE,movie));
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {


        return new AsyncTaskLoader<String>(this) {

            @Override
            protected void onStartLoading() {
                mLoadingIndicator.setVisibility(View.VISIBLE);

                forceLoad();
            }

            @Override
            public String loadInBackground() {
                String moviesUrl = args.getString(MOVIE_URL_EXTRA);
                String moviesResultInJson = null;
                try {
                    URL url = new URL(moviesUrl);
                    moviesResultInJson = NetworkUtil.getMoviesFromMovieDbWebsite(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return moviesResultInJson;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        //Make progress bar disappear
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        // Check if there are movies available and set to mAdapter or show error message
        if ( data != null && !data.equals("") ){

            mRv.setVisibility(View.VISIBLE);
            mErrorTv.setVisibility(View.INVISIBLE);
            List<Movie> movies = JsonUtil.parseListOfMoviesJson(data);
            mAdapter.setListOfMovies(movies);
        }else{

            mRv.setVisibility(View.INVISIBLE);
            mErrorTv.setVisibility(View.VISIBLE);
            mErrorTv.setText(R.string.error_message);

        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Check if the sort order has changed.
        if (key.equals(getString(R.string.sort_order_key))){
            // Get the new sort order value
            int choice = Integer.parseInt(sharedPreferences
                    .getString(key,getString(R.string.default_sort_order)));

            // Use the choice made to get the url to use
            String url = URLS[choice];

            // Put the url gotten into a bundle
            Bundle moviesBundle = new Bundle();
            moviesBundle.putString(MOVIE_URL_EXTRA,url);
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID,moviesBundle,  this);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
