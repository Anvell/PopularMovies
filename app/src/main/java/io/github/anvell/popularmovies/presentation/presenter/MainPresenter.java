package io.github.anvell.popularmovies.presentation.presenter;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.util.SparseArray;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.Stack;

import io.github.anvell.popularmovies.R;
import io.github.anvell.popularmovies.database.MoviesProviderClient;
import io.github.anvell.popularmovies.models.MovieDataSource;
import io.github.anvell.popularmovies.presentation.view.MainView;
import io.github.anvell.popularmovies.presentation.view.NotificationIndicators;
import io.github.anvell.popularmovies.utils.LoadingData;
import io.github.anvell.popularmovies.web.MovieDetails;
import io.github.anvell.popularmovies.web.MovieItem;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> implements LoadingData {

    private final MovieDataSource mMovieDataSource;
    private int mLastPage;
    private int mCurrentSortId;
    private int mLastIndex = -1;
    private boolean mIsLoadingData;
    private final Stack<Integer> mBackStack;
    private final ArrayList<MovieItem> mData;
    private final SparseArray<MovieDetails> mLocalData;
    private final CompositeDisposable disposables;

    public MainPresenter() {
        mData = new ArrayList<>();
        mLocalData = new SparseArray<>();
        mMovieDataSource = new MovieDataSource();
        mLastPage = MovieDataSource.DEFAULT_PAGE;
        mCurrentSortId = R.id.nav_popular;
        mIsLoadingData = false;
        disposables = new CompositeDisposable();
        mBackStack = new Stack<>();
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
                    if(mLastIndex >= 0) {
                        getViewState().notifyDataUpdated(mLastIndex);
                        mLastIndex = -1;
                    }
                })
                .subscribe(movieDetails -> mLocalData.put(movieDetails.id, movieDetails));
    }

    public void fetchMovieData() {
        fetchMovieData(mCurrentSortId);
    }

    public void fetchMovieData(int sorting) {
        mLastPage = MovieDataSource.DEFAULT_PAGE;
        int dataSize = mData.size();
        mData.clear();
        getViewState().notifyDataRemoved(0, dataSize);

        if (sorting == R.id.nav_favorite) {
            onSortingChanged(sorting);
            for (int i = 0; i < mLocalData.size(); i++) {
                mData.add(mLocalData.valueAt(i).toMovieItem());
            }
            getViewState().notifyDataUpdated(false);

        } else
            fetchMovieData(sorting, mLastPage);
    }

    public void fetchMovieDataNextPage() {
        if (mCurrentSortId != R.id.nav_favorite)
            fetchMovieData(mCurrentSortId, mLastPage+1);
    }

    private void fetchMovieData(int sorting, int page) {

        if(mMovieDataSource.maxPages > 0 && page > mMovieDataSource.maxPages) return;

        onLoadingData(true);
        int oldSize = mData.size();
        onSortingChanged(sorting);

        if(page == MovieDataSource.DEFAULT_PAGE) {
            getViewState().showNotification(NotificationIndicators.SWIPE_REFRESH);
        }

        disposables.add(
            mMovieDataSource.fetchMovieData(mData, getSortingKey(sorting), page, () -> {
                    if(page > mLastPage)
                        getViewState().notifyDataUpdated(oldSize, getMovieData().size() - oldSize);
                    else
                        getViewState().notifyDataUpdated(true);
                    onLoadingData(false);
                    mLastPage = page;
                }, this::onNetworkError)
        );
    }

    private void onNetworkError() {
        if(mData.isEmpty())
            getViewState().showNotification(NotificationIndicators.NO_CONNECTION_NOTIFICATION);
        else
            getViewState().showNotification(NotificationIndicators.LOADING_BAR);
    }

    private void onSortingChanged(int id) {
        if(mCurrentSortId != id) {
            mCurrentSortId = id;
            getViewState().onSortingChanged(id);
        }
    }

    public ArrayList<MovieItem> getMovieData() {
        return mData;
    }

    public SparseArray<MovieDetails> getMovieLocalData() {
        return mLocalData;
    }

    public void dispose() {
        disposables.clear();
    }

    public int getLastIndex() {
        return mLastIndex;
    }

    public void setLastIndex(int index) {
        this.mLastIndex = index;
    }

    public Stack<Integer> getBackStack() {
        return mBackStack;
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

