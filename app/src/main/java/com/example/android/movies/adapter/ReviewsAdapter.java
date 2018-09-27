package com.example.android.movies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.movies.R;
import com.example.android.movies.model.Review;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {
    private final Context context;
    private List<Review> reviews;

    public ReviewsAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review,parent,false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        String content = String.format("\"%s\"",reviews.get(position).getReview());
        holder.content_tv.setText(content);

        String author = "By " + reviews.get(position).getAuthor();
        holder.author_tv.setText(author);
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    public List<Review> getReviews() {
        return reviews;
    }

    @Override
    public int getItemCount() {
        if(reviews == null){
            return 0;
        }else return reviews.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder{

        final TextView author_tv;
        final TextView content_tv;

        ReviewViewHolder(View itemView) {
            super(itemView);

            author_tv = itemView.findViewById(R.id.author_tv);
            content_tv = itemView.findViewById(R.id.content_tv);
        }
    }
}
