package com.example.keerthana.popularmovies;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;

/**
 * An activity representing a single MovieItem detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MovieItemListActivity}.
 */
public class MovieItemDetailActivity extends AppCompatActivity {

    private boolean isFavourite;
    private String mId;
    public static String FAVOURITES_COLLECTION = "favourites";
    public static String FAVOURITES_PREFERENCE = "favourites+preference";

    private FloatingActionButton mFab;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movieitem_detail);
        mId = null;
        if (savedInstanceState == null) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        int pos = getIntent().getIntExtra(MovieItemDetailFragment.ARG_ITEM_ID,0);
        JSONObject info;
        try {
            info = MovieItemListActivity.movieList.getJSONObject(pos);
            mId = info.getString("id");
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }

            mFab = (FloatingActionButton) findViewById(R.id.fab);

            SharedPreferences prefs = getSharedPreferences(FAVOURITES_PREFERENCE, MODE_PRIVATE);
            if (prefs != null) {
                Log.d("PREFERENCES", String.valueOf(prefs.getStringSet(FAVOURITES_COLLECTION, null)));
                final HashSet<String> movieIds = (HashSet<String>) prefs.getStringSet(FAVOURITES_COLLECTION, null);
                if (movieIds!= null && movieIds.contains(mId)){
                    mFab.setImageResource(android.R.drawable.btn_star_big_on);
                    isFavourite = true;
                }else {
                    isFavourite = false;
                }
            }

            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFavourite){
                        Toast.makeText(getBaseContext(), "Removed from favourites", Toast.LENGTH_LONG).show();
                        isFavourite = false;
                        changeFab();

                        SharedPreferences preferences = getSharedPreferences(FAVOURITES_PREFERENCE, MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        HashSet<String> temp = new HashSet<>(preferences.getStringSet(FAVOURITES_COLLECTION, null));
                        temp.remove(mId);
                        editor.putStringSet(FAVOURITES_COLLECTION, temp);
                        editor.commit();
                    }else {
                        Toast.makeText(getBaseContext(), "Added to Favourites", Toast.LENGTH_LONG).show();
                        isFavourite = true;
                        changeFab();

                        SharedPreferences preferences = getSharedPreferences(FAVOURITES_PREFERENCE, MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();

                        HashSet<String> temp;
                        if (preferences.getStringSet(FAVOURITES_COLLECTION, null) != null) {
                            temp = new HashSet<>(preferences.getStringSet(FAVOURITES_COLLECTION, null));
                        }else {
                            temp = new HashSet<>();
                        }
                        temp.add(mId);
                        editor.putStringSet(FAVOURITES_COLLECTION, temp);
                        editor.commit();
                    }
                }
            });


            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                try {
                    appBarLayout.setTitle(info.getString("title"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    appBarLayout.setTitle(info.getString("title"));
                    ((TextView) appBarLayout.findViewById(R.id.date)).setText(getString(R.string.released_on)+" "
                            + info.getString("release_date"));
                    ImageView imageView= (ImageView) appBarLayout.findViewById(R.id.movie_poster);
                    if (isNetworkAvailable()) {
                        Picasso.with(this).load("http://image.tmdb.org/t/p/w185" + info.getString("poster_path")).into(imageView);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Show the Up button in the action bar.

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(MovieItemDetailFragment.ARG_ITEM_ID, pos);
            MovieItemDetailFragment fragment = new MovieItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movieitem_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, MovieItemListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void changeFab(){
        if (isFavourite){
            mFab.setImageResource(android.R.drawable.btn_star_big_on);
        }else {
            mFab.setImageResource(android.R.drawable.btn_star);
        }
    }
}
