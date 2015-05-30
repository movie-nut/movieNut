package weilin.myapplication;

import android.os.AsyncTask;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;

/**
 * Created by WeiLin on 29/5/15.
 */


public class MoviesDetails{
}


/*
public class MoviesDetails extends AsyncTask {

    private String movieTitle;
    private String api;

    public MoviesDetails() {
        api = "3f2950a48b75db414b1dbb148cfcad89";
        doInBackground(api);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        return null;
    }

    @Override
    protected Object doInBackground(String api) {

        TmdbMovies movies = new TmdbApi(api).getMovies();
        MovieDb movie = movies.getMovie(5444, "en");
        // setMovieTitle(movie.getOriginalTitle());
        return;
    }


    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieTitle() {
        return movieTitle;
    }
}
   // private void getDetailEditable (movieOutText) {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
      }

     //   TmdbMovies movies = new TmdbApi("3f2950a48b75db414b1dbb148cfcad89").getMovies();
       // MovieDb movie = movies.getMovie(movieOutText, LANGUAGE_ENGLISH, TmdbMovies.MovieMethod.values());
   // }

*/
