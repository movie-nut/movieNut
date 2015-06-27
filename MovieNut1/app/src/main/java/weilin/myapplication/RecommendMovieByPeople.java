package weilin.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.people.Person;
import info.movito.themoviedbapi.model.people.PersonCredit;


public class RecommendMovieByPeople extends Activity {
    int id;
    String displayMovies = "";
    String description = "\n" + "\n";
    String[] listOfDescription;
    String[] moviesInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String searchKeyWord = getSearchKeyword();

        permitsNetwork();

        TmdbApi accountApi = new TmdbApi("3f2950a48b75db414b1dbb148cfcad89");

        TmdbSearch searchResult = accountApi.getSearch();
        List<Person> list = searchResult.searchPerson(searchKeyWord, false, null).getResults();

        getId(list);

        if(id == -1){
            returnHomePage();
        } else {
            getMoviesInString(accountApi);
            Intent displyResults = new Intent(this, DisplayResults.class);
            displyResults.putExtra("movieInfo", moviesInfo);
            displyResults.putExtra("description", listOfDescription);
            startActivity(displyResults);
            finish();
        }
    }

    private void returnHomePage() {
        Intent returnHome = new Intent(this, MainActivity.class);
        startActivity(returnHome);
        this.finish();
        Toast.makeText(getApplicationContext(), "Movies or peoples could not be found!", Toast.LENGTH_LONG).show();
    }

    private void getMoviesInString(TmdbApi accountApi) {
        MovieDb movie;
        String releaseDate, movieTitle, character;
        List<PersonCredit> result = accountApi.getPeople().getPersonCredits(id).getCast();

        //String bio = accountApi.getPeople().getPersonInfo(id).getBiography();
       // displayMovies = displayMovies + " " + bio;
        for (int i = 0; i < result.size(); i++) {
            releaseDate = result.get(i).getReleaseDate();
            movieTitle = result.get(i).getMovieOriginalTitle();

            if(result.get(i).getCharacter().equals("")){
                character = "NOT KNOWN YET";
            } else {
                character = result.get(i).getCharacter();
            }

            if(releaseDate == null){
                releaseDate = "unknown";
            } else {
                releaseDate = releaseDate.substring(0, 4);
            }

                displayMovies = displayMovies + movieTitle +
                        "(" + releaseDate + ")" +" act as "
                        + character + "\n";

                assert result.get(i).getId() <= 0 : "null id";

                movie = accountApi.getMovies().getMovie(result.get(i).getId(), "");
                //description = description + "" + "\n";
                if(movie.getOverview().equals("")){
                    description = description + "NO DESCRIPTION YET" + "\n";
                } else {
                    description = description + movie.getOverview() + "\n";
                }
        }
        moviesInfo = displayMovies.split("\\r?\\n");
        listOfDescription = description.split("\\r?\\n");

    }

    private void getId(List<Person> list) {
        if(list.size() <= 0){
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


}
