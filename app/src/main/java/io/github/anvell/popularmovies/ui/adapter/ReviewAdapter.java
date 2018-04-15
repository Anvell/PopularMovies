package io.github.anvell.popularmovies.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.anvell.popularmovies.R;
import io.github.anvell.popularmovies.web.MovieReview;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private ArrayList<MovieReview> mReviews;

    public ReviewAdapter(ArrayList<MovieReview> reviews) {
        setHasStableIds(true);
        mReviews = reviews;
    }

    @Override
    public long getItemId(int position) {
        return mReviews.get(position).hashCode();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        MovieReview item = mReviews.get(position);
        holder.mAuthorView.setText(item.author);
        holder.mContentView.setText(item.content);
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_review_author) TextView mAuthorView;
        @BindView(R.id.tv_review_content) TextView mContentView;
        @BindView(R.id.ll_review_root) LinearLayout mViewRoot;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> {
                TextView tv = mContentView;
                if(tv.getMaxLines() < 5) {
                    tv.setMaxLines(Integer.MAX_VALUE);
                    tv.setEllipsize(null);
                } else {
                    tv.setMaxLines(4);
                    tv.setEllipsize(TextUtils.TruncateAt.END);
                }
            });
        }

    }

    public void updateDataSet(ArrayList<MovieReview> reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }
}
