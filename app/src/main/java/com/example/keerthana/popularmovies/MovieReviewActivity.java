package com.example.keerthana.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.mukesh.MarkdownView;

public class MovieReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_review);

        MarkdownView view = (MarkdownView) findViewById(R.id.review_content_text);
        view.setOpenUrlInBrowser(true);
        view.setMarkDownText(getIntent().getStringExtra(GetReviewsTask.REVIEW_EXTRA_CONTENT));

        TextView textView = (TextView) findViewById(R.id.review_author_text);
        textView.setText(getIntent().getStringExtra(GetReviewsTask.REVIEW_EXTRA_AUTHOR));
    }
}
