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

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private MoviesAdapter mMoviesAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    private String mShowBy;

    @BindView(R.id.recyclerview_movies_list) RecyclerView mRecyclerView;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // define views
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

    @Override
    public void onClick(long date) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

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

    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showMoviesDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

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
                    JSONArray data = responseJSON.getJSONArray(J_RESULTS);
                    mMoviesAdapter.swapData(data);
                    if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
                    mRecyclerView.smoothScrollToPosition(mPosition);
                    if (data.length() != 0) showMoviesDataView();
                } catch (JSONException e){
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    }
}
