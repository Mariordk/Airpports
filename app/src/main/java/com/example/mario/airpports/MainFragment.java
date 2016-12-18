package com.example.mario.airpports;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class MainFragment extends Fragment implements View.OnClickListener, TextWatcher {


    Spinner numero_vuelo;
    Spinner fecha_vuelo;
    private Button enviar;
    private TextView text_fecha;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_main,container,false);
        numero_vuelo = (Spinner)view.findViewById(R.id.numero_vuelo);
        enviar = (Button)view.findViewById(R.id.enviar);
        text_fecha = (TextView)view.findViewById(R.id.select_fecha);
        fecha_vuelo = (Spinner)view.findViewById(R.id.fecha_vuelo);
        Resources res = getResources();

        TabHost tabs=(TabHost)view.findViewById(android.R.id.tabhost);
        tabs.setup();

        TabHost.TabSpec spec = tabs.newTabSpec(getString(R.string.see_route));
        spec.setContent(R.id.tab1);
        spec.setIndicator(getString(R.string.see_route));
        tabs.addTab(spec);

        spec=tabs.newTabSpec(getString(R.string.seen_routes));
        spec.setContent(R.id.tab2);
        spec.setIndicator(getString(R.string.seen_routes));
        tabs.addTab(spec);

        tabs.setCurrentTab(0);


        RestNumeroVuelo restNumeroVuelo = new RestNumeroVuelo();
        restNumeroVuelo.execute();
        enviar.setOnClickListener(this);
        numero_vuelo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                text_fecha.setVisibility(View.VISIBLE);
                fecha_vuelo.setVisibility(View.VISIBLE);
                enviar.setVisibility(View.VISIBLE);
                RestFecha restFecha = new RestFecha();
                restFecha.execute(numero_vuelo.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        return view;
    }




    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this.getActivity(), MapsActivity.class);
        //Estos son los parametros que se pasan al activity del maps para poder usarlos alli
        //Vuelo seleccionado y fecha seleccionada
        intent.putExtra("numero_vuelo", numero_vuelo.getSelectedItem().toString());
        intent.putExtra("fecha_vuelo", fecha_vuelo.getSelectedItem().toString());
        //Iniciamos la nueva actividad
        startActivity(intent);
    }

    public class RestNumeroVuelo extends AsyncTask<String, Integer, Boolean> {


        String[] vuelos;
        @Override
        protected Boolean doInBackground(String... strUrl) {
            boolean result = true;

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet del =  new HttpGet("http://10.0.2.2:8080/Airpports/webresources/com.mycompany.airpports.entities.vuelos");
            del.setHeader("content-type", "application/json");

            try{

                HttpResponse resp = httpClient.execute(del);
                String respStr = EntityUtils.toString(resp.getEntity());

                JSONArray respJSON = new JSONArray(respStr);

                vuelos = new String[respJSON.length()];
                Log.d("ServicioRest","Exito!");
                for(int i=0; i<respJSON.length(); i++) {

                    JSONObject obj = respJSON.getJSONObject(i);

                    String vuelo = obj.getString("flight");

                    vuelos[i] = vuelo;

                }
            }  catch (Exception e) {
                Log.e("ServicioRest","Error!", e);
                result = false;
            }

            return result;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result)
            {
                ArrayAdapter<String> adaptador = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, vuelos);

                numero_vuelo.setAdapter(adaptador);
            }
        }
        }


    public class RestFecha extends AsyncTask<String, Integer, Boolean> {

        //Array donde se van a meter todas las fechas
        String[] fechas;
        @Override
        protected Boolean doInBackground(String... params) {
            //Para devolver el resultado de la conexion
            boolean result = true;

            HttpClient httpClient = new DefaultHttpClient();

            //Se coge el vuelo seleccionado
            String vuelo = params[0];

            //Se obtienen los datos de ese vuelo por el numero de vuelo
            HttpGet del = new HttpGet("http://10.0.2.2:8080/Airpports/webresources/com.mycompany.airpports.entities.mensajes/mensajes?flight="+ vuelo);

            //Establecemos el tipo de datos que queremos en el header de la peticion
            del.setHeader("content-type", "application/json");

            try{

                HttpResponse resp = httpClient.execute(del);
                String respStr = EntityUtils.toString(resp.getEntity());

                //Se meten los datos en un JSONArray
                JSONArray respJSON = new JSONArray(respStr);

               fechas = new String[respJSON.length()];
                Log.d("ServicioRest","Exito!");
                for(int i=0; i<respJSON.length(); i++) {

                    JSONObject obj = respJSON.getJSONObject(i);

                    //Se mete la fecha en la posicion i del array fechas
                    fechas[i] = obj.getString("time1");

                }
            }  catch (Exception e) {
                Log.e("ServicioRest","Error!", e);
                result = false;
            }

            return result;

        }

        @Override
        protected void onPostExecute(Boolean result) {

            //Para quitar las fechas repetidas se va a utilizar una variable de tipo set
            //Ademas se inicializa como de tipo LinkedHashSet para conservar el orden
            Set<String> setdates = new LinkedHashSet<>();

            //Ya que la fecha tiene el formato con la hora, en este caso se quita la hora quedandonos con la fecha que es lo que
            //se va a mostrar y lo que interesa
            for(int i = 0; i<fechas.length;i++){
                    //Se pasan las fechas del array de fechas al set
                    setdates.add(fechas[i].substring(0,10));
            }

            //Se pasan las fechas del set a un arraylist para pasarlo al spinner
            List<String> fechas_dates = new ArrayList<String>();
            fechas_dates.clear();
            fechas_dates.addAll(setdates);

            if (result)
            {
                //Se incluyen las fechas en un adaptador
                ArrayAdapter<String> adaptador_fechas = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,fechas_dates);

                //Se incluyen al spinner para poder ser seleccionadas
                fecha_vuelo.setAdapter(adaptador_fechas);
            }
        }
    }
    }

