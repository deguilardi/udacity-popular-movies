package com.guilardi.popularmovies;

/**
 * INSTRUCTIONS:
 * 1. Make a copy of this file Config.java
 * 2. replace the YOUR_API_KEY whith your movies db's api key
 */

public class Config_sample {

    // will try to resync with server every SYNC_INTERVAL hours
    // defined in hours
    public static int SYNC_INTERVAL = 3;

    // the number of columns on the home screen list
    public static int HOME_LIST_NUM_COLUMNS = 2;

    // movies db api key
    // @see https://developers.themoviedb.org/3/getting-started/introduction
    public static String MOVIE_DB_API_KEY = "YOUR_API_KEY";
}
