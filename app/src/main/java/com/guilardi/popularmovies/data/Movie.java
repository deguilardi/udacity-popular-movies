package com.guilardi.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by deguilardi on 5/14/18.
 *
 * This defines the movie object to transit in the app
 * Can be populated by JSON, Cursor or whatever other method
 */

public class Movie {

    private static final String TAG = Movie.class.getSimpleName();

    public static final String CONTENT_AUTHORITY = "com.guilardi.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";


    /**
     * JSON fields
     */
    private static final String J_VOTE_COUNT = "vote_count";
    private static final String J_ID = "id";
    private static final String J_VIDEO = "video";
    private static final String J_VOTE_AVARAGE = "vote_average";
    private static final String J_TITLE = "title";
    private static final String J_POPULARITY = "popularity";
    private static final String J_POSTER_PATH = "poster_path";
    private static final String J_ORIGINAL_LANGUAGE = "original_language";
    private static final String J_ORIGINAL_TITLE = "original_title";
    private static final String J_BACKDROP_PATH = "backdrop_path";
    private static final String J_ADULT = "adult";
    private static final String J_OVERVIEW = "overview";
    private static final String J_RELEASE_DATE = "release_date";


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
    }

    private int id;
    private String title;
    private String posterPath;
    private String year;
    private int duration;
    private double voteAvarage;
    private boolean favorite;
    private String overview;

    public Movie(JSONObject movieJson){
        try {
            id = movieJson.getInt(J_ID);
            title = movieJson.getString(J_TITLE);
            posterPath = movieJson.getString(J_POSTER_PATH);
            year =  movieJson.getString(J_RELEASE_DATE).substring(0, 4);
            duration = 123; // @TODO where is this info coming from?
            voteAvarage = movieJson.getDouble(J_VOTE_AVARAGE);
            favorite = false; // @TODO
            overview = movieJson.getString(J_OVERVIEW);
        }catch (JSONException e){
            Log.e(TAG, e.getMessage());
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getDuration() {
        return duration;
    }

    public String getDurationNormalized() {
        return String.valueOf(duration) + "min";
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getVoteAvarage() {
        return voteAvarage;
    }

    public String getVoteAvarageNormalized() {
        return String.valueOf(voteAvarage) + "/10";
    }

    public void setVoteAvarage(double voteAvarage) {
        this.voteAvarage = voteAvarage;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }
}
