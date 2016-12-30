package com.example.keerthana.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by keerthana on 24/12/16.
 */

public class SimpleItemRecyclerViewAdapter
        extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<String> mValues;
    private Context mContext;

    public SimpleItemRecyclerViewAdapter(ArrayList<String> items, Context context) {
        mValues = items;
        mContext = context;
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
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185"+holder.mItem).into(holder.mImg);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = v.getContext();
                Intent intent = new Intent(context,MovieItemDetailActivity.class);
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