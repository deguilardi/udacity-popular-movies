package com.guilardi.popularmovies.sync;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by deguilardi on 5/14/18.
 */

public class MoviesSyncIntentService extends IntentService {
    public MoviesSyncIntentService() {
        super("MoviesSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MoviesSyncTask.syncMovies(this);
    }
}
