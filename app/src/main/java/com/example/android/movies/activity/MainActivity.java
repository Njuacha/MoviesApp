package com.example.android.movies.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.movies.rest.ApiClient;
import com.example.android.movies.rest.ApiInterface;
import com.example.android.movies.MainActivityAdapter;
import com.example.android.movies.R;
import com.example.android.movies.model.Movie;
import com.example.android.movies.model.MovieResponse;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>>,MainActivityAdapter.ImageClickListerner, SharedPreferences.OnSharedPreferenceChangeListener{
    public static final String EXTRA_MOVIE = "movie extra";
    private static final String SORT_ORDER = "sort order";
    private final int MOVIES_LOADER_ID = 24;

    @BindView(R.id.recycler_view) RecyclerView mRv;
    @BindView(R.id.pb)  ProgressBar mLoadingIndicator;
    @BindView(R.id.error_tv) TextView mErrorTv;

    private MainActivityAdapter mAdapter;

    private final String API_KEY ="35562acca65b30345f1dad4fbcdae45d";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mAdapter = new MainActivityAdapter(this, this);

        mRv.setLayoutManager(new GridLayoutManager(this, 2));
        mRv.setAdapter(mAdapter);

        Bundle bundle = new Bundle();
        bundle.putString(SORT_ORDER,getPreferredSortOder());

        getSupportLoaderManager().initLoader(MOVIES_LOADER_ID,bundle,this);
    }


    private String getPreferredSortOder() {
        // Declare and instantiate the sharedPreferences object
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortValue = sharedPreferences.getString(getString(R.string.sort_order_key)
                ,getString(R.string.default_sort_order));

        // Register the sharedPreferences to the OnSharedPreferenceChangeListener of this activity
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        return sortValue;
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


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Check if the sort order has changed.
        if (key.equals(getString(R.string.sort_order_key))){
            // Get the new sort order value
            String sortValue = sharedPreferences
                    .getString(key,getString(R.string.default_sort_order));

            Bundle bundle = new Bundle();
            bundle.putString(SORT_ORDER,sortValue);
            getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID,bundle,this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, final Bundle args) {

        return new AsyncTaskLoader<List<Movie>>(this) {
            @Override
            protected void onStartLoading() {
                // If no args are available which contains the sort order, then return
                if( args == null){
                    return;
                }
                // Show that http request is being made by showing loading indicator
                mLoadingIndicator.setVisibility(View.VISIBLE);
                // Force a load
                forceLoad();

            }

            @Override
            public List<Movie> loadInBackground() {
                 /*
            Create a connection to the API i.e create client object. And map the client object to the interface which is service object
         */
                ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                // Perform an HTTP request using the service object just created depending on the sort choice of user
                Call<MovieResponse> call;
                String sortOrder = args.getString(SORT_ORDER);
                // If there is no sort order available for whatever mysterious reason, then return null
                if (TextUtils.isEmpty(sortOrder)||sortOrder == null){
                    return null;
                }
                if( sortOrder.equals(getString(R.string.top_rated_value))){
                    call = apiService.getTopRatedMovies(API_KEY);
                }else if( sortOrder.equals(getString(R.string.most_popular_value))){
                    call = apiService.getMostPopularMovies(API_KEY);
                }else {
                    return null;
                }

                List<Movie> movies = null;
                try {
                    Response<MovieResponse> response = call.execute();
                    movies = response.body().getResults();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return movies;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {
        // First make the loading stop
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        if(data == null){
            mRv.setVisibility(View.INVISIBLE);
            mErrorTv.setVisibility(View.VISIBLE);
            mErrorTv.setText(getString(R.string.error_message));
        }else {
            mRv.setVisibility(View.VISIBLE);
            mErrorTv.setVisibility(View.INVISIBLE);
            mAdapter.setListOfMovies(data);
        }

    }


    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

    }
}
