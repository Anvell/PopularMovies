package io.github.anvell.popularmovies.presentation.presenter;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.concurrent.atomic.AtomicReference;

import io.github.anvell.popularmovies.R;
import io.github.anvell.popularmovies.database.MoviesProviderClient;
import io.github.anvell.popularmovies.models.MovieDataSource;
import io.github.anvell.popularmovies.presentation.view.DetailsView;
import io.github.anvell.popularmovies.web.MovieDetails;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class DetailsPresenter extends MvpPresenter<DetailsView> {

    private final MovieDataSource mMovieDataSource;
    private final AtomicReference<MovieDetails> mMovieDetails;
    private final CompositeDisposable disposables;
    private boolean mIsLoadingData;

    public DetailsPresenter() {
        mMovieDataSource = new MovieDataSource();
        mMovieDetails = new AtomicReference<>(new MovieDetails());
        mIsLoadingData = false;
        disposables = new CompositeDisposable();
    }

    public void dispose() {
        disposables.clear();
    }

    @SuppressLint("CheckResult")
    public void markAs(boolean favourite, ContentResolver resolver) {
        if(isLoadingData()) return;

        if(favourite) {
            getMovieDetails().isFavourite = true;

            MoviesProviderClient.addMovie(resolver, getMovieDetails())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(disposables::add)
                    .subscribe(
                            s -> getViewState().toggleFavourite(true),
                            e -> getViewState().showMessage(R.string.error_database_update));
        } else {
            getMovieDetails().isFavourite = false;

            MoviesProviderClient.deleteMovie(resolver, getMovieDetails().id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(disposables::add)
                    .subscribe(
                            s -> getViewState().toggleFavourite(false),
                            e -> getViewState().showMessage(R.string.error_database_update));
        }
    }

    public void fetchMovieDetails(@NonNull ContentResolver resolver, int movieId) {
        mIsLoadingData = true;
        getViewState().showProgress();
        disposables.add(mMovieDataSource.fetchMovieDetailsData(mMovieDetails,
                                        resolver, movieId, this::updateView));
    }

    public boolean isLoadingData() {
        return mIsLoadingData;
    }

    public MovieDetails getMovieDetails() {
        return mMovieDetails.get();
    }

    private void updateView() {
        if(mMovieDetails.get() != null) {
            getViewState().updateDetails(mMovieDetails.get());
            getViewState().toggleFavourite(getMovieDetails().isFavourite);
        }
        mIsLoadingData = false;
        getViewState().hideProgress();
    }
}
