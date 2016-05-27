package com.herprogramacion.iwish.BD;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

/**
 * Created by Ferbajoo on 26/05/2016.
 */
public class ModelDB {
    Context context;

    public ModelDB(Context context) {
        this.context = context;
    }

    //Metodo para generar el objeto a base de datos
    public SQLiteDatabase getDB(){
     try{
        SQLiteDatabase db;
        CreateDB helper = new CreateDB(context,"Usuario",null,1);
        db = helper.getWritableDatabase();
        return db;
        } catch (Exception e) {
           return null;
        }
    }

    //Metodo para las insertar datos a la base, pide nombre de tabla y valores en ContentValue
    public void InsertValues(String tabla, ContentValues values, SQLiteDatabase db){
        try {
            db.insert(tabla, null, values);

        }catch (Exception e){}
    }

    public long buscar_datos(SQLiteDatabase db){
        SQLiteStatement s = db.compileStatement("Select count(*) FROM metas;");
        final long count = s.simpleQueryForLong();
        return count;
    }
    public void eliminar_registros(String query){
        try {
            getDB().execSQL(query);
        }catch (Exception e){}
    }
}
