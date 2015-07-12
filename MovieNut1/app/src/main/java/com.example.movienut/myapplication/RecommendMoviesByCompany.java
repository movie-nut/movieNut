package com.example.movienut.myapplication;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.Utils;
import info.movito.themoviedbapi.model.Collection;
import info.movito.themoviedbapi.model.Company;
import info.movito.themoviedbapi.model.MovieDb;


public class RecommendMoviesByCompany extends Activity {
    String displayMovies = "";
    String description = " " + "\n";
    String image = " " + "\n";
    String[] listOfImage;
    String[] listOfDescription;
    String[] moviesInfo;
    String[] companyName;
    String[] releaseDates;
    int idOfMovies = -1;
    String searchKeyWord;
    TmdbApi accountApi;
    List<Company> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_movie_by_people);

        searchKeyWord = getSearchKeyword();

        permitsNetwork();

        accountApi = new TmdbApi("3f2950a48b75db414b1dbb148cfcad89");

        TmdbSearch searchResult = accountApi.getSearch();
        list = searchResult.searchCompany(searchKeyWord, 0).getResults();

        try {
            if (list == null || list.size() <= 0) {
                throw new NullPointerException();
            } else {
                companyName = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    companyName[i] = list.get(i).getName();
                }

                ListView peopleNameList = (ListView) findViewById(R.id.listView2);
                moviesAdapter adapter = new moviesAdapter(this, companyName);
                peopleNameList.setAdapter(adapter);

                selectOneCompany(peopleNameList);
            }
        } catch (NullPointerException e) {
            returnHomePage();
        }
    }

    private void selectOneCompany(ListView peopleNameList) {
        peopleNameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                getId(list, position);

                try {
                    List<Collection> result = accountApi.getCompany().getCompanyMovies(idOfMovies, "", 0).getResults();
                    if (result == null || result.size() <= 0) {
                        throw new NullPointerException();
                    } else {
                        getListOfMovies(accountApi, result);
                        Intent displyResults = new Intent(RecommendMoviesByCompany.this, DisplayResults.class);
                        displyResults.putExtra("movieInfo", moviesInfo);
                        displyResults.putExtra("description", listOfDescription);
                        displyResults.putExtra("image", listOfImage);
                        displyResults.putExtra("releaseDate", releaseDates);
                        startActivity(displyResults);
                    }
                } catch (NullPointerException e) {
                    returnHomePage();
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

    private void getId(List<Company> list, int position) {
        if(list.size() > 0){
            idOfMovies = list.get(position).getId();
            displayMovies = "Movies made by " + " " + list.get(position).getName() + "\n";
        }
    }

    private void getListOfMovies(TmdbApi accountApi, List<Collection> result) {
        String releaseDate;

        MovieDb movie;
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

            displayMovies = displayMovies + result.get(i).getName() + "(" + releaseDate + ")" + "\n";
            movie = accountApi.getMovies().getMovie(result.get(i).getId(), "");

            if(movie.getOverview() == null){
                description = description + "NO DESCRIPTION YET" + "\n";
            } else {
                description = description + movie.getOverview() + "\n";
            }
            if (Utils.createImageUrl(accountApi, result.get(i).getPosterPath(), "original") != null) {
                image = image + Utils.createImageUrl(accountApi, result.get(i).getPosterPath(), "original").toString() + "\n";

            } else {
                image = image + "\n";
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
