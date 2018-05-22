package com.guilardi.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.guilardi.popularmovies.data.Movie;
import com.guilardi.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by deguilardi on 5/18/18.
 */

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private Movie mMovie;

    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
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
}
