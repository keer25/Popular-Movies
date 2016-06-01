package com.example.keerthana.popularmovies;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A fragment representing a single MovieItem detail screen.
 * This fragment is either contained in a {@link MovieItemListActivity}
 * in two-pane mode (on tablets) or a {@link MovieItemDetailActivity}
 * on handsets.
 */
public class MovieItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    private JSONObject info;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            int pos = getArguments().getInt(ARG_ITEM_ID);
            try {
                info = MovieItemListActivity.movieList.getJSONObject(pos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Activity activity = this.getActivity();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movieitem_detail, container, false);
        if (info != null) {
            try {
                ((TextView) rootView.findViewById(R.id.detail)).setText(info.getString("overview"));
                float avg = (float) info.getDouble("vote_average");
                ((RatingBar) rootView.findViewById(R.id.ratingBar)).setRating(avg/2);
                ((TextView) rootView.findViewById(R.id.rating)).setText(String.valueOf(avg)+" / 10");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return rootView;
    }

}
