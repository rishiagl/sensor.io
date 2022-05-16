package com.example.iops_1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    Button start;
    Spinner spinner;
    String position = "Indoor";
    public static final String EXTRA_MESSAGE = "com.example.iops_1.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start=(Button) findViewById((R.id.save));
        spinner = (Spinner) findViewById(R.id.currentposition);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.position, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        AdapterView.OnItemSelectedListener sp=new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                position = (String)adapterView.getItemAtPosition(i);

                /*if(position.equals("Indoor"))
                    p = 0;
                else if(position.equals("Outdoor"))
                    p = 1;
                else if(position.equals("Semi-Indoor"))
                    p = 2;
                else
                    p = 0;
                  */
                System.out.println("Success");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                position = "Indoor";
            }
        };

        spinner.setOnItemSelectedListener(sp);

        start.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onClick(View view){

                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);

                }

                Intent intent = new Intent(MainActivity.this, SensorActivity.class);
                String message = position;
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);


                /*try {
                        File root=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/IOPS_1");
                        if(!root.exists())
                        {
                            if(!root.mkdirs())
                            {
                                System.out.println("Root Not Created");
                            }

                        }
                        String csvname="first.csv";
                        File csv = new File(root,csvname);

                        CSVWriter csvWriter=new CSVWriter(new FileWriter(csv,true));

                        String row[]=new String[]{name1,no1};
                        csvWriter.writeNext(row);
                        csvWriter.close();
                        Toast.makeText(MainActivity.this,"File Successfully saved",Toast.LENGTH_LONG).show();

                    }

                    catch (IOException e){
                        e.printStackTrace();
                    }*/
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("paused");
    }


}