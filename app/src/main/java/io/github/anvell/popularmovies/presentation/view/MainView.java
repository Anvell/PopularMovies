package io.github.anvell.popularmovies.presentation.view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.*;

public interface MainView extends MvpView {
    @StateStrategyType(OneExecutionStateStrategy.class)
    void showMessage(int message);
    void showNotification(int indicator);
    void notifyDataUpdated();
    void notifyDataUpdated(int insertPosition, int length);
    void onSortingChanged(int id);
}