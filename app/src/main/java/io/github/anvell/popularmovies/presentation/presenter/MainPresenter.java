package io.github.anvell.popularmovies.presentation.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import io.github.anvell.popularmovies.R;
import io.github.anvell.popularmovies.presentation.view.MainView;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {

    public MainPresenter() {
        getViewState().showMessage(R.string.MOVIEDB_API_KEY);
    }
}

