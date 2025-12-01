package com.example.movie;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {

    Context context;
    ArrayList<Movie> list;

    public MovieAdapter(Context context, ArrayList<Movie> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovieHolder(LayoutInflater.from(context)
                .inflate(R.layout.row_movie, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
        Movie m = list.get(position);

        holder.title.setText(m.getName());
        holder.rating.setText("⭐ " + m.getRating());

        Glide.with(context)
                .load(m.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .into(holder.poster);

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, MovieDetailsActivity.class);
            i.putExtra("movieId", m.getId());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    class MovieHolder extends RecyclerView.ViewHolder {

        ImageView poster;
        TextView title, rating;

        public MovieHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.imgMovie);
            title = itemView.findViewById(R.id.txtTitle);
            rating = itemView.findViewById(R.id.txtRating);
        }
    }
}
