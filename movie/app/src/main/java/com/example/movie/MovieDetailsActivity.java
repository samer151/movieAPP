package com.example.movie;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

public class MovieDetailsActivity extends AppCompatActivity {

    ImageView imgPoster;
    TextView txtTitle, txtRating, txtDescription;
    Button editBtn, deleteBtn;

    FirebaseFirestore db;
    String movieId;
    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        imgPoster = findViewById(R.id.detailsPoster);
        txtTitle = findViewById(R.id.detailsTitle);
        txtRating = findViewById(R.id.detailsRating);
        txtDescription = findViewById(R.id.detailsDescription);
        editBtn = findViewById(R.id.editBtn);
        deleteBtn = findViewById(R.id.deleteBtn);

        db = FirebaseFirestore.getInstance();

        movieId = getIntent().getStringExtra("movieId");

        loadMovie();

        editBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, UpdateMovieActivity.class);
            i.putExtra("movieId", movieId);
            startActivity(i);
        });

        deleteBtn.setOnClickListener(v -> {
            db.collection("movies").document(movieId)
                    .delete()
                    .addOnSuccessListener(r -> {
                        Toast.makeText(this, "Movie Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        });
    }

    private void loadMovie() {
        db.collection("movies").document(movieId)
                .get().addOnSuccessListener(doc -> {
                    movie = doc.toObject(Movie.class);

                    Glide.with(this)
                            .load(movie.getImageUrl())
                            .into(imgPoster);

                    txtTitle.setText(movie.getName());
                    txtRating.setText("⭐ " + movie.getRating());
                    txtDescription.setText(movie.getDescription());
                });
    }
}
