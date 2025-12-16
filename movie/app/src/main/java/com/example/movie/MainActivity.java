package com.example.movie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerMovies, recyclerSlider;
    ArrayList<Movie> moviesList, sliderList, allMoviesList;
    MovieAdapter movieAdapter;
    SliderAdapter sliderAdapter;

    FirebaseFirestore db;
    FloatingActionButton addMovieBtn;
    ProgressBar loading;
    EditText searchBar;
    ImageView clearSearch, searchIcon;
    AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerMovies = findViewById(R.id.recyclerMovies);
        recyclerSlider = findViewById(R.id.recyclerSlider);
        addMovieBtn = findViewById(R.id.addMovieBtn);
        loading = findViewById(R.id.loading);
        searchBar = findViewById(R.id.searchBar);
        clearSearch = findViewById(R.id.clearSearch);
        searchIcon = findViewById(R.id.searchIcon);
        appBarLayout = findViewById(R.id.appBarLayout);

        db = FirebaseFirestore.getInstance();

        // Initialize lists
        moviesList = new ArrayList<>();
        sliderList = new ArrayList<>();
        allMoviesList = new ArrayList<>(); // This will store all movies for filtering

        // Vertical list
        recyclerMovies.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new MovieAdapter(this, moviesList);
        recyclerMovies.setAdapter(movieAdapter);

        // Horizontal slider
        recyclerSlider.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        sliderAdapter = new SliderAdapter(this, sliderList);
        recyclerSlider.setAdapter(sliderAdapter);

        addMovieBtn.setOnClickListener(v ->
                startActivity(new Intent(this, AddMovieActivity.class))
        );

        // Setup search functionality
        setupSearch();

        // 🔹 Seed the database (run once, then comment out)
        // fillDatabase();

        loadMovies();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMovies();
    }

    private void setupSearch() {
        // Text change listener for real-time search
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Show/hide clear button
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                    filterMovies(s.toString());
                } else {
                    clearSearch.setVisibility(View.GONE);
                    // Reset to show all movies when search is empty
                    moviesList.clear();
                    moviesList.addAll(allMoviesList);
                    movieAdapter.notifyDataSetChanged();

                    // Update slider with first 5 movies
                    updateSlider();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Clear search button
        clearSearch.setOnClickListener(v -> {
            searchBar.setText("");
            clearSearch.setVisibility(View.GONE);

            // Hide keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
        });

        // Search icon click (optional - can focus on search bar)
        searchIcon.setOnClickListener(v -> searchBar.requestFocus());

        // Handle search button on keyboard
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // Hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
                filterMovies(searchBar.getText().toString());
                return true;
            }
            return false;
        });
    }

    private void loadMovies() {
        loading.setVisibility(View.VISIBLE);

        db.collection("movies")
                .get()
                .addOnSuccessListener(query -> {
                    allMoviesList.clear();
                    moviesList.clear();
                    sliderList.clear();

                    for (DocumentSnapshot doc : query) {
                        Movie m = doc.toObject(Movie.class);
                        if (m != null) {
                            m.setId(doc.getId());
                            allMoviesList.add(m);
                        }
                    }

                    // Add all movies to display list
                    moviesList.addAll(allMoviesList);

                    // Update slider with first 5 movies
                    updateSlider();

                    movieAdapter.notifyDataSetChanged();
                    loading.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Log.e("MainActivity", "Error loading movies", e);
                    loading.setVisibility(View.GONE);
                });
    }

    private void updateSlider() {
        sliderList.clear();
        // Add first 5 movies to slider (or less if not enough movies)
        int count = Math.min(5, moviesList.size());
        for (int i = 0; i < count; i++) {
            sliderList.add(moviesList.get(i));
        }
        sliderAdapter.notifyDataSetChanged();
    }

    private void filterMovies(String query) {
        if (query.isEmpty()) {
            // Show all movies
            moviesList.clear();
            moviesList.addAll(allMoviesList);
        } else {
            // Filter movies based on query
            moviesList.clear();
            String lowerCaseQuery = query.toLowerCase(Locale.getDefault());

            for (Movie movie : allMoviesList) {
                if (movie.getName() != null && movie.getName().toLowerCase(Locale.getDefault()).contains(lowerCaseQuery)) {
                    moviesList.add(movie);
                }
            }
        }

        movieAdapter.notifyDataSetChanged();

        // Update slider with filtered results (first 5)
        updateSlider();
    }}