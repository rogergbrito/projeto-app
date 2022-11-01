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
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class PetFormActivity extends AppCompatActivity {
    DBController db = new DBController(PetFormActivity.this);
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
                        Intent data = result.getData();
                        if(data != null && result.getResultCode() == RESULT_OK && data.getData() != null) {
                            Uri selectedImageUri = data.getData();
                            InputStream inputStream = null;
                            try {
                                inputStream = getContentResolver().openInputStream(selectedImageUri);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 10, out)) {
                                Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                                int dimension = getSquareCropDimensionForBitmap(decoded);
                                decoded = ThumbnailUtils.extractThumbnail(decoded, dimension, dimension);
                                fotoPetView.setImageBitmap(decoded);
                            }
                        }
                    }
                });

        setTitle("Adicionar Pet");

        Bundle bundle = getIntent().getExtras();
        String petId = null;
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

        String finalPetId = petId;
        salvarButton.setOnClickListener(view -> {
            db = new DBController(PetFormActivity.this);

            Bitmap bitmap = ((BitmapDrawable) fotoPetView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

            fotoPet = baos.toByteArray();
            nomePet = nomePetView.getText().toString();
            racaPet = racaPetView.getText().toString();

            if(finalPetId != null){
                db.alteraPet(Integer.parseInt(finalPetId), nomePet, racaPet, fotoPet);
            }else{
                String result = db.inserePet(nomePet, racaPet, fotoPet);
            }

            finish();
        });

        fotoPetView.setOnClickListener(view -> {
            Uri fileuri = null;
            Intent camera_intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (camera_intent.resolveActivity(getPackageManager()) == null) {
                Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show();
            } else {
                activityResultLauncher.launch(camera_intent);
            }
        });
    }
        public int getSquareCropDimensionForBitmap(Bitmap bitmap)
        {
            return Math.min(bitmap.getWidth(), bitmap.getHeight());
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