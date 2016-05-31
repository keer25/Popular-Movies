package com.example.keerthana.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    public final String[] map= {"top_rated", "popular"};
    public static final String APP_ID = "d5e0ba6839dff91fe04d69bcb3bdabd3";
    public static JSONArray movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movieitem_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        toolbar.setLogo(R.mipmap.ic_launcher);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.movieitem_list);
        assert recyclerView != null;
        GridLayoutManager layout = new GridLayoutManager(getApplicationContext(), calSpan());
        recyclerView.setLayoutManager(layout);
    }



    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ArrayList<String> mValues;

        public SimpleItemRecyclerViewAdapter(ArrayList<String> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.movieitem_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);
            Picasso.with(getApplicationContext()).load("http://image.tmdb.org/t/p/w185"+holder.mItem).into(holder.mImg);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        Context context = v.getContext();
                        Intent intent = new Intent(context, MovieItemDetailActivity.class);
                        intent.putExtra(MovieItemDetailFragment.ARG_ITEM_ID, position);
                        context.startActivity(intent);

                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mImg;
            public String mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImg = (ImageView) view.findViewById(R.id.movie_poster);
            }

            @Override
            public String toString() {
                return super.toString() ;
            }
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
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String pref = map[position];
                GetMoviesTask getMoviesTask = new GetMoviesTask();
                getMoviesTask.execute(pref);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public class GetMoviesTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String dataJson = null;
            try {
                URL url = new URL("https://api.themoviedb.org/3/movie/"+ params[0] +"?api_key="+APP_ID);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    dataJson = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    dataJson = null;
                }
                return buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String dataJson){
            try {
                parseJson(dataJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void parseJson(String dataJson) throws JSONException {
        JSONObject json = new JSONObject(dataJson);
        movieList = json.getJSONArray("results");
        ArrayList<String> array = new ArrayList<String>();
        for (int i=0;i<movieList.length();i++) {
            JSONObject jsonObject = movieList.getJSONObject(i);
            String poster_path = jsonObject.getString("poster_path");
            long id = jsonObject.getLong("id");
            String item = poster_path;
            array.add(item);
            Log.i("StringURL", poster_path);
        }
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.movieitem_list);
        assert recyclerView != null;
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(array));


    }

}
