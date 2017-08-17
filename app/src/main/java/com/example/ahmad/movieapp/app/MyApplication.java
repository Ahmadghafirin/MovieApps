package com.example.ahmad.movieapp.app;

import android.app.Application;

import okhttp3.OkHttpClient;

/**
 * Created by ahmad on 11/08/17.
 */

public class MyApplication extends Application {

    private static OkHttpClient okHttpClient;
    public static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185";
    public static final String BASE_SEARCH_URL = "https://api.mathemoviedb.org/3/search/movie";

    @Override
    public void onCreate() {
        super.onCreate();

        okHttpClient = new OkHttpClient();
    }

    public static OkHttpClient getClient() {
        return okHttpClient;
    }
}
