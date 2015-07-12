package weilin.myapplication;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;


public class MainActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);

        tabHost.addTab(tabHost.newTabSpec("first").setIndicator("First").setContent(new Intent(this, SearchFeatures.class)));
        tabHost.addTab(tabHost.newTabSpec("second").setIndicator("Second").setContent(new Intent(this, AddWatchedMovies.class)));
        tabHost.setCurrentTab(0);

    }
}
