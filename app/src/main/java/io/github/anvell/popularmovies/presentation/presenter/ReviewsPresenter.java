package io.github.anvell.popularmovies.presentation.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import io.github.anvell.popularmovies.models.MovieDataSource;
import io.github.anvell.popularmovies.presentation.view.ReviewsView;
import io.github.anvell.popularmovies.utils.LoadingData;
import io.github.anvell.popularmovies.web.MovieReview;
import io.github.anvell.popularmovies.web.MovieReviews;
import io.reactivex.disposables.CompositeDisposable;

@InjectViewState
public class ReviewsPresenter extends MvpPresenter<ReviewsView> implements LoadingData {

    private final CompositeDisposable disposables;

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
        disposables = new CompositeDisposable();
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

        onLoadingData(true);
        disposables.add(mMovieDataSource.fetchMovieReviewsData(mMovieReviews,
                        movieId, currentPage, this::updateView));
    }

    private void updateView() {
        onLoadingData(false);
        if(mMovieReviews.get() != null)
            getViewState().updateReviews(getReviews());
    }

    public ArrayList<MovieReview> getReviews() {
        return mMovieReviews.get().reviews;
    }

    public int getPages() {
        return mMovieReviews.get().totalPages;
    }

    public void dispose() {
        disposables.clear();
    }

    @Override
    public void onLoadingData(boolean isLoading) {
        mIsLoadingData = isLoading;
    }

    @Override
    public boolean isLoadingData() {
        return mIsLoadingData;
    }
}
