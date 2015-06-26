package weilin.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;
import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.Artwork;
import info.movito.themoviedbapi.model.MovieDb;

public class RecommendSimilarMovie extends Activity {
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) throws IllegalArgumentException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar_movie);

        String searchKeyWord = getSearchKeyword();

        permitsNetwork();

        TmdbApi accountApi = new TmdbApi("3f2950a48b75db414b1dbb148cfcad89");
        TmdbSearch searchResult = accountApi.getSearch();
        List<MovieDb> list = searchResult.searchMovie(searchKeyWord, null, "", false, null).getResults();

        getId(list);

        if (id == -1) {
            returnHomePage();
        } else {
           TmdbMovies movies = accountApi.getMovies();
           MovieDb movie = movies.getMovie(id, "en");
//        List<MovieDb> similarMovies = movie.getSimilarMovies();

          String displayMovies = getListOfMovies(accountApi);

         display(displayMovies);
    }
    }

    private void returnHomePage() {
        Intent returnHome = new Intent(this, MainActivity.class);
        startActivity(returnHome);
        this.finish();
        Toast.makeText(getApplicationContext(), "Movies or peoples could not be found!", Toast.LENGTH_LONG).show();
    }

    private String getListOfMovies(TmdbApi accountApi) {
        List<MovieDb> result = accountApi.getMovies().getSimilarMovies(id, "en", 0).getResults();
        //  Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://api.themoviedb.org/3/movie/8966/similar?api_key=3f2950a48b75db414b1dbb148cfcad89"));
        // startActivity(browserIntent);

        String displayMovies = "Movies     Release Date";

        String image;
        for (int i = 0; i < result.size(); i++) {

            displayMovies = displayMovies + result.get(i).getOriginalTitle() + "     "
                    + result.get(i).getReleaseDate() + "\n";

            ImageView mImage = (ImageView) findViewById(R.id.imageView);
            mImage.setImageBitmap(BitmapFactory.decodeFile(result.get(i).getPosterPath()));

        }
        return displayMovies;
    }

    private void display(String displayMovies) {
        TextView textout = (TextView) findViewById(R.id.textView2);
        textout.setText(displayMovies);
    }

    private String getSearchKeyword() {
        Intent intent = getIntent();
        return intent.getStringExtra("searchKeyWord");
    }

    private void getId(List<MovieDb> list) {
        if(list.size() <= 0){
            Toast.makeText(getApplicationContext(), "Movies or peoples could not be found!", Toast.LENGTH_LONG).show();
            Intent returnHome = new Intent(this, MainActivity.class);
            startActivity(returnHome);
            finish();
        } else {
            id = list.get(0).getId();
        }
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
        getMenuInflater().inflate(R.menu.menu_similar_movie, menu);
        return true;
    }
/*
    private final void focusOnButtons(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ScrollView sv = (ScrollView)findViewById(R.id.scrl);
                sv.scrollTo(0, sv.getBottom());
            }
        },1000);
    }
*/
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
