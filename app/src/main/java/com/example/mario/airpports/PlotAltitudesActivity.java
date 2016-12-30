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
import com.google.android.gms.vision.text.Text;


import java.util.ArrayList;

/**
 * Created by Mario on 29/12/2016.
 */

public class PlotAltitudesActivity extends AppCompatActivity {

    TextView textoResumenAltitud;
    TextView altura_maxima;
    TextView altura_media;

    int alturaMaxima;
    int sumaAlturas;
    int mediaAlturas;
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.plot_altitude);

        LineChart lineChart = (LineChart) findViewById(R.id.linegraph);

        ArrayList<Entry> entries = new ArrayList<>();

        Bundle b = this.getIntent().getExtras();
        ArrayList<Integer> alturas = b.getIntegerArrayList("alturas");

        sumaAlturas = 0;
        textoResumenAltitud = (TextView) findViewById(R.id.textoResumenAltitud);
        textoResumenAltitud.setText(getString(R.string.altitude_summary)+" "+ getIntent().getExtras().getString("numero_vuelo")
                                  + " " + getString(R.string.the_day) + " " + getIntent().getExtras().getString("fecha_vuelo"));

        //Se obtiene la altura máxima alcanzada
        for (int j=0;j<alturas.size();j++){
            if (alturas.get(j)>alturaMaxima){
                alturaMaxima = alturas.get(j);
            }
        }
        altura_maxima = (TextView) findViewById(R.id.altitud_maxima);
        altura_maxima.setText(String.valueOf(alturaMaxima));



        for(int i=0; i<alturas.size();i++){
            sumaAlturas += alturas.get(i);
        }

        mediaAlturas = sumaAlturas/alturas.size();
        altura_media = (TextView) findViewById(R.id.altitud_media);
        altura_media.setText(String.valueOf(mediaAlturas));

        for(int i=0; i<alturas.size();i++){
            if(alturas.get(i)!=0){
                entries.add(new Entry(alturas.get(i),i));
            }

        }

        LineDataSet dataset = new LineDataSet(entries, getString(R.string.altitude));

        ArrayList<String> labels = new ArrayList<String>();

        for (int i=0; i<alturas.size();i++){
            labels.add(String.valueOf(i));
        }

        Legend l = lineChart.getLegend();
        l.setEnabled(false);
        LineData data = new LineData(labels, dataset);
        dataset.setCircleColor(Color.RED);
        dataset.setDrawValues(false);
        lineChart.setPinchZoom(false);
        lineChart.setDescription(getString(R.string.altitude));
        lineChart.animateX(2000);
        lineChart.setBorderColor(Color.RED);
        lineChart.setData(data);


    }
}
