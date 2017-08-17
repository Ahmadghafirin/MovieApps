package com.example.ahmad.movieapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.example.ahmad.movieapp.R;
import com.example.ahmad.movieapp.app.MyApplication;
import com.example.ahmad.movieapp.model.Movie;
import com.example.ahmad.movieapp.movie.MovieAdapter;
import com.example.ahmad.movieapp.recyclerview.RecyclerTouchListener;
import com.example.ahmad.movieapp.recyclerview.SpacesItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private String TAG = MainActivity.class.getSimpleName();
    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private ProgressDialog progressDialog;
    private Intent intent;
    private Menu menu;

    List<Movie> movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.btn_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.rv_movie);
        movieList = new ArrayList<>();
        adapter = new MovieAdapter(MainActivity.this);
        initRecyclerView();
        getMovie("now_playing");

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.now_playing:
                                movieList.clear();
                                adapter.clear();
                                getMovie("now_playing");
                                break;
                            case R.id.up_coming:
                                movieList.clear();
                                adapter.clear();
                                getMovie("upcoming");
                                break;
                            default:
                                movieList.clear();
                                adapter.clear();
                                getMovie("popular");
                        }
                        return true;
                    }
                });

    }

    @Override
    protected void onResume() {
        movieList.clear();
        adapter.notifyDataSetChanged();
        adapter.addAll(movieList);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_item, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    private void getSearch(String query) {
        Request request = new Request.Builder()
                .url(MyApplication.BASE_SEARCH_URL + "?api_key=b26733daf3a5f7fd722800d1110e79b8&query=" + query)
                .build();

        MyApplication.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String body = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(body);
                                JSONArray results = jsonObject.getJSONArray("results");
                                movieList.clear();
                                for (int i = 0; i < results.length(); i++) {
                                    JSONObject c = results.getJSONObject(i);

                                    String title = c.getString("title");
                                    String image = c.getString("poster_path");
                                    int id = c.getInt("id");
                                    movieList.add(new Movie(title, image, id));
                                    Log.d(TAG, "Search: " + movieList.toString());
                                }
                                adapter.addAll(movieList);
                                Log.d(TAG, "adapter size: " + adapter.getItemCount());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else Log.d(TAG, "run tidak sukses: " + response.message());
                    }
                });
            }
        });
    }

    public void initRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "initRecyclerView: " + adapter.getItemCount());
        recyclerView.addItemDecoration(new SpacesItemDecoration(this, R.dimen.space_5));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView,
                new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        intent = new Intent(MainActivity.this, DetailsMovieActivity.class);
                        Movie movie = adapter.getItem(position);
                        intent.putExtra("movie", movie);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));
    }

    public void getMovie(final String type) {
        Request request = new Request.Builder()
                .url(MyApplication.BASE_URL + type + "?api_key=b26733daf3a5f7fd722800d1110e79b8")
                .build();

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        MyApplication.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String body = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(body);
                                JSONArray movie = jsonObject.getJSONArray("results");
                                for (int i = 0; i < movie.length(); i++) {
                                    JSONObject c = movie.getJSONObject(i);

                                    String image = c.getString("poster_path");
                                    String title = c.getString("title");
                                    int id = c.getInt("id");
                                    movieList.add(new Movie(title, image, id));
                                    Log.d(TAG, type + movieList.get(i).toString());
                                    progressDialog.dismiss();
                                }
                                adapter.addAll(movieList);
                                Log.d(TAG, "adapter size: " + adapter.getItemCount());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, "onQueryTextSubmit: " + query);
        adapter.clear();
        getSearch(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(TAG, "onQueryTextChange: " + newText);
        adapter.clear();
        getSearch(newText);
        return false;
    }
}
