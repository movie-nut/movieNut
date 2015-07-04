package weilin.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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

        int id, positionInList;
        String displayMovies = "";
        String description;
        String image = "";
        String[] listOfImage;
        String[] listOfDescription;
        String[] moviesInfo;
    String[] releaseDates;
    String[] peopleName;
    private Menu menu;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_recommend_movie_by_people);

        String searchKeyWord = getSearchKeyword();

            permitsNetwork();

            TmdbApi accountApi = new TmdbApi("3f2950a48b75db414b1dbb148cfcad89");

            TmdbSearch searchResult = accountApi.getSearch();
            List<Person> list = searchResult.searchPerson(searchKeyWord, false, null).getResults();
/*
        peopleName = new String[list.size()];
        for(int i = 0; i < list.size(); i++){
            peopleName[i] = list.get(i).getName();
        }

        ListView peopleNameList = (ListView) findViewById(R.id.listView2);
        moviesAdapter adapter = new moviesAdapter(this, peopleName);
        peopleNameList.setAdapter(adapter);

*/
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

            List<PersonCredit> result = accountApi.getPeople().getPersonCredits(id).getCrew();
            getSelectedInfo(accountApi, result);

        }


    class moviesAdapter extends ArrayAdapter<String> {
        Context context;
        String[] list;

        moviesAdapter(Context c, String[] list) {
            super(c, R.layout.selection_row, R.id.textView,list);
            this.context = c;
            this.list = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = inflater.inflate(R.layout.selection_row, parent, false);
            TextView name = (TextView) row.findViewById(R.id.textView);
/*
            peopleName.setOnClickListener(new View.OnClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                   positionInList = position;
                }
            });
*/
            name.setText(list[position]);

            return row;
        }


    }

    private void getSelectedInfo(TmdbApi accountApi, List<PersonCredit> result) {
        String releaseDate;
        String movieTitle;
        MovieDb movie;
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

        releaseDates = new String[result.size() + 1];
        releaseDates[0] = "";

        for (int i = 0; i < result.size(); i++) {
            releaseDate = result.get(i).getReleaseDate();
            movieTitle = result.get(i).getMovieOriginalTitle();

            if (releaseDate == null) {
                releaseDate = "unknown";
                releaseDates[i + 1] = "";
            } else {
                releaseDate = releaseDate.substring(0, 4);
                releaseDates[i + 1] = releaseDate;
            }

            displayMovies = displayMovies + movieTitle +
                    "(" + releaseDate + ")" + "Position as " + result.get(i).getJob() + "\n";

            assert result.get(i).getId() <= 0 : "null id";

            movie = accountApi.getMovies().getMovie(result.get(i).getId(), "");

            if (movie.getOverview() == null || movie.getOverview().equals("")) {
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

