package com.example.android.movies.model;


import com.google.gson.annotations.SerializedName;

public class Review {
    @SerializedName("author")
    private String author;
    @SerializedName("content")
    private String review;

    public Review(String author, String review) {
        this.author = author;
        this.review = review;
    }

    public String getAuthor() {
        return author;
    }

    public String getReview() {
        return review;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public void setReview(String review){
        this.review = review;
    }


}
