package com.guilardi.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.guilardi.popularmovies.data.Movie;
import com.guilardi.popularmovies.data.Movies;
import com.guilardi.popularmovies.utilities.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity
        extends AppCompatActivity
        implements MoviesAdapter.MovieAdapterOnClickHandler, SharedPreferences.OnSharedPreferenceChangeListener {

    private MoviesAdapter mMoviesAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    private String mShowBy;
    private boolean mHasSelection;

    private static String SAVED_SCROLL_POSITION = "savedScrollPosition";

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

        // define num of columns based on device orientation
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int numColumns = Config.HOME_LIST_NUM_COLUMNS;
        numColumns = display.getRotation() == Surface.ROTATION_0 || display.getRotation() == Surface.ROTATION_180
                ? numColumns
                : (int) (numColumns * Config.HOME_LIST_COLUMNS_RATIO);

        // start layout and adapter
        GridLayoutManager layoutManager = new GridLayoutManager(this, numColumns);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMoviesAdapter = new MoviesAdapter(this, this);
        mRecyclerView.setAdapter(mMoviesAdapter);

        // start/load data
        setupSharedPreferences();
        if(!mShowBy.equals("favorite")){
            loadMoviesData();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        mHasSelection = false;

        if(mShowBy.equals("favorite")){
            loadMoviesData();
        }
    }

    private void loadMoviesData(){
        final Context context = this;
        Callback<Movies> callback = new Callback<Movies>(){

            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {
                Movies movies = response.body();
                mMoviesAdapter.swapData(movies);
                if (mPosition == RecyclerView.NO_POSITION){
                    mPosition = 0;
                }
                if (movies != null && movies.length() != 0) {
                    showMoviesDataView();

                    // save the returned value to the database
                    ContentValues[] contentValues = movies.getContentValues();
                    if (contentValues != null && contentValues.length != 0) {
                        ContentResolver contentResolver = context.getContentResolver();
                        contentResolver.delete(Movie.MovieEntry.CONTENT_URI,
                                Movie.MovieEntry.COLUMN_IS_FAVORITE + "=?", new String[]{"false"});
                        contentResolver.bulkInsert(Movie.MovieEntry.CONTENT_URI, contentValues);
                    }
                }
            }

            @Override
            public void onFailure(Call<Movies> call, Throwable t) {
                Toast.makeText(context, "Something went wrong, please check your internet connection and try again!", Toast.LENGTH_LONG).show();
            }
        };

        if(mShowBy.equals("favorite")){
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(Movie.MovieEntry.CONTENT_URI,
                    new String[]{Movie.MovieEntry.COLUMN_ID,
                            Movie.MovieEntry.COLUMN_TITLE,
                            Movie.MovieEntry.COLUMN_POSTER_PATH,
                            Movie.MovieEntry.COLUMN_VOTE_AVARAGE,
                            Movie.MovieEntry.COLUMN_OVERVIEW,
                            Movie.MovieEntry.COLUMN_RELEASE_DATE,
                            Movie.MovieEntry.COLUMN_IS_FAVORITE},
                    Movie.MovieEntry.COLUMN_IS_FAVORITE+"=?",
                    new String[]{"1"},
                    null);

            int count = 0;
            if(cursor != null){
                count =  cursor.getCount();
                mMoviesAdapter.swapData(cursor);
                cursor.close();
            }
            if(count == 0){
                Toast.makeText(this, "There is no favorite movie", Toast.LENGTH_LONG).show();
            }
        }
        else {
            NetworkUtils.getInstance().loadMovies(mShowBy, callback);
            showLoading();
        }
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
    public void onClick(int position, MoviesAdapter.MovieAdapterViewHolder adapterViewHolder) {
        if(mHasSelection){
            return;
        }
        mHasSelection = true;
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,
                adapterViewHolder.getThumbView(),
                getString(R.string.transition_thumb)
            );

        Intent detailsActivityIntent = new Intent(MainActivity.this, DetailActivity.class);
        detailsActivityIntent.putExtra("movie", mMoviesAdapter.getData().getMovieAtPosition(position));
        startActivity(detailsActivityIntent, options.toBundle());
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

        // scroll to the right position
        // delays to wait for the elements to be on the screen
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.smoothScrollToPosition(mPosition);
            }}, 100);
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

    @Override
    protected void onSaveInstanceState(Bundle outState){

        int offset = Config.HOME_LIST_NUM_COLUMNS;
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        offset = display.getRotation() == Surface.ROTATION_90 || display.getRotation() == Surface.ROTATION_270
                ? offset
                : (int) ((offset + 1) * Config.HOME_LIST_COLUMNS_RATIO);

        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        outState.putInt(SAVED_SCROLL_POSITION, layoutManager.findFirstVisibleItemPosition() + offset);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt(SAVED_SCROLL_POSITION);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
}
