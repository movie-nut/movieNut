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


public class MainActivity extends Activity {
    //implements AdapterView.OnItemSelectedListener {
    private Spinner spinner1;
    private TextView textout;
    private EditText movieOut;
    private String selectedType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.recommendationType, android.R.layout.simple_spinner_item);
        //spinner1.setAdapter(adapter);
        //spinner1.setOnItemSelectedListener(this);

    }

    public void buttonOnClick(View v) throws IOException {
        Button button = (Button) v;
        movieOut = (EditText) findViewById(R.id.txtMovies);

        textout = (TextView) findViewById(R.id.textView);

        //APi : 3f2950a48b75db414b1dbb148cfcad89
        //weblink: http://api.themoviedb.org/3/movie/550?api_key=3f2950a48b75db414b1dbb148cfcad89
        //http://api.themoviedb.org/3/search/movie?api_key=3f2950a48b75db414b1dbb148cfcad89&query=avengers
      //  http://api.themoviedb.org/3/movie/8966/similar?api_key=3f2950a48b75db414b1dbb148cfcad89

        doSpinner();

        Intent intent = handleDiffTypes();

      //  textout.setText(movieOut.getText().toString());

        intent.putExtra("searchKeyWord", movieOut.getText().toString());
        startActivity(intent);


}

    private Intent handleDiffTypes() {
        Intent intent = null;
        try {
            if (selectedType.contains("1. People")) {
                intent = new Intent(this, RecommendMovieByPeople.class);

            } else if (selectedType.equals("2. Movies")) {
                intent = new Intent(this, RecommendSimilarMovie.class);

            } else if (selectedType.equals("3. Collections")) {
                intent = new Intent(this, RecommendMoviesInCollection.class);

            } else if (selectedType.equals("4. Companies")) {
                intent = new Intent(this, RecommendMoviesByCompany.class);
            }
        }  catch (Exception exception) {
            Toast.makeText(getApplicationContext(), "Movies or peoples could not be found!", Toast.LENGTH_LONG).show();
        }
        return intent;
    }

    private void doSpinner() {
        spinner1 = (Spinner) findViewById(R.id.spinner);
        selectedType = spinner1.getSelectedItem().toString();
        textout.setText(selectedType);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
/*
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
     //   spinner1.getItemAtPosition(spinner1.getSelectedItemPosition()).toString());
        TextView myText = (TextView) view;
      //  Toast.makeText(this, "Select types of recommendation", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    */
}