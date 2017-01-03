package com.example.keerthana.popularmovies;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.ArraySet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MovieItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    public final String[] map= {"top_rated", "popular", "favourites"};
    public static String APP_ID ;
    public static JSONArray movieList;
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movieitem_list);
        APP_ID = this.getString(R.string.APIKEY);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.movieitem_list);
        assert recyclerView != null;
        GridLayoutManager layout = new GridLayoutManager(getApplicationContext(), calSpan());
        recyclerView.setLayoutManager(layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        toolbar.setLogo(R.mipmap.ic_launcher);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (pos == 2){
            new GetFavouritesTask(getBaseContext(), MovieItemListActivity.this).execute();
            Log.d("ACTIVITY_TRACK", "Recalculating favourites");
        }
    }

    private int calSpan(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int span = (int) (metrics.widthPixels/(metrics.xdpi*0.75));
        Log.i("Span for grid",String.valueOf(span));
        if (span <= 0) return 1;
        else return span;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.movie_list_menu, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.preference, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter); // set the adapter to provide layout of rows and content
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String pref = map[position];
                pos = position;
                GetMoviesTask getMoviesTask = new GetMoviesTask(getApplicationContext(), MovieItemListActivity.this);
                if (isNetworkAvailable()) {
                    if (position != 2){
                        getMoviesTask.execute(pref);
                    }else {
                        new GetFavouritesTask(getBaseContext(), MovieItemListActivity.this).execute();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.movieitem_list);
        assert recyclerView != null;
        GridLayoutManager layout = new GridLayoutManager(getApplicationContext(), calSpan());
        recyclerView.setLayoutManager(layout);
        super.onConfigurationChanged(newConfig);
    }

}
