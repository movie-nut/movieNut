package com.example.movienut.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class DisplayResults extends Activity {
    String[] moviesInfo;
    String[] description;
    String[] image;
    String[] releaseDates;
    ArrayList<Movies> movies = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_results);

        getMoviesInfo();
        //  getImageStored();

        ListView list = (ListView) findViewById(R.id.listView);
        moviesAdapter adapter = new moviesAdapter(this, moviesInfo, description, image);
        list.setAdapter(adapter);
    }

    private void getMoviesInfo() {
        Intent intent = getIntent();
        moviesInfo = intent.getStringArrayExtra("movieInfo");
        description = intent.getStringArrayExtra("description");
        image = intent.getStringArrayExtra("image");
        releaseDates = intent.getStringArrayExtra("releaseDate");

        try {
            storeInMovieClass();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Collections.sort(movies, new Comparator<Movies>() {
            public int compare(Movies o1, Movies o2) {
                if (o1.getDate() == "" || o2.getDate() == "" || o1.getDate() == null || o2.getDate() == null) {
                    return 0;
                }
                return o2.getDate().compareTo(o1.getDate());
            }
        });

        storeBackIntoString();

    }

    private void storeBackIntoString() {
        for (int i = 0; i < movies.size(); i++) {
            moviesInfo[i + 1] = movies.get(i).getMovieTitle();
            description[i + 1] = movies.get(i).getDescription();
            image[i + 1] = movies.get(i).getImageURL();
        }
    }

    private void storeInMovieClass() throws ParseException {

        for (int i = 1; i < moviesInfo.length; i++) {
            movies.add(new Movies());
            movies.get(i - 1).setMovieTitle(moviesInfo[i]);
            movies.get(i - 1).setDecription(description[i]);
            movies.get(i - 1).setImageURL(image[i]);
            movies.get(i - 1).setDate(releaseDates[i]);
        }
    }

    class moviesAdapter extends ArrayAdapter<String> implements weilin.myapplication.moviesAdapter {
        Context context;
        String[] moviesInfo;
        String[] description;
        String[] image;

        moviesAdapter(Context c, String[] moviesInfo, String[] description, String[] image) {
            super(c, R.layout.single_row, R.id.textView3, moviesInfo);
            this.context = c;
            this.moviesInfo = moviesInfo;
            this.description = description;
            this.image = image;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = inflater.inflate(R.layout.single_row, parent, false);
            ImageView myImage = (ImageView) row.findViewById(R.id.imageView);
            TextView movieTitles = (TextView) row.findViewById(R.id.textView3);
            TextView myDescription = (TextView) row.findViewById(R.id.textView4);

            movieTitles.setText(moviesInfo[position]);
            myDescription.setText(description[position]);

            if (image.length > position && (!image[position].equals("") || !(image[position] == null))) {

                String url = null;
                try {
                    url = new URL(image[position]).toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                Picasso.with(context).load(url).resize(50, 50).centerCrop().into(myImage);
            }
            return row;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}









