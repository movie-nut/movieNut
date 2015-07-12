package weilin.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by WeiLin on 12/7/15.
 */
public class SearchFeatures extends Activity {
        //implements AdapterView.OnItemSelectedListener {
        private Spinner spinner1;
        private TextView textout;
        private EditText movieOut;
        private String selectedType;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_search_features);

            // ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.recommendationType, android.R.layout.simple_spinner_item);
            //spinner1.setAdapter(adapter);
            //spinner1.setOnItemSelectedListener(this);

        }

        public void buttonOnClick(View v) throws IOException {
            Button button = (Button) v;
            movieOut = (EditText) findViewById(R.id.txtAdd);
            // textout = (TextView) findViewById(R.id.textView);

            String searchKeyword = movieOut.getText().toString();

            try {
                if (searchKeyword == null || searchKeyword.equals("")) {
                    throw new NullPointerException();
                } else {
                    doSpinner();
                    Intent intent = null;

                    if (selectedType.contains("Actors")) {
                        intent = new Intent(this, RecommendMovieByActor.class);


                    } else if (selectedType.contains("Directors")) {
                        intent = new Intent(this, RecommendMoviesByDirectorAuthor.class);


                    } else if (selectedType.equals("3. Similar Movies")) {
                        intent = new Intent(this, RecommendSimilarMovie.class);


                    } else if (selectedType.equals("4. Collections")) {
                        intent = new Intent(this, RecommendMoviesInCollection.class);

                    } else if (selectedType.equals("4. Companies")) {
                        intent = new Intent(this, RecommendMoviesByCompany.class);
                    }

                    intent.putExtra("searchKeyWord", searchKeyword);
                    startActivity(intent);
                }
            } catch (NullPointerException e) {
                Toast.makeText(this, "No keyword entered!", Toast.LENGTH_LONG).show();
            }
        }
            //APi : 3f2950a48b75db414b1dbb148cfcad89
            //weblink: http://api.themoviedb.org/3/movie/550?api_key=3f2950a48b75db414b1dbb148cfcad89
            //http://api.themoviedb.org/3/search/movie?api_key=3f2950a48b75db414b1dbb148cfcad89&query=avengers
            //  http://api.themoviedb.org/3/movie/8966/similar?api_key=3f2950a48b75db414b1dbb148cfcad89


        private void doSpinner() {
            spinner1 = (Spinner) findViewById(R.id.spinner);
            selectedType = spinner1.getSelectedItem().toString();
//        textout.setText(selectedType);
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_search_feature, menu);
            return true;
        }
    }



