package com.itp.sigedindocentes;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;

public class Restaurar extends AsyncTask<Void, Void, Void> {
    private Context contexto;
    private String body = "", urlParameters = "", datos = "", logs = "";
    private AdminSQLiteOpenHelper admin;
    private SQLiteDatabase bd;
    private Cursor fila;
    private ContentValues registro;
    private ProgressBar pb;

    public Restaurar(Context contexto, ProgressBar pb) {
        this.contexto = contexto;
        this.pb = pb;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        admin = new AdminSQLiteOpenHelper(contexto, vars.bd, null, vars.version);

        URL url = null;
        try {
            urlParameters = "iddoc=" + URLEncoder.encode(vars.idusu, "utf-8");
            url = new URL(vars.url_restaurar);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("charset", "utf-8");
            urlConnection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));

            //Enviar peticion
            DataOutputStream escribir = new DataOutputStream(urlConnection.getOutputStream());
            escribir.writeBytes(urlParameters);
            escribir.flush();
            escribir.close();

            Integer codigoRespuesta = Integer.parseInt(String.valueOf(urlConnection.getResponseCode()));
            if(codigoRespuesta.equals(200)) {
                body = readStream(urlConnection.getInputStream());
            }
            urlConnection.disconnect();
        } catch (MalformedURLException e) {
            //body = e.toString(); //URL incorrecta
            body = "100";
        } catch (SocketTimeoutException e){
            //body = e.toString(); //Finalizado el timeout, esperando la respuesta del servidor
            body = "200";
        } catch (UnknownHostException e) {
            //body = e.toString();
            body = "300";
        } catch (Exception e) {
            //body = e.toString(); //Error diferente a los anteriores
            body = "400";
        }
        return null;
    }

    protected String readStream(InputStream in) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuilder total = new StringBuilder();
        String line;

        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        if (r != null) {
            r.close();
        }
        in.close();
        return total.toString();
    }

    @Override
    protected void onPostExecute(Void result) {
        //Toast.makeText(contexto, body, Toast.LENGTH_LONG).show();
        //Log.i("Error", body);

        String backup[] = body.split("--");
        if(backup.length > 0) {
            String d[] = backup[0].split("\\|\\|");
            for(int i = 0; i < d.length; i++) {
                String atr[] = d[i].split("\\|");
                bd = admin.getWritableDatabase();
                registro = new ContentValues();
                registro.put("iddoc", atr[0]);
                registro.put("nomdoc", atr[1]);
                registro.put("periodo", atr[2]);
                registro.put("programa", atr[3]);
                registro.put("semestre", atr[4]);
                registro.put("unidad", atr[5]);
                registro.put("grupo", atr[6]);
                registro.put("idest", atr[7]);
                registro.put("nomest", atr[8]);
                registro.put("faltas", atr[9]);
                long reg = bd.insert("datos", null, registro);
                bd.close();
            }
            String l[] = backup[1].split("\\|\\|");
            for(int i = 0; i < l.length; i++) {
                String atr[] = l[i].split("\\|");
                //Toast.makeText(contexto, atr[0]+"", Toast.LENGTH_LONG).show();
                bd = admin.getWritableDatabase();
                registro = new ContentValues();
                registro.put("fecha", atr[0]);
                registro.put("periodo", atr[1]);
                registro.put("programa", atr[2]);
                registro.put("semestre", atr[3]);
                registro.put("unidad", atr[4]);
                registro.put("grupo", atr[5]);
                registro.put("estudiante", atr[6]);
                registro.put("faltas", atr[7]);
                long reg = bd.insert("logs", null, registro);
                bd.close();
            }
            //Llama a la activity de las unidades pero sin cargar los datos de la BD SIGEDIN
            /*Intent intent = new Intent().setClass(contexto, UnidadesActivity.class);
            contexto.startActivity(intent);
            ((LoginActivity) contexto).finish();
            pb.setVisibility(View.GONE);*/

            //Carga los datos de BD de copia de seguridad y
            //se conecta a la BD SIGEDIN y carga los demas datos
            CargarDatos cd = new CargarDatos(contexto, pb, 2, 4);
            cd.execute();
        }
        else {
            //Carga datos desde la BD SIGEDIN
            CargarDatos cd = new CargarDatos(contexto, pb, 1, 0);
            cd.execute();
        }

        super.onPostExecute(result);
    }
}