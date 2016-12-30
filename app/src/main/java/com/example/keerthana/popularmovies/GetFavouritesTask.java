package com.example.keerthana.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import static android.content.Context.MODE_PRIVATE;
import static com.example.keerthana.popularmovies.MovieItemListActivity.APP_ID;
import static com.example.keerthana.popularmovies.MovieItemListActivity.movieList;

/**
 * Created by keerthana on 31/12/16.
 */

public class GetFavouritesTask extends AsyncTask<Void, Void ,JSONArray> {

    private Context mContext;
    private MovieItemListActivity mActivity;

    public GetFavouritesTask(Context context, MovieItemListActivity activity){
        mContext = context;
        mActivity = activity;
    }

    @Override
    protected JSONArray doInBackground(Void... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        JSONArray array = new JSONArray();
        try {
            SharedPreferences preferences = mContext.getSharedPreferences(MovieItemDetailActivity.FAVOURITES_PREFERENCE, MODE_PRIVATE);
            if (preferences == null){
                return null;
            }
            HashSet<String> favs = new HashSet<>(preferences.getStringSet(MovieItemDetailActivity.FAVOURITES_COLLECTION ,null));

            Log.d("PREFERENCES", favs.toString());

            for ( String id : favs){
                URL url = new URL("https://api.themoviedb.org/3/movie/"+ id +"?api_key="+APP_ID);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    continue;
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
                    continue;
                }

                JSONObject object = new JSONObject(buffer.toString());
                array.put(object);
            }
            return array;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONArray dataJson){
        try {
            parseJson(dataJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parseJson(JSONArray dataJson) throws JSONException {
        movieList = dataJson;
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
