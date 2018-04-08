package io.github.anvell.popularmovies.presentation.presenter;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.os.Handler;
import android.util.SparseArray;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.HashMap;

import io.github.anvell.popularmovies.R;
import io.github.anvell.popularmovies.database.MoviesProviderClient;
import io.github.anvell.popularmovies.models.MovieDataSource;
import io.github.anvell.popularmovies.presentation.view.MainView;
import io.github.anvell.popularmovies.presentation.view.NotificationIndicators;
import io.github.anvell.popularmovies.web.MovieDetails;
import io.github.anvell.popularmovies.web.MovieItem;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {

    private final MovieDataSource mMovieDataSource;
    private int mCurrentSortId;
    private boolean mIsLoadingData;
    private ArrayList<MovieItem> mData;
    private SparseArray<MovieDetails> mLocalData;
    private int mLastPage;
    private final CompositeDisposable disposables;

    public MainPresenter() {
        mData = new ArrayList<>();
        mLocalData = new SparseArray<>();
        mMovieDataSource = new MovieDataSource();
        mLastPage = MovieDataSource.DEFAULT_PAGE;
        mCurrentSortId = R.id.nav_popular;
        mIsLoadingData = false;
        disposables = new CompositeDisposable();
    }

    private String getSortingKey(int key) {
        return key == R.id.nav_top_rated? MovieDataSource.SORT_BY_TOP_RATED : MovieDataSource.SORT_BY_POPULAR;
    }

    public int getCurrentSortId() {
        return mCurrentSortId;
    }

    @SuppressLint("CheckResult")
    public void fetchLocalMovieData(ContentResolver resolver) {
        mLocalData.clear();

        MoviesProviderClient.getMovies(resolver)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposables::add)
                .doOnComplete(() -> {
                    if (mCurrentSortId == R.id.nav_favorite)
                        fetchMovieData(mCurrentSortId);
                })
                .subscribe(movieDetails -> mLocalData.put(movieDetails.id, movieDetails));
    }

    public void fetchMovieData() {
        fetchMovieData(mCurrentSortId);
    }

    public void fetchMovieData(int sorting) {
        mData.clear();
        mLastPage = MovieDataSource.DEFAULT_PAGE;

        if (sorting == R.id.nav_favorite) {
            onSortingChanged(sorting);
            for (int i = 0; i < mLocalData.size(); i++) {
                mData.add(mLocalData.valueAt(i).toMovieItem());
            }
            getViewState().notifyDataUpdated();

        } else
            fetchMovieData(sorting, mLastPage);
    }

    public void fetchMovieDataNextPage() {
        if (mCurrentSortId != R.id.nav_favorite)
            fetchMovieData(mCurrentSortId, mLastPage+1);
    }

    private void fetchMovieData(int sorting, int page) {

        if(mMovieDataSource.maxPages > 0 && page > mMovieDataSource.maxPages) return;

        mIsLoadingData = true;
        int oldSize = mData.size();
        onSortingChanged(sorting);

        mMovieDataSource.fetchMovieData(mData, getSortingKey(sorting), page, () -> {
                if(page > mLastPage)
                    getViewState().notifyDataUpdated(oldSize, getMovieData().size() - oldSize);
                else
                    getViewState().notifyDataUpdated();
                mIsLoadingData = false;
                mLastPage = page;
            },
            () -> handleNetworkError(sorting, page));
    }

    private void handleNetworkError(int sorting, int page) {
        if(mData.isEmpty())
            getViewState().showNotification(NotificationIndicators.NO_CONNECTION_NOTIFICATION);
        else
            getViewState().showNotification(NotificationIndicators.LOADING_CIRCLE);

        new Handler().postDelayed(() -> fetchMovieData(sorting, page), MovieDataSource.REQUEST_DELAY);
    }

    private void onSortingChanged(int id) {
        if(mCurrentSortId != id) {
            mCurrentSortId = id;
            getViewState().onSortingChanged(id);
        }
    }

    public boolean isLoadingData() {
        return mIsLoadingData;
    }

    public ArrayList<MovieItem> getMovieData() {
        return mData;
    }
}

