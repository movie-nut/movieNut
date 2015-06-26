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


public class RecommendMoviesByCompany extends Activity {
   private String displayMovies;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_movies_by_company);

        String searchKeyWord = getSearchKeyword();

        permitsNetwork();

        TmdbApi accountApi = new TmdbApi("3f2950a48b75db414b1dbb148cfcad89");

        TmdbSearch searchResult = accountApi.getSearch();
        List<Company> list = searchResult.searchCompany(searchKeyWord, 0).getResults();

        getId(list);

        getListOfMovies(searchKeyWord, accountApi, id);

        display();
    }

    private void getId(List<Company> list) {
        if(list.size() <= 0){
            Toast.makeText(getApplicationContext(), "Keyword typed not found!!!",
                        Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            id = list.get(0).getId();
        }
    }

    private void display() {
        TextView textOut = (TextView) findViewById(R.id.textView);
        textOut.setText(displayMovies);
    }

    private void getListOfMovies(String searchKeyWord, TmdbApi accountApi, int id) {
        List<Collection> result = accountApi.getCompany().getCompanyMovies(id, "", 0).getResults();

        displayMovies = "Company" + searchKeyWord + "\n";
        for (int i = 0; i < result.size(); i++) {
            displayMovies = displayMovies + result.get(i).getName() + "\n";
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
