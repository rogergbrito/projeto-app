package com.example.dionisiopet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.splashscreen.SplashScreen;

import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
SimpleCursorAdapter adapter;
Cursor cursor;
androidx.appcompat.view.ActionMode mActionMode;
int selectedpetPosition;
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
                new String[]{SQLiteDB.ID_PET, SQLiteDB.NOME_PET, SQLiteDB.RACA_PET, SQLiteDB.FOTO_PET},
                new int[]{R.id.idPet, R.id.nomePet, R.id.racaPet, R.id.imageViewPet},
                0);

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.imageViewPet) {
                    byte[] tempFoto = cursor.getBlob(cursor.getColumnIndexOrThrow(SQLiteDB.FOTO_PET));
                    ImageView imageView = ((ImageView) view);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inScaled = false;
                    Bitmap bm = BitmapFactory.decodeByteArray(tempFoto, 0, tempFoto.length, options);
                    imageView.setImageBitmap(bm);
                    return true;
                }

                return false;
            }
        });

        int petCount = cursor.getCount();

        if (petCount == 0) {
            Intent noPetsIntent = new Intent(getApplicationContext(), NoPetsActivity.class);
            startActivity(noPetsIntent);
        }

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(view -> {
            Intent petFormIntent = new Intent(getApplicationContext(), PetFormActivity.class);
            startActivity(petFormIntent);
        });

        GridView lista = findViewById(R.id.listView);
        lista.setAdapter(adapter);
        lista.setChoiceMode(GridView.CHOICE_MODE_SINGLE);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent fullPetDetailsIntent = new Intent(getApplicationContext(), FullPetDetailsActivity.class);
                fullPetDetailsIntent.putExtra("id", Long.toString(id));
                startActivity(fullPetDetailsIntent);
            }
        });

        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(mActionMode != null){
                    return false;
                }
                lista.setSelection(position);
                selectedpetPosition = Integer.parseInt(Long.toString(id));
                mActionMode = startSupportActionMode(mActionModeCallback);
                return true;
            }
        });
    }

    @Override
    protected  void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.action_menu, menu);
            petDb = new DBController(getBaseContext());
            Cursor petCursor = petDb.carregaPetById(selectedpetPosition);
            mode.setTitle(petCursor.getString(petCursor.getColumnIndexOrThrow(SQLiteDB.NOME_PET)));
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()){
                case R.id.editPet:
                    mode.finish();
                    Intent petFormIntent = new Intent(getApplicationContext(), PetFormActivity.class);
                    petFormIntent.putExtra("id", Integer.toString(selectedpetPosition));
                    startActivity(petFormIntent);
                    return true;

                case R.id.deletePet:
                    petDb = new DBController(getBaseContext());
                    Cursor petCursor = petDb.carregaPetById(selectedpetPosition);
                    String nomePetSelecionado = petCursor.getString(petCursor.getColumnIndexOrThrow(SQLiteDB.NOME_PET));

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
                    builder.setTitle("Excluir");
                    builder.setMessage("Deseja excluir " + nomePetSelecionado + "? :(");
                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            petDb.deletaPet(selectedpetPosition);
                            refreshList();
                        }
                    });
                    builder.setNegativeButton("Não", null);
                    builder.show();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    private void refreshList(){
        petDb = new DBController(getBaseContext());
        cursor = petDb.carregaPets();
        adapter.swapCursor(cursor);
        adapter.notifyDataSetChanged();

        if(cursor.getCount() == 0){
            Intent noPetsIntent = new Intent(MainActivity.this, NoPetsActivity.class);
            startActivity(noPetsIntent);
        }
    }
}