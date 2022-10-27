package com.example.dionisiopet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
SimpleCursorAdapter adapter;
Cursor cursor;
    DBController petDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        petDb = new DBController(getBaseContext());
        cursor = petDb.carregaPets();

        adapter = new SimpleCursorAdapter(
                this,
                R.layout.pet_item,
                cursor,
                new String [] {SQLiteDB.ID_PET, SQLiteDB.NOME_PET, SQLiteDB.RACA_PET},
                new int[] {R.id.idPet, R.id.nomePet, R.id.racaPet},
                0);

        int petCount = cursor.getCount();

        if(petCount == 0){
            Intent noPetsIntent = new Intent(getApplicationContext(), NoPetsActivity.class);
            startActivity(noPetsIntent);
        }

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(view -> {
            Intent petFormIntent = new Intent(getApplicationContext(), PetFormActivity.class);
            startActivity(petFormIntent);
        });

        ListView lista = findViewById(R.id.listView);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String petId = Long.toString(id);
                Intent petFormIntent = new Intent(getApplicationContext(), PetFormActivity.class);
                petFormIntent.putExtra("id", petId);
                startActivity(petFormIntent);
            }
        });
    }

    @Override
    protected  void onRestart() {

        super.onRestart();
    }

    @Override
    protected void onResume() {
        petDb = new DBController(getBaseContext());
        cursor = petDb.carregaPets();
        adapter.swapCursor(cursor);
        adapter.notifyDataSetChanged();
        super.onResume();
    }
}