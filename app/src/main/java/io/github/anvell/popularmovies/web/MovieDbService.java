package io.github.anvell.popularmovies.web;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieDbService {

    @GET("/3/movie/{sorting}?")
    Call<MoviesResource> getMovies(@Path("sorting") String sorting, @Query("api_key") String apiKey, @Query("page") int page);

    @GET("/3/movie/{movie_id}?append_to_response=videos")
    Call<MovieDetails> getMovieDetails(@Path("movie_id") int id, @Query("api_key") String apiKey);

    @GET("/3/movie/{movie_id}/reviews?")
    Call<MovieReviews> getMovieReviews(@Path("movie_id") int id, @Query("api_key") String apiKey, @Query("page") int page);
}
