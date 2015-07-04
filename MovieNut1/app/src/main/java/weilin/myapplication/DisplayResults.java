package weilin.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class DisplayResults extends Activity {
    String[] moviesInfo;
    String[] description;
    String[] image;
    String[] releaseDates;
    ArrayList<Movies> movies = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_results);

        getMoviesInfo();

        ListView list = (ListView) findViewById(R.id.listView);
        moviesAdapter adapter = new moviesAdapter(this, moviesInfo, description, image);
        list.setAdapter(adapter);
    }

    private void getMoviesInfo() {
        Intent intent = getIntent();
        moviesInfo = intent.getStringArrayExtra("movieInfo");
        description = intent.getStringArrayExtra("description");
        image = intent.getStringArrayExtra("image");
        releaseDates = intent.getStringArrayExtra("releaseDate");

        try {
            storeInMovieClass();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Collections.sort(movies, new Comparator<Movies>() {
            public int compare(Movies o1, Movies o2) {
                if (o1.getDate() == "" || o2.getDate() == "" || o1.getDate() == null || o2.getDate() == null) {
                    return 0;
                }
                return o2.getDate().compareTo(o1.getDate());
            }
        });

        storeBackIntoString();

    }

    private void storeBackIntoString() {
        for(int i = 0; i < movies.size(); i++){
            moviesInfo[i + 1] = movies.get(i).getMovieTitle();
            description[i + 1] = movies.get(i).getDescription();
            image[i + 1] = movies.get(i).getImageURL();
        }
    }

    private void storeInMovieClass() throws ParseException {

        for(int i = 1; i < moviesInfo.length; i++){
            movies.add(new Movies());
            movies.get(i - 1).setMovieTitle(moviesInfo[i]);
            movies.get(i - 1).setDecription(description[i]);
            movies.get(i - 1).setImageURL(image[i]);
            movies.get(i - 1).setDate(releaseDates[i]);
        }
    }

    class moviesAdapter extends ArrayAdapter<String> {
    Context context;
    String[] moviesInfo;
    String[] description;
    String[] image;

    //put images int image[]
    moviesAdapter(Context c, String[] moviesInfo, String[] description, String[] image){
        super(c, R.layout.single_row, R.id.textView3,moviesInfo);
        this.context = c;
        this.moviesInfo = moviesInfo;
        this.description = description;
        this.image = image;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.single_row, parent, false);
      ImageView myImage = (ImageView) row.findViewById(R.id.imageView);
        TextView movieTitles = (TextView) row.findViewById(R.id.textView3);
        TextView myDescription = (TextView) row.findViewById(R.id.textView4);

        movieTitles.setText(moviesInfo[position]);
        myDescription.setText(description[position]);

        URL url = null;
      if(image.length > position && (!image[position].equals("") || !(image[position] == null))) {
          try {
              url = new URL(image[position]);
          } catch (MalformedURLException e) {
              e.printStackTrace();
          }
          Bitmap bmp = null;
          try {
              if(url != null){
                  bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
              }
          } catch (IOException e) {
              e.printStackTrace();
          }
          myImage.setImageBitmap(bmp);
      }
        return row;
    }
}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_results, menu);
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
