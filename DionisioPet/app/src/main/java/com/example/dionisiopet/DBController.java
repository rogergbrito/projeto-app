package com.example.dionisiopet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBController {
    private SQLiteDatabase db;
    private SQLiteDB banco;

    public DBController(Context context){
        banco = new SQLiteDB(context);
    }

    public String inserePet(String nome, String raca, byte[] foto){
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put(SQLiteDB.NOME_PET, nome);
        valores.put(SQLiteDB.RACA_PET, raca);
        valores.put(SQLiteDB.FOTO_PET, foto);

        resultado = db.insert(SQLiteDB.TABELA_PETS, null, valores);
        db.close();

        if (resultado ==-1)
            return "Erro ao inserir registro";
        else
            return "Registro inserido com sucesso";
    }

    public Cursor carregaPets(){
        Cursor cursor;
        String[] campos =  {SQLiteDB.ID_PET, SQLiteDB.NOME_PET, SQLiteDB.RACA_PET, SQLiteDB.FOTO_PET};
        db = banco.getReadableDatabase();
        cursor = db.query(banco.TABELA_PETS, campos, null, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    public Cursor carregaPetById(int id){
        Cursor cursor;
        String[] campos =  {SQLiteDB.ID_PET, SQLiteDB.NOME_PET, SQLiteDB.RACA_PET, SQLiteDB.FOTO_PET};
        String where = SQLiteDB.ID_PET + "=" + id;
        db = banco.getReadableDatabase();
        cursor = db.query(SQLiteDB.TABELA_PETS, campos, where,null,null,null,null,null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }
}

