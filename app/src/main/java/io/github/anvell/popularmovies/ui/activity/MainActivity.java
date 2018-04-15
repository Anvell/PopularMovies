package io.github.anvell.popularmovies.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.anvell.popularmovies.R;
import io.github.anvell.popularmovies.presentation.presenter.MainPresenter;
import io.github.anvell.popularmovies.presentation.view.MainView;
import io.github.anvell.popularmovies.presentation.view.NotificationIndicators;
import io.github.anvell.popularmovies.ui.adapter.MovieAdapter;
import io.github.anvell.popularmovies.utils.EndlessRecyclerViewScrollListener;

public class MainActivity extends MvpAppCompatActivity
        implements MainView, NavigationView.OnNavigationItemSelectedListener {

    @InjectPresenter
    MainPresenter mMainPresenter;

    private MovieAdapter mAdapter;

    @BindView(R.id.srl_main_activity) SwipeRefreshLayout swipeLoader;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.movie_grid) RecyclerView movieGridView;
    @BindView(R.id.pg_loading_movies) ProgressBar loadingBar;
    @BindView(R.id.tv_no_connection) TextView errorNoConnectionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toolbar.setTitle(R.string.sort_popular);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        swipeLoader.setOnRefreshListener(() -> {
            if(!mMainPresenter.isLoadingData()) {
                mMainPresenter.fetchMovieData();
            } else
                swipeLoader.setRefreshing(false);
        });

        onInitializeMovieGrid();

        if(savedInstanceState == null) {
            mMainPresenter.fetchLocalMovieData(getContentResolver());
        } else {
            onSortingChanged(mMainPresenter.getCurrentSortId());
        }

        if(savedInstanceState == null || mMainPresenter.getMovieData().isEmpty()) {
            mMainPresenter.fetchMovieData();
        }
    }

    @Override
    protected void onResume() {
        if(mMainPresenter.getLastIndex() != RecyclerView.NO_POSITION) {
            mMainPresenter.fetchLocalMovieData(getContentResolver());
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(mMainPresenter.getBackStack().empty()) {
                super.onBackPressed();
            } else {
                Integer id = mMainPresenter.getBackStack().pop();
                if(id != mMainPresenter.getCurrentSortId()) {
                    mMainPresenter.dispose();
                    mMainPresenter.fetchMovieData(id);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return item.getItemId() == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        drawer.closeDrawer(GravityCompat.START);
        if(id != mMainPresenter.getCurrentSortId()) {
            mMainPresenter.dispose();
            mMainPresenter.getBackStack().push(mMainPresenter.getCurrentSortId());
            mMainPresenter.fetchMovieData(id);
        }
        return true;
    }

    @Override
    public void showMessage(int message) {
        Toast t = Toast.makeText(this, message, Toast.LENGTH_LONG);
        t.show();
    }

    @Override
    public void showNotification(int indicator) {
        if((indicator & NotificationIndicators.NO_CONNECTION_NOTIFICATION)
                == NotificationIndicators.NO_CONNECTION_NOTIFICATION)
            errorNoConnectionText.setVisibility(View.VISIBLE);

        if((indicator & NotificationIndicators.LOADING_BAR)
                == NotificationIndicators.LOADING_BAR)
            loadingBar.setVisibility(View.VISIBLE);

        if((indicator & NotificationIndicators.SWIPE_REFRESH)
                == NotificationIndicators.SWIPE_REFRESH)
            swipeLoader.setRefreshing(true);
    }

    @Override
    public void notifyDataUpdated(boolean showFavorites) {
        loadingBar.setVisibility(View.GONE);
        errorNoConnectionText.setVisibility(View.GONE);
        swipeLoader.setRefreshing(false);
        mAdapter.notifyDataSetChanged(showFavorites);
    }

    @Override
    public void notifyDataUpdated(int position) {
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void notifyDataUpdated(int position, int length) {
        loadingBar.setVisibility(View.GONE);
        mAdapter.notifyItemRangeInserted(position, length);
    }

    @Override
    public void notifyDataRemoved(int position, int length) {
        mAdapter.notifyItemRangeRemoved(position, length);
    }

    @Override
    public void onSortingChanged(int id) {
        int strId;
        switch (id) {
            case R.id.nav_top_rated:
                strId = R.string.sort_top_rated;
                break;
            case R.id.nav_favorite:
                strId = R.string.sort_favorites;
                break;
            default:
                strId = R.string.sort_popular;
                break;
        }
        toolbar.setTitle(strId);
    }

    private void openDetailsActivity(int position) {
        mMainPresenter.setLastIndex(position);

        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(getString(R.string.intent_movie_id),
                mMainPresenter.getMovieData().get(position).id);
        intent.putExtra(getString(R.string.intent_movie_title),
                mMainPresenter.getMovieData().get(position).title);
        startActivity(intent);
    }

    private void onInitializeMovieGrid() {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float widthDip = displayMetrics.widthPixels / displayMetrics.density;
        float imageWidthDip = getResources().getInteger(R.integer.poster_width) / displayMetrics.density;

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, (int)(widthDip / imageWidthDip));
        movieGridView.setLayoutManager(gridLayoutManager);
        ItemAnimator animator = movieGridView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        mAdapter = new MovieAdapter(mMainPresenter.getMovieData(), mMainPresenter.getMovieLocalData(),
                                   (mMainPresenter.getCurrentSortId() != R.id.nav_favorite));
        mAdapter.setOnItemClickListener((view, position) -> openDetailsActivity(position));
        movieGridView.setAdapter(mAdapter);

        EndlessRecyclerViewScrollListener endlessListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {

            @Override
            protected void onLoadMore(int totalItemsCount, RecyclerView view) {
                mMainPresenter.fetchMovieDataNextPage();
            }
        };
        movieGridView.addOnScrollListener(endlessListener);
    }
}
