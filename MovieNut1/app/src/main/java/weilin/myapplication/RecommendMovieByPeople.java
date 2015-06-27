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
    String displayMovies = "Movie Title     Character     Release Date" + "\n";
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
        List<PersonCredit> result = accountApi.getPeople().getPersonCredits(id).getCast();

        for (int i = 0; i < result.size(); i++) {
            displayMovies = displayMovies + result.get(i).getMovieOriginalTitle() +" -> "
                    + result.get(i).getCharacter() + "(" + result.get(i).getReleaseDate().substring(0, 4) + ")" + "\n";
            movie = accountApi.getMovies().getMovie(result.get(i).getId(), "");
            description = description + movie.getOverview() + "\n";
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
