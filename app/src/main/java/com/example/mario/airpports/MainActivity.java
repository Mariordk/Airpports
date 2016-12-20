package com.example.mario.airpports;

import android.content.Intent;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {


    private FragmentTabHost tabHost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabHost= (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(this,
                getSupportFragmentManager(),android.R.id.tabcontent);
        tabHost.addTab(tabHost.newTabSpec(getString(R.string.see_route)).setIndicator(getString(R.string.see_route)),
                MainFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec(getString(R.string.seen_routes)).setIndicator(getString(R.string.seen_routes)),
                FlightFragment.class, null);

       /* super.onCreate(savedInstanceState);
        // Comprobar si la actividad ya ha sido creada con anterioridad
        if (savedInstanceState == null) {
            // Crear un fragment
            MainFragment fragment = new MainFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment,
                            fragment.getClass().getSimpleName())
                    .commit();
        }*/
    }
}
