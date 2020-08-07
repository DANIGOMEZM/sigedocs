package com.itp.sigedindocentes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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

public class Backup extends AsyncTask<Void, Void, Void> {
    private Context contexto;
    private String body = "", urlParameters = "", datos = "", logs = "";
    private AdminSQLiteOpenHelper admin;
    private SQLiteDatabase bd;
    private Cursor fila;
    private ProgressBar pb;

    public Backup(Context contexto) {
        this.contexto = contexto;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        admin = new AdminSQLiteOpenHelper(contexto, vars.bd, null, vars.version);

        bd = admin.getWritableDatabase();
        fila = bd.rawQuery("SELECT * FROM datos WHERE faltas >= 1", null);
        while (fila.moveToNext()) {
            datos += fila.getString(2) + "|" + fila.getString(3) + "|" + fila.getString(4) + "|" +
                    fila.getString(5) + "|" + fila.getString(6) + "|" + fila.getString(7) + "|" +
                    fila.getString(8) + "|" + fila.getString(9) + "|" + fila.getString(10) + "||";
        }
        bd.close();

        bd = admin.getWritableDatabase();
        fila = bd.rawQuery("SELECT * FROM logs", null);
        while (fila.moveToNext()) {
            logs += fila.getString(1) + "|" + fila.getString(2) + "|" + fila.getString(3) + "|" +
                    fila.getString(4) + "|" + fila.getString(5) + "|" + fila.getString(6) + "|" +
                    fila.getString(7) + "|" + fila.getString(8) + "||";
        }
        bd.close();

        URL url = null;
        try {
            urlParameters = "iddoc=" + URLEncoder.encode(vars.idusu, "utf-8") +
                    "&datos=" + URLEncoder.encode(datos, "utf-8") +
                    "&logs=" + URLEncoder.encode(logs, "utf-8");
            url = new URL(vars.url_backup);
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

        /*if(body.equals("ok")) {
            Toast.makeText(contexto, "Copia de seguridad creada exitosamente", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(contexto, "No se pudo crear la copia de seguridad", Toast.LENGTH_LONG).show();
        }*/
        //pb.setVisibility(View.GONE);

        super.onPostExecute(result);
    }
}