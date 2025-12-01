package com.example.movie;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderHolder> {

    Context context;
    ArrayList<Movie> list;

    public SliderAdapter(Context context, ArrayList<Movie> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SliderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderHolder(LayoutInflater.from(context)
                .inflate(R.layout.row_slider_movie, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SliderHolder holder, int position) {
        Movie m = list.get(position);

        Glide.with(context)
                .load(m.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .into(holder.sliderImage);

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, MovieDetailsActivity.class);
            i.putExtra("movieId", m.getId());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    class SliderHolder extends RecyclerView.ViewHolder {
        ImageView sliderImage;

        public SliderHolder(@NonNull View itemView) {
            super(itemView);
            sliderImage = itemView.findViewById(R.id.sliderImage);
        }
    }
}

