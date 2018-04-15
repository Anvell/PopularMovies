package io.github.anvell.popularmovies.presentation.view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import io.github.anvell.popularmovies.web.MovieDetails;

public interface DetailsView extends MvpView {
    void updateDetails(MovieDetails movieDetails);
    @StateStrategyType(OneExecutionStateStrategy.class)
    void toggleFavourite(boolean favourite);
    @StateStrategyType(OneExecutionStateStrategy.class)
    void showMessage(int message);

    void showProgress();
    void hideProgress();
}
