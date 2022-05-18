package com.example.iops_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    Button start;
    Spinner spinner;
    String position = "Indoor";
    int frequency = 0;
    int delay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerFuction();
        setFrequency();
        buttonFunctions();
    }

    public void spinnerFuction()
    {
        spinner = (Spinner) findViewById(R.id.currentposition);

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

    public void setFrequency()
    {
        EditText text= findViewById(R.id.frequency);
        try {
            frequency = Integer.parseInt(text.getText().toString());
        }
        catch(NumberFormatException n)
        {
            System.out.println("Could not parse" + n);
        }

    }
    public void buttonFunctions()
    {
        start=(Button) findViewById((R.id.start));

        start.setOnClickListener(view -> {

            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);

            }
            if(frequency>=0.2 && frequency<=50) {

                delay = (1/frequency)*1000000;

                Toast toast = Toast.makeText(getApplicationContext(), "Frequency is set, Registering Sensors", Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(MainActivity.this, SensorActivity.class);
                Bundle bundle= new Bundle();
                bundle.putString("Position",position);
                bundle.putString("Delay",String.valueOf(delay));
                intent.putExtras(bundle);
                startActivity(intent);
            }
            else
            {
                Toast toast = Toast.makeText(getApplicationContext(), "Frequency is either too small or too large.", Toast.LENGTH_SHORT);
                toast.show();
            }
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