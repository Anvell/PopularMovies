package io.github.anvell.popularmovies.models;

import android.content.ContentResolver;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.github.anvell.popularmovies.BuildConfig;
import io.github.anvell.popularmovies.database.MoviesProviderClient;
import io.github.anvell.popularmovies.web.ApiClient;
import io.github.anvell.popularmovies.web.MovieDbService;
import io.github.anvell.popularmovies.web.MovieDetails;
import io.github.anvell.popularmovies.web.MovieItem;
import io.github.anvell.popularmovies.web.MovieReviews;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MovieDataSource {

    public static final String SORT_BY_POPULAR = "popular";
    public static final String SORT_BY_TOP_RATED = "top_rated";
    public static final String MOVIEDB_DATE_SEPARATOR = "-";
    public static final int DEFAULT_PAGE = 1;

    private static final int REQUEST_DELAY = 3000;

    public int maxPages = -1;

    private final MovieDbService mClient;

    public MovieDataSource() {
        mClient = ApiClient.getClient();
    }

    public Disposable fetchMovieData(ArrayList<MovieItem> data, String sorting, int page,
                               Runnable onSuccess, Runnable onFailure) {

        return mClient.getMovies(sorting, BuildConfig.MOVIEDB_API_KEY, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(t -> onFailure.run())
                .retryWhen(t -> t.delay(REQUEST_DELAY, TimeUnit.MILLISECONDS))
                .subscribe(movies -> {
                    maxPages = movies.totalPages;
                    data.addAll(movies.results);
                    onSuccess.run();
                });
    }

    public Disposable fetchMovieDetailsData(@NonNull AtomicReference<MovieDetails> data,
                                      @NonNull ContentResolver resolver,
                                      int movieId, Runnable onSuccess) {

        return MoviesProviderClient.getMovie(resolver, movieId)
                .onErrorResumeNext(mClient.getMovieDetails(movieId, BuildConfig.MOVIEDB_API_KEY))
                .compose(requestWithRetry())
                .subscribe((movieDetails, throwable) -> {
                    data.set(movieDetails);
                    onSuccess.run();
                });
    }

    public Disposable fetchMovieReviewsData(AtomicReference<MovieReviews> data,
                                            int movieId, int page, Runnable onSuccess) {

        return mClient.getMovieReviews(movieId, BuildConfig.MOVIEDB_API_KEY, page)
                .compose(requestWithRetry())
                .subscribe((movieReviews, throwable) -> {
                    data.set(movieReviews);
                    onSuccess.run();
                });
    }

    private static <T> SingleTransformer<T, T> requestWithRetry() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(t -> t.delay(REQUEST_DELAY, TimeUnit.MILLISECONDS));
    }
}
