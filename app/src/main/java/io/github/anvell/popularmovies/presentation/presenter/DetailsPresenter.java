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

    private MovieDataSource mMovieDataSource;
    private AtomicReference<MovieDetails> mMovieDetails;
    private boolean mIsLoadingData;

    public DetailsPresenter() {
        mMovieDataSource = new MovieDataSource();
        mMovieDetails = new AtomicReference<>(new MovieDetails());
        mIsLoadingData = false;
    }

    public void fetchMovieDetails(int movieId) {
        mIsLoadingData = true;
        mMovieDataSource.fetchMovieDetailsData(mMovieDetails, movieId,
                         this::updateView, () -> handleNetworkError(movieId));
    }

    private void updateView() {
        if(mMovieDetails.get() != null)
            getViewState().updateDetails(mMovieDetails.get());
        mIsLoadingData = false;
    }

    private void handleNetworkError(int movieId) {
//        if(mData.isEmpty())
//            getViewState().showNotification(NotificationIndicators.NO_CONNECTION_NOTIFICATION);
//        else
//            getViewState().showNotification(NotificationIndicators.LOADING_CIRCLE);

        new Handler().postDelayed(() -> fetchMovieDetails(movieId), 3000);
    }
}
