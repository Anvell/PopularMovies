package io.github.anvell.popularmovies.web;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieReview {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("author")
    @Expose
    public String author;
    @SerializedName("content")
    @Expose
    public String content;
    @SerializedName("url")
    @Expose
    public String url;
}
