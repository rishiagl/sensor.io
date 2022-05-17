package com.example.iops_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;


import com.opencsv.CSVWriter;

import org.apache.commons.lang3.ObjectUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SensorActivity extends AppCompatActivity {

    String position;
    TextView positiontext;
    Button outdoor;
    Button indoor;
    Button semiindoor;
    SensorManager sm= null;
    List<Sensor> allDeviceSensors;
    sensorEvent[] sensorObject;
    File root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        Intent intent = getIntent();
        position = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        initiateDirectory();

        buttonsFuctions();

        sensorFunction();
    }

    public void initiateDirectory()
    {
        root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/IOPS_1");
        if (!root.exists()) {
            if (!root.mkdirs()) {
                System.out.println("Root Not Created");
            }
        }

    }

    public void buttonsFuctions()
    {
        positiontext= findViewById(R.id.currentposition);
        positiontext.setText("Current Position: " + position);
        indoor= findViewById(R.id.indoor);
        outdoor= findViewById(R.id.outdoor);
        semiindoor= findViewById(R.id.semiindoor);

        indoor.setOnClickListener(view -> {

            position="Indoor";
            positiontext.setText("Current Position: " + position);
        });

        outdoor.setOnClickListener(view -> {

            position="Outdoor";
            positiontext.setText("Current Position: " + position);
        });

        semiindoor.setOnClickListener(view -> {

            position="Semi-indoor";
            positiontext.setText("Current Position: " + position);
        });

    }

    public void sensorFunction()
    {
        sm= (SensorManager)getSystemService(SENSOR_SERVICE);
        allDeviceSensors = sm.getSensorList(Sensor.TYPE_ALL);
        sensorObject = new sensorEvent[allDeviceSensors.size()];

        for(int i=0;i<allDeviceSensors.size();i++)
        {
            sensorObject[i]=new sensorEvent(allDeviceSensors.get(i),i);
        }

        for(int i=0;i<allDeviceSensors.size();i++)
        {
            sm.registerListener(sensorObject[i].sel, allDeviceSensors.get(i), SensorManager.SENSOR_DELAY_NORMAL);
        }


    }

    public class sensorEvent {

        Sensor sensor;
        int index;
        float[] values;
        String[] data;
        CSVWriter csvWriter;
        File file;

        public sensorEvent(Sensor sensor1,int index1)
        {
            sensor=sensor1;
            index=index1;
            file = new File(root, sensor.getName().concat(".csv"));

            {
                try {
                    csvWriter = new CSVWriter(new FileWriter(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        SensorEventListener sel = new SensorEventListener() {

            public void onAccuracyChanged(Sensor sensor, int accuracy) { }
            public void onSensorChanged(SensorEvent event) {
                values = event.values;
                data=new String[values.length+1];
                int i;
                for(i=0;i< values.length;i++)
                {
                    data[i]=String.valueOf(values[i]);
                }
                data[i]=position;
                csvWriter.writeNext(data);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("resumed");
    }
    @Override
    protected void onPause() {
        System.out.println("paused");
        super.onPause();
    }
    @Override
    protected void onStop() {
        System.out.println("stoped");
        super.onStop();
    }
    @Override
    protected void onDestroy(){

        for(int i=0;i<allDeviceSensors.size();i++)
        {
            sm.unregisterListener(sensorObject[i].sel);
            try {
                sensorObject[i].csvWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Destroyed");
        super.onDestroy();
    }
}