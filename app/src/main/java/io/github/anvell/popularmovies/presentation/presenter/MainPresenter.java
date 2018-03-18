package io.github.anvell.popularmovies.presentation.presenter;

import android.os.Handler;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.List;

import io.github.anvell.popularmovies.R;
import io.github.anvell.popularmovies.models.MovieDataSource;
import io.github.anvell.popularmovies.presentation.view.MainView;
import io.github.anvell.popularmovies.presentation.view.NotificationIndicators;
import io.github.anvell.popularmovies.web.MovieItem;
import retrofit2.Retrofit;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {

    private MovieDataSource mDataSource;
    private int mCurrentSortId;
    private boolean mIsLoadingData;
    private ArrayList<MovieItem> mData;
    private int mLastPage;

    public MainPresenter() {
        mDataSource = new MovieDataSource();
        mData = new ArrayList<>();
        mLastPage = MovieDataSource.DEFAULT_PAGE;
        mCurrentSortId = R.id.nav_popular;
    }

    private String getSortingKey(int key) {
        return key == R.id.nav_top_rated? MovieDataSource.SORT_BY_TOP_RATED : MovieDataSource.SORT_BY_POPULAR;
    }

    public int getCurrentSortId() {
        return mCurrentSortId;
    }

    public void fetchMovieData() {
        fetchMovieData(mCurrentSortId);
    }

    public void fetchMovieData(int sorting) {
        mData.clear();
        mLastPage = MovieDataSource.DEFAULT_PAGE;
        fetchMovieData(sorting, mLastPage);
    }

    public void fetchMovieDataNextPage() {
        fetchMovieData(mCurrentSortId, mLastPage+1);
    }

    private void fetchMovieData(int sorting, int page) {
        mIsLoadingData = true;
        int oldSize = getMovieData().size();
        onSortingChanged(sorting);

        mDataSource.fetchMovieData(mData, getSortingKey(sorting), page, () -> {
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

        new Handler().postDelayed(() -> fetchMovieData(sorting, page), 3000);
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

