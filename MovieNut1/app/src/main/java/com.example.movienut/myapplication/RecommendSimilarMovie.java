package com.example.movienut.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.Utils;
import info.movito.themoviedbapi.model.MovieDb;

public class RecommendSimilarMovie extends Activity {
    int idOfMovies;
    String displayMovies = "";
    String description = " " + "\n";
    String[] listOfDescription;
    String[] moviesInfo;
    String[] listOfImage;
    String[] releaseDates;
    List<MovieDb> list;
    int[] positionOfSimilarTitle;
    TmdbApi accountApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_movie_by_people);

        String searchKeyWord = getSearchKeyword();

        permitsNetwork();

        accountApi = new TmdbApi("3f2950a48b75db414b1dbb148cfcad89");
        TmdbSearch searchResult = accountApi.getSearch();
        list = searchResult.searchMovie(searchKeyWord, null, "", false, null).getResults();
        searchKeyWord = searchKeyWord.toUpperCase();

        try {
            if (list == null || list.size() <= 0) {
                throw new NullPointerException();
            } else {
                handleSimilarMovie(searchKeyWord);
            }

        } catch (NullPointerException e) {
            returnHomePage();
        }
    }

    private void handleSimilarMovie(String searchKeyWord) {
        String[] moviesName = new String[list.size()];
        String[] description = new String[list.size()];
        String releaseDate;
        int index = 0;

        for (int i = 0; i < list.size(); i++) {

                if (list.get(i).getReleaseDate() == null) {
                    releaseDate = "UNKNOWN";
                } else {
                    releaseDate = list.get(i).getReleaseDate().substring(0, 4);
                }

                moviesName[i] = list.get(i).getOriginalTitle() + "(" + releaseDate + ")";
                description[i] = list.get(i).getOverview();
                //positionOfSimilarTitle[index] = i;
                //index++;
            }
        ListView movieList = (ListView) findViewById(R.id.listView2);
        moviesAdapter adapter = new moviesAdapter(this, moviesName, description);
        movieList.setAdapter(adapter);

        selectOneMovie(movieList);

      //  evulateSimilarTitle(moviesName, description, index);
    }

    private void evulateSimilarTitle(String[] moviesName, String[] description, int index) {
        if (index > 1) {
            ListView movieList = (ListView) findViewById(R.id.listView2);
            moviesAdapter adapter = new moviesAdapter(this, moviesName, description);
            movieList.setAdapter(adapter);

            selectOneMovie(movieList);
        } else if (index == 1) {
            getId(list, positionOfSimilarTitle[0]);
            searchSimilarMovieDirectly();
        }
    }

    private void searchSimilarMovieDirectly() {
        try {
            List<MovieDb> result = accountApi.getMovies().getSimilarMovies(idOfMovies, "", 0).getResults();
            if (result == null || result.size() <= 0) {
                throw new NullPointerException();
            } else {

                getListOfMovies(result);
                startAnotherActivity(moviesInfo, listOfDescription, listOfImage, releaseDates);
                finish();
            }
        }catch (NullPointerException e) {
            returnHomePage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void selectOneMovie(ListView peopleNameList) {
        peopleNameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                getId(list, position);
                try {
                    List<MovieDb> result = accountApi.getMovies().getSimilarMovies(idOfMovies, "", 0).getResults();

                    if (result == null || result.size() <= 0) {
                        throw new NullPointerException();
                    } else {
                        getListOfMovies(result);
                        startAnotherActivity(moviesInfo, listOfDescription, listOfImage, releaseDates);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    returnHomePage();
                }

            }
        });
    }

    private void startAnotherActivity(String[] moviesInfo, String[] listOfDescription, String[] listOfImage, String[] releaseDates) {
        Intent displyResults = new Intent(RecommendSimilarMovie.this, DisplayResults.class);
        displyResults.putExtra("movieInfo", moviesInfo);
        displyResults.putExtra("description", listOfDescription);
        displyResults.putExtra("image", listOfImage);
        displyResults.putExtra("releaseDate", releaseDates);
        startActivity(displyResults);
    }


    class moviesAdapter extends ArrayAdapter<String> {
        Context context;
        String[] list;
        String[] description;
        private int[] colors = new int[] { Color.parseColor("#fffff1d6"), Color.parseColor("#D2E4FC") };

    moviesAdapter(Context c, String[] titleLists, String[] description) {
        super(c, R.layout.selection_row, R.id.textView, titleLists);
        this.context = c;
        this.list = titleLists;
        this.description = description;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.selection_row, parent, false);
        TextView name = (TextView) row.findViewById(R.id.textView);
        TextView descriptionOut = (TextView) row.findViewById(R.id.textView2);
        descriptionOut.setVisibility(View.VISIBLE);

        int colorPos = position % colors.length;
        row.setBackgroundColor(colors[colorPos]);

        descriptionOut.setText(description[position]);
        name.setText(list[position]);

        return row;
    }

}

    private void returnHomePage() {
        Intent returnHome = new Intent(this, MainActivity.class);
        startActivity(returnHome);
        this.finish();
        Toast.makeText(getApplicationContext(), "Movies could not be found!", Toast.LENGTH_LONG).show();
    }

    private void getListOfMovies(List<MovieDb> result) throws IOException {
        String releaseDate;
        //  Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://api.themoviedb.org/3/movie/8966/similar?api_key=3f2950a48b75db414b1dbb148cfcad89"));
        // startActivity(browserIntent);

        String image = " " + "\n";
        releaseDates = new String[result.size() + 1];
        releaseDates[0] = "";
        Map<String, Boolean> map = Storage.loadMap(getApplicationContext());

        image = getMoviesInfo(result, image, map);
        moviesInfo = displayMovies.split("\\r?\\n");
         listOfDescription = description.split("\\r?\\n");
        listOfImage = image.split("\\r?\\n");
    }

    private String getMoviesInfo(List<MovieDb> result, String image, Map<String, Boolean> map) {
        String releaseDate;
        for (int i = 0; i < result.size(); i++) {
            if (map.get(String.valueOf(result.get(i).getId())) == null) {
                releaseDate = result.get(i).getReleaseDate();
                releaseDate = addReleaseDate(releaseDate, i);

                displayMovies = displayMovies + result.get(i).getOriginalTitle() + "("
                        + releaseDate + ")" + "\n";

                addDescription(result, i);
                image = addImageUrl(result, image, i);

            }
        }
        return image;
    }

    private String addImageUrl(List<MovieDb> result, String image, int i) {
        if (Utils.createImageUrl(accountApi, result.get(i).getPosterPath(), "original") != null) {
            image = image + Utils.createImageUrl(accountApi, result.get(i).getPosterPath(), "original").toString() + "\n";

        } else {
            image = image + " " + "\n";
        }
        return image;
    }

    private void addDescription(List<MovieDb> result, int i) {
        if (result.get(i).getOverview() == null) {
            description = description + "NO DESCRIPTION YET" + "\n";
        } else {
            description = description + result.get(i).getOverview() + "\n";
        }
    }

    private String addReleaseDate(String releaseDate, int i) {
        if (releaseDate == null) {
            releaseDate = "unknown";
            releaseDates[i + 1] = "";
        } else {
            releaseDate = releaseDate.substring(0, 4);
            releaseDates[i + 1] = releaseDate;
        }
        return releaseDate;
    }

    private String getSearchKeyword() {
        Intent intent = getIntent();
        return intent.getStringExtra("searchKeyWord");
    }

    private void getId(List<MovieDb> list, int position) {
            idOfMovies = list.get(position).getId();
            displayMovies = "Similar Movies of " + list.get(position).getOriginalTitle() + "\n";

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
