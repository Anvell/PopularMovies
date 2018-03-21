package io.github.anvell.popularmovies.presentation.view;

import com.arellomobile.mvp.MvpView;
import io.github.anvell.popularmovies.web.MovieDetails;

public interface DetailsView extends MvpView {
    void updateDetails(MovieDetails movieDetails);
    void showProgress();
    void hideProgress();
}
