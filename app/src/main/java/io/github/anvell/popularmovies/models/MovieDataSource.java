package io.github.anvell.popularmovies.models;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.github.anvell.popularmovies.BuildConfig;
import io.github.anvell.popularmovies.web.ApiClient;
import io.github.anvell.popularmovies.web.MovieDbService;
import io.github.anvell.popularmovies.web.MovieItem;
import io.github.anvell.popularmovies.web.MoviesResource;
import retrofit2.Callback;
import retrofit2.Call;
import retrofit2.Response;

public class MovieDataSource {

    public static final String SORT_BY_POPULAR = "popular";
    public static final String SORT_BY_TOP_RATED = "top_rated";
    public static final int DEFAULT_PAGE = 1;

    private MovieDbService mClient;

    public MovieDataSource() {
        mClient = ApiClient.getClient();
    }

    public void fetchMovieData(ArrayList<MovieItem> data, String sorting, int page, Runnable onSuccess, Runnable onFailure) {
        mClient.getMovies(sorting, BuildConfig.MOVIEDB_API_KEY, page).enqueue(new Callback<MoviesResource>() {
            @Override
            public void onResponse(@NonNull Call<MoviesResource> call, @NonNull Response<MoviesResource> response) {
                if(response.isSuccessful() && response.body() != null) {
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
}
