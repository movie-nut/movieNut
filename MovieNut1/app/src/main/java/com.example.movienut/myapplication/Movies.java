package com.example.movienut.myapplication;

import java.io.Serializable;

/**
 * Created by WeiLin on 4/7/15.
 */
public class Movies implements Serializable {
    private String movieTitle;
    private String date;
    private String description;
    private String imageURL;


    public Movies(){

    }

    public void setMovieTitle(String movieTitle){
        this.movieTitle = movieTitle;
    }

    public void setDate(String date){
        this.date = date;
    }

    public void setDecription(String description){
        this.description = description;
    }

    public void setImageURL(String imageURL){
        this.imageURL = imageURL;
    }

    public String getMovieTitle(){
        return movieTitle;
    }

    public String getDate(){
        return date;
    }

    public String getDescription(){
        return description;
    }

    public String getImageURL(){
        return imageURL;
    }
}
