package com.guilardi.popularmovies.data;

/**
 * Created by deguilardi on 5/22/18.
 */

public class Trailers {

    private Integer id;
    private Trailer[] results = new Trailer[0];

    private Trailers() {}

    public Trailer getTrailerAtPosition(int position){
        return results[position];
    }

    public int length(){
        return results.length;
    }
}
