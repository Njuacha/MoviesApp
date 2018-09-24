package com.example.android.movies.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;


/**
 * Created by hubert on 8/7/18.
 */
@Entity
public class Movie implements Parcelable{
    @NonNull
    @PrimaryKey
    @SerializedName("id")
    private final int id;
    @SerializedName("original_title")
    private final String originalTitle;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("overview")
    private final String aPlotSynopsis;
    @SerializedName("vote_average")
    private float userRating;
    @SerializedName("release_date")
    private final String releaseDate;

    public Movie(int id, String originalTitle, String posterPath, String aPlotSynopsis, float userRating, String releaseDate) {
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

    public String getAPlotSynopsis() {
        return aPlotSynopsis;
    }

    public float getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setPosterPath(String posterPath){
        this.posterPath = posterPath;
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
        dest.writeFloat(userRating);
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