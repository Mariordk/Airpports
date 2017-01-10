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

        //Se obtiene el gráfico lineal de la interfaz gráfica para manejarlo
        LineChart lineChart = (LineChart) findViewById(R.id.linegraph);

        //Arraylist en el que irán los puntos que aparecerán en la gráfica
        //De tipo Entry, que es el tipo necesario para pintar en la gráfica
        ArrayList<Entry> entries = new ArrayList<>();

        //Se obtienen los extras pasados del anterior intent
        Bundle b = this.getIntent().getExtras();
        //Se meten en un arraylist de velocidades
        ArrayList<Integer> velocidades = b.getIntegerArrayList("velocidades");

        sumaVelocidades = 0;

        //Se modifica el texto que irá encima de la gráfica para el vuelo y la fecha seleccionada
        textoResumenVelocidad = (TextView) findViewById(R.id.textoResumenVelocidad);
        textoResumenVelocidad.setText(getString(R.string.speed_summary)+" "+ getIntent().getExtras().getString("numero_vuelo")
                + " " + getString(R.string.the_day) + " " + getIntent().getExtras().getString("fecha_vuelo"));

        //Se obtiene la velocidad máxima alcanzada
        for (int j=0;j<velocidades.size();j++){
            if (velocidades.get(j)>velocidadMaxima){
                velocidadMaxima = velocidades.get(j);
            }
        }

        //Se pone en la interfaz la altura máxima
        velocidad_maxima = (TextView) findViewById(R.id.velocidad_maxima);
        velocidad_maxima.setText(String.valueOf(velocidadMaxima));


        //Se obtiene la suma de las velocidades
        for(int i=0; i<velocidades.size();i++){
            sumaVelocidades += velocidades.get(i);
        }

        //Se obtiene la media de las velocidades
        mediaVelocidades = sumaVelocidades/velocidades.size();

        //Se pone en la interfaz la velocidad media, que esta se pone en un textview
        velocidad_media = (TextView) findViewById(R.id.velocidad_media);
        velocidad_media.setText(String.valueOf(mediaVelocidades));

        //Se añaden las velocidades y la posición de esa velocidad al array de entradas
        for(int i=0; i<velocidades.size();i++){
            if(velocidades.get(i)!=0){
                entries.add(new Entry(velocidades.get(i),i));
            }

        }

        //El dataset para desplegar las entradas/puntos
        LineDataSet dataset = new LineDataSet(entries, getString(R.string.altitude));

        //ArrayList con las etiquetas de cada punto
        ArrayList<String> labels = new ArrayList<String>();

        //Se añaden las etiquetas al arraylist anterior
        for (int i=0; i<velocidades.size();i++){
            labels.add(String.valueOf(i));
        }

        //Se oculta la leyenda (no es necesaria)
        Legend l = lineChart.getLegend();
        l.setEnabled(false);

        //Establecemos los ajustes de los puntos y las lineas que forman
        LineData data = new LineData(labels, dataset);
        dataset.setCircleColor(Color.RED);
        dataset.setDrawValues(false);
        lineChart.setPinchZoom(false);
        lineChart.setDescription(getString(R.string.speed));
        lineChart.animateX(2000); //Animación de 2 segundos que pinta los puntos
        lineChart.setBorderColor(Color.RED);
        lineChart.setData(data); //Se le pasan los datos a la gráfica


    }
}
