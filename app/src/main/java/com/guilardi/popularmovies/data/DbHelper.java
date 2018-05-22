package com.guilardi.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.guilardi.popularmovies.Config;

/**
 * Created by deguilardi on 5/14/18.
 */

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, Config.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE =
                "CREATE TABLE " + Movie.MovieEntry.TABLE_NAME + " (" +
                        Movie.MovieEntry.COLUMN_ID                + " INTEGER NOT NULL PRIMARY KEY, " +
                        Movie.MovieEntry.COLUMN_VOTE_COUNT        + " INTEGER NOT NULL, " +
                        Movie.MovieEntry.COLUMN_HAS_VIDEO         + " NUMERIC NOT NULL, " +
                        Movie.MovieEntry.COLUMN_VOTE_AVARAGE      + " INTEGER NOT NULL, " +
                        Movie.MovieEntry.COLUMN_TITLE             + " TEXT NOT NULL, " +
                        Movie.MovieEntry.COLUMN_POPULARITY        + " REAL NOT NULL, " +
                        Movie.MovieEntry.COLUMN_POSTER_PATH       + " TEXT NOT NULL, " +
                        Movie.MovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT NOT NULL, " +
                        Movie.MovieEntry.COLUMN_ORIGINAL_TITLE    + " TEXT NOT NULL, " +
                        Movie.MovieEntry.COLUMN_BACKDROP_PATH     + " TEXT NOT NULL, " +
                        Movie.MovieEntry.COLUMN_IS_ADULT          + " NUMERIC NOT NULL, " +
                        Movie.MovieEntry.COLUMN_OVERVIEW          + " TEXT NOT NULL, " +
                        Movie.MovieEntry.COLUMN_RELEASE_DATE      + " TEXT NOT NULL, " +
                        Movie.MovieEntry.COLUMN_IS_FAVORITE       + " NUMERIC NOT NULL, " +
                        " UNIQUE (" + Movie.MovieEntry.COLUMN_ID + ") ON CONFLICT REPLACE);";
        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Movie.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
