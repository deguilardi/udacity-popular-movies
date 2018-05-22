package com.guilardi.popularmovies.data;

/**
 * Created by deguilardi on 5/18/18.
 *
 * Singleton class to hold the movie list data
 */

public class Movies {

    private static final String TAG = Movies.class.getSimpleName();

    private Integer page;
    private Integer total_results;
    private Integer total_pages;
    private Movie[] results = new Movie[0];

    private Movies() {}

    public Movie getMovieAtPosition(int position){
        return results[position];
    }

    public int length(){
        return results.length;
    }
}
