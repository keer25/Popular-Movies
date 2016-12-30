package com.example.keerthana.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.example.keerthana.popularmovies.MovieItemListActivity.APP_ID;
import static com.example.keerthana.popularmovies.MovieItemListActivity.movieList;

/**
 * Created by keerthana on 24/12/16.
 */

public class GetMoviesTask extends AsyncTask<String, Void, String> {

    private Context mContext;
    private MovieItemListActivity mActivity;

    public GetMoviesTask(Context context, MovieItemListActivity activity){
        mContext = context;
        mActivity = activity;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL("https://api.themoviedb.org/3/movie/"+ params[0] +"?api_key="+APP_ID);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
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
                return null;
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

    public void parseJson(String dataJson) throws JSONException {
        JSONObject json = new JSONObject(dataJson);
        movieList = json.getJSONArray("results");
        ArrayList<String> array = new ArrayList<String>();
        for (int i=0;i<movieList.length();i++) {
            JSONObject jsonObject = movieList.getJSONObject(i);
            String poster_path = jsonObject.getString("poster_path");
            String item = poster_path;
            array.add(item);
            Log.i("StringURL", poster_path);
        }
        RecyclerView recyclerView = (RecyclerView) mActivity.findViewById(R.id.movieitem_list);
        assert recyclerView != null;
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(array, mContext));
    }

}
