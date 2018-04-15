package io.github.anvell.popularmovies.presentation.view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.*;

@StateStrategyType(OneExecutionStateStrategy.class)
public interface MainView extends MvpView {
    void showMessage(int message);
    void showNotification(int indicator);
    void notifyDataUpdated(boolean showFavorites);
    void notifyDataUpdated(int position);
    void notifyDataUpdated(int position, int length);
    void notifyDataRemoved(int position, int length);
    void onSortingChanged(int id);
}