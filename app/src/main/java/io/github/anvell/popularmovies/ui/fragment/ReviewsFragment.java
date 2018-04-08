package io.github.anvell.popularmovies.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arellomobile.mvp.MvpFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.anvell.popularmovies.R;
import io.github.anvell.popularmovies.presentation.presenter.ReviewsPresenter;
import io.github.anvell.popularmovies.presentation.view.ReviewsView;
import io.github.anvell.popularmovies.ui.adapter.ReviewAdapter;
import io.github.anvell.popularmovies.web.MovieReview;

public class ReviewsFragment extends MvpFragment implements ReviewsView {

    @InjectPresenter
    ReviewsPresenter mReviewsPresenter;

    @BindView(R.id.rv_reviews) RecyclerView reviewsListView;
    @BindView(R.id.tv_reviews_title) TextView reviewsTitle;

    private int mMovieId;
    private ReviewAdapter mAdapter;

    public ReviewsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mMovieId = getArguments().getInt(getString(R.string.intent_movie_id));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        ButterKnife.bind(this, view);

        mAdapter = new ReviewAdapter(mReviewsPresenter.getReviews());
        reviewsListView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        reviewsListView.setAdapter(mAdapter);
        reviewsListView.setNestedScrollingEnabled(false);

        if(mMovieId != 0) mReviewsPresenter.fetchMovieReviews(mMovieId, ReviewsPresenter.ReviewsDirection.CURRENT);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void updateReviews(ArrayList<MovieReview> reviews) {
        mAdapter.updateDataSet(reviews);
        if(reviews.size() > 0) reviewsTitle.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }
}
