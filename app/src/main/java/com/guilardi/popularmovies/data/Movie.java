package com.guilardi.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by deguilardi on 5/14/18.
 */

public class Movie {
    public static final String CONTENT_AUTHORITY = "com.guilardi.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_GENRES = "genres";

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

        public static String getSqlSelectForList() {
            return "";
        }
    }

    public static final class GenreEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_GENRES)
                .build();

        public static final String TABLE_NAME = "genres";
        public static final String COLUMN_ID = "id";
    }
}
