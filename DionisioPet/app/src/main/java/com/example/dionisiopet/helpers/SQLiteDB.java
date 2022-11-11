package com.example.dionisiopet.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDB extends SQLiteOpenHelper {
    public static final String NOME_BANCO_PETS = "dionisiopet.db";

    //Tabela de Pets
    public static final String TABELA_PETS = "pets";
    public static final String ID_PET = "_id";
    public static final String NOME_PET = "nome";
    public static final String RACA_PET = "raca";
    public static final String DATA_NASCIMENTO = "nascimento";
    public static final String FOTO_PET = "foto";
    public static final int VERSAO_TABELA_PETS = 1;

    //Tabela de Vacinas
    public static final String TABELA_VACINAS = "vacinas";
    public static final String ID_VACINA = "_id";
    public static final String ID_PET_VACINA = "id_pet";
    public static final String NOME_VACINA = "nome";
    public static final String DATA_VACINA = "data";
    public static final int VERSAO_TABLELA_VACINA = 1;

    public SQLiteDB(Context context) {
        super(context, NOME_BANCO_PETS, null, VERSAO_TABELA_PETS);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE " + TABELA_PETS + "("
                + ID_PET + " integer primary key autoincrement,"
                + NOME_PET + " text,"
                + RACA_PET + " text,"
                + DATA_NASCIMENTO + " text,"
                + FOTO_PET + " text" + ")";

        String sql1 = "CREATE TABLE " + TABELA_VACINAS + "("
                + ID_VACINA + " integer primary key autoincrement,"
                + ID_PET_VACINA + " integer,"
                + NOME_VACINA + " integer,"
                + DATA_VACINA + " integer,"
                + "FOREIGN KEY(" + ID_PET_VACINA +") REFERENCES " + TABELA_PETS + " (" + ID_PET +" )" + ")";

        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.execSQL(sql1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABELA_PETS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABELA_VACINAS);
        onCreate(sqLiteDatabase);
    }
}
