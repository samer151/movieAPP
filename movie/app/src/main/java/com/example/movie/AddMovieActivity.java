package com.example.movie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class AddMovieActivity extends AppCompatActivity {

    EditText nameField, ratingField, descField;
    ImageView imgPreview;
    Button uploadBtn, saveBtn;
    Uri imageUri;

    FirebaseFirestore db;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);

        nameField = findViewById(R.id.nameField);
        ratingField = findViewById(R.id.ratingField);
        descField = findViewById(R.id.descField);
        imgPreview = findViewById(R.id.movieImage);
        uploadBtn = findViewById(R.id.uploadImageBtn);
        saveBtn = findViewById(R.id.saveBtn);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        uploadBtn.setOnClickListener(v -> chooseImage());
        saveBtn.setOnClickListener(v -> uploadMovie());
    }

    private void chooseImage() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageUri = data.getData();
            imgPreview.setImageURI(imageUri);
        }
    }

    private void uploadMovie() {
        if (imageUri == null) {
            Toast.makeText(this, "Select an image first!", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference ref = storage.getReference("movies/" + System.currentTimeMillis() + ".jpg");

        ref.putFile(imageUri).addOnSuccessListener(task -> {
            ref.getDownloadUrl().addOnSuccessListener(url -> {

                HashMap<String, Object> map = new HashMap<>();
                map.put("name", nameField.getText().toString());
                map.put("rating", ratingField.getText().toString());
                map.put("description", descField.getText().toString());
                map.put("imageUrl", url.toString());

                db.collection("movies").add(map).addOnSuccessListener(r -> {
                    Toast.makeText(this, "Movie added!", Toast.LENGTH_SHORT).show();
                    finish();
                });

            });
        });
    }
}
