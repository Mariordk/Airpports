package com.example.mario.airpports;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private FragmentTabHost tabHost;
    private MenuItem delete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabHost= (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(),android.R.id.tabcontent);
        tabHost.addTab(tabHost.newTabSpec(getString(R.string.see_route)).setIndicator(getString(R.string.see_route)), MainFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec(getString(R.string.seen_routes)).setIndicator(getString(R.string.seen_routes)), FlightFragment.class, null);



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
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        menu.getItem(0).setVisible(false);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            int tab = 0;
            public void onTabChanged(String str) {
                if(tab % 2 == 0){
                    menu.getItem(0).setVisible(true);
                    tab++;
                }
                else
                {
                    menu.getItem(0).setVisible(false);
                    tab++;
                }

            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(getString(R.string.delete_flights));

                alertDialogBuilder
                        .setMessage(getString(R.string.confirm_delete))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.yes),new DialogInterface.OnClickListener() {
                            // Lo que sucede si se pulsa yes
                            public void onClick(DialogInterface dialog, int id) {
                                int uri = getContentResolver().delete(FlightContract.CONTENT_URI,null,null);
                                Toast toast1 = Toast.makeText(getApplicationContext(), getString(R.string.number_deleted_flights) + uri, Toast.LENGTH_LONG);
                                toast1.show();
                            }

                        })
                        .setNegativeButton(getString(R.string.No),new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // Si se pulsa no hace nada
                                dialog.cancel();
                            }
                        });

                //Se crea el alertDialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                //Se muestra
                alertDialog.show();

                return true;
            default:
                return false;
        }
    }
}
