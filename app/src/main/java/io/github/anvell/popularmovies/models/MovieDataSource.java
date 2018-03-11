package io.github.anvell.popularmovies.models;

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

    private MovieDbService mClient;
    private ArrayList<MovieItem> mData;
    private int mLastPage;

    public MovieDataSource() {
        mClient = ApiClient.getClient();
        mData = new ArrayList<>();
        mLastPage = 1;
    }

    public void fetchMovieData(String sorting, Runnable onSuccess, Runnable onFailure) {
        mClient.getMovies(sorting, BuildConfig.MOVIEDB_API_KEY, mLastPage).enqueue(new Callback<MoviesResource>() {
            @Override
            public void onResponse(Call<MoviesResource> call, Response<MoviesResource> response) {
                if(response.isSuccessful()) {
                    mData.addAll(response.body().results);
//                    Log.d("MainActivity", "movies loaded from API");
                    onSuccess.run();
                } else {
                    onFailure.run();
                }
            }

            @Override
            public void onFailure(Call<MoviesResource> call, Throwable t) {
                onFailure.run();
            }
        });
    }

    public void clearMovieData() {
        mData.clear();
    }

    public ArrayList<MovieItem> getMovieData() {
        return mData;
    }

}
