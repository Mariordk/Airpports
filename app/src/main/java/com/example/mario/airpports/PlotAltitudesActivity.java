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

        //Se obtiene el gráfico lineal de la interfaz gráfica para manejarlo
        LineChart lineChart = (LineChart) findViewById(R.id.linegraph);

        //Arraylist en el que irán los puntos que aparecerán en la gráfica
        //De tipo Entry, que es el tipo necesario para pintar en la gráfica
        ArrayList<Entry> entries = new ArrayList<>();

        //Se obtienen los extras pasados del anterior intent
        Bundle b = this.getIntent().getExtras();

        //Se meten en un arraylist de alturas
        ArrayList<Integer> alturas = b.getIntegerArrayList("alturas");

        sumaAlturas = 0;

        //Se modifica el texto que irá encima de la gráfica para el vuelo y la fecha seleccionada
        textoResumenAltitud = (TextView) findViewById(R.id.textoResumenAltitud);
        textoResumenAltitud.setText(getString(R.string.altitude_summary)+" "+ getIntent().getExtras().getString("numero_vuelo")
                                  + " " + getString(R.string.the_day) + " " + getIntent().getExtras().getString("fecha_vuelo"));


        //Se obtiene la altura máxima alcanzada
        for (int j=0;j<alturas.size();j++){
            if (alturas.get(j)>alturaMaxima){
                alturaMaxima = alturas.get(j);
            }
        }

        //Se pone en la interfaz la altura máxima
        altura_maxima = (TextView) findViewById(R.id.altitud_maxima);
        altura_maxima.setText(String.valueOf(alturaMaxima));

        //Se obtiene la suma de las alturas
        for(int i=0; i<alturas.size();i++){
            sumaAlturas += alturas.get(i);
        }

        //Se obtiene la media de las alturas
        mediaAlturas = sumaAlturas/alturas.size();

        //Se pone en la interfaz la altura media, que esta se pone en un textview
        altura_media = (TextView) findViewById(R.id.altitud_media);
        altura_media.setText(String.valueOf(mediaAlturas));

        //Se añaden las alturas y la posición de esa altura al array de entradas
        for(int i=0; i<alturas.size();i++){
            if(alturas.get(i)!=0){
                entries.add(new Entry(alturas.get(i),i));
            }

        }

        //El dataset para desplegar las entradas/puntos
        LineDataSet dataset = new LineDataSet(entries, getString(R.string.altitude));

        //ArrayList con las etiquetas de cada punto
        ArrayList<String> labels = new ArrayList<String>();

        //Se añaden las etiquetas al arraylist anterior
        for (int i=0; i<alturas.size();i++){
            labels.add(String.valueOf(i));
        }

        //Se oculta la leyenda (no es necesaria)
        Legend l = lineChart.getLegend();
        l.setEnabled(false);

        LineData data = new LineData(labels, dataset);

        //Establecemos los ajustes de los puntos y las lineas que forman
        dataset.setCircleColor(Color.RED);
        dataset.setDrawValues(false);
        lineChart.setPinchZoom(false);
        lineChart.setDescription(getString(R.string.altitude));
        lineChart.animateX(2000); //Animación de 2 segundos que pinta los puntos
        lineChart.setBorderColor(Color.RED);
        lineChart.setData(data); //Se le pasan los datos a la gráfica


    }
}
