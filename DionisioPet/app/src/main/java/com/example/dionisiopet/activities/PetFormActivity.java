package com.example.dionisiopet.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dionisiopet.R;
import com.example.dionisiopet.helpers.DBController;
import com.example.dionisiopet.helpers.ImageHelper;
import com.example.dionisiopet.helpers.SQLiteDB;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

public class PetFormActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    DBController db = new DBController(PetFormActivity.this);
    String nomePet;
    String racaPet;
    byte[] fotoPet;
    String nascimentoPet;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_form);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        EditText nomePetView = findViewById(R.id.editTextTextPetName);
        EditText racaPetView = findViewById(R.id.editTextTextPetRace);
        ImageView fotoPetView = findViewById(R.id.addPetImage);
        Button salvarButton = findViewById(R.id.button2);
        Button nascimentoButton = findViewById(R.id.buttonSelectBirth);
        TextView aniversarioTextView = findViewById(R.id.nascimentoValue);
        aniversarioTextView.setText("");

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
                            fotoPetView.setImageBitmap(bitmap);
                        }
                    }
                }
        );

        nascimentoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
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
            nascimentoPet = petCursor.getString(petCursor.getColumnIndexOrThrow(SQLiteDB.DATA_NASCIMENTO));

            String fotoPath = petCursor.getString(petCursor.getColumnIndexOrThrow(SQLiteDB.FOTO_PET));
            if(fotoPath != null && fotoPath != "") {
                try {
                    fotoPetView.setImageBitmap(ImageHelper.getFile(PetFormActivity.this, fotoPath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            setTitle("Editar");
            nomePetView.setText(nomePet);
            racaPetView.setText(racaPet);
            aniversarioTextView.setText(nascimentoPet);
        }

        String finalPetId = petId;
        salvarButton.setOnClickListener(view -> {
            nomePet = nomePetView.getText().toString();
            racaPet = racaPetView.getText().toString();
            nascimentoPet = aniversarioTextView.getText().toString();

            db = new DBController(PetFormActivity.this);
            String fotoFileName = nomePet + ".jpeg";

            if(fotoPetView.getDrawable().isFilterBitmap()){
                Bitmap bitmap = ((BitmapDrawable) fotoPetView.getDrawable()).getBitmap();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                bitmap.recycle();
                fotoPet = baos.toByteArray();
                try {
                    ImageHelper.saveFile(this, fotoFileName, fotoPet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(finalPetId != null){
                db.alteraPet(Integer.parseInt(finalPetId), nomePet, racaPet, nascimentoPet, fotoFileName);
            }else{
                String result = db.inserePet(nomePet, racaPet, nascimentoPet, fotoFileName);
            }

            finish();
        });

        fotoPetView.setOnClickListener(view -> {
            Intent pick_foto_intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (pick_foto_intent.resolveActivity(getPackageManager()) == null) {
                Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show();
            } else {
                activityResultLauncher.launch(pick_foto_intent);
            }
        });
    }

    public int getSquareCropDimensionForBitmap(Bitmap bitmap)
    {
        return Math.min(bitmap.getWidth(), bitmap.getHeight());
    }

    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();

        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
                .setTextColor(ResourcesCompat.getColor(getResources(), R.color.secondaryColor, null));
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                .setTextColor(ResourcesCompat.getColor(getResources(), R.color.secondaryColor, null));
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

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month , int dayOfMonth) {
        int mesCerto = month +1; //Month é index zero based
        String diaHelper = String.valueOf(dayOfMonth).length() > 1 ? String.valueOf(dayOfMonth) : "0" + String.valueOf(dayOfMonth);
        String mesHelper = String.valueOf(mesCerto).length() > 1 ? String.valueOf(mesCerto) : "0" + String.valueOf(mesCerto);
        String dateText = diaHelper + "/" + mesHelper + "/" + year;
        TextView aniversarioTextView = findViewById(R.id.nascimentoValue);
        aniversarioTextView.setText(dateText);
        nascimentoPet = dateText;
    }
}