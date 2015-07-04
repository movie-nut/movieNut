package weilin.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.Utils;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.people.Person;
import info.movito.themoviedbapi.model.people.PersonCredit;


public class RecommendMovieByActor extends Activity {
    int id;
    String displayMovies = "";
    String description = "";
    String image = "";
    String[] listOfImage;
    String[] listOfDescription;
    String[] moviesInfo;
    String[] releaseDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_movie_by_people);

        String searchKeyWord = getSearchKeyword();

        permitsNetwork();

        TmdbApi accountApi = new TmdbApi("3f2950a48b75db414b1dbb148cfcad89");

        TmdbSearch searchResult = accountApi.getSearch();
        List<Person> list = searchResult.searchPerson(searchKeyWord, false, null).getResults();

        getId(list);

        if (id == -1) {
            returnHomePage();
        } else {
            getMoviesInString(accountApi);

            Intent displyResults = new Intent(this, DisplayResults.class);
            displyResults.putExtra("movieInfo", moviesInfo);
            displyResults.putExtra("description", listOfDescription);
            displyResults.putExtra("image", listOfImage);
            displyResults.putExtra("releaseDate", releaseDates);
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

        if(Utils.createImageUrl(accountApi, accountApi.getPeople().getPersonInfo(id).getProfilePath(), "original") != null) {
            image = Utils.createImageUrl(accountApi, accountApi.getPeople().getPersonInfo(id).getProfilePath(), "original").toString() + "\n";
        } else {
            image = " " + "\n";
        }

        if(accountApi.getPeople().getPersonInfo(id).getBiography() != null){
            description = accountApi.getPeople().getPersonInfo(id).getBiography();
            description = description.replaceAll("\\n", " ");
            description = description + "\n";
        } else {
            description = "" + "\n";
        }


        //    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(profile));
        //  startActivity(browserIntent);
        releaseDates = new String[result.size() + 1];
        releaseDates[0] = " ";

        for (int i = 0; i < result.size(); i++) {

            releaseDate = result.get(i).getReleaseDate();
            movieTitle = result.get(i).getMovieOriginalTitle();

            if (result.get(i).getCharacter() == null || result.get(i).getCharacter().equals("")) {
                character = "NOT KNOWN YET";
            } else {
                character = result.get(i).getCharacter();
            }

            if (releaseDate == null) {
                releaseDate = "unknown";
                releaseDates[i + 1] = " ";
            } else {
                releaseDate = releaseDate.substring(0, 4);
                releaseDates[i + 1] = releaseDate;
            }

            displayMovies = displayMovies + movieTitle +
                    "(" + releaseDate + ")" + " act as "
                    + character + "\n";

            assert result.get(i).getId() <= 0 : "null id";

            movie = accountApi.getMovies().getMovie(result.get(i).getId(), "");

            if (movie.getOverview() == null) {
                description = description + "NO DESCRIPTION YET" + "\n";
            } else {
                description = description + movie.getOverview() + "\n";
            }

            if (Utils.createImageUrl(accountApi, result.get(i).getPosterPath(), "original") != null) {
                image = image + Utils.createImageUrl(accountApi, result.get(i).getPosterPath(), "original").toString() + "\n";

            } else {
                image = image + " " + "\n";
            }
        }

        moviesInfo = displayMovies.split("\\r?\\n");
        listOfDescription = description.split("\\r?\\n");
        listOfImage = image.split("\\r?\\n");

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

}
