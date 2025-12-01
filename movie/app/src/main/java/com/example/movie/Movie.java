package com.example.movie;

public class Movie {
    private String id;
    private String name;
    private String rating;
    private String imageUrl;
    private String description;

    public Movie() {}

    public Movie(String id, String name, String rating, String imageUrl, String description) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getRating() { return rating; }
    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }

    public void setId(String id) { this.id = id; }
}
