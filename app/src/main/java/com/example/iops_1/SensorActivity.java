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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class SensorActivity extends AppCompatActivity {

    String position;
    File appRootFile;
    File sensorFiles;
    File currentFile;
    TextView positionText;
    Button outdoor;
    Button indoor;
    Button semiindoor;
    Button saveAndExit;
    SensorManager sm= null;
    ArrayList<sensorEvent> sensorObject = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        setTitle("Sensor Activity");

        Bundle bundle = getIntent().getExtras();
        position = bundle.getString("Position");

        initiateDirectory();
        buttonFunctions();
        sensorFunction();
    }

    public void initiateDirectory()
    {
        appRootFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Sensor App");
        if (!appRootFile.exists()) {
            if (!appRootFile.mkdirs()) {
                System.out.println("Root Not Created");
            }
        }

        sensorFiles = new File(appRootFile + "/Sensor Files");
        if (!sensorFiles.exists()) {
            if (!sensorFiles.mkdirs()) {
                System.out.println("Root Not Created");
            }
        }

        Long date=System.currentTimeMillis();
        SimpleDateFormat dateFormat =new SimpleDateFormat("dd_MM_yyyy_HH_mm", Locale.getDefault());
        String dateStr = dateFormat.format(date);
        System.out.println(dateStr);

        currentFile = new File( sensorFiles + "/" + dateStr);
        if (!currentFile.exists()) {
            if (!currentFile.mkdirs()) {
                System.out.println("Root Not Created");
            }
        }
    }

    public void buttonFunctions()
    {
        String positionPrefix = "Current Position: ";
        positionText = findViewById(R.id.currentposition);
        positionText.setText(positionPrefix.concat(position));
        indoor = findViewById(R.id.indoor);
        outdoor = findViewById(R.id.outdoor);
        semiindoor = findViewById(R.id.semiindoor);
        saveAndExit = findViewById(R.id.SaveAndExit);

        indoor.setOnClickListener(view -> {

            position="Indoor";
            positionText.setText(positionPrefix.concat(position));
        });

        outdoor.setOnClickListener(view -> {

            position="Outdoor";
            positionText.setText(positionPrefix.concat(position));
        });

        semiindoor.setOnClickListener(view -> {

            position="Semi-indoor";
            positionText.setText(positionPrefix.concat(position));
        });

        saveAndExit.setOnClickListener(view -> onBackPressed());
    }
    public void sensorFunction()
    {
        sm= (SensorManager)getSystemService(SENSOR_SERVICE);

        if(sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!= null) {
            sensorObject.add(new sensorEvent(sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)));
        }
        else
        {
            System.out.println("fail");
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
            sm.registerListener(sensorObject.get(i).sel, sensorObject.get(i).sensor, SensorManager.SENSOR_DELAY_NORMAL);
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
            file = new File(currentFile, sensor.getName().concat(".csv"));

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
        System.out.println("SensorActivity stopped");
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
        Toast toast = Toast.makeText(getApplicationContext(), "Sensors Unregistered and File Saved at ".concat(String.valueOf(currentFile)), Toast.LENGTH_LONG);
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