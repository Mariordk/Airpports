package com.example.mario.airpports;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

/**
 * Created by Mario on 03/01/2017.
 */

public class PlotSpeedsActivity extends AppCompatActivity {

    TextView textoResumenVelocidad;
    TextView velocidad_maxima;
    TextView velocidad_media;

    int velocidadMaxima;
    int sumaVelocidades;
    int mediaVelocidades;

    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.plot_speed);

        LineChart lineChart = (LineChart) findViewById(R.id.linegraph);

        ArrayList<Entry> entries = new ArrayList<>();

        Bundle b = this.getIntent().getExtras();
        ArrayList<Integer> velocidades = b.getIntegerArrayList("velocidades");

        sumaVelocidades = 0;
        textoResumenVelocidad = (TextView) findViewById(R.id.textoResumenVelocidad);
        textoResumenVelocidad.setText(getString(R.string.speed_summary)+" "+ getIntent().getExtras().getString("numero_vuelo")
                + " " + getString(R.string.the_day) + " " + getIntent().getExtras().getString("fecha_vuelo"));

        //Se obtiene la altura máxima alcanzada
        for (int j=0;j<velocidades.size();j++){
            if (velocidades.get(j)>velocidadMaxima){
                velocidadMaxima = velocidades.get(j);
            }
        }
        velocidad_maxima = (TextView) findViewById(R.id.velocidad_maxima);
        velocidad_maxima.setText(String.valueOf(velocidadMaxima));



        for(int i=0; i<velocidades.size();i++){
            sumaVelocidades += velocidades.get(i);
        }

        mediaVelocidades = sumaVelocidades/velocidades.size();
        velocidad_media = (TextView) findViewById(R.id.velocidad_media);
        velocidad_media.setText(String.valueOf(mediaVelocidades));

        for(int i=0; i<velocidades.size();i++){
            if(velocidades.get(i)!=0){
                entries.add(new Entry(velocidades.get(i),i));
            }

        }

        LineDataSet dataset = new LineDataSet(entries, getString(R.string.altitude));

        ArrayList<String> labels = new ArrayList<String>();

        for (int i=0; i<velocidades.size();i++){
            labels.add(String.valueOf(i));
        }

        Legend l = lineChart.getLegend();
        l.setEnabled(false);
        LineData data = new LineData(labels, dataset);
        dataset.setCircleColor(Color.RED);
        dataset.setDrawValues(false);
        lineChart.setPinchZoom(false);
        lineChart.setDescription(getString(R.string.speed));
        lineChart.animateX(2000);
        lineChart.setBorderColor(Color.RED);
        lineChart.setData(data);


    }
}
