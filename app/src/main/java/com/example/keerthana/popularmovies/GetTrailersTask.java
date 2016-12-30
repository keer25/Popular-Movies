package com.example.keerthana.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.LayoutInflaterCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

import static com.example.keerthana.popularmovies.MovieItemListActivity.APP_ID;

/**
 * Created by keerthana on 25/12/16.
 */

public class GetTrailersTask extends AsyncTask<String, Void, String> {

    private Context mContext;
    private FragmentActivity mActivity;

    GetTrailersTask(Context context, FragmentActivity activity){
        mActivity = activity;
        mContext = context;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL("https://api.themoviedb.org/3/movie/"+ params[0] + "/videos?api_key="+APP_ID);
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
        try{
            //Populates the ListView
            JSONArray trailers = new JSONObject(dataJson).getJSONArray("results");
            LinearLayout container = (LinearLayout) mActivity.findViewById(R.id.trailer_container);
            int resource  = R.layout.trailer_text_view;
            displaytrailers(mContext, trailers, container, resource);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    //Takes parameters to associate adapter with a listView and generate trailer Link
    private void displaytrailers(final Context context, JSONArray trailers, LinearLayout container, int resource ) throws JSONException{
        final ArrayList<String> trailerLinks  = new ArrayList<>(trailers.length());
        ArrayList<String> trailerTitles = new ArrayList<>(trailers.length());
        int trailerPos = 0;
        while (trailerPos < trailers.length()){
            JSONObject trailerObject  = (JSONObject) trailers.get(trailerPos);
            trailerLinks.add(generateLink(trailerObject.getString("key")));
            trailerTitles.add("<u>"+trailerObject.getString("name")+"</u>");
            trailerPos++;
        }

        Log.d("Trailer", String.valueOf(trailerLinks.size()));
        //ArrayAdapter adapter = new ArrayAdapter(context, resource, trailerLinks);
//        listView.setOnTouchListener(new View.OnTouchListener() {
//            // Setting on Touch Listener for handling the touch inside ScrollView
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // Disallow the touch request for parent scroll on touch of child view
//                v.getParent().requestDisallowInterceptTouchEvent(true);
//                return false;
//            }
//        });
        //setListViewHeightBasedOnChildren(listView);
        //listView.setAdapter(adapter);
        //setListViewHeightBasedOnChildren(listView)
        for (int i = 0; i<trailerLinks.size(); i++){

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            TextView view = (TextView) inflater.inflate(R.layout.trailer_text_view, null);
            view.setText(Html.fromHtml(trailerTitles.get(i)));
            final int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerLinks.get(finalI)));
                    context.startActivity(viewIntent);
                }
            });

            container.addView(view);
        }

        //Log.d("Trailer", String.valueOf(listView.getCount()));
    }

    //Youtube Link Template https://www.youtube.com/watch?v=<key>
    private String generateLink(String key){
       return "https://www.youtube.com/watch?v=" + key ;
    }

}
