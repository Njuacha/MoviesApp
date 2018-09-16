package com.example.android.movies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.movies.R;
import com.example.android.movies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by hubert on 8/6/18.
 */

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.PosterViewHolder> {

    private List<Movie> mMovies;
    private final Context mContext;
    private final ImageClickListerner mImageClickListerner;
    //final String  BASE_PATH = "http://image.tmdb.org/t/p/w185/";
    public final static String  BASE_PATH = "http://image.tmdb.org/t/p/original";

    public MainActivityAdapter(Context context, ImageClickListerner mImageClickListerner){
        mContext = context;
        this.mImageClickListerner = mImageClickListerner;
    }

    @NonNull
    @Override
    public PosterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_poster,parent,false);
        return new PosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PosterViewHolder holder, int position) {
        // Gets the movie from the list of movies
        Movie movie = mMovies.get(position);
        // Gets the path of the movie

        String path = BASE_PATH + movie.getPosterPath();

        // Uses the path of movie to load movie into image view
        Picasso.with(mContext).load(path).placeholder(R.mipmap.ic_launcher).into(holder.poster);
    }

    public void setListOfMovies(List<Movie> movies){
        mMovies = movies;
        notifyDataSetChanged();
    }

    public interface ImageClickListerner{
        void onImagedClicked(Movie movie);
    }
    @Override
    public int getItemCount() {
        if (mMovies == null){
            return 0;
        }else
            return mMovies.size();
    }

    public class PosterViewHolder extends RecyclerView.ViewHolder{

        final ImageView poster;

        public PosterViewHolder(View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.image_poster);
            poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mImageClickListerner.onImagedClicked(mMovies.get(getAdapterPosition()));
                }
            });
        }

    }
}
