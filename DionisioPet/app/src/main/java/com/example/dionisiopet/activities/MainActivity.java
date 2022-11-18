package com.example.dionisiopet.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.splashscreen.SplashScreen;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import com.example.dionisiopet.R;
import com.example.dionisiopet.helpers.DBController;
import com.example.dionisiopet.helpers.ImageHelper;
import com.example.dionisiopet.helpers.SQLiteDB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
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

        ActionBar actionBar = getSupportActionBar();

        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        //fade.excludeTarget(actionBar.getCustomView(), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        petDb = new DBController(MainActivity.this);
        cursor = petDb.carregaPets();

        adapter = new SimpleCursorAdapter(
                this,
                R.layout.pet_item,
                cursor,
                new String[]{SQLiteDB.ID_PET, SQLiteDB.NOME_PET, SQLiteDB.DATA_NASCIMENTO, SQLiteDB.FOTO_PET},
                new int[]{R.id.idPet, R.id.nomePet, R.id.racaPet, R.id.imageViewPet},
                0);

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.imageViewPet) {
                    ImageView imageView = ((ImageView) view);
                    String fotoPath = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteDB.FOTO_PET));
                    if(fotoPath != null && fotoPath != "") {
                        try {
                            imageView.setImageBitmap(ImageHelper.getFile(MainActivity.this, fotoPath));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
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
                ImageView imageView = view.findViewById(R.id.imageViewPet);

                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(MainActivity.this, imageView, imageView.getTransitionName());
                Intent fullPetDetailsIntent = new Intent(getApplicationContext(), FullPetDetailsActivity.class);
                fullPetDetailsIntent.putExtra("id", Long.toString(id));

                startActivity(fullPetDetailsIntent, options.toBundle());
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
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
//        String partialValue = s.toString();
//        Cursor temp = petDb.queryPet(partialValue);
//        if(temp.getCount() != adapter.getCursor().getCount()){
//            adapter.swapCursor(temp);
//            adapter.notifyDataSetChanged();
//        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if(query.length() > 0){
            String partialValue = query.toString();
            Cursor temp = petDb.queryPet(partialValue);
            if(temp.getCount() != adapter.getCursor().getCount()){
                adapter.swapCursor(temp);
                adapter.notifyDataSetChanged();
            }
        }else{
            refreshList();
        }
        return false;
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
                    String fotoPetPath = petCursor.getString(petCursor.getColumnIndexOrThrow(SQLiteDB.FOTO_PET));

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
                    builder.setTitle("Excluir");
                    builder.setMessage("Deseja excluir " + nomePetSelecionado + "? :(");
                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            petDb.deletaPet(selectedpetPosition);
                            ImageHelper.deleteFile(getApplicationContext(), fotoPetPath);
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