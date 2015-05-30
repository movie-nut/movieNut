package weilin.myapplication.java;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;
import weilin.myapplication.R;

public class MovieDetail extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
        String searchKeyWord = intent.getStringExtra("searchKeyWord");

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

            TmdbApi accountApi = new TmdbApi("3f2950a48b75db414b1dbb148cfcad89");
            //  .getMovies();
            TmdbSearch searchResult = accountApi.getSearch();
            List<MovieDb> list = searchResult.searchMovie(searchKeyWord, null, "en", false, null).getResults();
            // MovieDb movie = movies.Movie(searchKeyWord, "en");

        int id;
        id = list.get(0).getId();
        TmdbMovies movies = accountApi.getMovies();
        MovieDb movie = movies.getMovie(id, "en");
        List<MovieDb> similarMovies = movie.getSimilarMovies();

        String m = "";
        for(int i = 0; i < list.size(); i++){
            m = m + list.get(i).getOriginalTitle() + "\n";
        }

        TextView textout = (TextView) findViewById(R.id.textView2);
        textout.setText(m);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
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
