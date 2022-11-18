package com.example.dionisiopet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.dionisiopet.R;
import com.example.dionisiopet.dialogs.NewVacinaDialog;
import com.example.dionisiopet.helpers.DBController;
import com.example.dionisiopet.helpers.ImageHelper;
import com.example.dionisiopet.helpers.SQLiteDB;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;

import kotlin.io.ConsoleKt;

public class  FullPetDetailsActivity extends AppCompatActivity implements NewVacinaDialog.NewVacinaDialogListener {
    DBController db = new DBController(FullPetDetailsActivity.this);
    private Menu menu;
    final Calendar myCalendar= Calendar.getInstance();
    String nomePet;
    SimpleCursorAdapter adapter;
    Cursor cursor;
    Integer petIdNumber = 0;
    Integer filter = 0; //ID PARA QUERY - 0 data, 1 nome

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_pet_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);
        ListView listView = findViewById(R.id.gridViewVacinas);
        ImageView imagePetview = findViewById(R.id.imageViewPet);

        Bundle bundle = getIntent().getExtras();
        String petId = null;

        petId = bundle.getString("id");
        petIdNumber = Integer.parseInt(petId);

        db = new DBController(this);
        Cursor petCursor = db.carregaPetById(Integer.parseInt(petId));

        nomePet = petCursor.getString(petCursor.getColumnIndexOrThrow(SQLiteDB.NOME_PET));
        String fotoPath = petCursor.getString(petCursor.getColumnIndexOrThrow(SQLiteDB.FOTO_PET));

        try {
            imagePetview.setImageBitmap(ImageHelper.getFile(FullPetDetailsActivity.this, fotoPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setTitle(nomePet);

        fixAnimation();
        setupFabScroll();

        cursor = db.getVacinasFromPet(petIdNumber, filter);
        adapter = new SimpleCursorAdapter(
                this,
                R.layout.vacina_item,
                cursor,
                new String[]{SQLiteDB.NOME_VACINA, SQLiteDB.DATA_VACINA},
                new int[]{R.id.textNomeVacina, R.id.textDataVacina},
                0);

        listView.setAdapter(adapter);

        int finalPetId = petIdNumber;
        fab.setOnClickListener(view -> {
            openNewVacinaDialog();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.fullpet_menu, menu);
        MenuItem item = menu.findItem(R.id.addVacina);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FloatingActionButton fab = findViewById(R.id.floatingActionButton2);
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
                params.setBehavior(null);
                fab.requestLayout();
                fab.setVisibility(View.GONE);
                finishAfterTransition();
                return true;

            case R.id.addVacina:
                openNewVacinaDialog();
                return true;

            case R.id.sort:
                AlertDialog.Builder builder = new AlertDialog.Builder(FullPetDetailsActivity.this, R.style.AlertDialogTheme);
                builder.setTitle("Ordenar por: ");
                builder.setSingleChoiceItems(new String[]{"Data", "Nome"}, filter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        filter = i;
                        dialogInterface.cancel();
                        refreshList();
                    }
                });
                builder.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        params.setBehavior(null);
        fab.requestLayout();
        fab.setVisibility(View.GONE);

        super.onBackPressed();
    }

    private void setupFabScroll() {
        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    MenuItem item = menu.findItem(R.id.addVacina);
                    item.setVisible(true);
                } else if (isShow) {
                    isShow = false;
                    MenuItem item = menu.findItem(R.id.addVacina);
                    item.setVisible(false);
                    fab.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void fixAnimation(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);

        fab.setVisibility(View.INVISIBLE);

        getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionEnd(Transition transition) {
                Animator anim   = ObjectAnimator.ofFloat(null, "alpha", 0f, 1f);
                anim.setInterpolator(new DecelerateInterpolator());
                fab.setVisibility(View.VISIBLE);
                //toolbar.setVisibility(View.VISIBLE);

                anim.start();
            }

            @Override
            public void onTransitionStart(Transition transition) {}

            @Override
            public void onTransitionCancel(Transition transition) {}

            @Override
            public void onTransitionPause(Transition transition) {}

            @Override
            public void onTransitionResume(Transition transition) {}
        });
    }

    public void openNewVacinaDialog(){
        NewVacinaDialog vacinaDialog = new NewVacinaDialog();
        vacinaDialog.show(getSupportFragmentManager(), "vacina dialog");
    }

    private void refreshList(){
        db = new DBController(getBaseContext());
        cursor = db.getVacinasFromPet(petIdNumber, filter);
        adapter.swapCursor(cursor);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void applyText(String nomeVacina, String dataVacina) {
        if(dataVacina == null || dataVacina == ""){
            int year = Calendar.getInstance().get(Calendar.YEAR);
            int month = Calendar.getInstance().get(Calendar.MONTH);
            int dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

            String diaHelper = String.valueOf(dayOfMonth).length() > 1 ? String.valueOf(dayOfMonth) : "0" + String.valueOf(dayOfMonth);
            String mesHelper = String.valueOf(month).length() > 1 ? String.valueOf(month + 1) : "0" + String.valueOf(month + 1);
            String dateText = diaHelper + "/" + mesHelper + "/" + year;

            dataVacina = dateText;
        }
        db.insereVacina(petIdNumber, nomeVacina, dataVacina);
        refreshList();
    }
}