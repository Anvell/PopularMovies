package io.github.anvell.popularmovies.models;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.github.anvell.popularmovies.BuildConfig;
import io.github.anvell.popularmovies.database.MoviesProviderClient;
import io.github.anvell.popularmovies.utils.AtomicCallback;
import io.github.anvell.popularmovies.web.ApiClient;
import io.github.anvell.popularmovies.web.MovieDbService;
import io.github.anvell.popularmovies.web.MovieDetails;
import io.github.anvell.popularmovies.web.MovieItem;
import io.github.anvell.popularmovies.web.MovieReviews;
import io.github.anvell.popularmovies.web.MoviesResource;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDataSource {

    public static final String SORT_BY_POPULAR = "popular";
    public static final String SORT_BY_TOP_RATED = "top_rated";
    public static final String MOVIEDB_DATE_SEPARATOR = "-";
    public static final int REQUEST_DELAY = 3000;
    public static final int DEFAULT_PAGE = 1;

    public int maxPages = -1;

    private final MovieDbService mClient;

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
                        if (response.isSuccessful() && response.body() != null) {
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

    @SuppressLint("CheckResult")
    public Disposable fetchMovieDetailsData(@NonNull AtomicReference<MovieDetails> data,
                                      @NonNull ContentResolver resolver,
                                      int movieId, Runnable onSuccess) {

        return MoviesProviderClient.getMovie(resolver, movieId)
                .onErrorResumeNext(mClient.getMovieDetails(movieId, BuildConfig.MOVIEDB_API_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(t -> t.delay(REQUEST_DELAY, TimeUnit.MILLISECONDS))
                .subscribe((movieDetails, throwable) -> {
                    data.set(movieDetails);
                    onSuccess.run();
                });
    }

    public void fetchMovieReviewsData(AtomicReference<MovieReviews> data, int movieId, int page,
                                      Runnable onSuccess, Runnable onFailure) {
        mClient.getMovieReviews(movieId, BuildConfig.MOVIEDB_API_KEY, page)
                .enqueue(new AtomicCallback<>(data, onSuccess, onFailure));
    }
}
