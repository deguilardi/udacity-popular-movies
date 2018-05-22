package com.guilardi.popularmovies.data;

/**
 * Created by deguilardi on 5/22/18.
 */

public class Reviews {

    private Integer id;
    private Review[] results = new Review[0];

    private Reviews() {}

    public Review getReviewAtPosition(int position){
        return results[position];
    }

    public int length(){
        return results.length;
    }
}
