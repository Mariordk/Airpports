package com.example.mario.airpports;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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


public class MainFragment extends Fragment implements View.OnClickListener, TextWatcher {


    private Spinner numero_vuelo;
    private Button enviar;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_main,container,false);
        numero_vuelo = (Spinner)view.findViewById(R.id.numero_vuelo);
        enviar = (Button)view.findViewById(R.id.enviar);
        TextView text = (TextView)view.findViewById(R.id.texto);
        String json ="";
        /*Aquí habrá que meter los numeros de vuelo disponibles en una List*/
        /*ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this.getActivity(),R.array.valores_array,android.R.layout.simple_spinner_item);
        numero_vuelo.setAdapter(adapter);*/

        Rest rest = new Rest();
        rest.execute();
        enviar.setOnClickListener(this);
        //("http://localhost:8080/Airpports/webresources/com.mycompany.airpports.entities.vuelos");




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
        //Iniciamos la nueva actividad
        startActivity(intent);

    }

    public class Rest extends AsyncTask<String, Integer, Boolean> {


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

    }

