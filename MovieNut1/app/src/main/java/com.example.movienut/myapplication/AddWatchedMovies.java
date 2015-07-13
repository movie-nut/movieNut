package com.example.movienut.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;

/**
 * Created by WeiLin on 12/7/15.
 */
public class AddWatchedMovies extends Activity {
    public List<MovieDb> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_watched_movies);

        Map<String, Boolean> map = Storage.loadMap(getApplicationContext());

        ArrayList<Movies> watchedMovieList = (ArrayList<Movies>)getIntent().getSerializableExtra("watchedMovies");

            for(int i = 0; i < watchedMovieList.size(); i++) {
                TmdbApi accountApi = new TmdbApi("3f2950a48b75db414b1dbb148cfcad89");
                TmdbSearch searchResult = accountApi.getSearch();
                list = searchResult.searchMovie(watchedMovieList.get(i).getMovieTitle(), null, "", false, null).getResults();
                for (int j = 0; j < list.size(); j++) {
                    if (watchedMovieList.get(i).getDate().equals(list.get(i).getReleaseDate())) {
                        map.put(String.valueOf(list.get(i).getId()), true);
                        break;
                    }
                }
            }

        Storage.saveMap(map, getApplicationContext());
    }

    public void buttonOnClick1(View v) throws IOException {
        EditText movieOut = (EditText) findViewById(R.id.txtAdd);

        String searchKeyword = movieOut.getText().toString();

        try {
            if (searchKeyword == null || searchKeyword.equals("")) {
                throw new NullPointerException();
            } else {
                runSearchKeyword(searchKeyword);
            }
        } catch (NullPointerException e) {
            Toast.makeText(this, "No keyword entered!", Toast.LENGTH_LONG).show();
        }
    }

    private void runSearchKeyword(String searchKeyword) {
        if(searchKeyword.equals("clearByAdmin")){
            Storage.saveMap(new HashMap<String, Boolean>(), getApplicationContext());
            Toast.makeText(this, "ALL CLEAR!", Toast.LENGTH_LONG).show();
        } else {
            permitsNetwork();

            TmdbApi accountApi = new TmdbApi("3f2950a48b75db414b1dbb148cfcad89");
            TmdbSearch searchResult = accountApi.getSearch();
            list = searchResult.searchMovie(searchKeyword, null, "", false, null).getResults();

            getListOfMovies();
        }
    }

    private void getListOfMovies() {
        try {
            if (list == null || list.size() <= 0) {
                throw new NullPointerException();
            } else {
                String[] moviesName = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    moviesName[i] = list.get(i).getOriginalTitle() + "\n" + list.get(i).getOverview();
                }

                ListView moviesList = (ListView) findViewById(R.id.listView3);
                moviesAdapter adapter = new moviesAdapter(this, moviesName);
                moviesList.setAdapter(adapter);

                selectOneMovie(moviesList);

            }
        } catch (NullPointerException e) {
            Toast.makeText(this, "Movies entered are not found!", Toast.LENGTH_LONG).show();
        }
    }

    private void permitsNetwork() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    //APi : 3f2950a48b75db414b1dbb148cfcad89
    //weblink: http://api.themoviedb.org/3/movie/550?api_key=3f2950a48b75db414b1dbb148cfcad89
    //http://api.themoviedb.org/3/search/movie?api_key=3f2950a48b75db414b1dbb148cfcad89&query=avengers
    //  http://api.themoviedb.org/3/movie/8966/similar?api_key=3f2950a48b75db414b1dbb148cfcad89

    private void selectOneMovie(ListView peopleNameList) {
        peopleNameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Map<String, Boolean> map = Storage.loadMap(getApplicationContext());
                map.put(String.valueOf(list.get(position).getId()), true);
                Storage.saveMap(map, getApplicationContext());
                Toast.makeText(getApplicationContext(), list.get(position).getOriginalTitle() + " is added as watched movie!", Toast.LENGTH_LONG).show();
            }

        });
    }


    class moviesAdapter extends ArrayAdapter<String> {
        Context context;
        String[] list;

        moviesAdapter(Context c, String[] list) {
            super(c, R.layout.selection_row, R.id.textView, list);
            this.context = c;
            this.list = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = inflater.inflate(R.layout.selection_row, parent, false);
            TextView name = (TextView) row.findViewById(R.id.textView);

            name.setText(list[position]);

            return row;
        }

    }
}



