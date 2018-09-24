package com.example.android.movies.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.android.movies.model.ReviewWithId;

import java.util.List;

@Dao
public interface ReviewDao {
    @Insert
    void insertReviews(List<ReviewWithId> reviews);

    @Query("Select * from ReviewWithId where id=:id")
    List<ReviewWithId> getReviewsWithId(int id);

    @Query("Delete from ReviewWithId where id=:id")
    void deleteReviewsForMovie( int id);
}
