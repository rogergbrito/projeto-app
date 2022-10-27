package com.example.dionisiopet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class NoPetsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_pets);

        Button button = findViewById(R.id.button);

        button.setOnClickListener(v -> {
            Intent petFormIntent = new Intent(getApplicationContext(), PetFormActivity.class);
            startActivity(petFormIntent);

            finish();
        });


    }
}