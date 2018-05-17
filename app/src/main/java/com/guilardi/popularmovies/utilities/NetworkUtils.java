package com.guilardi.popularmovies.utilities;

import android.app.Activity;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;

import com.guilardi.popularmovies.Config;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by deguilardi on 5/9/18.
 */

public final class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String PARAM_API_KEY = "api_key";

    // movie constraints
    public abstract static class C_MOVIE{
        public static final String SERVER_URL = "https://api.themoviedb.org/3";
        public static final String PATH_MOVIES_POPULAR = "movie/popular";
        public static final String PATH_MOVIES_TOP_RATED = "movie/top_rated";
    }

    // thumb constraints
    public abstract static class C_THUMB {
        public static final String SERVER_URL = "http://image.tmdb.org/t";
        public static final String PATH_SIZE_ORIGINAL = "/p/original";
        public static final String PATH_SIZE_THUMB_92 = "/p/w92";
        public static final String PATH_SIZE_THUMB_154 = "/p/w154";
        public static final String PATH_SIZE_THUMB_185 = "/p/w185";
        public static final String PATH_SIZE_THUMB_342 = "/p/w342";
        public static final String PATH_SIZE_THUMB_500 = "/p/w500";
        public static final String PATH_SIZE_THUMB_780 = "/p/w780";
    }

    public static URL getMoviesListURLWithType(String type){
        return getURL(NetworkUtils.C_MOVIE.SERVER_URL, type);
    }

    public static URL getThumbURL(String posterPath, Activity context){

        // define the size based on the screen width
        String size;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        if(screenWidth > 500*Config.HOME_LIST_NUM_COLUMNS){
            size = NetworkUtils.C_THUMB.PATH_SIZE_THUMB_780;
        }
        else if(screenWidth > 342*Config.HOME_LIST_NUM_COLUMNS){
            size = NetworkUtils.C_THUMB.PATH_SIZE_THUMB_500;
        }
        else if(screenWidth > 185*Config.HOME_LIST_NUM_COLUMNS){
            size = NetworkUtils.C_THUMB.PATH_SIZE_THUMB_342;
        }
        else if(screenWidth > 154*Config.HOME_LIST_NUM_COLUMNS){
            size = NetworkUtils.C_THUMB.PATH_SIZE_THUMB_185;
        }
        else if(screenWidth > 92*Config.HOME_LIST_NUM_COLUMNS){
            size = NetworkUtils.C_THUMB.PATH_SIZE_THUMB_154;
        }
        else{
            size = NetworkUtils.C_THUMB.PATH_SIZE_THUMB_92;
        }

        return getURL(NetworkUtils.C_THUMB.SERVER_URL, size + posterPath);
    }

    public static URL getURL(String serverUrl, String path){
        return buildUrlWithServerAndPath(serverUrl, path);
    }

    private static URL buildUrlWithServerAndPath(String serverUrl, String path) {
        if(Config.MOVIE_DB_API_KEY.equals("") || Config.MOVIE_DB_API_KEY.equals("YOUR_API_KEY")){
            throw new RuntimeException("Config.MOVIE_DB_API_KEY must be defined");
        }

        Uri queryUri = Uri.parse(serverUrl).buildUpon()
                .appendEncodedPath(path)
                .appendQueryParameter(PARAM_API_KEY, Config.MOVIE_DB_API_KEY)
                .build();
        try {
            URL queryUrl = new URL(queryUri.toString());
            Log.v(TAG, "URL: " + queryUrl);
            return queryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }
}
