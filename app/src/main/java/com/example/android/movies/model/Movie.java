package com.example.android.movies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


/**
 * Created by hubert on 8/7/18.
 */

public class Movie implements Parcelable{
    @SerializedName("id")
    private final Integer id;
    @SerializedName("original_title")
    private final String originalTitle;
    @SerializedName("poster_path")
    private final String posterPath;
    @SerializedName("overview")
    private final String aPlotSynopsis;
    @SerializedName("user_rating")
    private int userRating;
    @SerializedName("release_date")
    private final String releaseDate;

    public Movie(int id, String originalTitle, String posterPath, String aPlotSynopsis, int userRating, String releaseDate) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.aPlotSynopsis = aPlotSynopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    private Movie(Parcel in) {
        id = in.readInt();
        originalTitle = in.readString();
        posterPath = in.readString();
        aPlotSynopsis = in.readString();
        userRating = in.readInt();
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

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getaPlotSynopsis() {
        return aPlotSynopsis;
    }

    public int getUserRating() {
        return userRating;
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
        dest.writeString(originalTitle);
        dest.writeString(posterPath);
        dest.writeString(aPlotSynopsis);
        dest.writeInt(userRating);
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