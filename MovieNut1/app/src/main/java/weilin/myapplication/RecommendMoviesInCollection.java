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
import info.movito.themoviedbapi.model.CollectionInfo;
import info.movito.themoviedbapi.model.people.Person;
import info.movito.themoviedbapi.model.people.PersonCredit;


public class RecommendMoviesInCollection extends Activity {
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_movies_in_collection);

        Intent intent = getIntent();
        String searchKeyWord = intent.getStringExtra("searchKeyWord");

        permitsNetwork();

        TmdbApi accountApi = new TmdbApi("3f2950a48b75db414b1dbb148cfcad89");
        TmdbSearch searchResult = accountApi.getSearch();
        List<Collection> list = searchResult.searchCollection(searchKeyWord, "", null).getResults();

        try {
            getId(list);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String displayMovies = getMoviesInString(accountApi);

        display(displayMovies);
    }

    private String getMoviesInString(TmdbApi accountApi) {
        List<Collection> result = accountApi.getCollections().getCollectionInfo(id, "").getParts();

        String displayMovies = "Movie Title" + "\n";


        for (int i = 0; i < result.size(); i++) {
            displayMovies = displayMovies + result.get(i).getName() + "\n";
                 //   + result.get(i).getCharacter() + "     " + result.get(i).getReleaseDate() + "\n";

        }
        return displayMovies;
    }

    private void getId(List<Collection> list) {
        if (list.size() <= 0) {
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

    private void display(String displayMovies) {
        TextView textout = (TextView) findViewById(R.id.textView2);
        textout.setText(displayMovies);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recommend_movies_in_collection, menu);
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
