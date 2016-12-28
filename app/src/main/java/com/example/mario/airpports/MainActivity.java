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
        //Se inicializa el tabhost donde se incluirán los tabs
        tabHost= (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(),android.R.id.tabcontent);

        //Se añaden los tabs, cada uno con su fragment
        tabHost.addTab(tabHost.newTabSpec(getString(R.string.see_route)).setIndicator(getString(R.string.see_route)), MainFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec(getString(R.string.seen_routes)).setIndicator(getString(R.string.seen_routes)), FlightFragment.class, null);



    }

    //Método para crear el menú
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {



        //Se infla el menú en el activity
        getMenuInflater().inflate(R.menu.menu, menu);
        //Con menu.getItem(0) se obtiene el item de la papelera, que es el que nos permite eliminar los vuelos consultados
        //Puesto que el primer fragment que aparece es el de consulta de ruta y no de vuelos consultados
        //Se oculta haciendo setVisible(false)
        menu.getItem(0).setVisible(false);

        //Listener para cambiar de tab
        //Es decir de la consulta de ruta a las ultimas consultas realizadas
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            //Variable para saber en que tab se está
            int tab = 0;

            //Al cambiar de tab
            public void onTabChanged(String str) {
                //Si la variable tab es par, esto significara que estamos en el tab de los ultimos vuelos consultados
                if(tab % 2 == 0){
                    //Se hace visible la papelera
                    menu.getItem(0).setVisible(true);
                    //Aumentamos en uno la variable tab
                    tab++;
                }
                else
                {
                    //Si la variable tab es impar, se hace no visible ya que significa que se está en la tab de consultar ruta
                    menu.getItem(0).setVisible(false);
                    tab++;
                }

            }
        });
        return true;
    }

    //Al seleccionar un item del menú
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Se obtiene el item del menu seleccionado
        switch (item.getItemId()) {
            //Si el item es el de borrar los vuelos consultados, cuyo id es delete
            case R.id.delete:

                //Se crea el constructor del alert dialog que sirve para confirmar el borrado de los vuelos consultados
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                //Se establece el titulo del alertdialog
                alertDialogBuilder.setTitle(getString(R.string.delete_flights));

                //Se establecen diferentes preferencias
                alertDialogBuilder
                        .setMessage(getString(R.string.confirm_delete))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.yes),new DialogInterface.OnClickListener() {
                            // Lo que sucede si se pulsa yes
                            public void onClick(DialogInterface dialog, int id) {
                                //Se vacia la tabla de la base de datos
                                int uri = getContentResolver().delete(FlightContract.CONTENT_URI,null,null);
                                //Se muestra un toast con la cantidad de vuelos eliminados
                                Toast toast1 = Toast.makeText(getApplicationContext(), getString(R.string.number_deleted_flights) + uri, Toast.LENGTH_LONG);
                                toast1.show();
                            }

                        })
                        .setNegativeButton(getString(R.string.No),new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // Si se pulsa no, no hace nada, simplemente se cierra el alert dialog
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
