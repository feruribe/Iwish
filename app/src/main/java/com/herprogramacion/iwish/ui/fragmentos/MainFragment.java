package com.herprogramacion.iwish.ui.fragmentos;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.herprogramacion.iwish.BD.ModelDB;
import com.herprogramacion.iwish.R;
import com.herprogramacion.iwish.modelo.Meta;
import com.herprogramacion.iwish.tools.Constantes;
import com.herprogramacion.iwish.ui.MetaAdapter;
import com.herprogramacion.iwish.ui.actividades.InsertActivity;
import com.herprogramacion.iwish.web.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


/**
 * Fragmento principal que contiene la lista de las metas
 */
public class MainFragment extends Fragment {

    /*
    Etiqueta de depuracion
     */
    private static final String TAG = MainFragment.class.getSimpleName();

    /*
    Adaptador del recycler view
     */
    private MetaAdapter adapter;

    /*
    Instancia global del recycler view
     */
    private RecyclerView lista;

    /*
    instancia global del administrador
     */
    private RecyclerView.LayoutManager lManager;

    /*
    Instancia global del FAB
     */
    com.melnykov.fab.FloatingActionButton fab;
    private Gson gson = new Gson();

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_main, container, false);

        lista = (RecyclerView) v.findViewById(R.id.reciclador);
        lista.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(getActivity());
        lista.setLayoutManager(lManager);

        // Cargar datos en el adaptador
        cargarAdaptador();

        // Obtener instancia del FAB
        fab = (com.melnykov.fab.FloatingActionButton) v.findViewById(R.id.fab);

        // Asignar escucha al FAB
        fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Iniciar actividad de inserción
                        getActivity().startActivityForResult(
                                new Intent(getActivity(), InsertActivity.class), 3);
                    }
                }
        );

        return v;
    }

    /**
     * Carga el adaptador con las metas obtenidas
     * en la respuesta
     */
    public void cargarAdaptador() {
        // Petición GET
        VolleySingleton.
                getInstance(getActivity()).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                Constantes.GET,
                                null,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // Procesar la respuesta Json
                                        procesarRespuesta(response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Meta[] metas = gson.fromJson(ConvertiraJson().toString(), Meta[].class);
                                        // Inicializar adaptador
                                        adapter = new MetaAdapter(Arrays.asList(metas), getActivity());
                                        // Setear adaptador a la lista
                                        lista.setAdapter(adapter);
                             Toast.makeText(getActivity(), "Trabajando sin conexión", Toast.LENGTH_SHORT).show();
                                    }
                                }

                        )
                );
    }

    /**
     * Interpreta los resultados de la respuesta y así
     * realizar las operaciones correspondientes
     *
     * @param response Objeto Json con la respuesta
     */
    private void procesarRespuesta(JSONObject response) {
        try {
            SQLiteDatabase db;
            ModelDB base = new ModelDB(getActivity());
            db = base.getDB();
            // Obtener atributo "estado"
            String estado = response.getString("estado");
            int datos = response.getJSONArray("metas").length();
            if (datos!=base.buscar_datos(db)){
                Guardar_datos_SinInternet(response);
            }else{
                Log.e("No guarde nada", "Son: "+ base.buscar_datos(db) +" En json" + datos);
            }
            switch (estado) {
                case "1": // EXITO
                    // Obtener array "metas" Json
                    JSONArray mensaje = response.getJSONArray("metas");
                    // Parsear con Gson
                    Meta[] metas = gson.fromJson(mensaje.toString(), Meta[].class);
                    // Inicializar adaptador
                    adapter = new MetaAdapter(Arrays.asList(metas), getActivity());
                    // Setear adaptador a la lista
                    lista.setAdapter(adapter);
                    break;
                case "2": // FALLIDO
                    String mensaje2 = response.getString("mensaje");
                    Toast.makeText(
                            getActivity(),
                            mensaje2,
                            Toast.LENGTH_LONG).show();
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

    }

    public void Guardar_datos_SinInternet(JSONObject response){
        SQLiteDatabase db;
        ModelDB base = new ModelDB(getActivity());
        db = base.getDB();
        base.eliminar_registros("delete from metas");
        try {
            String estado  = response.getString("estado");
        switch (estado) {
            case "1": // EXITO
                // Obtener array "metas" Json
                JSONArray mensaje = response.getJSONArray("metas");
                   for (int i=0; i< mensaje.length(); i++){
                        JSONObject jsonObject = mensaje.getJSONObject(i);
                        ContentValues values = new ContentValues();
                        values.put("idMeta",Integer.parseInt(jsonObject.getString("idMeta")));
                        values.put("titulo", jsonObject.getString("titulo"));
                        values.put("descripcion", jsonObject.getString("descripcion"));
                        values.put("prioridad", jsonObject.getString("prioridad"));
                        values.put("fechaLim", jsonObject.getString("fechaLim"));
                        values.put("categoria", jsonObject.getString("categoria"));
                        base.InsertValues("metas",values,db);
                    }
                      break;
                case "2": // FALLIDO
                    String mensaje2 = response.getString("mensaje");
                    Toast.makeText(
                            getActivity(),
                            mensaje2,
                            Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("Datos Guardados", "Son: "+ base.buscar_datos(db));
        db.close();
    }

    private JSONArray ConvertiraJson(){
        SQLiteDatabase db;
        ModelDB base = new ModelDB(getActivity());
        db = base.getDB();
        Cursor cursor = db.rawQuery("SELECT  * FROM metas", null );
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for( int i=0 ;  i< totalColumn ; i++ ){
                if( cursor.getColumnName(i) != null ){
                    try {
                        if( cursor.getString(i) != null ) {
                            rowObject.put(cursor.getColumnName(i) ,cursor.getString(i) );
                        }
                        else {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    } catch( Exception e ) {
                        Log.d("Exception json", e.getMessage()  );
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        Log.d("Final Json", resultSet.toString());
        return resultSet;
    }


}
