package com.example.keerthana.popularmovies;

/**
 * Created by keerthana on 31/5/16.
 */
public class MovieItem {
    long id;
    String poster_path;

    public MovieItem(long id, String poster_path){
        this.id = id;
        this.poster_path = poster_path;
    }
}
