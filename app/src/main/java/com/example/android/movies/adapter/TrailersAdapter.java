package com.example.android.movies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.android.movies.R;
import com.example.android.movies.model.Video;

import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    private Context context;
    private List<Video> videos;
    private TrailerClickListener listener;

    public TrailersAdapter(Context context, TrailerClickListener listener){
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie,parent,false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        holder.textView.setText(videos.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if ( videos == null){
            return 0;
        }
        else return videos.size() ;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
        notifyDataSetChanged();
    }

    public interface TrailerClickListener{
        void onTrailerClicked(String videoKey);
    }



    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textView;

        public TrailerViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onTrailerClicked(videos.get(getAdapterPosition()).getKey());
        }
    }
}

