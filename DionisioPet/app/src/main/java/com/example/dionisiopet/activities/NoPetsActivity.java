package com.example.dionisiopet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;

import com.example.dionisiopet.R;
import com.example.dionisiopet.helpers.DBController;

public class NoPetsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_pets);

        Button button = findViewById(R.id.button);

        button.setOnClickListener(v -> {
            Intent petFormIntent = new Intent(NoPetsActivity.this, PetFormActivity.class);
            startActivity(petFormIntent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Cursor cursor = new DBController(this).carregaPets();
        if(cursor.getCount() > 0){
            finish();
        }
    }
}