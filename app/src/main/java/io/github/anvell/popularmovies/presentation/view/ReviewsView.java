package io.github.anvell.popularmovies.presentation.view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.ArrayList;
import io.github.anvell.popularmovies.web.MovieReview;

public interface ReviewsView extends MvpView {
    @StateStrategyType(OneExecutionStateStrategy.class)
    void updateReviews(ArrayList<MovieReview> reviews);

    void showProgress();
    void hideProgress();
}
