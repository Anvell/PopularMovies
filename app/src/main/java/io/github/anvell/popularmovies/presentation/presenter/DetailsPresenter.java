package io.github.anvell.popularmovies.presentation.presenter;

import android.os.Handler;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.concurrent.atomic.AtomicReference;

import io.github.anvell.popularmovies.models.MovieDataSource;
import io.github.anvell.popularmovies.presentation.view.DetailsView;
import io.github.anvell.popularmovies.web.MovieDetails;

@InjectViewState
public class DetailsPresenter extends MvpPresenter<DetailsView> {

    private final MovieDataSource mMovieDataSource;
    private final AtomicReference<MovieDetails> mMovieDetails;
    private boolean mIsLoadingData;

    public DetailsPresenter() {
        mMovieDataSource = new MovieDataSource();
        mMovieDetails = new AtomicReference<>(new MovieDetails());
        mIsLoadingData = false;
    }

    public void fetchMovieDetails(int movieId) {
        mIsLoadingData = true;
        getViewState().showProgress();
        mMovieDataSource.fetchMovieDetailsData(mMovieDetails, movieId,
                         this::updateView, () -> handleNetworkError(movieId));
    }

    public boolean isLoadingData() {
        return mIsLoadingData;
    }

    public MovieDetails getMovieDetails() {
        return mMovieDetails.get();
    }

    private void updateView() {
        if(mMovieDetails.get() != null)
            getViewState().updateDetails(mMovieDetails.get());
        mIsLoadingData = false;
        getViewState().hideProgress();
    }

    private void handleNetworkError(int movieId) {
        new Handler().postDelayed(() -> fetchMovieDetails(movieId), MovieDataSource.REQUEST_DELAY);
    }
}
