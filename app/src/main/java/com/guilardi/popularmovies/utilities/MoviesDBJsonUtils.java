package com.guilardi.popularmovies.utilities;

import android.content.ContentValues;
import android.content.Context;

import com.guilardi.popularmovies.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by deguilardi on 5/14/18.
 */


// JSON example
//{"page":1,
// "total_results":19858,
// "total_pages":993,
// "results":[
//     {"vote_count":1732,
//      "id":337167,
//      "video":false,
//      "vote_average":6,
//      "title":"Fifty Shades Freed",
//      "popularity":635.02367,
//      "poster_path":"\/jjPJ4s3DWZZvI4vw8Xfi4Vqa1Q8.jpg",
//      "original_language":"en",
//      "original_title":"Fifty Shades Freed",
//      "genre_ids":[18,10749],
//      "backdrop_path":"\/9ywA15OAiwjSTvg3cBs9B7kOCBF.jpg",
//      "adult":false,
//      "overview":"Believing they have left behind shadowy figures from their past, newlyweds Christian and Ana fully embrace an inextricable connection and shared life of luxury. But just as she steps into her role as Mrs. Grey and he relaxes into an unfamiliar stability, new threats could jeopardize their happy ending before it even begins.",
//      "release_date":"2018-02-07"},
//     {...},{...},{...}
// ]
//}

public class MoviesDBJsonUtils {

    private static final String J_RESULTS = "results";
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
    private static final String J_RESULTS_GENRE_IDS = "genre_ids";
    private static final String J_RESULTS_RELEASE_DATE = "release_date";

    public static ContentValues[] getMoviesContentValuesFromJson(Context context, String inputJsonString) throws JSONException {
        JSONObject mainJson = new JSONObject(inputJsonString);
        JSONArray resultsJsonArray = mainJson.getJSONArray(J_RESULTS);

        ContentValues[] output = new ContentValues[resultsJsonArray.length()];
        for (int i = 0; i < resultsJsonArray.length(); i++) {
            JSONObject movieJson = resultsJsonArray.getJSONObject(i);

            ContentValues movieValues = new ContentValues();
            movieValues.put(Movie.MovieEntry.COLUMN_VOTE_COUNT, movieJson.getInt(J_RESULTS_VOTE_COUNT));
            movieValues.put(Movie.MovieEntry.COLUMN_ID, movieJson.getInt(J_RESULTS_ID));
            movieValues.put(Movie.MovieEntry.COLUMN_HAS_VIDEO, movieJson.getBoolean(J_RESULTS_VIDEO));
            movieValues.put(Movie.MovieEntry.COLUMN_VOTE_AVARAGE, movieJson.getInt(J_RESULTS_VOTE_AVARAGE));
            movieValues.put(Movie.MovieEntry.COLUMN_TITLE, movieJson.getString(J_RESULTS_TITLE));
            movieValues.put(Movie.MovieEntry.COLUMN_POPULARITY, movieJson.getDouble(J_RESULTS_POPULARITY));
            movieValues.put(Movie.MovieEntry.COLUMN_POSTER_PATH, movieJson.getString(J_RESULTS_POSTER_PATH));
            movieValues.put(Movie.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, movieJson.getString(J_RESULTS_ORIGINAL_LANGUAGE));
            movieValues.put(Movie.MovieEntry.COLUMN_ORIGINAL_TITLE, movieJson.getString(J_RESULTS_ORIGINAL_TITLE));
            movieValues.put(Movie.MovieEntry.COLUMN_BACKDROP_PATH, movieJson.getString(J_RESULTS_BACKDROP_PATH));
            movieValues.put(Movie.MovieEntry.COLUMN_IS_ADULT, movieJson.getBoolean(J_RESULTS_ADULT));
            movieValues.put(Movie.MovieEntry.COLUMN_OVERVIEW, movieJson.getString(J_RESULTS_OVERVIEW));
            movieValues.put(Movie.MovieEntry.COLUMN_RELEASE_DATE, movieJson.getString(J_RESULTS_RELEASE_DATE));

            JSONArray genresArray = movieJson.getJSONArray(J_RESULTS_GENRE_IDS);
            for (int ii = 0; ii < genresArray.length(); ii++) {
                // @TODO store/return genre ids list
                int genreId = genresArray.getInt(ii);
            }

            output[i] = movieValues;
        }

        return output;
    }
}
