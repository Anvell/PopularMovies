package io.github.anvell.popularmovies.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.anvell.popularmovies.R;
import io.github.anvell.popularmovies.presentation.presenter.DetailsPresenter;
import io.github.anvell.popularmovies.presentation.presenter.MainPresenter;
import io.github.anvell.popularmovies.presentation.view.DetailsView;
import io.github.anvell.popularmovies.web.MovieDetails;

public class DetailsActivity extends MvpAppCompatActivity implements DetailsView {

    @InjectPresenter
    DetailsPresenter mDetailsPresenter;

    @BindView(R.id.details_toolbar) Toolbar toolbar;
    @BindView(R.id.tv_details_year) TextView detailsYear;
    @BindView(R.id.iv_poster) ImageView detailsPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        int movieId = intent.getIntExtra(getString(R.string.intent_movie_id), 0);
        String movieTitle = intent.getStringExtra(getString(R.string.intent_movie_title));

        if(movieTitle != null)
            toolbar.setTitle(movieTitle);
        setSupportActionBar(toolbar);

        mDetailsPresenter.fetchMovieDetails(movieId);
    }

    @Override
    public void updateDetails(MovieDetails movieDetails) {

        // Let's avoid super verbose date parsing madness...
        int index = movieDetails.releaseDate.indexOf("-");
        detailsYear.setText(movieDetails.releaseDate.substring(0, index));

        if(movieDetails.posterPath instanceof String)
            updatePoster((String)movieDetails.posterPath);
    }

    private void updatePoster(String posterPath) {
        String posterUrl = getString(R.string.poster_base_path) + posterPath;
        Picasso.with(this)
                .load(posterUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(detailsPoster);
    }
}
