package com.example.dionisiopet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);

//        View.OnClickListener handler = new View.OnClickListener(){
//
//            public void onClick(View v) {
//
//                if(v==button){
//                    // doStuff
//                    Intent intentMain = new Intent(getApplicationContext(), activity_dois.class);
//                    startActivity(intentMain);
//                }
//            }
//        };

        button.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intentMain = new Intent(getApplicationContext(), activity_dois.class);
                startActivity(intentMain);
            }
        });



    }
}