package io.github.anvell.popularmovies.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.anvell.popularmovies.R;
import io.github.anvell.popularmovies.web.MovieItem;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private ArrayList<MovieItem> mMovieItems;
    private Context mContext;
    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public MovieAdapter(ArrayList<MovieItem> movieItems) {
        setHasStableIds(true);
        mMovieItems = movieItems;
    }

    @Override
    public long getItemId(int position) {
        return mMovieItems.get(position).posterPath.hashCode();
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

        int posterWidth = mContext.getResources().getInteger(R.integer.poster_width);
        String posterUrl = mContext.getString(R.string.images_base_path)
                           + String.valueOf(posterWidth)
                           + mMovieItems.get(position).posterPath;

        Picasso.with(getContext())
                .load(posterUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mMovieItems.size();
    }

    private Context getContext() {
        return mContext;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.movie_grid_image) ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener((View view) -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(itemView, position);
                    }
                }
            });
        }
    }

}
