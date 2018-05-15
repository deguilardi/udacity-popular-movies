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

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by deguilardi on 5/14/18.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieAdapterViewHolder>{

    Activity mContext;
    private Cursor mCursor;
    final private MovieAdapterOnClickHandler mClickHandler; // @TODO

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

        // define the image size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int sizeW = (int) (screenWidth / 2);
        int sizeH = (int) (sizeW * 1.63);

        mCursor.moveToPosition(position);
        String posterPath = mCursor.getString(mCursor.getColumnIndex(Movie.MovieEntry.COLUMN_POSTER_PATH));
        URL thumbURL = NetworkUtils.getThumbURL(posterPath, mContext);
        Picasso.with(mContext).load(thumbURL.toString())
                .resize(sizeW, sizeH)
                .centerCrop()
                .into(movieAdapterViewHolder.thumbView);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
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
