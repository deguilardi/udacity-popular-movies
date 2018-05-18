package com.guilardi.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.guilardi.popularmovies.data.Movie;
import com.guilardi.popularmovies.data.Movies;
import com.guilardi.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by deguilardi on 5/18/18.
 */

public class DetailActivity extends AppCompatActivity {

    private Movie mMovie;

    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.title) TextView mTitle;
    @BindView(R.id.thumb_view) ImageView mThumbView;
    @BindView(R.id.year) TextView mYear;
    @BindView(R.id.duration) TextView mDuration;
    @BindView(R.id.vote_avarage) TextView mVoteAvarage;
    @BindView(R.id.overview) TextView mOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // extra binds
        ButterKnife.bind(this);

        // get the movieID
        Bundle b = getIntent().getExtras();
        if(b != null){
            int position = b.getInt("position");
            mMovie = Movies.getInstance().getMovieAtPosition(position);
            bindViewValues();
        }
        else{
            Toast.makeText(this, "An error has occurred. Pleas try again later", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * bind the view values with the mMovie object
     */
    private void bindViewValues(){
        mTitle.setText(mMovie.getTitle());
        mYear.setText(mMovie.getYear());
        mDuration.setText(mMovie.getDurationNormalized());
        mOverview.setText(mMovie.getOverview());
        mVoteAvarage.setText(mMovie.getVoteAvarageNormalized());

        // poster image
        String posterPath = mMovie.getPosterPath();
        URL thumbURL = NetworkUtils.getThumbURL(posterPath, this);
        Picasso.with(this).load(thumbURL.toString())
                .into(mThumbView);
    }
}
