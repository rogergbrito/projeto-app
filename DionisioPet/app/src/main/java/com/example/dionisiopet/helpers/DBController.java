package com.example.dionisiopet.helpers;

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

    public String inserePet(String nome, String raca, String nascimento, String fotoPath){
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put(SQLiteDB.NOME_PET, nome);
        valores.put(SQLiteDB.RACA_PET, raca);
        valores.put(SQLiteDB.DATA_NASCIMENTO, nascimento);
        valores.put(SQLiteDB.FOTO_PET, fotoPath);

        resultado = db.insert(SQLiteDB.TABELA_PETS, null, valores);
        db.close();

        if (resultado ==-1)
            return "Erro ao inserir registro";
        else
            return "Registro inserido com sucesso";
    }

    public Cursor carregaPets(){
        Cursor cursor;
        String[] campos =  {SQLiteDB.ID_PET, SQLiteDB.NOME_PET, SQLiteDB.RACA_PET, SQLiteDB.DATA_NASCIMENTO, SQLiteDB.FOTO_PET};
        db = banco.getReadableDatabase();
        cursor = db.query(SQLiteDB.TABELA_PETS, campos, null, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    public Cursor carregaPetById(int id){
        Cursor cursor;
        String[] campos =  {SQLiteDB.ID_PET, SQLiteDB.NOME_PET, SQLiteDB.RACA_PET, SQLiteDB.DATA_NASCIMENTO, SQLiteDB.FOTO_PET};
        String where = SQLiteDB.ID_PET + "=" + id;
        db = banco.getReadableDatabase();
        cursor = db.query(SQLiteDB.TABELA_PETS, campos, where,null,null,null,null,null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    public void alteraPet(int id, String nome, String raca, String nascimento,String fotoPath){
        ContentValues valores;
        String where;

        db = banco.getWritableDatabase();

        where = SQLiteDB.ID_PET + "=" + id;

        valores = new ContentValues();
        valores.put(SQLiteDB.NOME_PET, nome);
        valores.put(SQLiteDB.RACA_PET, raca);
        valores.put(SQLiteDB.DATA_NASCIMENTO, nascimento);
        valores.put(SQLiteDB.FOTO_PET, fotoPath);

        db.update(SQLiteDB.TABELA_PETS, valores, where,null);
        db.close();
    }

    public void deletaPet(int id){
        String where = SQLiteDB.ID_PET + "=" + id;
        db = banco.getReadableDatabase();
        db.delete(SQLiteDB.TABELA_PETS, where,null);

        db = banco.getReadableDatabase();
        String where2 = SQLiteDB.ID_PET_VACINA + "=" + id;
        db.delete(SQLiteDB.TABELA_VACINAS, where2, null);


        db.close();
    }

    public Cursor queryPet(String nome){
        Cursor cursor;
        String where = SQLiteDB.NOME_PET + " LIKE" + " '%" + nome + "%'";
        db = banco.getReadableDatabase();
        String[] campos =  {SQLiteDB.ID_PET, SQLiteDB.NOME_PET, SQLiteDB.RACA_PET, SQLiteDB.DATA_NASCIMENTO, SQLiteDB.FOTO_PET};
        cursor = db.query(SQLiteDB.TABELA_PETS, campos, where,null,null,null,null,null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    public String insereVacina(int idPet, String nome, String data){
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put(SQLiteDB.ID_PET_VACINA, idPet);
        valores.put(SQLiteDB.NOME_VACINA, nome);
        valores.put(SQLiteDB.DATA_VACINA, data);

        resultado = db.insert(SQLiteDB.TABELA_VACINAS, null, valores);
        db.close();

        if (resultado ==-1)
            return "Erro ao inserir registro";
        else
            return "Registro inserido com sucesso";
    }

    public Cursor getVacinasFromPet(int petId, int filter){
        Cursor cursor;
        String sql = "";
        switch (filter){
            case 0: //Data
                sql = "select *, substr(data, 7,4)as yy, substr(data,4,2) as mm, substr(data,1,2) as dd from "
                        + SQLiteDB.TABELA_VACINAS + " WHERE " + SQLiteDB.ID_PET_VACINA + "=" + petId
                        + " ORDER BY yy DESC, mm DESC, dd DESC";
                break;
            case 1: //Nome
                sql = "select * FROM " + SQLiteDB.TABELA_VACINAS + " WHERE " + SQLiteDB.ID_PET_VACINA
                        + "=" + petId + " ORDER BY " + SQLiteDB.NOME_VACINA;
                break;
        }

        //String[] campos =  {SQLiteDB.ID_VACINA, SQLiteDB.ID_PET_VACINA, SQLiteDB.NOME_VACINA, SQLiteDB.DATA_VACINA};
        //String where = SQLiteDB.ID_PET_VACINA + "=" + petId;
        db = banco.getReadableDatabase();
        //cursor = db.query(SQLiteDB.TABELA_VACINAS, campos, where,null,null,null,null,null);
        cursor = db.rawQuery(sql, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }
}

