package com.guilardi.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.io.Serializable;

/**
 * Created by deguilardi on 5/14/18.
 *
 * This defines the movie object to transit in the app
 * Can be populated by JSON, Cursor or whatever other method
 */

public class Movie implements Serializable {

    public static final String CONTENT_AUTHORITY = "com.guilardi.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    /*
     * hold the ContentProvider and DB constraints
     */
    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES)
                .build();

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_HAS_VIDEO = "has_video";
        public static final String COLUMN_VOTE_AVARAGE = "vote_avarage";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_IS_ADULT = "is_adult";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_IS_FAVORITE = "is_favorite";
    }


    private Integer id;
    private String title;
    private String poster_path;
    private Double vote_average;
    private String overview;
    private String release_date;

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    private String year;
    private Boolean favorite;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public Double getVote_average() {
        return vote_average;
    }

    public void setVote_average(Double vote_average) {
        this.vote_average = vote_average;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getYear() {
        return release_date.substring(0, 4);
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }
}
