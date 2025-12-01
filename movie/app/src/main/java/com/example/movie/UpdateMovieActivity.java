package com.example.movie;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class UpdateMovieActivity extends AppCompatActivity {

    EditText nameField, ratingField, descField;
    ImageView imgPreview;
    Button saveBtn;
    Uri newImageUri;

    FirebaseFirestore db;
    FirebaseStorage storage;
    String movieId;
    String oldImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_movie);

        nameField = findViewById(R.id.nameField);
        ratingField = findViewById(R.id.ratingField);
        descField = findViewById(R.id.descField);
        imgPreview = findViewById(R.id.movieImage);
        saveBtn = findViewById(R.id.saveBtn);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        movieId = getIntent().getStringExtra("movieId");

        loadMovie();

        imgPreview.setOnClickListener(v -> chooseImage());
        saveBtn.setOnClickListener(v -> saveMovie());
    }

    private void loadMovie() {
        db.collection("movies").document(movieId).get()
                .addOnSuccessListener(doc -> {
                    Movie m = doc.toObject(Movie.class);
                    nameField.setText(m.getName());
                    ratingField.setText(m.getRating());
                    descField.setText(m.getDescription());

                    oldImageUrl = m.getImageUrl();

                    Glide.with(this).load(oldImageUrl).into(imgPreview);
                });
    }

    private void chooseImage() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200 && resultCode == RESULT_OK) {
            newImageUri = data.getData();
            imgPreview.setImageURI(newImageUri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveMovie() {
        if (newImageUri != null) {
            uploadNewImage();
        } else {
            updateMovie(oldImageUrl);
        }
    }

    private void uploadNewImage() {
        StorageReference ref =
                storage.getReference("movies/" + System.currentTimeMillis() + ".jpg");

        ref.putFile(newImageUri)
                .addOnSuccessListener(task -> ref.getDownloadUrl()
                        .addOnSuccessListener(url -> updateMovie(url.toString())));
    }

    private void updateMovie(String imageUrl) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", nameField.getText().toString());
        map.put("rating", ratingField.getText().toString());
        map.put("description", descField.getText().toString());
        map.put("imageUrl", imageUrl);

        db.collection("movies").document(movieId)
                .update(map)
                .addOnSuccessListener(r -> {
                    Toast.makeText(this, "Movie updated!", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}
