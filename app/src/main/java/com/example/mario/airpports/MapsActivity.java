package com.example.mario.airpports;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    DbHelper dbHelper;
    SQLiteDatabase db;


    private GoogleMap mMap;
    Spinner numero_vuelo;
    Button tipoMapa;

    CheckBox adsbhub;
    CheckBox frambuesa;
    CheckBox flightradar24;
    CheckBox flightaware;

    int[] message;
    String[] aircraft;
    double[] longitude;
    double[] latitude;
    int[] altitude;
    int[] speed;
    String[] time1 ;
    int[] source;
    int size;
    boolean result = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        numero_vuelo = (Spinner) findViewById(R.id.numero_vuelo);
        tipoMapa = (Button) findViewById(R.id.tipoMapa);
        frambuesa = (CheckBox) findViewById(R.id.frambuesa);
        adsbhub = (CheckBox) findViewById(R.id.adsbhub);
        flightradar24 = (CheckBox) findViewById(R.id.flightradar24);
        flightaware = (CheckBox) findViewById(R.id.flightaware);

        Rest rest = new Rest();
        rest.execute(getIntent().getExtras().getString("numero_vuelo"));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dbHelper = new DbHelper(this);

        tipoMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL){
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }else if(mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE){
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }else if(mMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a circle near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


    public class Rest extends AsyncTask<String, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(String... params) {

            //Variable para saber si se hace la conexion al rest con exito
            boolean result = true;


            HttpClient httpClient = new DefaultHttpClient();

            //Se coge el numero de vuelo seleccionado en el anterior activity
            String vuelo = params[0];

            //Conexion a la url del rest que coge los datos
            HttpGet del = new HttpGet("http://10.0.2.2:8080/Airpports/webresources/com.mycompany.airpports.entities.mensajes/mensajes?flight="+ vuelo);

            //Con esto se indica que vamos a obtener datos en json
            del.setHeader("content-type", "application/json");

            try
            {
                //Conexion
                HttpResponse resp = httpClient.execute(del);
                String respStr = EntityUtils.toString(resp.getEntity());

                //Se meten los datos en un Array de tipo json
                JSONArray respJSON = new JSONArray(respStr);

                //Inicializamos las variables con el tamañod el array anterior
                message = new int[respJSON.length()];
                aircraft = new String[respJSON.length()];
                longitude = new double[respJSON.length()];
                latitude = new double[respJSON.length()];
                altitude = new int[respJSON.length()];
                speed = new int[respJSON.length()];
                source = new int[respJSON.length()];
                time1 = new String[respJSON.length()];

                //Se introducen los datos del array json en los arrays obtenidos anteriormente
                for(int i=0; i<respJSON.length(); i++) {

                    JSONObject obj = respJSON.getJSONObject(i);

                    message[i] = obj.getInt("message");
                    aircraft[i] = obj.getString("aircraft");
                    longitude[i] = obj.getDouble("longitude");
                    latitude[i] = obj.getDouble("latitude");
                    altitude[i] = obj.getInt("altitude");
                    speed[i] = obj.getInt("speed");
                    source[i] = obj.getInt("source");
                    time1[i] = obj.getString("time1");


                }

            } catch (Exception e){
                result = false;
                e.printStackTrace();
            }

            return result;
        }
        @Override
        protected void onPostExecute(Boolean result) {

            //Con esto se establecen los bounds de los marcadores
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            List<Marker> marcadores = new ArrayList<Marker>();
            //ArrayList para almacenar las alturas y velocidades del vuelo seleccionado para calcular las máximas y luego realizar
            //las gráficas pertinentes
            List<Integer> alturas = new ArrayList<Integer>();
            List<Integer> velocidades = new ArrayList<Integer>();
            //Variable para almacenar las alturas del vuelo y recorrer el array
            int altura=0;
            //Si ha habido exito en la conexion al servicio rest
            if (result){

                //Para cada mensaje
                for(int i=0; i<message.length;i++){
                    //Se coge la fecha del anterior activity y se compara con las obtenidas, si coinciden se pinta en el mapa
                    if(time1[i].substring(0,10).equals(getIntent().getExtras().getString("fecha_vuelo"))) {


                        LatLng punto = new LatLng(latitude[i], longitude[i]);
                        //Dependiendo de la fuente de la que se recibe el mensaje, se pinta un avión u otro
                        switch (source[i]){

                            //ADSBHUB
                            case 1:
                                mMap.addMarker(new MarkerOptions().
                                        position(punto)
                                        .title(getIntent().getExtras().getString("numero_vuelo"))
                                        .snippet(getString(R.string.longitude) +": " + String.valueOf(longitude[i]) +"   " + getString(R.string.latitude) +": "+ String.valueOf(latitude[i]) + "\n" +
                                                    getString(R.string.altitude) +": " + String.valueOf(altitude[i])+" m   " + getString(R.string.speed) + ": " + String.valueOf(speed[i]) +" km/h \n" +
                                                   getString(R.string.datehour) +": " + time1[i]  + "\n" + getString(R.string.source)+": ADSBHUB")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.avionblack)));
                                break;

                            //FRAMBUESA
                            case 2:
                                mMap.addMarker(new MarkerOptions().position(punto)
                                        .title(getIntent().getExtras().getString("numero_vuelo"))
                                        .snippet(getString(R.string.longitude) +": " + String.valueOf(longitude[i]) +"   " + getString(R.string.latitude) +": "+ String.valueOf(latitude[i]) + "\n" +
                                                getString(R.string.altitude) +": " + String.valueOf(altitude[i])+" m   " +  getString(R.string.speed) + ": " + String.valueOf(speed[i]) +" km/h \n" +
                                                getString(R.string.datehour) +": " + time1[i] + "\n" + getString(R.string.source) + ": FRAMBUESA")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.avionblue)));
                                break;

                            //FLIGHTRADAR24
                            case 3:
                                mMap.addMarker(new MarkerOptions().position(punto)
                                        .title(getIntent().getExtras().getString("numero_vuelo"))
                                        .snippet(getString(R.string.longitude) +": " + String.valueOf(longitude[i]) +"   " + getString(R.string.latitude) +": "+ String.valueOf(latitude[i]) + "\n" +
                                                getString(R.string.altitude) +": " + String.valueOf(altitude[i])+" m   " + getString(R.string.speed) + ": " + String.valueOf(speed[i]) +" km/h \n" +
                                                getString(R.string.datehour) +": " + time1[i] + "\n" + getString(R.string.source)+": FLIGHTRADAR24")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.avionred)));
                                break;

                            //FLIGHTAWARE
                            case 4:
                                mMap.addMarker(new MarkerOptions().position(punto)
                                        .title(getIntent().getExtras().getString("numero_vuelo"))
                                        .snippet(getString(R.string.longitude) +": " + String.valueOf(longitude[i]) +"   " + getString(R.string.latitude) +": "+ String.valueOf(latitude[i]) + "\n" +
                                                getString(R.string.altitude) +": " + String.valueOf(altitude[i])+" m   " + getString(R.string.speed) + ": " + String.valueOf(speed[i]) +" km/h \n" +
                                                getString(R.string.datehour) +": " + time1[i] + "\n" + getString(R.string.source)+": FLIGHTAWARE")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.aviongreen)));
                                break;
                        }

                        //Se incluyen los puntos de latitud y longitud para establecer los limites para mover la camara
                        builder.include(punto);

                        alturas.add(altitude[i]);
                        velocidades.add(speed[i]);
                        altura++;


                    }


                }
                //Abrimos la base de datos para escribir
                db = dbHelper.getWritableDatabase();
                //Se crea el registro en un objeto de tipo ContentValues
                ContentValues nuevoVuelo = new ContentValues();
                //Ponemos en el registro el numero de vuelo
                nuevoVuelo.put(FlightContract.Column.NUMERO_VUELO,getIntent().getExtras().getString("numero_vuelo"));
                //Ponemos en el registro la fecha de vuelo
                nuevoVuelo.put(FlightContract.Column.FECHA_VUELO,getIntent().getExtras().getString("fecha_vuelo"));


                long fecha_consulta = System.currentTimeMillis();
                nuevoVuelo.put(FlightContract.Column.FECHA_CONSULTA, fecha_consulta);



                int altura_maxima = 0;
                //Se obtiene la altura máxima alcanzada
                for (int j=0;j<alturas.size();j++){
                    if (alturas.get(j)>altura_maxima){
                        altura_maxima = alturas.get(j);
                    }
                }

                //Se pone la altura maxima en el registro
                nuevoVuelo.put(FlightContract.Column.ALTURA_MAXIMA, altura_maxima);

                int velocidad_maxima = 0;
                //Se obtiene la velocidad máxima alcanzada
                for (int j=0;j<velocidades.size();j++){
                    if (velocidades.get(j)>velocidad_maxima){
                        velocidad_maxima = velocidades.get(j);
                    }
                }

                //Se pone la velocidad maxima en el registro
                nuevoVuelo.put(FlightContract.Column.VELOCIDAD_MAXIMA, velocidad_maxima);

             Uri uri = getContentResolver().insert(FlightContract.CONTENT_URI, nuevoVuelo);

/*
                //Se inserta en la base de datos
                db.insert(FlightContract.TABLE, null, nuevoVuelo);
                //Se cierra la base de datos
                db.close();
*/

                //Establecemos el LatLngBounds para la camara
                LatLngBounds bounds = builder.build();
                //Padding de los puntos en el mapa
                int padding = 50;
                //Inicializamos la camara con los dos parametros anteriores
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                //Se la asignamos al mapa
                mMap.moveCamera(cu);

                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {

                        LinearLayout info = new LinearLayout(getApplicationContext());
                        info.setOrientation(LinearLayout.VERTICAL);

                        TextView title = new TextView(getApplicationContext());
                        title.setTextColor(Color.BLACK);
                        title.setGravity(Gravity.CENTER);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setText(marker.getTitle());

                        TextView snippet = new TextView(getApplicationContext());
                        snippet.setTextColor(Color.GRAY);
                        snippet.setText(marker.getSnippet());

                        info.addView(title);
                        info.addView(snippet);

                        return info;
                    }
                });
            }
        }
    }

}

