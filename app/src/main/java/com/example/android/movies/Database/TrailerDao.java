package com.example.android.movies.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.android.movies.model.TrailerVideoWithId;

import java.util.List;

@Dao
public interface TrailerDao {
    @Insert
    void insertTrailers(List<TrailerVideoWithId> trailers);

    @Query("Select * from TrailerVideoWithId where id=:id")
    List<TrailerVideoWithId> getTrailerVideosWithId(int id);

}
