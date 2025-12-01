package com.example.movie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.util.Log;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerMovies, recyclerSlider;
    ArrayList<Movie> moviesList, sliderList;
    MovieAdapter movieAdapter;
    SliderAdapter sliderAdapter;

    FirebaseFirestore db;
    FloatingActionButton addMovieBtn;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerMovies = findViewById(R.id.recyclerMovies);
        recyclerSlider = findViewById(R.id.recyclerSlider);
        addMovieBtn = findViewById(R.id.addMovieBtn);
        loading = findViewById(R.id.loading);

        db = FirebaseFirestore.getInstance();

        // Vertical list
        recyclerMovies.setLayoutManager(new LinearLayoutManager(this));
        moviesList = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, moviesList);
        recyclerMovies.setAdapter(movieAdapter);

        // Horizontal slider
        recyclerSlider.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        sliderList = new ArrayList<>();
        sliderAdapter = new SliderAdapter(this, sliderList);
        recyclerSlider.setAdapter(sliderAdapter);

        addMovieBtn.setOnClickListener(v ->
                startActivity(new Intent(this, AddMovieActivity.class))
        );

        // 🔹 Seed the database (run once, then comment out)
        fillDatabase();

        loadMovies();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMovies();
    }

    private void loadMovies() {
        loading.setVisibility(View.VISIBLE);

        db.collection("movies")
                .get()
                .addOnSuccessListener(query -> {
                    moviesList.clear();
                    sliderList.clear();

                    for (DocumentSnapshot doc : query) {
                        Movie m = doc.toObject(Movie.class);
                        m.setId(doc.getId());

                        moviesList.add(m);

                        // Add 5 trending to slider
                        if (sliderList.size() < 5)
                            sliderList.add(m);
                    }

                    movieAdapter.notifyDataSetChanged();
                    sliderAdapter.notifyDataSetChanged();
                    loading.setVisibility(View.GONE);
                });
    }

    // 🔹 Seed movies into Firestore (safe integration)
    private void fillDatabase() {

        ArrayList<Movie> movies = new ArrayList<>();

        movies.add(new Movie(null, "The Dark Knight", "9.0",
                "https://upload.wikimedia.org/wikipedia/en/8/8a/Dark_Knight.jpg",
                "Batman raises the stakes in his war on crime."));

        movies.add(new Movie(null, "Avengers: Endgame", "8.4",
                "https://upload.wikimedia.org/wikipedia/en/f/f9/TheAvengers2012Poster.jpg",
                "After the devastating events of Avengers: Infinity War, the universe is in ruins."));

        movies.add(new Movie(null, "Inception", "8.8",
                "https://upload.wikimedia.org/wikipedia/en/6/66/Inception_%282010%29_theatrical_poster.jpg",
                "A thief who steals corporate secrets through the use of dream-sharing technology."));

        movies.add(new Movie(null, "Interstellar", "8.6",
                "https://upload.wikimedia.org/wikipedia/en/b/bc/Interstellar_film_poster.jpg",
                "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival."));

        movies.add(new Movie(null, "Parasite", "8.6",
                "https://upload.wikimedia.org/wikipedia/en/5/53/Parasite_%282019_film%29.png",
                "Greed and class discrimination threaten the newly formed symbiotic relationship between the wealthy Park family and the destitute Kim clan."));

        movies.add(new Movie(null, "The Godfather", "9.2",
                "https://upload.wikimedia.org/wikipedia/en/1/1c/Godfather_ver1.jpg",
                "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son."));

        movies.add(new Movie(null, "Joker", "8.4",
                "https://upload.wikimedia.org/wikipedia/en/e/e1/Joker_%282019_film%29_poster.jpg",
                "In Gotham City, mentally troubled comedian Arthur Fleck embarks on a downward spiral of social revolution and crime."));

        movies.add(new Movie(null, "Titanic", "7.8",
                "https://upload.wikimedia.org/wikipedia/en/2/2e/Titanic_poster.jpg",
                "A seventeen-year-old aristocrat falls in love with a kind but poor artist aboard the luxurious, ill-fated R.M.S. Titanic."));

        movies.add(new Movie(null, "The Matrix", "8.7",
                "https://upload.wikimedia.org/wikipedia/en/c/c1/The_Matrix_Poster.jpg",
                "A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers."));

        movies.add(new Movie(null, "Avengers: Infinity War", "8.4",
                "https://upload.wikimedia.org/wikipedia/en/4/4d/Avengers_Infinity_War_poster.jpg",
                "The Avengers and their allies must be willing to sacrifice all in an attempt to defeat the powerful Thanos."));

        movies.add(new Movie(null, "The Lion King", "8.5",
                "https://upload.wikimedia.org/wikipedia/en/3/3d/The_Lion_King_poster.jpg",
                "Lion prince Simba and his father are targeted by his bitter uncle, who wants to ascend the throne himself."));

        movies.add(new Movie(null, "Spider-Man: No Way Home", "8.3",
                "https://upload.wikimedia.org/wikipedia/en/f/f9/Spider-Man_No_Way_Home_poster.jpg",
                "With Spider-Man's identity now revealed, Peter asks Doctor Strange for help. Chaos ensues."));

        movies.add(new Movie(null, "Jaws", "8.0",
                "https://upload.wikimedia.org/wikipedia/en/e/eb/Jaws_poster.jpg",
                "A giant great white shark arrives on the shores of a New England beach resort."));

        movies.add(new Movie(null, "Gladiator", "8.5",
                "https://upload.wikimedia.org/wikipedia/en/8/8d/Gladiator_ver1.jpg",
                "A former Roman General sets out to exact vengeance against the corrupt emperor who murdered his family."));

        movies.add(new Movie(null, "Forrest Gump", "8.8",
                "https://upload.wikimedia.org/wikipedia/en/6/67/Forrest_Gump_poster.jpg",
                "The presidencies of Kennedy and Johnson, the Vietnam War, and more through the eyes of an Alabama man with a low IQ."));

        // Add movies to Firestore
        for (Movie m : movies) {
            db.collection("movies")
                    .add(m)
                    .addOnSuccessListener(documentReference ->
                            Log.d("Firestore", "Movie added: " + documentReference.getId()))
                    .addOnFailureListener(e ->
                            Log.e("Firestore", "Error adding movie", e));
        }
    }
}
