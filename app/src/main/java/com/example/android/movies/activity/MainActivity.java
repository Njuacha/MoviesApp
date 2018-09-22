package com.example.android.movies.activity;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.movies.viewmodel.MainViewModel;
import com.example.android.movies.adapter.MainActivityAdapter;
import com.example.android.movies.R;
import com.example.android.movies.model.Movie;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<LiveData<List<Movie>>>,MainActivityAdapter.ImageClickListerner, SharedPreferences.OnSharedPreferenceChangeListener{
    public static final String EXTRA_MOVIE = "movie extra";
    private static final String SORT_ORDER = "sort order";
    public static final String EXTRA_FAVORITE = "extra favorite state";
    private final int MOVIES_LOADER_ID = 24;
    private MainViewModel mViewModel;


    @BindView(R.id.recycler_view) RecyclerView mRv;
    @BindView(R.id.pb)  ProgressBar mLoadingIndicator;
    @BindView(R.id.error_tv) TextView mErrorTv;

    private MainActivityAdapter mAdapter;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mAdapter = new MainActivityAdapter(this, this);

        mRv.setLayoutManager(new GridLayoutManager(this, 2));
        mRv.setAdapter(mAdapter);
        // Initialize the view model
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

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
        if(movie != null) {
            Intent intent = new Intent(MainActivity.this,DetailActivity.class);
            intent.putExtra(EXTRA_MOVIE,movie);
            intent.putExtra(EXTRA_FAVORITE,mViewModel.isFavorite());
            startActivity(intent);
        }
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
    public Loader<LiveData<List<Movie>>> onCreateLoader(int id, final Bundle args) {

        return new AsyncTaskLoader<LiveData<List<Movie>>>(this) {
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
            public LiveData<List<Movie>> loadInBackground() {
                LiveData<List<Movie>> liveMovies = mViewModel.getMovies(args.getString(SORT_ORDER));
                // Determine from the view model if it is a favorite movie and inform adapter
                mAdapter.setFavorite(mViewModel.isFavorite());
                return liveMovies;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<LiveData<List<Movie>>> loader, LiveData<List<Movie>> data) {
        // First make the loading stop
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        data.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                mRv.setVisibility(View.VISIBLE);
                mErrorTv.setVisibility(View.INVISIBLE);
                mAdapter.setListOfMovies(movies);
            }
        });
        if(data.getValue() == null ){
            mRv.setVisibility(View.INVISIBLE);
            mErrorTv.setVisibility(View.VISIBLE);
            mErrorTv.setText(getString(R.string.error_message));
        }


    }


    @Override
    public void onLoaderReset(Loader<LiveData<List<Movie>>> loader) {

    }
}
