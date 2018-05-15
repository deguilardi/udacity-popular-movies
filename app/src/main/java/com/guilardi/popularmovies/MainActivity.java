package com.guilardi.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.guilardi.popularmovies.data.Movie;
import com.guilardi.popularmovies.sync.MoviesSyncUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity
        extends AppCompatActivity
        implements MoviesAdapter.MovieAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ID_MOVIES_LOADER = 1000;

    private MoviesAdapter mMoviesAdapter;
    private int mPosition = RecyclerView.NO_POSITION;

    @BindView(R.id.recyclerview_movies_list) RecyclerView mRecyclerView;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        LinearLayoutManager layoutManager = new GridLayoutManager(this, Config.HOME_LIST_NUM_COLUMNS);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMoviesAdapter = new MoviesAdapter(this, this);
        mRecyclerView.setAdapter(mMoviesAdapter);
        showLoading();
        getSupportLoaderManager().initLoader(ID_MOVIES_LOADER, null, this);
        MoviesSyncUtils.initialize(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(long date) {

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {

            case ID_MOVIES_LOADER:
                Uri forecastQueryUri = Movie.MovieEntry.CONTENT_URI;
                String sortOrder = Movie.MovieEntry.COLUMN_VOTE_AVARAGE + " DESC";
                String selection = Movie.MovieEntry.getSqlSelectForList();

                return new CursorLoader(this,
                        forecastQueryUri,
                        new String[]{Movie.MovieEntry.COLUMN_ID,
                                Movie.MovieEntry.COLUMN_POSTER_PATH},
                        selection,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mMoviesAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0) showMoviesDataView();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(null);
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showMoviesDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}
