package com.example.mario.airpports;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
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


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Spinner numero_vuelo;
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
        Rest rest = new Rest();
        rest.execute(getIntent().getExtras().getString("numero_vuelo"));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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



            boolean result = true;

            HttpClient httpClient = new DefaultHttpClient();

            String vuelo = params[0];

            HttpGet del = new HttpGet("http://10.0.2.2:8080/Airpports/webresources/com.mycompany.airpports.entities.mensajes/mensajes?flight="+ vuelo);

            del.setHeader("content-type", "application/json");

            try
            {
                HttpResponse resp = httpClient.execute(del);
                String respStr = EntityUtils.toString(resp.getEntity());

                JSONArray respJSON = new JSONArray(respStr);

                message = new int[respJSON.length()];
                aircraft = new String[respJSON.length()];
                longitude = new double[respJSON.length()];
                latitude = new double[respJSON.length()];
                altitude = new int[respJSON.length()];
                speed = new int[respJSON.length()];
                source = new int[respJSON.length()];
                time1 = new String[respJSON.length()];

                size = respJSON.length();
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

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            if (result){
                for(int i=0; i<message.length;i++){
                    if(time1[i].substring(0,10).equals(getIntent().getExtras().getString("fecha_vuelo"))) {
                        LatLng punto = new LatLng(latitude[i], longitude[i]);
                        switch (source[i]){
                            case 1:
                                mMap.addMarker(new MarkerOptions().position(punto).title(String.valueOf(message[i])).icon(BitmapDescriptorFactory.fromResource(R.drawable.avionblack)));
                                break;

                            case 2:
                                mMap.addMarker(new MarkerOptions().position(punto).title(String.valueOf(message[i])).icon(BitmapDescriptorFactory.fromResource(R.drawable.avionblue)));
                                break;

                            case 3:
                                mMap.addMarker(new MarkerOptions().position(punto).title(String.valueOf(message[i])).icon(BitmapDescriptorFactory.fromResource(R.drawable.avionred)));
                                break;

                            case 4:
                                mMap.addMarker(new MarkerOptions().position(punto).title(String.valueOf(message[i])).icon(BitmapDescriptorFactory.fromResource(R.drawable.aviongreen)));
                                break;
                        }

                        builder.include(punto);

                    }

                }
                LatLngBounds bounds = builder.build();
                int padding = 0; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.moveCamera(cu);
            }
        }
    }

}

