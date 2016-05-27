package com.herprogramacion.iwish.ui.actividades;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.herprogramacion.iwish.BD.ModelDB;
import com.herprogramacion.iwish.R;
import com.herprogramacion.iwish.tools.Constantes;
import com.herprogramacion.iwish.ui.fragmentos.MainFragment;

/**
 * Actividad principal que contiene un fragmento con una lista.
 * Recuerda que la nueva librería de soporte reemplazó la clase
 * {@link android.support.v7.app.ActionBarActivity} por
 * {@link AppCompatActivity} para el uso de la action bar
 * en versiones antiguas.
 */
public class MainActivity extends AppCompatActivity {
    ModelDB base;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        base = new ModelDB(MainActivity.this);
        db = base.getDB();
        // Creación del fragmento principal
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment(), "MainFragment")
                    .commit();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constantes.CODIGO_DETALLE || requestCode == 3) {
            if (resultCode == RESULT_OK || resultCode == 203) {
                MainFragment fragment = (MainFragment) getSupportFragmentManager().
                        findFragmentByTag("MainFragment");
                fragment.cargarAdaptador();
            }
        }
    }
}
