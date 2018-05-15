package com.guilardi.popularmovies.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.guilardi.popularmovies.Config;
import com.guilardi.popularmovies.data.Movie;

import java.util.concurrent.TimeUnit;

/**
 * Created by deguilardi on 5/14/18.
 */

public class MoviesSyncUtils {
    private static final int SYNC_INTERVAL_HOURS = Config.SYNC_INTERVAL;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

    private static boolean sInitialized;
    private static final String SYNC_TAG = "movies-sync";

    static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job syncMoviesJob = dispatcher.newJobBuilder()
                .setService(MoviesFirebaseJobService.class)
                .setTag(SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(syncMoviesJob);
    }

    synchronized public static void initialize(@NonNull final Context context) {
        if (sInitialized){
            return;
        }
        sInitialized = true;
        scheduleFirebaseJobDispatcherSync(context);

        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                String selectionStatement = Movie.MovieEntry.getSqlSelectForList();

                Cursor cursor = context.getContentResolver().query(
                        Movie.MovieEntry.CONTENT_URI,
                        new String[]{Movie.MovieEntry.COLUMN_ID},
                        selectionStatement,
                        null,
                        null);

                if (cursor == null || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }
                if(cursor != null) {
                    cursor.close();
                }
            }
        });

        checkForEmpty.start();
    }

    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, MoviesSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}
