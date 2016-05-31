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

import com.example.keerthana.popularmovies.dummy.DummyContent;
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

    /**
     * The dummy content this fragment is presenting.
     */
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
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            int pos = getArguments().getInt(ARG_ITEM_ID);
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                try {
                    info = MovieItemListActivity.movieList.getJSONObject(pos);
                    appBarLayout.setTitle(info.getString("title"));
                    ((TextView) appBarLayout.findViewById(R.id.date)).setText("Released on " + info.getString("release_date"));
                    ImageView imageView= (ImageView) appBarLayout.findViewById(R.id.movie_poster);
                    Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185"+info.getString("poster_path")).into(imageView);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movieitem_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (info != null) {
            try {
                ((TextView) rootView.findViewById(R.id.movieitem_detail)).setText(info.getString("overview"));
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
