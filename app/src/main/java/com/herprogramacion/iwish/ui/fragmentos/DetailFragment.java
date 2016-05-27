package com.herprogramacion.iwish.ui.fragmentos;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.herprogramacion.iwish.ui.actividades.UpdateActivity;
import com.herprogramacion.iwish.web.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    /**
     * Etiqueta de depuración
     */
    private static final String TAG = DetailFragment.class.getSimpleName();

    /*
    Instancias de Views
     */
    private ImageView cabecera;
    private TextView titulo;
    private TextView descripcion;
    private TextView prioridad;
    private TextView fechaLim;
    private TextView categoria;
    private ImageButton editButton;
    private String extra;
    private Gson gson = new Gson();

    public DetailFragment() {
    }

    public static DetailFragment createInstance(String idMeta) {
        DetailFragment detailFragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constantes.EXTRA_ID, idMeta);
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail, container, false);

        // Obtención de views
        cabecera = (ImageView) v.findViewById(R.id.cabecera);
        titulo = (TextView) v.findViewById(R.id.titulo);
        descripcion = (TextView) v.findViewById(R.id.descripcion);
        prioridad = (TextView) v.findViewById(R.id.prioridad);
        fechaLim = (TextView) v.findViewById(R.id.fecha);
        categoria = (TextView) v.findViewById(R.id.categoria);
        editButton = (ImageButton) v.findViewById(R.id.fab);

        // Obtener extra del intent de envío
        extra = getArguments().getString(Constantes.EXTRA_ID);

        // Setear escucha para el fab
        editButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Iniciar actividad de actualización
                        Intent i = new Intent(getActivity(), UpdateActivity.class);
                        i.putExtra(Constantes.EXTRA_ID, extra);
                        getActivity().startActivityForResult(i, Constantes.CODIGO_ACTUALIZACION);
                    }
                }
        );

        // Cargar datos desde el web service
        cargarDatos();

        return v;
    }

    /**
     * Obtiene los datos desde el servidor
     */
    public void cargarDatos() {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_BY_ID + "?idMeta=" + extra;

        // Realizar petición GET_BY_ID
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar respuesta Json
                                procesarRespuesta(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                procesarRespuesta_SinInternet(ConvertiraJson(extra));
                                Toast.makeText(getActivity(), "Trabajando sin conexión", Toast.LENGTH_SHORT).show();
                            }
                        }
                )
        );
    }

    /**
     * Procesa cada uno de los estados posibles de la
     * respuesta enviada desde el servidor
     *
     * @param response Objeto Json
     */
    private void procesarRespuesta(JSONObject response) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            switch (mensaje) {
                case "1":
                    // Obtener objeto "meta"
                    JSONObject object = response.getJSONObject("meta");
                    //Parsear objeto 
                    Meta meta = gson.fromJson(object.toString(), Meta.class);

                    // Asignar color del fondo
                    switch (meta.getCategoria()) {
                        case "Salud":
                            cabecera.setBackgroundColor(getResources().getColor(R.color.saludColor));
                            break;
                        case "Finanzas":
                            cabecera.setBackgroundColor(getResources().getColor(R.color.finanzasColor));
                            break;
                        case "Espiritual":
                            cabecera.setBackgroundColor(getResources().getColor(R.color.espiritualColor));
                            break;
                        case "Profesional":
                            cabecera.setBackgroundColor(getResources().getColor(R.color.profesionalColor));
                            break;
                        case "Material":
                            cabecera.setBackgroundColor(getResources().getColor(R.color.materialColor));
                            break;
                    }

                    // Seteando valores en los views
                    titulo.setText(meta.getTitulo());
                    descripcion.setText(meta.getDescripcion());
                    prioridad.setText(meta.getPrioridad());
                    fechaLim.setText(meta.getFechaLim());
                    categoria.setText(meta.getCategoria());

                    break;

                case "2":
                    String mensaje2 = response.getString("mensaje");
                    Toast.makeText(
                            getActivity(),
                            mensaje2,
                            Toast.LENGTH_LONG).show();
                    break;

                case "3":
                    String mensaje3 = response.getString("mensaje");
                    Toast.makeText(
                            getActivity(),
                            mensaje3,
                            Toast.LENGTH_LONG).show();
                    break;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void procesarRespuesta_SinInternet(JSONObject object) {
        Meta meta = gson.fromJson(object.toString(), Meta.class);

        // Asignar color del fondo
        switch (meta.getCategoria()) {
            case "Salud":
                cabecera.setBackgroundColor(getResources().getColor(R.color.saludColor));
                break;
            case "Finanzas":
                cabecera.setBackgroundColor(getResources().getColor(R.color.finanzasColor));
                break;
            case "Espiritual":
                cabecera.setBackgroundColor(getResources().getColor(R.color.espiritualColor));
                break;
            case "Profesional":
                cabecera.setBackgroundColor(getResources().getColor(R.color.profesionalColor));
                break;
            case "Material":
                cabecera.setBackgroundColor(getResources().getColor(R.color.materialColor));
                break;
        }

        // Seteando valores en los views
        titulo.setText(meta.getTitulo());
        descripcion.setText(meta.getDescripcion());
        prioridad.setText(meta.getPrioridad());
        fechaLim.setText(meta.getFechaLim());
        categoria.setText(meta.getCategoria());
    }



    public JSONObject ConvertiraJson(String idMeta){
        SQLiteDatabase db;
        ModelDB base = new ModelDB(getActivity());
        db = base.getDB();
        Cursor cursor = db.rawQuery("SELECT  * FROM metas where idMeta="+idMeta, null );
        JSONArray resultSet = new JSONArray();
        JSONObject rowObject = new JSONObject();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();

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
                        Log.d("Exception json", e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return rowObject;
    }
}
