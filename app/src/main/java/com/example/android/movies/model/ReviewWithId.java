package com.example.android.movies.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity( primaryKeys = {"id","author","content"}
        ,foreignKeys = {@ForeignKey(entity = Movie.class, parentColumns = "id", childColumns = "id", onDelete = CASCADE)})
public class ReviewWithId {
    private int id;
    @NonNull
    private String author;
    @NonNull
    private String content;

    @Ignore
    public ReviewWithId(int id, Review review){
        this.id = id;
        author = review.getAuthor();
        content = review.getReview();
    }

    public ReviewWithId(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Review getReview() {
        return new Review(author,content);
    }

}

