package weilin.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.Utils;
import info.movito.themoviedbapi.model.Artwork;
import info.movito.themoviedbapi.model.MovieDb;

public class RecommendSimilarMovie extends Activity {
    int idOfMovies;
    String displayMovies = "";
    String description = "\n";
    String[] listOfDescription;
    String[] moviesInfo;
    String[] listOfImage;
    String[] releaseDates;
    List<MovieDb> list;
    TmdbApi accountApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_movie_by_people);

        String searchKeyWord = getSearchKeyword();
        // displayMovies = "similar movies as" + searchKeyWord + "/n";

        permitsNetwork();

        accountApi = new TmdbApi("3f2950a48b75db414b1dbb148cfcad89");
        TmdbSearch searchResult = accountApi.getSearch();
        list = searchResult.searchMovie(searchKeyWord, null, "", false, null).getResults();

        String[] moviesName = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            moviesName[i] = list.get(i).getOriginalTitle() + "\n" + list.get(i).getOverview();
        }

        ListView peopleNameList = (ListView) findViewById(R.id.listView2);
        moviesAdapter adapter = new moviesAdapter(this, moviesName);
        peopleNameList.setAdapter(adapter);

        peopleNameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                getId(list, position);

                if (idOfMovies == -1) {
                    returnHomePage();
                } else {

                    try {
                        getListOfMovies();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent displyResults = new Intent(RecommendSimilarMovie.this, DisplayResults.class);
                    displyResults.putExtra("movieInfo", moviesInfo);
                    displyResults.putExtra("description", listOfDescription);
                    displyResults.putExtra("image", listOfImage);
                    displyResults.putExtra("releaseDate", releaseDates);
                    startActivity(displyResults);
                }
            }
        });

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

        name.setText(list[position]);

        return row;
    }

}

    private void returnHomePage() {
        Intent returnHome = new Intent(this, MainActivity.class);
        startActivity(returnHome);
        this.finish();
        Toast.makeText(getApplicationContext(), "Movies or peoples could not be found!", Toast.LENGTH_LONG).show();
    }

    private void getListOfMovies() throws IOException {
        String releaseDate;
        List<MovieDb> result = accountApi.getMovies().getSimilarMovies(idOfMovies, "", 0).getResults();
        //  Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://api.themoviedb.org/3/movie/8966/similar?api_key=3f2950a48b75db414b1dbb148cfcad89"));
        // startActivity(browserIntent);

        String image = " " + "\n";
        releaseDates = new String[result.size() + 1];
        releaseDates[0] = "";

        for (int i = 0; i < result.size(); i++) {
            releaseDate = result.get(i).getReleaseDate();
            if(releaseDate == null){
                releaseDate = "unknown";
                releaseDates[i + 1] = "";
            } else {
                releaseDate = releaseDate.substring(0, 4);
                releaseDates[i + 1] = releaseDate;
            }

            displayMovies = displayMovies + result.get(i).getOriginalTitle() + "("
                    + releaseDate + ")" + "\n";

            if(result.get(i).getOverview() == null){
                description = description + "NO DESCRIPTION YET" + "\n";
            } else {
                description = description + result.get(i).getOverview() + "\n";
            }
            if(Utils.createImageUrl(accountApi, result.get(i).getPosterPath(), "original") != null) {
                image = image + Utils.createImageUrl(accountApi, result.get(i).getPosterPath(), "original").toString() + "\n";

            } else {
                image = image + " " + "\n";
            }

        }
         moviesInfo = displayMovies.split("\\r?\\n");
         listOfDescription = description.split("\\r?\\n");
        listOfImage = image.split("\\r?\\n");
    }

    private String getSearchKeyword() {
        Intent intent = getIntent();
        return intent.getStringExtra("searchKeyWord");
    }

    private void getId(List<MovieDb> list, int position) {
        if(list.size() <= 0){
            idOfMovies = -1;
        } else {
            idOfMovies = list.get(position).getId();
            displayMovies = "Similar Movies of " + list.get(position).getOriginalTitle() + "\n";
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
