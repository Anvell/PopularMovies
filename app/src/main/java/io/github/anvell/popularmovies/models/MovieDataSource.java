package io.github.anvell.popularmovies.models;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.github.anvell.popularmovies.BuildConfig;
import io.github.anvell.popularmovies.web.ApiClient;
import io.github.anvell.popularmovies.web.MovieDbService;
import io.github.anvell.popularmovies.web.MovieDetails;
import io.github.anvell.popularmovies.web.MovieItem;
import io.github.anvell.popularmovies.web.MoviesResource;
import retrofit2.Callback;
import retrofit2.Call;
import retrofit2.Response;

public class MovieDataSource {

    public static final String SORT_BY_POPULAR = "popular";
    public static final String SORT_BY_TOP_RATED = "top_rated";
    public static final int DEFAULT_PAGE = 1;

    public int maxPages = -1;

    private MovieDbService mClient;

    public MovieDataSource() {
        mClient = ApiClient.getClient();
    }

    public void fetchMovieData(ArrayList<MovieItem> data, String sorting, int page,
                               Runnable onSuccess, Runnable onFailure) {
        mClient.getMovies(sorting, BuildConfig.MOVIEDB_API_KEY, page)
               .enqueue(new Callback<MoviesResource>() {
            @Override
            public void onResponse(@NonNull Call<MoviesResource> call,
                                   @NonNull Response<MoviesResource> response) {
                if(response.isSuccessful() && response.body() != null) {
                    //noinspection ConstantConditions
                    maxPages = response.body().totalPages;
                    //noinspection ConstantConditions
                    data.addAll(response.body().results);
                    onSuccess.run();
                } else {
                    onFailure.run();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MoviesResource> call, @NonNull Throwable t) {
                onFailure.run();
            }
        });
    }

    public void fetchMovieDetailsData(AtomicReference<MovieDetails> data, int movieId,
                                      Runnable onSuccess, Runnable onFailure) {
        mClient.getMovieDetails(movieId, BuildConfig.MOVIEDB_API_KEY)
               .enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(@NonNull Call<MovieDetails> call,
                                   @NonNull Response<MovieDetails> response) {
                if(response.isSuccessful() && response.body() != null) {
                    data.set(response.body());
                    onSuccess.run();
                } else {
                    onFailure.run();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieDetails> call, @NonNull Throwable t) {
                onFailure.run();
            }
        });
    }
}
