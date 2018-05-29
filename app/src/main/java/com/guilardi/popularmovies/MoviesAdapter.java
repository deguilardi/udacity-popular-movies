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
import com.guilardi.popularmovies.data.Movies;
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
    private Movies mData;
    final private MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler {
        void onClick(int position, MovieAdapterViewHolder adapterViewHolder);
    }

    public MoviesAdapter(@NonNull Activity context, MovieAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movies_list_item, viewGroup, false);
        view.setFocusable(true);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapterViewHolder movieAdapterViewHolder, int position) {

        // define the image size based on device size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int sizeW = (int) (screenWidth / 2);
        int sizeH = (int) (sizeW * 1.63);

        // parse te result
        Movie movie = mData.getMovieAtPosition(position);
        String posterPath = movie.getPoster_path();
        URL thumbURL = NetworkUtils.getThumbURL(posterPath, mContext, Config.HOME_LIST_NUM_COLUMNS);
        Picasso.with(mContext).load(thumbURL.toString())
                .resize(sizeW, sizeH)
                .centerCrop()
                .into(movieAdapterViewHolder.thumbView);
    }

    @Override
    public int getItemCount() {
        if (mData == null) return 0;
        return mData.length();
    }

    public void swapData(Movies data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void swapData(Cursor cursor){
        mData = new Movies(cursor);
        notifyDataSetChanged();
    }

    public Movies getData(){
        return mData;
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.thumb_view) ImageView thumbView;

        MovieAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mClickHandler.onClick(position, this);
        }

        public ImageView getThumbView(){
            return thumbView;
        }
    }
}
