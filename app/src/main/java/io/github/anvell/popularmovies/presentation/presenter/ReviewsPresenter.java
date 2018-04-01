package io.github.anvell.popularmovies.presentation.presenter;

import android.os.Handler;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import io.github.anvell.popularmovies.models.MovieDataSource;
import io.github.anvell.popularmovies.presentation.view.MainView;
import io.github.anvell.popularmovies.presentation.view.ReviewsView;
import io.github.anvell.popularmovies.web.MovieDetails;
import io.github.anvell.popularmovies.web.MovieReview;
import io.github.anvell.popularmovies.web.MovieReviews;

@InjectViewState
public class ReviewsPresenter extends MvpPresenter<ReviewsView> {

    public enum ReviewsDirection {
        CURRENT, NEXT, PREVIOUS
    }

    private final MovieDataSource mMovieDataSource;
    private final AtomicReference<MovieReviews> mMovieReviews;
    private boolean mIsLoadingData;
    private int currentPage = 1;

    public ReviewsPresenter() {
        mMovieDataSource = new MovieDataSource();
        mMovieReviews = new AtomicReference<>(new MovieReviews());
    }

    public void fetchMovieReviews(int movieId, ReviewsDirection direction) {

        switch (direction) {
            case CURRENT: break;
            case NEXT:
                if(currentPage < mMovieReviews.get().totalPages) currentPage++;
                else return;
                break;
            case PREVIOUS:
                if(currentPage > 1) currentPage--;
                else return;
                break;
        }

        mIsLoadingData = true;
        getViewState().showProgress();
        mMovieDataSource.fetchMovieReviewsData(mMovieReviews, movieId, currentPage,
                this::updateView, () -> handleNetworkError(movieId));
    }

    public boolean isLoadingData() {
        return mIsLoadingData;
    }

    private void updateView() {
        if(mMovieReviews.get() != null)
            getViewState().updateReviews(getReviews());
        mIsLoadingData = false;
        getViewState().hideProgress();
    }

    private void handleNetworkError(int movieId) {
        new Handler().postDelayed(() -> fetchMovieReviews(movieId, ReviewsDirection.CURRENT),
                                        MovieDataSource.REQUEST_DELAY);
    }

    public ArrayList<MovieReview> getReviews() {
        return mMovieReviews.get().reviews;
    }

    public int getPages() {
        return mMovieReviews.get().totalPages;
    }
}
