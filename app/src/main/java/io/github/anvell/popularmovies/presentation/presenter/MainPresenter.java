package io.github.anvell.popularmovies.presentation.presenter;

import android.os.Handler;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.List;

import io.github.anvell.popularmovies.R;
import io.github.anvell.popularmovies.models.MovieDataSource;
import io.github.anvell.popularmovies.presentation.view.MainView;
import io.github.anvell.popularmovies.web.MovieItem;
import retrofit2.Retrofit;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {

    private MovieDataSource mDataSource;
    private int mCurrentSort;

    public MainPresenter() {
        mDataSource = new MovieDataSource();
    }

    public void fetchMovieData(String sorting) {
        mDataSource.fetchMovieData(sorting, () -> getViewState().notifyDataUpdated(),
            () -> new Handler().postDelayed(() -> fetchMovieData(sorting), 1000));
    }

    public void sortIdChanged(int id) {
        if(mCurrentSort != id) {
            mCurrentSort = id;
            getViewState().onSortingChanged(id);

            fetchMovieData(mCurrentSort == R.id.nav_top_rated?
                    MovieDataSource.SORT_BY_TOP_RATED : MovieDataSource.SORT_BY_POPULAR);
        }
    }

    public ArrayList<MovieItem> getMovieData() {
        return mDataSource.getMovieData();
    }
}

