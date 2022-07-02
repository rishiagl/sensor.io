package com.example.iops_1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SensorActivity extends AppCompatActivity {

    private String position;
    private int delay;
    TextView positiontext;
    Button outdoor;
    Button indoor;
    Button semiindoor;
    Button saveAndExit;
    SensorManager sm= null;
    ArrayList<sensorEvent> sensorObject = new ArrayList<>();
    File rootdir;
    File tempFiles;
    File savedFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        Bundle bundle = getIntent().getExtras();
        position = bundle.getString("Position");
        delay = Integer.parseInt(bundle.getString("Delay"));

        System.out.println(delay);
        initiateDirectory();

        buttonFuctions();

        sensorFunction();

    }

    public void initiateDirectory()
    {
        rootdir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/IOPS_1");
        if (!rootdir.exists()) {
            if (!rootdir.mkdirs()) {
                System.out.println("Root Not Created");
            }
        }

        tempFiles = new File(rootdir + "/Temp");
        if (!tempFiles.exists()) {
            if (!tempFiles.mkdirs()) {
                System.out.println("Root Not Created");
            }
        }

        savedFiles = new File(rootdir + "/Saved Files");
        if (!savedFiles.exists()) {
            if (!savedFiles.mkdirs()) {
                System.out.println("Root Not Created");
            }
        }

    }

    public void buttonFuctions()
    {
        String positionPrefix = "Current Position: ";
        positiontext = findViewById(R.id.currentposition);
        positiontext.setText(positionPrefix.concat(position));
        indoor = findViewById(R.id.indoor);
        outdoor = findViewById(R.id.outdoor);
        semiindoor = findViewById(R.id.semiindoor);
        saveAndExit = findViewById(R.id.SaveAndExit);

        indoor.setOnClickListener(view -> {

            position="Indoor";
            positiontext.setText(positionPrefix.concat(position));
        });

        outdoor.setOnClickListener(view -> {

            position="Outdoor";
            positiontext.setText(positionPrefix.concat(position));
        });

        semiindoor.setOnClickListener(view -> {

            position="Semi-indoor";
            positiontext.setText(positionPrefix.concat(position));
        });

        saveAndExit.setOnClickListener(view -> onBackPressed());
    }

    public void sensorFunction()
    {
        sm= (SensorManager)getSystemService(SENSOR_SERVICE);

        if(sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!= null) {
            sensorObject.add(new sensorEvent(sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)));
        }
        if(sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!=null) {
            sensorObject.add(new sensorEvent(sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)));
        }
        if(sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!= null) {
            sensorObject.add(new sensorEvent(sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE)));
        }
        if(sm.getDefaultSensor(Sensor.TYPE_LIGHT)!=null) {
            sensorObject.add(new sensorEvent(sm.getDefaultSensor(Sensor.TYPE_LIGHT)));
        }
        if(sm.getDefaultSensor(Sensor.TYPE_PRESSURE)!=null) {
            sensorObject.add(new sensorEvent(sm.getDefaultSensor(Sensor.TYPE_PRESSURE)));
        }
        if(sm.getDefaultSensor(Sensor.TYPE_PROXIMITY)!=null) {
            sensorObject.add(new sensorEvent(sm.getDefaultSensor(Sensor.TYPE_PROXIMITY)));
        }
        if(sm.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)!=null) {
            sensorObject.add(new sensorEvent(sm.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)));
        }
        if(sm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)!=null) {
            sensorObject.add(new sensorEvent(sm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)));
        }

        for(int i=0;i<sensorObject.size();i++)
        {
            sm.registerListener(sensorObject.get(i).sel, sensorObject.get(i).sensor, delay);
        }


    }

    public class sensorEvent {

        Sensor sensor;
        float[] values;
        String[] data;
        CSVWriter csvWriter;
        File file;
        long startTime;

        public sensorEvent(Sensor sensor1)
        {
            sensor = sensor1;
            startTime = System.nanoTime();
            file = new File(tempFiles, sensor.getName().concat(".csv"));

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
                data=new String[values.length+2];
                int i;
                for(i=0;i< values.length;i++)
                {
                    data[i]=String.valueOf(values[i]);
                }
                data[i] = position;
                data[i+1] = String.valueOf(event.timestamp - startTime);
                try {
                    csvWriter.writeNext(data);
                }
                 catch(NullPointerException e) {
                     e.printStackTrace();
                 }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("SensorActivity resumed");
    }
    @Override
    protected void onPause() {
        System.out.println("SensorActivity paused");
        super.onPause();
    }
    @Override
    protected void onStop() {
        System.out.println("SensorActivity stoped");
        super.onStop();
    }
    @Override
    protected void onDestroy(){

        for(int i=0;i<sensorObject.size();i++)
        {
            sm.unregisterListener(sensorObject.get(i).sel);
            try {
                sensorObject.get(i).csvWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Toast toast = Toast.makeText(getApplicationContext(), "Sensors Unregistered and File Saved at ".concat(String.valueOf(tempFiles)), Toast.LENGTH_LONG);
        toast.show();
        System.out.println("SensorActivity Destroyed");
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putExtra("result","Back Press");
        setResult(Activity.RESULT_CANCELED,intent);
        finish();
        super.onBackPressed();
    }
}