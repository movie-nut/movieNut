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
import info.movito.themoviedbapi.model.Collection;
import info.movito.themoviedbapi.model.Company;
import info.movito.themoviedbapi.model.MovieDb;


public class RecommendMoviesByCompany extends Activity {
    String displayMovies = "";
    String description = "\n";
    String[] listOfDescription;
    String[] moviesInfo;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        String searchKeyWord = getSearchKeyword();

        permitsNetwork();

        TmdbApi accountApi = new TmdbApi("3f2950a48b75db414b1dbb148cfcad89");

        TmdbSearch searchResult = accountApi.getSearch();
        List<Company> list = searchResult.searchCompany(searchKeyWord, 0).getResults();

        getId(list);

        if(id == -1){
            returnHomePage();
        } else {

            getListOfMovies(searchKeyWord, accountApi, id);
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

    private void getId(List<Company> list) {
        if(list.size() <= 0){
            id = -1;
        } else {
            id = list.get(0).getId();
        }
    }

    private void getListOfMovies(String searchKeyWord, TmdbApi accountApi, int id) {
        List<Collection> result = accountApi.getCompany().getCompanyMovies(id, "", 0).getResults();
        String releaseDate;
        displayMovies = "Company" + " " + searchKeyWord + "\n";
        MovieDb movie;



        for (int i = 0; i < result.size(); i++) {
            releaseDate = result.get(i).getReleaseDate();
            if(releaseDate == null){
                releaseDate = "unknown";
            } else {
                releaseDate = releaseDate.substring(0, 4);
            }

            displayMovies = displayMovies + result.get(i).getName() + "(" + releaseDate + ")" + "\n";
            movie = accountApi.getMovies().getMovie(result.get(i).getId(), "");

            if(movie.getOverview().equals("")){
                description = description + "NO DESCRIPTION YET" + "\n";
            } else {
                description = description + movie.getOverview() + "\n";
            }
        }
        moviesInfo = displayMovies.split("\\r?\\n");
        listOfDescription = description.split("\\r?\\n");
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
        getMenuInflater().inflate(R.menu.menu_recommend_movies_by_company, menu);
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
