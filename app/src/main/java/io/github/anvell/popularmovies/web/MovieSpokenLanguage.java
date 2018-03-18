
package io.github.anvell.popularmovies.web;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieSpokenLanguage {

    @SerializedName("iso_639_1")
    @Expose
    public String iso6391;
    @SerializedName("name")
    @Expose
    public String name;

}
