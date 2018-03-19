package io.github.anvell.popularmovies.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.anvell.popularmovies.R;
import io.github.anvell.popularmovies.presentation.presenter.DetailsPresenter;
import io.github.anvell.popularmovies.presentation.view.DetailsView;
import io.github.anvell.popularmovies.web.MovieDetails;

public class DetailsActivity extends MvpAppCompatActivity implements DetailsView {

    @InjectPresenter
    DetailsPresenter mDetailsPresenter;

    @BindView(R.id.cv_rating) CardView detailsRatingCard;
    @BindView(R.id.tv_details_title) TextView detailsTitle;
    @BindView(R.id.tv_details_year) TextView detailsYear;
    @BindView(R.id.tv_details_rating) TextView detailsRating;
    @BindView(R.id.tv_details_overview) TextView detailsOverview;
    @BindView(R.id.tv_details_overview_body) TextView detailsOverviewBody;

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
            detailsTitle.setText(movieTitle);

        mDetailsPresenter.fetchMovieDetails(movieId);
    }

    @Override
    public void updateDetails(MovieDetails movieDetails) {

        // Let's avoid super verbose date parsing...
        int index = movieDetails.releaseDate.indexOf("-");
        detailsYear.setText(movieDetails.releaseDate.substring(0, index));

        detailsOverview.setVisibility(View.VISIBLE);
        detailsRatingCard.setVisibility(View.VISIBLE);

        if(movieDetails.posterPath instanceof String)
            updatePoster(movieDetails.backdropPath);

        detailsRating.setText(String.valueOf(movieDetails.voteAverage));
        detailsOverviewBody.setText(movieDetails.overview);
    }

    private void updatePoster(String posterPath) {
        String posterUrl = getString(R.string.poster_base_path_hd) + posterPath;
        Picasso.with(this)
                .load(posterUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(detailsPoster);
    }
}
