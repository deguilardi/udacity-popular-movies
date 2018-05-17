package com.guilardi.popularmovies;

import android.app.Activity;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.guilardi.popularmovies.data.Movie;
import com.guilardi.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by deguilardi on 5/14/18.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieAdapterViewHolder>{

    Activity mContext;
    private JSONArray mData;
    final private MovieAdapterOnClickHandler mClickHandler; // @TODO

    private static final String J_RESULTS_VOTE_COUNT = "vote_count";
    private static final String J_RESULTS_ID = "id";
    private static final String J_RESULTS_VIDEO = "video";
    private static final String J_RESULTS_VOTE_AVARAGE = "vote_average";
    private static final String J_RESULTS_TITLE = "title";
    private static final String J_RESULTS_POPULARITY = "popularity";
    private static final String J_RESULTS_POSTER_PATH = "poster_path";
    private static final String J_RESULTS_ORIGINAL_LANGUAGE = "original_language";
    private static final String J_RESULTS_ORIGINAL_TITLE = "original_title";
    private static final String J_RESULTS_BACKDROP_PATH = "backdrop_path";
    private static final String J_RESULTS_ADULT = "adult";
    private static final String J_RESULTS_OVERVIEW = "overview";
    private static final String J_RESULTS_RELEASE_DATE = "release_date";

    public interface MovieAdapterOnClickHandler {
        void onClick(long date);
    }

    public MoviesAdapter(@NonNull Activity context, MovieAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movies_list_item, viewGroup, false);
        view.setFocusable(true);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder movieAdapterViewHolder, int position) {

        // define the image size based on device size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int sizeW = (int) (screenWidth / 2);
        int sizeH = (int) (sizeW * 1.63);

        // parse te result
        try {
            JSONObject movieJson = mData.getJSONObject(position);
            String posterPath = movieJson.getString(J_RESULTS_POSTER_PATH);
            URL thumbURL = NetworkUtils.getThumbURL(posterPath, mContext);
            Picasso.with(mContext).load(thumbURL.toString())
                    .resize(sizeW, sizeH)
                    .centerCrop()
                    .into(movieAdapterViewHolder.thumbView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null) return 0;
        return mData.length();
    }

    public void swapData(JSONArray data) {
        mData = data;
        notifyDataSetChanged();
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.thumb_view) ImageView thumbView;

        MovieAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public void onClick(View v) {
        }
    }
}
