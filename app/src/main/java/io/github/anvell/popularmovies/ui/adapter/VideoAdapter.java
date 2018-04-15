package io.github.anvell.popularmovies.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.anvell.popularmovies.R;
import io.github.anvell.popularmovies.web.MovieVideo;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder>{

    private static final String DEFAULT_SITE = "YouTube";

    private final ArrayList<MovieVideo> mVideoItems;
    private Context mContext;
    private VideoAdapter.OnItemClickListener mListener;

    public void setOnItemClickListener(VideoAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public VideoAdapter(ArrayList<MovieVideo> videos) {
        setHasStableIds(true);
        mVideoItems = videos;
    }

    @Override
    public long getItemId(int position) {
        return mVideoItems.get(position).hashCode();
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View videoCardView = inflater.inflate(R.layout.video_item, parent, false);

        return new VideoAdapter.ViewHolder(videoCardView);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.ViewHolder holder, int position) {

        if(position >= 0 && position < mVideoItems.size()
                && mVideoItems.get(position).site.equals(DEFAULT_SITE)) {
            MovieVideo video = mVideoItems.get(position);
            holder.captionTextView.setText(video.name);

            Picasso.with(getContext())
                    .load(getYoutubeUrl(video.key))
                    .resize(0, 100)
                    .into(holder.imageView);
        } else
            holder.itemView.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mVideoItems.size();
    }

    private Context getContext() {
        return mContext;
    }

    private String getYoutubeUrl(String id) {
        return getContext().getString(R.string.youtube_base_path) + id +
               getContext().getString(R.string.youtube_image_path);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_video_thumbnail) ImageView imageView;
        @BindView(R.id.tv_video_title) TextView captionTextView;

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
