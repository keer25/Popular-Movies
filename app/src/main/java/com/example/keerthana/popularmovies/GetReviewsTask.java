package com.example.keerthana.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
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

import static com.example.keerthana.popularmovies.MovieItemListActivity.APP_ID;

/**
 * Created by keerthana on 30/12/16.
 */

public class GetReviewsTask extends AsyncTask<String, Void, String> {

    private Context mContext;
    private FragmentActivity mActivity;

    public static final String REVIEW_EXTRA_AUTHOR = "review_extra";
    public static final String REVIEW_EXTRA_CONTENT = "review_content";

    GetReviewsTask(Context context, FragmentActivity activity){
        mContext = context;
        mActivity = activity;
    }


    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL("https://api.themoviedb.org/3/movie/"+ params[0] + "/reviews?api_key="+APP_ID);
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
            JSONArray reviews = new JSONObject(dataJson).getJSONArray("results");
            LinearLayout container = (LinearLayout) mActivity.findViewById(R.id.review_container);

            Log.d("REVIEWS", String.valueOf(reviews.length()));
            if (reviews.length() == 0){
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View reviewItem = inflater.inflate(R.layout.review_item, null);

                TextView textView = (TextView) reviewItem.findViewById(R.id.review_author_text);
                textView.setText("No Reviews");

                container.addView(reviewItem);
            }

            for (int i = 0; i < reviews.length() ; i++) {
                final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View reviewItem = inflater.inflate(R.layout.review_item, null);
                final JSONObject review = (JSONObject) reviews.get(i);

                TextView review_author = (TextView) reviewItem.findViewById(R.id.review_author_text);
                TextView review_content = (TextView) reviewItem.findViewById(R.id.review_content_text);

                final String author_string = review.getString("author");

                review_author.setText(author_string);

                final String content_string = review.getString("content");
                String content = null;
                if (content_string.length() > 130){
                    content = content_string.substring(0, 130) + "...";
                }else {
                    content = content_string;
                }
                review_content.setText(content);

                reviewItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, MovieReviewActivity.class);
                        intent.putExtra(REVIEW_EXTRA_AUTHOR, author_string);
                        intent.putExtra(REVIEW_EXTRA_CONTENT, content_string);
                        mContext.startActivity(intent);
                    }
                });

                container.addView(reviewItem);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
