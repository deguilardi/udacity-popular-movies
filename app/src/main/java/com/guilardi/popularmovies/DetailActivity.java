package com.guilardi.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.guilardi.popularmovies.data.Movie;
import com.guilardi.popularmovies.data.Reviews;
import com.guilardi.popularmovies.data.Trailer;
import com.guilardi.popularmovies.data.Trailers;
import com.guilardi.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by deguilardi on 5/18/18.
 */

public class DetailActivity extends AppCompatActivity
        implements TrailersAdapter.TrailersAdapterOnClickHandler, ReviewsAdapter.ReviewsAdapterOnClickHandler, View.OnClickListener{

    private Movie mMovie;

    private TrailersAdapter mTrailersAdapter;
    private int mTrailersPosition;
    @BindView(R.id.recyclerview_trailers_list) RecyclerView mTrailersRecyclerView;
    @BindView(R.id.pb_trailers_loading) ProgressBar mTrailersLoadingIndicator;
    @BindView(R.id.trailers_error_message) TextView mTrailersErrorMessage;

    private ReviewsAdapter mReviewsAdapter;
    private int mReviewsPosition;
    @BindView(R.id.recyclerview_reviews_list) RecyclerView mReviewsRecyclerView;
    @BindView(R.id.pb_reveiews_loading) ProgressBar mReviewsLoadingIndicator;
    @BindView(R.id.reviews_error_message) TextView mReviewsErrorMessage;

    @BindView(R.id.main_content) CoordinatorLayout mMainContent;
    @BindView(R.id.thumb_view) ImageView mThumbView;
    @BindView(R.id.year) TextView mYear;
    @BindView(R.id.vote_avarage) TextView mVoteAvarage;
    @BindView(R.id.overview) TextView mOverview;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.title) TextView mTitle;
    @BindView(R.id.btn_favorite) Button mFavoriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // extra binds
        ButterKnife.bind(this);
        if(mToolbar != null) {
            setSupportActionBar(mToolbar);
            if(getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }

        // get the movieID
        Bundle b = getIntent().getExtras();
        if(b != null){
            mMovie = (Movie) b.get("movie");
            bindViewValues();
        }
        else{
            Toast.makeText(this, "An error has occurred. Pleas try again later", Toast.LENGTH_LONG).show();
        }

        // retrieve info from db
        // basically it's all to know if the movie was favorited or not
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(Movie.MovieEntry.CONTENT_URI,
                new String[]{Movie.MovieEntry.COLUMN_IS_FAVORITE},
                Movie.MovieEntry.COLUMN_ID+"=?",
                new String[]{mMovie.getId().toString()},
                null);
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            boolean isFavorite = cursor.getInt(0) == 1;
            mMovie.setFavorite(isFavorite);
            cursor.close();
            if(isFavorite){
                mFavoriteButton.setText(R.string.action_remove_from_favorites);
            }
        }

        // start layout and adapters
        mTrailersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTrailersAdapter = new TrailersAdapter(this, this);
        mTrailersRecyclerView.setAdapter(mTrailersAdapter);
        mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReviewsAdapter = new ReviewsAdapter(this, this);
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);

        // handle favorite button click
        mFavoriteButton.setOnClickListener(this);

        loadTrailersData();
        loadReviewsData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // this will make the animation work correctly
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void loadTrailersData(){
        Callback<Trailers> callback = new Callback<Trailers>(){

            @Override
            public void onResponse(Call<Trailers> call, Response<Trailers> response) {
                Trailers trailers = response.body();
                mTrailersAdapter.swapData(trailers);
                if (mTrailersPosition == RecyclerView.NO_POSITION){
                    mTrailersPosition = 0;
                }
                mTrailersRecyclerView.smoothScrollToPosition(mTrailersPosition);
                if (trailers != null && trailers.length() != 0) {
                    showTrailersDataView();
                }
                else{
                    showTrailersErrorMessage();
                }
            }

            @Override
            public void onFailure(Call<Trailers> call, Throwable t) {
                Toast.makeText(getParent(), "Something went wrong, please check your internet connection and try again!", Toast.LENGTH_LONG).show();
            }
        };
        NetworkUtils.getInstance().loadTrailers(mMovie, callback);
        showTrailersLoading();
    }

    private void loadReviewsData(){
        Callback<Reviews> callback = new Callback<Reviews>(){

            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                Reviews reviews = response.body();
                mReviewsAdapter.swapData(reviews);
                if (mReviewsPosition == RecyclerView.NO_POSITION){
                    mReviewsPosition = 0;
                }
                mReviewsRecyclerView.smoothScrollToPosition(mReviewsPosition);
                if (reviews != null && reviews.length() != 0) {
                    showReviewsDataView();
                }
                else{
                    showReviewsErrorMessage();
                }
            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {
                Toast.makeText(getParent(), R.string.message_something_wrong_internet, Toast.LENGTH_LONG).show();
            }
        };
        NetworkUtils.getInstance().loadReviews(mMovie, callback);
        showReviewsLoading();
    }

    /**
     * Show the loading spinning image
     */
    private void showTrailersLoading() {
        mTrailersRecyclerView.setVisibility(View.INVISIBLE);
        mTrailersLoadingIndicator.setVisibility(View.VISIBLE);
    }
    private void showReviewsLoading() {
        mReviewsRecyclerView.setVisibility(View.INVISIBLE);
        mReviewsLoadingIndicator.setVisibility(View.VISIBLE);
    }

    /**
     * Show the list content and hide the loading
     */
    private void showTrailersDataView() {
        mTrailersLoadingIndicator.setVisibility(View.INVISIBLE);
        mTrailersRecyclerView.setVisibility(View.VISIBLE);
    }
    private void showReviewsDataView() {
        mReviewsLoadingIndicator.setVisibility(View.INVISIBLE);
        mReviewsRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Show error messages
     */
    private void showTrailersErrorMessage(){
        mTrailersLoadingIndicator.setVisibility(View.INVISIBLE);
        mTrailersErrorMessage.setVisibility(View.VISIBLE);
    }
    private void showReviewsErrorMessage(){
        mReviewsLoadingIndicator.setVisibility(View.INVISIBLE);
        mReviewsErrorMessage.setVisibility(View.VISIBLE);
    }

    /**
     * bind the view values with the mMovie object
     */
    private void bindViewValues(){
        mTitle.setText(mMovie.getTitle());
        mYear.setText(mMovie.getYear());
        mOverview.setText(mMovie.getOverview());
        mVoteAvarage.setText(mMovie.getVote_average() + "/10");

        // poster image
        String posterPath = mMovie.getPoster_path();
        URL thumbURL = NetworkUtils.getThumbURL(posterPath, this, 4);
        Picasso.with(this).load(thumbURL.toString())
                .fit()
                .centerCrop()
                .error(R.drawable.image_not_found)
                .into(mThumbView);
    }

    // trailers click callback
    @Override
    public void onClick(int position, TrailersAdapter.TrailersAdapterViewHolder adapterViewHolder) {
        Intent youtubeIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
        Trailer trailer = mTrailersAdapter.getData().getTrailerAtPosition(position);
        String videoID = trailer.getKey();
        if (youtubeIntent != null) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + videoID)));
        }
        else{
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoID));
            startActivity(browserIntent);
        }
    }

    // reviews click callback
    @Override
    public void onClick(int position, ReviewsAdapter.ReviewsAdapterViewHolder adapterViewHolder) {

    }

    // buttons click callback
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.btn_favorite){

            final boolean isFavoriting = !mMovie.getFavorite();
            final int snackbarMessage;
            final int snackbarMessage2;
            if(isFavoriting){
                snackbarMessage = R.string.message_marked_as_favorite;
                snackbarMessage2 = R.string.message_removed_from_favorites;
                setFavorite(true);
            }
            else{
                snackbarMessage = R.string.message_removed_from_favorites;
                snackbarMessage2 = R.string.message_marked_as_favorite;
                setFavorite(false);
            }

            Snackbar snackbar = Snackbar
                    .make(mMainContent, snackbarMessage, Snackbar.LENGTH_LONG)
                    .setAction(R.string.action_undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View snackbar2View) {
                            if(isFavoriting){
                                setFavorite(false);
                            }
                            else{
                                setFavorite(true);
                            }
                            Snackbar.make(mMainContent, snackbarMessage2, Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                    });
            snackbar.show();
        }
    }

    private void setFavorite(boolean favorite){
        mMovie.setFavorite(favorite);
        if(favorite){
            mFavoriteButton.setText(R.string.action_remove_from_favorites);
        }
        else{
            mFavoriteButton.setText(R.string.action_mark_as_favorite);
        }

        ContentResolver contentResolver = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Movie.MovieEntry.COLUMN_IS_FAVORITE, favorite);
        contentResolver.update(mMovie.getContentUri(),
                values,
                Movie.MovieEntry.COLUMN_ID+"=?",
                new String[]{mMovie.getId().toString()});
    }
}
