package io.github.anvell.popularmovies.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.BindDrawable;
import butterknife.ButterKnife;
import io.github.anvell.popularmovies.R;
import io.github.anvell.popularmovies.models.MoviePoster;

public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.ViewHolder> {

    private ArrayList<MoviePoster> mMoviePostersData;
    private Context mContext;

    public MoviePosterAdapter() {
        mMoviePostersData = MoviePoster.fetchMoviePosters();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View movieGridView = inflater.inflate(R.layout.movie_grid_item, parent, false);
        return new ViewHolder(movieGridView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.with(getContext())
                .load(MoviePoster.TEST_URL)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mMoviePostersData.size();
    }

    public Context getContext() {
        return mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.movie_grid_image) ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
