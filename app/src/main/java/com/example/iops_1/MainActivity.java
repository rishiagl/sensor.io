package com.example.iops_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    Spinner spinner;
    String position = "Indoor";
    Button start;
    Button fileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askPermissions();
        spinnerFunction();
        buttonFunction();
    }
    public void askPermissions()
    {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);

        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
    }

    public void spinnerFunction()
    {
        spinner = findViewById(R.id.current_position);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.position, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        AdapterView.OnItemSelectedListener sp=new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                position = (String)adapterView.getItemAtPosition(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                position = "Indoor";
            }
        };

        spinner.setPrompt("Select");
        spinner.setOnItemSelectedListener(sp);
    }

    public void buttonFunction()
    {
        start=findViewById(R.id.start);

        start.setOnClickListener(view -> {

            Intent intent = new Intent(MainActivity.this, SensorActivity.class);
            Bundle bundle= new Bundle();
            bundle.putString("Position",position);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        fileManager = findViewById(R.id.fileManager);

        fileManager.setOnClickListener(view -> {

            Intent intent = new Intent(MainActivity.this, FileManager.class);
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Sensor App" ;
            //String path = Environment.getExternalStorageDirectory().getPath();
            intent.putExtra("path",path);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("MainActivity resumed");
    }
    @Override
    protected void onPause() {
        System.out.println("MainActivity paused");
        super.onPause();
    }
    @Override
    protected void onStop() {
        System.out.println("MainActivity stoped");
        super.onStop();
    }
    @Override
    protected void onDestroy(){
        System.out.println("MainActivity Destroyed");
        super.onDestroy();
    }
}