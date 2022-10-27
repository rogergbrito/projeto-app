package com.example.dionisiopet;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class PetFormActivity extends AppCompatActivity {
    DBController db;
    String nomePet;
    String racaPet;
    byte[] fotoPet;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_form);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        EditText nomePetView = findViewById(R.id.editTextTextPetName);
        EditText racaPetView = findViewById(R.id.editTextTextPetRace);
        ImageView fotoPetView = findViewById(R.id.imageView);
        Button salvarButton = findViewById(R.id.button2);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK && result.getData() != null){
                            Bundle bundle1 = result.getData().getExtras();
                            Bitmap bitmap = (Bitmap) bundle1.get("data");
                            fotoPetView.setImageBitmap(bitmap);
                        }
                    }
                });

        setTitle("Adicionar Pet");

        Bundle bundle = getIntent().getExtras();
        String petId;
        if(bundle != null){
            petId = bundle.getString("id");
            setTitle(petId);
            db = new DBController(this);
            Cursor petCursor = db.carregaPetById(Integer.parseInt(petId));

            nomePet = petCursor.getString(petCursor.getColumnIndexOrThrow(SQLiteDB.NOME_PET));
            racaPet = petCursor.getString(petCursor.getColumnIndexOrThrow(SQLiteDB.RACA_PET));

            byte[] tempFoto = petCursor.getBlob(petCursor.getColumnIndexOrThrow(SQLiteDB.FOTO_PET));
            Bitmap bm = BitmapFactory.decodeByteArray(tempFoto, 0 ,tempFoto.length);

            setTitle("Editar");
            nomePetView.setText(nomePet);
            racaPetView.setText(racaPet);
            fotoPetView.setImageBitmap(bm);
        }

        salvarButton.setOnClickListener(view -> {
            Bitmap bitmap = ((BitmapDrawable) fotoPetView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            fotoPet = baos.toByteArray();
            nomePet = nomePetView.getText().toString();
            racaPet = racaPetView.getText().toString();

            DBController db = new DBController(getBaseContext());
            String result = db.inserePet(nomePet, racaPet, fotoPet);
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

            finish();
        });

        fotoPetView.setOnClickListener(view -> {
            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(camera_intent.resolveActivity(getPackageManager()) != null){
                activityResultLauncher.launch(camera_intent);
            }else{
                Toast.makeText(this, "Nenhum app de camera", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}