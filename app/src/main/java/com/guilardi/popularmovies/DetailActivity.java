package com.guilardi.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.guilardi.popularmovies.data.Movie;
import com.guilardi.popularmovies.data.Reviews;
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
        implements TrailersAdapter.TrailersAdapterOnClickHandler, ReviewsAdapter.ReviewsAdapterOnClickHandler{

    private Movie mMovie;

    private TrailersAdapter mTrailersAdapter;
    private int mTrailersPosition;
    @BindView(R.id.recyclerview_trailers_list) RecyclerView mTrailersRecyclerView;
    @BindView(R.id.pb_trailers_loading) ProgressBar mTrailersLoadingIndicator;

    private ReviewsAdapter mReviewsAdapter;
    private int mReviewsPosition;
    @BindView(R.id.recyclerview_reviews_list) RecyclerView mReviewsRecyclerView;
    @BindView(R.id.pb_reveiews_loading) ProgressBar mReviewsLoadingIndicator;

    @BindView(R.id.thumb_view) ImageView mThumbView;
    @BindView(R.id.year) TextView mYear;
    @BindView(R.id.vote_avarage) TextView mVoteAvarage;
    @BindView(R.id.overview) TextView mOverview;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.title) TextView mTitle;

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

        // start layout and adapters
        mTrailersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTrailersAdapter = new TrailersAdapter(this, this);
        mTrailersRecyclerView.setAdapter(mTrailersAdapter);
        mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReviewsAdapter = new ReviewsAdapter(this, this);
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);

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
            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {
                Toast.makeText(getParent(), "Something went wrong, please check your internet connection and try again!", Toast.LENGTH_LONG).show();
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
                .into(mThumbView);
    }

    @Override
    public void onClick(int position, TrailersAdapter.TrailersAdapterViewHolder adapterViewHolder) {

    }

    @Override
    public void onClick(int position, ReviewsAdapter.ReviewsAdapterViewHolder adapterViewHolder) {

    }
}
