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

public class DisplayResults extends Activity {
    int id;
    String[] moviesInfo;
    String[] description;
    String[] image;

    @Override
    protected void onCreate(Bundle savedInstanceState) throws IllegalArgumentException {
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
      if(image.length > position && !image[position].equals("")) {
          try {
              url = new URL(image[position]);
          } catch (MalformedURLException e) {
              e.printStackTrace();
          }
          Bitmap bmp = null;
          try {
              bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
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
