package io.github.anvell.popularmovies.web;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MovieReviews {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("page")
    @Expose
    public Integer page;
    @SerializedName("results")
    @Expose
    public ArrayList<MovieReview> reviews = new ArrayList<>();
    @SerializedName("total_pages")
    @Expose
    public Integer totalPages = 1;
    @SerializedName("total_results")
    @Expose
    public Integer totalResults;

}
