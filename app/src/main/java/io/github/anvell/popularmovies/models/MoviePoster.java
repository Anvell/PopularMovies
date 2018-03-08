package io.github.anvell.popularmovies.models;

import java.util.ArrayList;

public class MoviePoster {

    public final static String TEST_URL = "https://cdn-images-1.medium.com/fit/c/120/120/1*bU3Gui54JBJZjkVhO0Xwhw.png";
    private String mMoviePosterUrl;
    private String mID;

    public MoviePoster() {
        mMoviePosterUrl = TEST_URL;
    }

    public static ArrayList<MoviePoster> fetchMoviePosters() {
        ArrayList<MoviePoster> posters = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            posters.add(new MoviePoster());
        }
        return posters;
    }
}
