package io.github.anvell.popularmovies.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.anvell.popularmovies.R;
import io.github.anvell.popularmovies.models.MovieDataSource;
import io.github.anvell.popularmovies.presentation.presenter.DetailsPresenter;
import io.github.anvell.popularmovies.presentation.view.DetailsView;
import io.github.anvell.popularmovies.ui.adapter.MovieAdapter;
import io.github.anvell.popularmovies.ui.adapter.VideoAdapter;
import io.github.anvell.popularmovies.utils.EndlessRecyclerViewScrollListener;
import io.github.anvell.popularmovies.web.MovieDetails;
import io.github.anvell.popularmovies.web.MovieGenre;
import io.github.anvell.popularmovies.web.MovieVideo;
import io.github.anvell.popularmovies.web.MovieVideos;

public class DetailsActivity extends MvpAppCompatActivity implements DetailsView {

    @InjectPresenter
    DetailsPresenter mDetailsPresenter;

    private VideoAdapter mAdapter;

    @BindView(R.id.pg_loading_details) ProgressBar detailsLoadingBar;
    @BindView(R.id.cv_rating) CardView detailsRatingCard;
    @BindView(R.id.tv_details_title) TextView detailsTitle;
    @BindView(R.id.tv_details_year) TextView detailsYear;
    @BindView(R.id.tv_details_rating) TextView detailsRating;
    @BindView(R.id.tv_details_rating_caption) TextView detailsRatingCaption;
    @BindView(R.id.tv_details_overview) TextView detailsOverview;
    @BindView(R.id.tv_details_overview_body) TextView detailsOverviewBody;
    @BindView(R.id.tv_details_genres) TextView detailsGenres;
    @BindView(R.id.tv_details_genres_body) TextView detailsGenresBody;
    @BindView(R.id.iv_poster) ImageView detailsPoster;
    @BindView(R.id.rv_videos) RecyclerView videosListView;
    @BindView(R.id.ll_movies) LinearLayout videosView;

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

        if(savedInstanceState == null && !mDetailsPresenter.isLoadingData())
            mDetailsPresenter.fetchMovieDetails(movieId);
    }

    @Override
    public void updateDetails(MovieDetails movieDetails) {

        // Let's avoid verbose date parsing...
        int index = movieDetails.releaseDate.indexOf(MovieDataSource.MOVIEDB_DATE_SEPARATOR);
        detailsYear.setText(movieDetails.releaseDate.substring(0, index));

        revealViews(detailsOverview, detailsRatingCard, detailsGenres);

        if(movieDetails.backdropPath != null) {
            updatePoster(movieDetails.backdropPath);
        } else
            if(movieDetails.posterPath != null && movieDetails.posterPath instanceof String) {
            updatePoster((String) movieDetails.posterPath);
        }

        if(movieDetails.voteAverage > 0)
            detailsRating.setText(String.valueOf(movieDetails.voteAverage));
        else detailsRatingCaption.setText(R.string.details_rating_none);

        detailsOverviewBody.setText(movieDetails.overview);
        detailsGenresBody.setText(joinGenres(movieDetails.genres));

        if(movieDetails.videos.results != null) {
            configureVideosList(movieDetails.videos.results);
            revealViews(videosView);
        }
    }

    @Override
    public void showProgress() {
        detailsLoadingBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        detailsLoadingBar.setVisibility(View.GONE);
    }

    private String joinGenres(List<MovieGenre> genres) {

        if(genres == null || genres.size() < 1) return "";
        String sep = getString(R.string.details_genre_separator);
        StringBuilder sb = new StringBuilder();
        for(Iterator<MovieGenre> i = genres.iterator(); i.hasNext();) {
            sb.append(i.next().name);
            if(i.hasNext()) sb.append(sep);
        }
        return sb.toString();
    }

    private void revealViews(View... textViews) {
        for(View v : textViews) v.setVisibility(View.VISIBLE);
    }

    private void updatePoster(String posterPath) {
        int imageWidth = getResources().getInteger(R.integer.backdrop_width);
        String posterUrl = getString(R.string.images_base_path)
                           + String.valueOf(imageWidth) + posterPath;
        Picasso.with(this)
                .load(posterUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(detailsPoster);
    }

    private void openVideoIntent(String id) {

        Uri videoUri = Uri.parse(getString(R.string.youtube_activity) + ":" + id);
        Intent intentLocal = new Intent(Intent.ACTION_VIEW, videoUri);

        if (isIntentAvailable(intentLocal)) {
            startActivity(intentLocal);
        } else {
            videoUri = Uri.parse(getString(R.string.youtube_video_path) + id);
            Intent intentWeb = new Intent(Intent.ACTION_VIEW, videoUri);
            startActivity(intentWeb);
        }
    }

    private boolean isIntentAvailable(Intent intent) {
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return activities.size() > 0;
    }

    private void configureVideosList(ArrayList<MovieVideo> videos) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        videosListView.setLayoutManager(layoutManager);

        mAdapter = new VideoAdapter(videos);
        mAdapter.setOnItemClickListener((view, position) ->
                 openVideoIntent(videos.get(position).key));
        videosListView.setAdapter(mAdapter);

    }
}
