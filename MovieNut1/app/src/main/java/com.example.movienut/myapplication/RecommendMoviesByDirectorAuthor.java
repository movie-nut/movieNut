package com.example.movienut.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.Utils;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.people.Person;
import info.movito.themoviedbapi.model.people.PersonCredit;

/**
 * Created by WeiLin on 29/6/15.
 */
public class RecommendMoviesByDirectorAuthor extends Activity {
        int id;
        String displayMovies = "";
        String description;
        String image = "";
        String[] listOfImage;
        String[] listOfDescription;
        String[] moviesInfo;
    String[] releaseDates;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_recommend_movie_by_people);

        String searchKeyWord = getSearchKeyword();

            permitsNetwork();

            TmdbApi accountApi = new TmdbApi("3f2950a48b75db414b1dbb148cfcad89");

            TmdbSearch searchResult = accountApi.getSearch();
            List<Person> list = searchResult.searchPerson(searchKeyWord, false, null).getResults();



        try {
            if(list == null || list.size() <= 0){
                throw new NullPointerException();
            } else {
                getId(list);
                searchDirectorOrAuthor(accountApi);
            }
        } catch (NullPointerException e) {
            returnHomePage();
        }
    }

    private void searchDirectorOrAuthor(TmdbApi accountApi) {
        try {
            List<PersonCredit> result = accountApi.getPeople().getPersonCredits(id).getCrew();
            if (result == null || result.size() <= 0) {
                throw new NullPointerException();
            } else {
                getMoviesInString(accountApi, result);

                Intent displyResults = new Intent(this, DisplayResults.class);
                displyResults.putExtra("movieInfo", moviesInfo);
                displyResults.putExtra("description", listOfDescription);
                displyResults.putExtra("image", listOfImage);
                displyResults.putExtra("releaseDate", releaseDates);
                startActivity(displyResults);

                finish();
            }
        } catch (NullPointerException e) {
            returnHomePage();
        }
    }


    private void returnHomePage() {
            Intent returnHome = new Intent(this, MainActivity.class);
            startActivity(returnHome);
            this.finish();
            Toast.makeText(getApplicationContext(), "Director or author could not be found!", Toast.LENGTH_LONG).show();
        }

        private void getMoviesInString(TmdbApi accountApi, List<PersonCredit> result) {
            getSelectedInfo(accountApi, result);

        }

    private void getSelectedInfo(TmdbApi accountApi, List<PersonCredit> result) {
        String releaseDate;
        String movieTitle;
        MovieDb movie;
        getPeoplePhoto(accountApi);
        getBiolography(accountApi);

        releaseDates = new String[result.size() + 1];
        releaseDates[0] = "";

        for (int i = 0; i < result.size(); i++) {
            //if(map.get)
            releaseDate = result.get(i).getReleaseDate();
            movieTitle = result.get(i).getMovieOriginalTitle();

            releaseDate = getReleaseDates(releaseDate, i);

            displayMovies = displayMovies + movieTitle +
                    "(" + releaseDate + ")" + "Position as " + result.get(i).getJob() + "\n";

            assert result.get(i).getId() <= 0 : "null id";

            movie = accountApi.getMovies().getMovie(result.get(i).getId(), "");

            addDescription(movie);

            addImageUrl(accountApi, result, i);
        }

        moviesInfo = displayMovies.split("\\r?\\n");
        listOfDescription = description.split("\\r?\\n");
        listOfImage = image.split("\\r?\\n");
    }

    private void getBiolography(TmdbApi accountApi) {
        if(accountApi.getPeople().getPersonInfo(id).getBiography() != null){
            description = accountApi.getPeople().getPersonInfo(id).getBiography();
            description = description.replaceAll("\\n", " ");
            description = description + "\n";
        } else {
            description = "" + "\n";
        }
    }

    private void getPeoplePhoto(TmdbApi accountApi) {
        if(Utils.createImageUrl(accountApi, accountApi.getPeople().getPersonInfo(id).getProfilePath(), "original") != null) {
            image = Utils.createImageUrl(accountApi, accountApi.getPeople().getPersonInfo(id).getProfilePath(), "original").toString() + "\n";
        } else {
            image = " " + "\n";
        }
    }

    private void addImageUrl(TmdbApi accountApi, List<PersonCredit> result, int i) {
        if (Utils.createImageUrl(accountApi, result.get(i).getPosterPath(), "original") != null) {
            image = image + Utils.createImageUrl(accountApi, result.get(i).getPosterPath(), "original").toString() + "\n";
        } else {
            image = image + " " + "\n";
        }
    }

    private void addDescription(MovieDb movie) {
        if (movie.getOverview() == null || movie.getOverview().equals("")) {
            description = description + "NO DESCRIPTION YET" + "\n";
        } else {
            description = description + movie.getOverview() + "\n";
        }
    }

    private String getReleaseDates(String releaseDate, int i) {
        if (releaseDate == null) {
            releaseDate = "unknown";
            releaseDates[i + 1] = "";
        } else {
            releaseDate = releaseDate.substring(0, 4);
            releaseDates[i + 1] = releaseDate;
        }
        return releaseDate;
    }


    private void getId(List<Person> list) {
            if (list.size() <= 0) {
                id = -1;
            } else {
                displayMovies = list.get(0).getName() + "\n" + displayMovies;
                id = list.get(0).getId();
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
    getMenuInflater().inflate(R.menu.menu_recommend_movie_by_people, menu);
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

