package com.guilardi.popularmovies.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by deguilardi on 5/14/18.
 */

public class MoviesFirebaseJobService extends JobService {

    private AsyncTask<Void, Void, Void> mFetchMoviesTask;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        mFetchMoviesTask = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                MoviesSyncTask.syncMovies(context);
                jobFinished(jobParameters, false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(jobParameters, false);
            }
        };

        mFetchMoviesTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mFetchMoviesTask != null) {
            mFetchMoviesTask.cancel(true);
        }
        return true;
    }
}
