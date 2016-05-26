package com.ferbajoo.iwish;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.herprogramacion.iwish.R;

/**
 * Esta actividad contiene un fragmento que muestra el detalle
 * de las metas.
 */
public class DetailActivity extends AppCompatActivity {
    /**
     * Instancia global de la meta a detallar
     */
    private String idMeta;

    /**
     * Inicia una nueva instancia de la actividad
     *
     * @param activity Contexto desde donde se lanzará
     * @param idMeta   Identificador de la meta a detallar
     */
    public static void launch(Activity activity, String idMeta) {
        Intent intent = getLaunchIntent(activity, idMeta);
        activity.startActivityForResult(intent, Constantes.CODIGO_DETALLE);
    }

    /**
     * Construye un Intent a partir del contexto y la actividad
     * de detalle.
     *
     * @param context Contexto donde se inicia
     * @param idMeta  Identificador de la meta
     * @return Intent listo para usar
     */
    public static Intent getLaunchIntent(Context context, String idMeta) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(Constantes.EXTRA_ID, idMeta);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            // Dehabilitar titulo de la actividad
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            // Setear ícono "X" como Up button
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);
        }

        // Retener instancia
        if (getIntent().getStringExtra(Constantes.EXTRA_ID) != null)
            idMeta = getIntent().getStringExtra(Constantes.EXTRA_ID);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, DetailFragment.createInstance(idMeta), "DetailFragment")
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}