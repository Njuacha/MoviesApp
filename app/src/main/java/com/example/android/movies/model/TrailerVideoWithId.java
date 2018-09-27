package com.example.android.movies.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static com.example.android.movies.viewmodel.DetailActivityViewModel.TRAILER_TYPE;

@Entity( primaryKeys = {"id","key"}
         ,foreignKeys = {@ForeignKey(entity = Movie.class, parentColumns = "id", childColumns = "id", onDelete = CASCADE)})
public class TrailerVideoWithId {

    private int id;
    private String name;
    @SuppressWarnings("NullableProblems")
    @NonNull
    private String key;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKey(@NonNull String key) {
        this.key = key;
    }

    public TrailerVideoWithId() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @NonNull
    public String getKey() {
        return key;
    }

    @Ignore
    public TrailerVideoWithId(int id, Video video){
        this.id = id;
        name = video.getName();
        key = video.getKey();
    }

    public Video getVideo() {
        return new Video(name,key,TRAILER_TYPE);
    }
}
