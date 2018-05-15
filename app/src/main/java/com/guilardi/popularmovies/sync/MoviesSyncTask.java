package com.guilardi.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.text.format.DateUtils;

import com.guilardi.popularmovies.data.Movie;
import com.guilardi.popularmovies.utilities.MoviesDBJsonUtils;
import com.guilardi.popularmovies.utilities.NetworkUtils;

import java.net.URL;

/**
 * Created by deguilardi on 5/14/18.
 */

public class MoviesSyncTask {
    synchronized public static void syncMovies(Context context) {

        try {
            URL requestUrl = NetworkUtils.getMoviesListURL();
            String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);
            ContentValues[] mooviesValues = MoviesDBJsonUtils.getMoviesContentValuesFromJson(context, jsonWeatherResponse);

            if (mooviesValues != null && mooviesValues.length != 0) {
                ContentResolver contentResolver = context.getContentResolver();
                contentResolver.delete(Movie.MovieEntry.CONTENT_URI,null,null);
                contentResolver.bulkInsert(Movie.MovieEntry.CONTENT_URI, mooviesValues);

                boolean notificationsEnabled = true; // @TODO MyPreferences.areNotificationsEnabled(context);
                long timeSinceLastNotification = 0; // @TODO MyPreferences.getEllapsedTimeSinceLastNotification(context);
                boolean oneDayPassedSinceLastNotification = false;
                if (timeSinceLastNotification >= DateUtils.HOUR_IN_MILLIS) {
                    oneDayPassedSinceLastNotification = true;
                }
                if (notificationsEnabled && oneDayPassedSinceLastNotification) {
                    // @TODO
//                    NotificationUtils.notifyUserOfNewMoviesList(context);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
