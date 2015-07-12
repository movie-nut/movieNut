package com.example.movienut.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
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

import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class DisplayResults extends Activity {
    String[] moviesInfo;
    String[] description;
    String[] image;
    String[] releaseDates;
    ArrayList<Movies> movies = new ArrayList<>();
    private HashMap<String, Bitmap> pictures = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_results);

        getMoviesInfo();
        //  getImageStored();

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
        for (int i = 0; i < movies.size(); i++) {
            moviesInfo[i + 1] = movies.get(i).getMovieTitle();
            description[i + 1] = movies.get(i).getDescription();
            image[i + 1] = movies.get(i).getImageURL();
        }
    }

    private void storeInMovieClass() throws ParseException {

        for (int i = 1; i < moviesInfo.length; i++) {
            movies.add(new Movies());
            movies.get(i - 1).setMovieTitle(moviesInfo[i]);
            movies.get(i - 1).setDecription(description[i]);
            movies.get(i - 1).setImageURL(image[i]);
            movies.get(i - 1).setDate(releaseDates[i]);
        }
    }

    public void getImageStored() {
        //  if()
    }

    class moviesAdapter extends ArrayAdapter<String> implements weilin.myapplication.moviesAdapter {
        Context context;
        String[] moviesInfo;
        String[] description;
        String[] image;

        //put images int image[]
        moviesAdapter(Context c, String[] moviesInfo, String[] description, String[] image) {
            super(c, R.layout.single_row, R.id.textView3, moviesInfo);
            this.context = c;
            this.moviesInfo = moviesInfo;
            this.description = description;
            this.image = image;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = inflater.inflate(R.layout.single_row, parent, false);
            ImageView myImage = (ImageView) row.findViewById(R.id.imageView);
            TextView movieTitles = (TextView) row.findViewById(R.id.textView3);
            TextView myDescription = (TextView) row.findViewById(R.id.textView4);

            movieTitles.setText(moviesInfo[position]);
            myDescription.setText(description[position]);

            if (image.length > position && (!image[position].equals("") || !(image[position] == null))) {
                Bitmap bmp;
//                ImageView view = (ImageView) convertView;
  //              if (view == null) {
    //                view = new ImageView(context);
      //          }
                String url = null;
                try {
                    url = new URL(image[position]).toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

               // Picasso.with(context).load(url).into(myImage);
                Picasso.with(context)
                        .load(url)
                        .resize(50, 50)
                        .centerCrop()
                        .into(myImage);
            }

        /*
              if (image[position] == null
                      || pictures.get(image[position]) == null) {
                  BitmapFactory.Options options = new BitmapFactory.Options();
                  options.inDither = false;                     //Disable Dithering mode
                  options.inPurgeable = true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
                  options.inInputShareable = true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
                  options.inTempStorage = new byte[32 * 1024];
                  options.inSampleSize = 4;


                  URL url = null;
                  try {
                      url = new URL(image[position]);
                  } catch (MalformedURLException e) {
                      e.printStackTrace();
                  }

                  //1.pop
                  BitmapDownloaderTask.download(image[position], myImage);
                  BitmapDownloaderTask task = new BitmapDownloaderTask(myImage);
                  task.execute(image[position]);


                  //other
                  ImageLoader imageLoader = new ImageLoader(getApplicationContext());
                  myImage.setTag(image[position]);
                  imageLoader.DisplayImage(image[position], DisplayResults.this, myImage);
*/



    /* class BitmapDownloaderTask, see below */

            //    bmp = decodeFile(image[position]);
            //  pictures.put(image[position], bmp);

            // else {
            //    bmp = pictures.get(image[position]);


            //   myImage.setImageBitmap(bmp);

            return row;
        }


        private Bitmap decodeFile(String f) {
            try {
                // Decode image size
                URL url = new URL(f);
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, o);

                // The new size we want to scale to
                final int REQUIRED_SIZE = 70;

                // Find the correct scale value. It should be the power of 2.
                int scale = 1;
                while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                        o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                    scale *= 2;
                }

                // Decode with inSampleSize
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = scale;

                return BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, o2);
            } catch (FileNotFoundException e) {
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_results, menu);
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


/*
    boolean canUseForInBitmap(
            Bitmap candidate, BitmapFactory.Options targetOptions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // From Android 4.4 (KitKat) onward we can re-use if the byte size of
            // the new bitmap is smaller than the reusable bitmap candidate
            // allocation byte count.
            int width = targetOptions.outWidth / targetOptions.inSampleSize;
            int height = targetOptions.outHeight / targetOptions.inSampleSize;
            int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
            return byteCount <= candidate.getAllocationByteCount();
        }

        // On earlier versions, the dimensions must match exactly and the inSampleSize must be 1
        return candidate.getWidth() == targetOptions.outWidth
                && candidate.getHeight() == targetOptions.outHeight
                && targetOptions.inSampleSize == 1;
    }
        */

        /**
         * A helper function to return the byte usage per pixel of a bitmap based on its configuration.
         */
    /*
    static int getBytesPerPixel(Bitmap.Config config) {
        if (config == Bitmap.Config.ARGB_8888) {
            return 4;
        } else if (config == Bitmap.Config.RGB_565) {
            return 2;
        } else if (config == Bitmap.Config.ARGB_4444) {
            return 2;
        } else if (config == Bitmap.Config.ALPHA_8) {
            return 1;
        }
        return 1;
    }
*/







