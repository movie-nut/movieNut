package com.example.movienut.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import info.movito.themoviedbapi.AbstractTmdbApi;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbGenre;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.Utils;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.people.Person;

/**
 * Created by WeiLin on 12/7/15.
 */
public class RecommendMovieByGenre extends Activity {
    private TmdbApi accountApi;
    private List<Genre> genreList;
    String displayMovies;
    String description = " " + "\n";
    String[] listOfDescription;
    String[] moviesInfo;
    String[] listOfImage;
    String[] releaseDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_movie_by_people);

        String searchKeyWord = getSearchKeyword();

        permitsNetwork();

        accountApi = new TmdbApi("3f2950a48b75db414b1dbb148cfcad89");


       if(searchKeyWord.equals("UserAccess")) {
            genreList = accountApi.getGenre().getGenreList("");

           //name: action, adventure, animation, comedy, crime, documentry, drama, family, fantasy, foreign, history, horror
           //,music, mystery, romance, science fiction, tv movie, thriller, war, western.
           String[] genreType = new String[genreList.size()];
           for (int i = 0; i < genreList.size(); i++) {
               genreType[i] = genreList.get(i).getName();
           }

           ListView genreTypeList = (ListView) findViewById(R.id.listView2);
           moviesAdapter adapter = new moviesAdapter(this, genreType);
           genreTypeList.setAdapter(adapter);

           selectOneMovie(genreTypeList);

       } else {
           Intent intent = getIntent();
           String genre = intent.getStringExtra("genre").toLowerCase();
           genreList = accountApi.getGenre().getGenreList("");

           for(int i = 0; i < genreList.size(); i++){
               if(genre.equals(genreList.get(i).getName().toLowerCase())) {
                   displayMoviesBasedOnGenre(genreList.get(i).getId());
               }
           }

       }
    }

    private void selectOneMovie(ListView genreTypeList) {
        genreTypeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int idOfGenre = genreList.get(position).getId();
                displayMovies = genreList.get(position).getName() + " movies" + "\n";

                displayMoviesBasedOnGenre(idOfGenre);

            }

        });
    }

    private void displayMoviesBasedOnGenre(int idOfGenre) {
        try {
            List<MovieDb> result = accountApi.getGenre().getGenreMovies(idOfGenre, "", null, true).getResults();

            if (result == null || result.size() <= 0) {
                throw new NullPointerException();
            } else {
                getListOfMovies(result);
                Intent displyResults = new Intent(RecommendMovieByGenre.this, DisplayResults.class);
                displyResults.putExtra("movieInfo", moviesInfo);
                displyResults.putExtra("description", listOfDescription);
                displyResults.putExtra("image", listOfImage);
                displyResults.putExtra("releaseDate", releaseDates);
                startActivity(displyResults);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            returnHomePage();
        }
    }

    private void returnHomePage() {
        Intent returnHome = new Intent(this, MainActivity.class);
        startActivity(returnHome);
        this.finish();
        Toast.makeText(getApplicationContext(), "Error encountered!", Toast.LENGTH_LONG).show();
    }

    private void getListOfMovies(List<MovieDb> result) throws IOException {
        String releaseDate;

        String image = " " + "\n";
        releaseDates = new String[result.size() + 1];
        releaseDates[0] = "";
        Map<String, Boolean> map = Storage.loadMap(getApplicationContext());

        for (int i = 0; i < result.size(); i++) {
            if (map.get(String.valueOf(result.get(i).getId())) == null) {
                releaseDate = result.get(i).getReleaseDate();
                releaseDate = addReleaseDate(releaseDate, i);

                displayMovies = displayMovies + result.get(i).getOriginalTitle() + "("
                        + releaseDate + ")" + "\n";

                addDescription(result, i);
                image = addImageUrl(result, image, i);

            }
        }
        moviesInfo = displayMovies.split("\\r?\\n");
        listOfDescription = description.split("\\r?\\n");
        listOfImage = image.split("\\r?\\n");
    }

    private String addImageUrl(List<MovieDb> result, String image, int i) {
        if (Utils.createImageUrl(accountApi, result.get(i).getPosterPath(), "original") != null) {
            image = image + Utils.createImageUrl(accountApi, result.get(i).getPosterPath(), "original").toString() + "\n";

        } else {
            image = image + " " + "\n";
        }
        return image;
    }

    private void addDescription(List<MovieDb> result, int i) {
        if (result.get(i).getOverview() == null) {
            description = description + "NO DESCRIPTION YET" + "\n";
        } else {
            description = description + result.get(i).getOverview() + "\n";
        }
    }

    private String addReleaseDate(String releaseDate, int i) {
        if (releaseDate == null) {
            releaseDate = "unknown";
            releaseDates[i + 1] = "";
        } else {
            releaseDate = releaseDate.substring(0, 4);
            releaseDates[i + 1] = releaseDate;
        }
        return releaseDate;
    }

    class moviesAdapter extends ArrayAdapter<String> {
        Context context;
        String[] list;
        private int[] colors = new int[] { Color.parseColor("#fffff1d6"), Color.parseColor("#D2E4FC") };

        moviesAdapter(Context c, String[] list) {
            super(c, R.layout.selection_row, R.id.textView, list);
            this.context = c;
            this.list = list;
        }

            @Override
        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.selection_row, parent, false);
            TextView name = (TextView) row.findViewById(R.id.textView);

                int colorPos = position % colors.length;
                row.setBackgroundColor(colors[colorPos]);

                name.setText(list[position]);

            return row;
        }

    }

    private String getSearchKeyword() {
        Intent intent = getIntent();
        return intent.getStringExtra("searchKeyWord");
    }

    private void permitsNetwork() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_feature, menu);
        return true;
    }
}
