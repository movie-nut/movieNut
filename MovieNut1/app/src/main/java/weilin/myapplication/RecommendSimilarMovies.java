package weilin.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.widget.Toast;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.Utils;
import info.movito.themoviedbapi.model.MovieDb;


/**
 * Created by WeiLin on 27/6/15.
 */
public class RecommendSimilarMovies {
    static int id;
    static String displayMovies = "Movies     Release Date" + "\n";
    static String description = "\n";
    static String[] listOfDescription;
    static String[] moviesInfo;
    static Context c;

    public RecommendSimilarMovies(String searchKeyword) {
        searchSimilarMovie(searchKeyword);
    }

    public static void searchSimilarMovie(String keyword){
       String searchKeyWord = keyword;
        permitsNetwork();

        TmdbApi accountApi = new TmdbApi("3f2950a48b75db414b1dbb148cfcad89");
        TmdbSearch searchResult = accountApi.getSearch();
        List<MovieDb> list = searchResult.searchMovie(searchKeyWord, null, "", false, null).getResults();

        getId(list);

        if (id == -1) {
            returnHomePage();
        } else {
            getListOfMovies(accountApi);
            Intent displyResults = new Intent(c, DisplayResults.class);
            displyResults.putExtra("movieInfo", moviesInfo);
            displyResults.putExtra("description", listOfDescription);
            c.startActivity(displyResults);
    }
}

    private static void returnHomePage() {
        Intent returnHome = new Intent(c, MainActivity.class);
        c.startActivity(returnHome);
      //  Toast.makeText(MainActivity.this, "Movies or peoples could not be found!", Toast.LENGTH_LONG).show();
    }



    private static void getListOfMovies(TmdbApi accountApi) {
        List<MovieDb> result = accountApi.getMovies().getSimilarMovies(id, "en", 0).getResults();
        //  Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://api.themoviedb.org/3/movie/8966/similar?api_key=3f2950a48b75db414b1dbb148cfcad89"));
        // startActivity(browserIntent);

        String image;
        for (int i = 0; i < result.size(); i++) {

            displayMovies = displayMovies + result.get(i).getOriginalTitle() + "     "
                    + result.get(i).getReleaseDate() + "\n";


            description = description + result.get(i).getOverview() + "\n";

            image = Utils.createImageUrl(accountApi, result.get(i).getPosterPath(), "original").toString();

            //   ImageView mImage = (ImageView) findViewById(R.id.imageView);
            //Uri url = Utils.createImageUrl(accountApi, result.get(i).getPosterPath(), "original");
            //     mImage.setImageURI(url);
            // mImage.setImageBitmap(BitmapFactory.decodeFile(result.get(i).getPosterPath());

        }
        moviesInfo = displayMovies.split("\\r?\\n");
        listOfDescription = description.split("\\r?\\n");
    }

    private static void getId(List<MovieDb> list) {
        if(list.size() <= 0){
           id = -1;
        } else {
            id = list.get(0).getId();
        }
    }

    private static void permitsNetwork() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

}
