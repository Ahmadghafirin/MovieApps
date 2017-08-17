package com.example.ahmad.movieapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ahmad.movieapp.R;
import com.example.ahmad.movieapp.app.MyApplication;
import com.example.ahmad.movieapp.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class DetailsMovieActivity extends AppCompatActivity {

    private static final String TAG = DetailsMovieActivity.class.getSimpleName();
    private Movie movie;
    private ImageView ivImage;
    private TextView tvDistributor, tvCountry, tvRating, tvSynopsis, tvGenre, tvRelease;
    private String nameGenre, nameDsbtr, nameCountry, synopsis, release;
    private double rating;
    private ProgressDialog progressDialog;
    File imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_movie);

        tvDistributor = (TextView) findViewById(R.id.tv_distributor);
        tvCountry = (TextView) findViewById(R.id.tv_country);
        tvRating = (TextView) findViewById(R.id.tv_rating);
        tvSynopsis = (TextView) findViewById(R.id.tv_synopsis);
        ivImage = (ImageView) findViewById(R.id.iv_details);
        tvGenre = (TextView) findViewById(R.id.tv_genre);
        tvRelease = (TextView) findViewById(R.id.tv_release);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_share);
        setSupportActionBar(toolbar);


        movie = getIntent().getParcelableExtra("movie");
        getDetails(movie.getId());
        getSupportActionBar().setTitle(movie.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Glide.with(DetailsMovieActivity.this).load(MyApplication.BASE_IMAGE_URL + movie.getImageUrl()).into(ivImage);
    }

    private void getDetails(int id) {
        Request request = new Request.Builder()
                .url(MyApplication.BASE_URL + id + "?api_key=b26733daf3a5f7fd722800d1110e79b8")
                .get().build();

        progressDialog = new ProgressDialog(DetailsMovieActivity.this);
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
                                JSONArray genres = jsonObject.getJSONArray("genres");
                                for (int i = 0; i < genres.length(); i++) {
                                    JSONObject genre = genres.getJSONObject(i);
                                    nameGenre = genre.getString("name");
                                    movie = new Movie(nameGenre);
                                    Log.d(TAG, "genres : " + nameGenre);
                                    tvGenre.setText(nameGenre);
                                }
                                JSONArray distributor = jsonObject.getJSONArray("production_companies");
                                for (int i = 0; i < distributor.length(); i++) {
                                    JSONObject dsbtr = distributor.getJSONObject(i);
                                    nameDsbtr = dsbtr.getString("name");
                                    movie = new Movie(nameDsbtr);
                                    Log.d(TAG, "distributor: " + nameDsbtr);
                                    tvDistributor.setText(nameDsbtr);
                                }
                                JSONArray countries = jsonObject.getJSONArray("production_countries");
                                for (int i = 0; i < countries.length(); i++) {
                                    JSONObject country = countries.getJSONObject(i);
                                    nameCountry = country.getString("name");
                                    movie = new Movie(nameCountry);
                                    Log.d(TAG, "country: " + nameCountry);
                                    tvCountry.setText(nameCountry);
                                }
                                synopsis = jsonObject.getString("overview");
                                Log.d(TAG, "synopsis: " + synopsis);
                                rating = jsonObject.getDouble("vote_average");
                                Log.d(TAG, "rating: " + rating);
                                release = jsonObject.getString("release_date");
                                Log.d(TAG, "release: " + release);
                                movie = new Movie(synopsis, rating, release);
                                tvSynopsis.setText(synopsis);
                                tvRating.setText(String.valueOf(rating));
                                tvRelease.setText(release);
                                progressDialog.dismiss();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                Bitmap bitmap = takeScreenShot();
                saveBitmap(bitmap);
                shareIt();
                break;
            case android.R.id.home:
                finish();
            default:
                break;
        }
        return true;
    }

    public Bitmap takeScreenShot() {
        View view = findViewById(android.R.id.content).getRootView();
        view.setDrawingCacheEnabled(true);
        return view.getDrawingCache();
    }

    public void saveBitmap(Bitmap bitmap) {
        imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshoot.png");
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            Log.e("saveBitmap: ", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("saveBitmap: ", e.getMessage());
            e.printStackTrace();
        }
    }

    private void shareIt() {
        Uri uri = Uri.fromFile(imagePath);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        String shareBody = "ScreenShot Movie";
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "My Catch Score");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
}
