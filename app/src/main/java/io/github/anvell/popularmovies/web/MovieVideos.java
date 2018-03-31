package io.github.anvell.popularmovies.web;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MovieVideos {
    @SerializedName("results")
    @Expose
    public ArrayList<MovieVideo> results = null;
}
