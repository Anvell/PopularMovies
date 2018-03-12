package io.github.anvell.popularmovies.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.anvell.popularmovies.R;
import io.github.anvell.popularmovies.models.MovieDataSource;
import io.github.anvell.popularmovies.presentation.presenter.MainPresenter;
import io.github.anvell.popularmovies.presentation.view.MainView;
import io.github.anvell.popularmovies.ui.adapter.MovieAdapter;

public class MainActivity extends MvpAppCompatActivity
        implements MainView, NavigationView.OnNavigationItemSelectedListener {

    @InjectPresenter
    MainPresenter mMainPresenter;

    private MovieAdapter mAdapter;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.movie_grid) RecyclerView movieGridView;
    @BindView(R.id.pg_loading_movies) ProgressBar loadingBar;

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

        configureMovieGrid();

        if(savedInstanceState == null || mMainPresenter.getMovieData().isEmpty())
            mMainPresenter.sortIdChanged(R.id.nav_popular);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return item.getItemId() == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        drawer.closeDrawer(GravityCompat.START);
        mMainPresenter.sortIdChanged(id);
        return true;
    }

    @Override
    public void showMessage(int message) {
        Toast t = Toast.makeText(this, message, Toast.LENGTH_LONG);
        t.show();
    }

    @Override
    public void notifyDataUpdated() {
        loadingBar.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSortingChanged(int id) {
        int strId;
        switch (id) {
            case R.id.nav_top_rated:
                strId = R.string.sort_top_rated;
                break;
            default:
                strId = R.string.sort_popular;
                break;
        }
        toolbar.setTitle(strId);
    }

    private void configureMovieGrid() {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float width = (displayMetrics.widthPixels * 160) / displayMetrics.density;
        float imageWidth = (342 * 160) / displayMetrics.density;

        movieGridView.setLayoutManager(new GridLayoutManager(this, Math.round(width / imageWidth)));

        mAdapter = new MovieAdapter(mMainPresenter.getMovieData());
        movieGridView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener((view, position) -> {
            Toast.makeText(this, mMainPresenter.getMovieData().get(position).originalTitle + " was clicked!", Toast.LENGTH_SHORT).show();
        });
    }
}
