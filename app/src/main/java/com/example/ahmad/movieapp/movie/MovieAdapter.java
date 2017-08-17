package com.example.ahmad.movieapp.movie;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ahmad.movieapp.R;
import com.example.ahmad.movieapp.app.MyApplication;
import com.example.ahmad.movieapp.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahmad on 11/08/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private List<Movie> dataSet;
    private Context context;

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tv_title);
            image = itemView.findViewById(R.id.iv_movie);
        }
    }

    public MovieAdapter(Context context) {
        this.dataSet = new ArrayList<>();
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie movie = dataSet.get(position);

        holder.title.setText(movie.getTitle());
        Glide.with(context)
                .load(MyApplication.BASE_IMAGE_URL + movie.getImageUrl())
                .placeholder(R.drawable.ic_android_black_24dp)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public Movie getItem(int position) {
        return dataSet.get(position);
    }

    public void addAll(List<Movie> dataSet) {
        this.dataSet.addAll(dataSet);
        notifyDataSetChanged();
    }

    public void clear() {
        dataSet.clear();
        notifyDataSetChanged();
    }
}
