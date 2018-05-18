package com.guilardi.popularmovies.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by deguilardi on 5/18/18.
 *
 * Singleton class to hold the movie list data
 */

public class Movies {

    private static final String TAG = Movies.class.getSimpleName();
    private static Movies instance = null;
    private Movie[] mData = new Movie[0];

    private Movies(JSONArray input){
        try {
            mData = new Movie[input.length()];
            for (int i = 0; i < input.length(); i++) {
                mData[i] = new Movie(input.getJSONObject(i));
            }
        }
        catch(JSONException e){
            Log.e(TAG, e.getMessage());
        }
    }

    private Movies() {

    }

    public static void initWithJSONArray(JSONArray input){
        instance = new Movies(input);
    }

    public static Movies getInstance(){
        if (instance == null) {
            instance = new Movies();
        }
        return instance;
    }

    public Movie getMovieAtPosition(int position){
        return mData[position];
    }

    public static int length(){
        return getInstance().mData.length;
    }
}
