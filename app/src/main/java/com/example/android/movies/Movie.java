package com.example.android.movies;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by hubert on 8/7/18.
 */

public class Movie implements Parcelable{
    private final int id;
    private final String original_title;
    private final String movie_poster_path;
    private final String a_plot_synopsis;
    private final int user_rating;
    private final String releaseDate;

    public Movie(int id, String original_title, String movie_poster_path, String a_plot_synopsis, int user_rating, String releaseDate) {
        this.id = id;
        this.original_title = original_title;
        this.movie_poster_path = movie_poster_path;
        this.a_plot_synopsis = a_plot_synopsis;
        this.user_rating = user_rating;
        this.releaseDate = releaseDate;
    }

    private Movie(Parcel in) {
        id = in.readInt();
        original_title = in.readString();
        movie_poster_path = in.readString();
        a_plot_synopsis = in.readString();
        user_rating = in.readInt();
        releaseDate = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getMovie_poster_path() {
        return movie_poster_path;
    }

    public String getA_plot_synopsis() {
        return a_plot_synopsis;
    }

    public int getUser_rating() {
        return user_rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(original_title);
        dest.writeString(movie_poster_path);
        dest.writeString(a_plot_synopsis);
        dest.writeInt(user_rating);
        dest.writeString(releaseDate);
    }
}
/*
    original title
    movie poster image thumbnail
    A plot synopsis (called overview in the api)
    user rating (called vote_average in the api)
    release date
*/