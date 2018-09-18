package com.example.android.movies.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.android.movies.model.Movie;

import java.util.List;

@Dao
public interface MovieDoa {
    @Insert
    void insertMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);

    @Query("Select * From Movie")
    LiveData<List<Movie>> loadFavorites();



}
