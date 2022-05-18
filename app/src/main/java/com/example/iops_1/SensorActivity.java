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
import java.util.List;

public class SensorActivity extends AppCompatActivity {

    private String position;
    private int delay;
    TextView positiontext;
    Button outdoor;
    Button indoor;
    Button semiindoor;
    Button saveAndExit;
    SensorManager sm= null;
    List<Sensor> allDeviceSensors;
    sensorEvent[] sensorObject;
    File root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        Bundle bundle = getIntent().getExtras();
        position = bundle.getString("Position");
        delay = Integer.parseInt(bundle.getString("Delay"));

        initiateDirectory();

        buttonFuctions();

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
        allDeviceSensors = sm.getSensorList(Sensor.TYPE_ALL);
        sensorObject = new sensorEvent[allDeviceSensors.size()];

        for(int i=0;i<allDeviceSensors.size();i++)
        {
            sensorObject[i]=new sensorEvent(allDeviceSensors.get(i),i);
        }

        for(int i=0;i<allDeviceSensors.size();i++)
        {
            sm.registerListener(sensorObject[i].sel, allDeviceSensors.get(i), delay);
        }


    }

    public class sensorEvent {

        Sensor sensor;
        int index;
        float[] values;
        String[] data;
        CSVWriter csvWriter;
        File file;
        long startTime;

        public sensorEvent(Sensor sensor1,int index1)
        {
            sensor = sensor1;
            index = index1;
            startTime = System.nanoTime();
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
                data=new String[values.length+2];
                int i;
                for(i=0;i< values.length;i++)
                {
                    data[i]=String.valueOf(values[i]);
                }
                //System.out.println("jfytytyty");
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

        for(int i=0;i<allDeviceSensors.size();i++)
        {
            sm.unregisterListener(sensorObject[i].sel);
            try {
                sensorObject[i].csvWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Toast toast = Toast.makeText(getApplicationContext(), "Sensors Unregistered and File Saved at ".concat(String.valueOf(root)), Toast.LENGTH_LONG);
        toast.show();
        System.out.println("SensorActivity Destroyed");
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        //pass whatevere you want
        Intent intent = getIntent();
        intent.putExtra("result","Back Press");
        setResult(Activity.RESULT_CANCELED,intent);
        finish();
        super.onBackPressed();
    }
}