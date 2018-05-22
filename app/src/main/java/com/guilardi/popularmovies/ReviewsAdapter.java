package com.guilardi.popularmovies;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guilardi.popularmovies.data.Review;
import com.guilardi.popularmovies.data.Reviews;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by deguilardi on 5/22/18.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    Activity mContext;
    private Reviews mData;
    final private ReviewsAdapterOnClickHandler mClickHandler;

    public interface ReviewsAdapterOnClickHandler {
        void onClick(int position, ReviewsAdapterViewHolder adapterViewHolder);
    }

    public ReviewsAdapter(@NonNull Activity context, ReviewsAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.reviews_list_item, viewGroup, false);
        view.setFocusable(true);
        return new ReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapterViewHolder adapterViewHolder, int position) {
        Review review = mData.getReviewAtPosition(position);
        adapterViewHolder.content.setText(review.getContent());
        adapterViewHolder.author.setText(review.getAuthor());
    }

    @Override
    public int getItemCount() {
        if (mData == null) return 0;
        return mData.length();
    }

    public void swapData(Reviews data) {
        mData = data;
        notifyDataSetChanged();
    }

    public Reviews getData(){
        return mData;
    }

    class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.content) TextView content;
        @BindView(R.id.author) TextView author;

        ReviewsAdapterViewHolder(View view) {
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
