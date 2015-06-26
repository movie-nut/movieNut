package weilin.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.people.Person;
import info.movito.themoviedbapi.model.people.PersonCredit;


public class RecommendMovieByPeople extends Activity {
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) throws IllegalArgumentException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_movie_by_people);
        String searchKeyWord = getSearchKeyword();

        permitsNetwork();

        TmdbApi accountApi = new TmdbApi("3f2950a48b75db414b1dbb148cfcad89");

        TmdbSearch searchResult = accountApi.getSearch();
        List<Person> list = searchResult.searchPerson(searchKeyWord, false, null).getResults();

        getId(list);

        String displayMovies = getMoviesInString(accountApi);

        display(displayMovies);
    }

    private String getMoviesInString(TmdbApi accountApi) {
        List<PersonCredit> result = accountApi.getPeople().getPersonCredits(id).getCast();

        String displayMovies = "Movie Title     Character     Release Date" + "\n";


        for (int i = 0; i < result.size(); i++) {
            displayMovies = displayMovies + result.get(i).getMovieOriginalTitle() + "     "
                    + result.get(i).getCharacter() + "     " + result.get(i).getReleaseDate() + "\n";
        }
        return displayMovies;
    }

    private void display(String displayMovies) {
        TextView textout = (TextView) findViewById(R.id.textView2);
        textout.setText(displayMovies);
    }

    private void getId(List<Person> list) {
        try {
            if(list.size() <= 0){
                throw new IllegalArgumentException("Search Keyword is not detected!");
            } else {
                id = list.get(0).getId();
            }

        } catch (IllegalArgumentException exception) {
            throw exception;
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
