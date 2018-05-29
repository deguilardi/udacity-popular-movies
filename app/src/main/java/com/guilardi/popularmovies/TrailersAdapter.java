package com.guilardi.popularmovies;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guilardi.popularmovies.data.Trailer;
import com.guilardi.popularmovies.data.Trailers;
import com.guilardi.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by deguilardi on 5/22/18.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersAdapterViewHolder> {

    Activity mContext;
    private Trailers mData;
    final private TrailersAdapterOnClickHandler mClickHandler;

    public interface TrailersAdapterOnClickHandler {
        void onClick(int position, TrailersAdapterViewHolder adapterViewHolder);
    }

    public TrailersAdapter(@NonNull Activity context, TrailersAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public TrailersAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.trailers_list_item, viewGroup, false);
        view.setFocusable(true);
        return new TrailersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailersAdapterViewHolder adapterViewHolder, int position) {
        Trailer trailer = mData.getTrailerAtPosition(position);
        adapterViewHolder.title.setText(trailer.getName());

        URL thumbURL = NetworkUtils.getYoutubeThumbURL(trailer);
        Picasso.with(mContext).load(thumbURL.toString())
                .fit()
                .centerCrop()
                .into(adapterViewHolder.thumb);
    }

    @Override
    public int getItemCount() {
        if (mData == null) return 0;
        return mData.length();
    }

    public void swapData(Trailers data) {
        mData = data;
        notifyDataSetChanged();
    }

    public Trailers getData(){
        return mData;
    }

    class TrailersAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.title) TextView title;
        @BindView(R.id.thumb) ImageView thumb;

        TrailersAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mClickHandler.onClick(position, this);
        }
    }
}
