package com.guilardi.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by deguilardi on 5/18/18.
 *
 * Singleton class to hold the movie list data
 */

public class Movies {

    private Integer page;
    private Integer total_results;
    private Integer total_pages;
    private Movie[] results = new Movie[0];

    private Movies() {}

    public Movies(Cursor cursor){
        total_results = cursor.getCount();
        results = new Movie[total_results];
        if(total_results > 0) {
            cursor.moveToFirst();
            int i = 0;
            do {
                results[i] = new Movie(cursor);
                i++;
            } while (cursor.moveToNext());
        }
    }

    public Movie getMovieAtPosition(int position){
        return results[position];
    }

    public int length(){
        return results.length;
    }

    public ContentValues[] getContentValues(){
        ContentValues[] output = new ContentValues[length()];
        for (int i = 0; i < length(); i++) {
            Movie movie = getMovieAtPosition(i);
            ContentValues entry = new ContentValues();
            entry.put(Movie.MovieEntry.COLUMN_ID, movie.getId());
            entry.put(Movie.MovieEntry.COLUMN_VOTE_AVARAGE, movie.getVote_average());
            entry.put(Movie.MovieEntry.COLUMN_TITLE, movie.getTitle());
            entry.put(Movie.MovieEntry.COLUMN_POSTER_PATH, movie.getPoster_path());
            entry.put(Movie.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
            entry.put(Movie.MovieEntry.COLUMN_RELEASE_DATE, movie.getRelease_date());
            entry.put(Movie.MovieEntry.COLUMN_IS_FAVORITE, movie.getFavorite());
            output[i] = entry;
        }
        return  output;
    }
}
