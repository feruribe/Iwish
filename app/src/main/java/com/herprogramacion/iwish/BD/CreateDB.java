package com.herprogramacion.iwish.BD;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ferbajoo on 26/05/2016.
 */
public class CreateDB extends SQLiteOpenHelper {

    Context context;
    //Sentencia SQL para crear la tabla de Usuarios
    String metas = "CREATE TABLE metas " +
            "(idMeta INTEGER PRIMARY KEY," +
            "titulo TEXT," +
            "descripcion TEXT" +
            ",prioridad  TEXT," +
            "fechaLim TEXT," +
            "categoria TEXT)";

    public CreateDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta la sentencia SQL de creacion de la tabla
        db.execSQL(metas);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Se elimina la version anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS metas");
        //Se crea la nueva version de la tabla
        db.execSQL(metas);
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

}
