package com.guilardi.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.guilardi.popularmovies.data.Movies;
import com.guilardi.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity
        extends AppCompatActivity
        implements MoviesAdapter.MovieAdapterOnClickHandler, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MoviesAdapter mMoviesAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    private String mShowBy;

    @BindView(R.id.recyclerview_movies_list) RecyclerView mRecyclerView;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // extra binds
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        // start layout and adapter
        LinearLayoutManager layoutManager = new GridLayoutManager(this, Config.HOME_LIST_NUM_COLUMNS);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMoviesAdapter = new MoviesAdapter(this, this);
        mRecyclerView.setAdapter(mMoviesAdapter);

        // start/load data
        setupSharedPreferences();
        loadMoviesData();
    }

    private void loadMoviesData(){
        new MoviesSyncTask().execute(mShowBy);
        showLoading();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Responds to main menu selections
     *
     * @param item The item selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Responds to clicks from the list.
     *
     * @param position The position of the selected item
     */
    @Override
    public void onClick(int position) {
        Intent detailsActivityIntent = new Intent(MainActivity.this, DetailActivity.class);
        detailsActivityIntent.putExtra("position", position);
        startActivity(detailsActivityIntent);
    }

    /**
     * Called when any shared preference is changed
     *
     * @param sharedPreferences Changed SharedPreferences object
     * @param key The key of the preference changed
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.pref_show_by_key))){
            mShowBy = sharedPreferences.getString(getString(R.string.pref_show_by_key),
                    getResources().getString(R.string.pref_show_by_default));
            loadMoviesData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Show the loading spinning image
     */
    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    /**
     * Show the list content and hide the loading
     */
    private void showMoviesDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Start the shared preferences, loading its values
     */
    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mShowBy = sharedPreferences.getString(getString(R.string.pref_show_by_key),
                getResources().getString(R.string.pref_show_by_default));

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    class MoviesSyncTask extends AsyncTask<String, Void, String> {
        private static final String J_RESULTS = "results";

        @Override
        protected String doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String listType = params[0];

            try {
                URL requestUrl = NetworkUtils.getMoviesListURLWithType(listType);
                return NetworkUtils.getResponseFromHttpUrl(requestUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                try {
                    JSONObject responseJSON = new JSONObject(response);
                    JSONArray dataArray = responseJSON.getJSONArray(J_RESULTS);
                    Movies.initWithJSONArray(dataArray);
                    mMoviesAdapter.swapData(Movies.getInstance());
                    if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
                    mRecyclerView.smoothScrollToPosition(mPosition);
                    if (Movies.length() != 0){
                        showMoviesDataView();
                    }
                } catch (JSONException e){
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    }
}
