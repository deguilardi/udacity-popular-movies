package com.guilardi.popularmovies.utilities;

import android.app.Activity;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;

import com.guilardi.popularmovies.BuildConfig;
import com.guilardi.popularmovies.data.Movie;
import com.guilardi.popularmovies.data.Movies;
import com.guilardi.popularmovies.data.Reviews;
import com.guilardi.popularmovies.data.Trailers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by deguilardi on 5/9/18.
 */

public final class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String PARAM_API_KEY = "api_key";
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String PATH_VIDEOS_POPULAR = "popular";
    private static final String PATH_VIDEOS_TOP_RATED = "top_rated";
    private static final String PATH_VIDEO_VIDEOS = "videos";
    private static final String PATH_VIDEO_REVIEWS = "reviews";

    protected static NetworkUtils instance;
    private Service service;

    // thumb constraints
    public abstract static class C_THUMB{
        public static final String SERVER_URL = "http://image.tmdb.org/t";
        public static final String PATH_SIZE_ORIGINAL = "/p/original";
        public static final String PATH_SIZE_THUMB_92 = "/p/w92";
        public static final String PATH_SIZE_THUMB_154 = "/p/w154";
        public static final String PATH_SIZE_THUMB_185 = "/p/w185";
        public static final String PATH_SIZE_THUMB_342 = "/p/w342";
        public static final String PATH_SIZE_THUMB_500 = "/p/w500";
        public static final String PATH_SIZE_THUMB_780 = "/p/w780";
    }

    private NetworkUtils(){
        if(BuildConfig.MOVIE_DB_API_KEY.equals("") || BuildConfig.MOVIE_DB_API_KEY.equals("YOUR_API_KEY")){
            throw new RuntimeException("Config.MOVIE_DB_API_KEY must be defined");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.service = retrofit.create(Service.class);
    }

    public static synchronized NetworkUtils getInstance() {
        if(instance == null){
            instance = new NetworkUtils();
        }
        return instance;
    }

    public void loadMovies(String showBy, Callback<Movies> callback){
        Map<String,String> params = new HashMap<>();
        params.put(PARAM_API_KEY, BuildConfig.MOVIE_DB_API_KEY);
        Call<Movies> call = service.loadMovies(showBy, params);
        call.enqueue(callback);
    }

    public void loadTrailers(Movie movie, Callback<Trailers> callback){
        Map<String,String> params = new HashMap<>();
        params.put(PARAM_API_KEY, BuildConfig.MOVIE_DB_API_KEY);
        Call<Trailers> call = service.loadTrailers(movie.getId(), params);
        call.enqueue(callback);
    }

    public void loadReviews(Movie movie, Callback<Reviews> callback){
        Map<String,String> params = new HashMap<>();
        params.put(PARAM_API_KEY, BuildConfig.MOVIE_DB_API_KEY);
        Call<Reviews> call = service.loadReviews(movie.getId(), params);
        call.enqueue(callback);
    }

    public static URL getThumbURL(String posterPath, Activity context, int widthDivider){

        // define the size based on the screen width
        // this will make sure the downloaded image is the best fit
        String size;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        if(screenWidth > 500 * widthDivider){
            size = NetworkUtils.C_THUMB.PATH_SIZE_THUMB_780;
        }
        else if(screenWidth > 342 * widthDivider){
            size = NetworkUtils.C_THUMB.PATH_SIZE_THUMB_500;
        }
        else if(screenWidth > 185 * widthDivider){
            size = NetworkUtils.C_THUMB.PATH_SIZE_THUMB_342;
        }
        else if(screenWidth > 154 * widthDivider){
            size = NetworkUtils.C_THUMB.PATH_SIZE_THUMB_185;
        }
        else if(screenWidth > 92 * widthDivider){
            size = NetworkUtils.C_THUMB.PATH_SIZE_THUMB_154;
        }
        else{
            size = NetworkUtils.C_THUMB.PATH_SIZE_THUMB_92;
        }

        return buildUrlWithServerAndPath(C_THUMB.SERVER_URL, size + posterPath);
    }

    private static URL buildUrlWithServerAndPath(String serverUrl, String path) {
        Uri queryUri = Uri.parse(serverUrl).buildUpon()
                .appendEncodedPath(path)
                .appendQueryParameter(PARAM_API_KEY, BuildConfig.MOVIE_DB_API_KEY)
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


    /**
     * RETROFIT INTERFACES AND CLASSES
     */
    interface Service{
        @GET("{showBy}")
        Call<Movies> loadMovies(@Path("showBy") String showBy, @QueryMap Map<String,String> params);

        @GET("{movieID}/" + PATH_VIDEO_VIDEOS)
        Call<Trailers> loadTrailers(@Path("movieID") Integer movieID, @QueryMap Map<String,String> params);

        @GET("{movieID}/" + PATH_VIDEO_REVIEWS)
        Call<Reviews> loadReviews(@Path("movieID") Integer movieID, @QueryMap Map<String,String> params);
    }
}
