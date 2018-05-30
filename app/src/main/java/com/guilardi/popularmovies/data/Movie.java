package com.guilardi.popularmovies.data;

import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import java.io.Serializable;

/**
 * Created by deguilardi on 5/14/18.
 *
 * This defines the movie object to transit in the app
 * Can be populated by JSON, Cursor or whatever other method
 */

public class Movie implements Parcelable {

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
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_VOTE_AVARAGE = "vote_avarage";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_IS_FAVORITE = "is_favorite";
    }


    // remote fields
    private Integer id;
    private String title;
    private String poster_path;
    private Double vote_average;
    private String overview;
    private String release_date;

    // these fields aren't from remote json
    private String year;
    private Boolean favorite;

    public Movie(Cursor cursor){
        id = cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_ID));
        title = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_TITLE));
        poster_path = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH));
        vote_average = cursor.getDouble(cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_AVARAGE));
        overview = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW));
        release_date = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE));
        favorite = cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_IS_FAVORITE)) == 1;
    }

    protected Movie(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        title = in.readString();
        poster_path = in.readString();
        if (in.readByte() == 0) {
            vote_average = null;
        } else {
            vote_average = in.readDouble();
        }
        overview = in.readString();
        release_date = in.readString();
        year = in.readString();
        byte tmpFavorite = in.readByte();
        favorite = tmpFavorite == 0 ? null : tmpFavorite == 1;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(title);
        dest.writeString(poster_path);
        if (vote_average == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(vote_average);
        }
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(year);
        dest.writeByte((byte) (favorite == null ? 0 : favorite ? 1 : 2));
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

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
        return favorite != null ? favorite : false;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public Uri getContentUri(){
        return BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES)
                .appendPath(getId().toString())
                .build();
    }
}
