package com.itp.sigedindocentes;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;

public class CargarDatos extends AsyncTask<Void, Void, Void> {
    private Context contexto;
    private String body = "", urlParameters = "", datos = "", mensaje = "";
    private AdminSQLiteOpenHelper admin;
    private SQLiteDatabase bd;
    private Cursor fila;
    private ContentValues registro;
    private ProgressBar pb;
    private int band, band2 = 0, band3 = 0;

    public CargarDatos(Context contexto, ProgressBar pb, int band, int band3) {
        this.contexto = contexto;
        this.pb = pb;
        this.band = band;
        this.band3 = band3;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        admin = new AdminSQLiteOpenHelper(contexto, vars.bd, null, vars.version);

        URL url = null;
        try {
            urlParameters = "usu=" + URLEncoder.encode(vars.idusu, "utf-8") +
                    "&pswd=" + URLEncoder.encode(vars.pass, "utf-8");
            url = new URL(vars.url);
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

        int r = 0;
        try {
            if (!body.equals("100") && !body.equals("200") && !body.equals("300") && !body.equals("400")) {
                if (!body.equals("") && !body.equals("[]") && !body.equals("datos_incorrectos")) {
                    JSONArray jArray = new JSONArray(body);
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject user = jArray.getJSONObject(i);
                        String iddoc = URLDecoder.decode(user.getString("iddoc"), "UTF-8");
                        String nomdoc = URLDecoder.decode(user.getString("nomdoc"), "UTF-8");
                        String periodo = URLDecoder.decode(user.getString("periodo"), "UTF-8");
                        String programa = URLDecoder.decode(user.getString("programa"), "UTF-8");
                        String semestre = "", sem = URLDecoder.decode(user.getString("semestre"), "UTF-8");
                        if (sem.equals("PRIMER SEMESTRE")) semestre = "1";
                        else if (sem.equals("SEGUNDO SEMESTRE")) semestre = "2";
                        else if (sem.equals("TERCER SEMESTRE")) semestre = "3";
                        else if (sem.equals("CUARTO SEMESTRE")) semestre = "4";
                        else if (sem.equals("QUINTO SEMESTRE")) semestre = "5";
                        else if (sem.equals("SEXTO SEMESTRE")) semestre = "6";
                        else if (sem.equals("SEPTIMO SEMESTRE")) semestre = "7";
                        else if (sem.equals("OCTAVO SEMESTRE")) semestre = "8";
                        else if (sem.equals("NOVENO SEMESTRE")) semestre = "9";
                        else semestre = "10";
                        String unidad = URLDecoder.decode(user.getString("unidad"), "UTF-8");
                        String grupo = URLDecoder.decode(user.getString("grupo"), "UTF-8");
                        String idest = URLDecoder.decode(user.getString("idest"), "UTF-8");
                        String nomest = URLDecoder.decode(user.getString("nomest"), "UTF-8");
                        if (band == 1) {
                            bd = admin.getWritableDatabase();
                            registro = new ContentValues();
                            registro.put("iddoc", iddoc);
                            registro.put("nomdoc", nomdoc);
                            registro.put("periodo", periodo);
                            registro.put("programa", programa);
                            registro.put("semestre", semestre);
                            registro.put("unidad", unidad);
                            registro.put("grupo", grupo);
                            registro.put("idest", idest);
                            registro.put("nomest", nomest);
                            long reg = bd.insert("datos", null, registro);
                            bd.close();
                        } else if (band == 2) {
                            bd = admin.getWritableDatabase();
                            fila = bd.rawQuery("SELECT COUNT(iddatos) FROM datos WHERE iddoc = '"+iddoc+"' AND nomdoc = '"+nomdoc+"'"+
                                    "AND periodo = '"+periodo+"' AND programa = '"+programa+"' AND semestre = '"+semestre+"'"+
                                    "AND unidad = '"+unidad+"' AND grupo = '"+grupo+"' AND idest = '"+idest+"' AND nomest = '"+nomest+"'", null);
                            int c = 0;
                            if (fila.moveToFirst()) {
                                c = fila.getInt(0);
                            }
                            bd.close();
                            if (c == 0) {
                                bd = admin.getWritableDatabase();
                                registro = new ContentValues();
                                registro.put("iddoc", iddoc);
                                registro.put("nomdoc", nomdoc);
                                registro.put("periodo", periodo);
                                registro.put("programa", programa);
                                registro.put("semestre", semestre);
                                registro.put("unidad", unidad);
                                registro.put("grupo", grupo);
                                registro.put("idest", idest);
                                registro.put("nomest", nomest);
                                long reg = bd.insert("datos", null, registro);
                                bd.close();
                                band2 = 1;
                                datos = datos + programa + " - " + unidad + " - " + semestre + grupo + "\n" +
                                        MethodsClass.textCapWords(nomest) +"\n\n";
                                r++;
                            }
                        }
                    }
                    if (band == 1 || band == 3 || band3 == 4) {
                        Intent intent = new Intent().setClass(contexto, UnidadesActivity.class);
                        contexto.startActivity(intent);
                        ((LoginActivity) contexto).finish();
                        pb.setVisibility(View.GONE);
                    } else {
                        if (band2 == 1) {
                            if (r > 1) {
                                //Toast.makeText(contexto, "Se agregaron "+ r +" nuevos registros:\n\n" + datos, Toast.LENGTH_LONG).show();
                                mensaje = "Se agregaron "+ r +" nuevos registros";
                            } else {
                                //Toast.makeText(contexto, "Se agregó "+ r +" nuevo registro:\n\n" + datos, Toast.LENGTH_LONG).show();
                                mensaje = "Se agregó "+ r +" nuevo registro";
                            }

                            StringBuilder stringBuilder = new StringBuilder(datos);
                            //Se invierte la cadena para quitar los saltos de línea que están al final
                            StringBuilder sb = new StringBuilder(stringBuilder.reverse().toString().replaceFirst("\n\n", ""));

                            AlertDialog.Builder builder = new AlertDialog.Builder(contexto, R.style.CustomAlertDialog);
                            builder.setTitle(mensaje);
                            builder.setMessage(sb.reverse());
                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (band3 == 1) {
                                        Intent intent = new Intent(contexto, UnidadesActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        contexto.startActivity(intent);
                                    }
                                    if (band3 == 2) {
                                        Intent intent = new Intent(contexto, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        contexto.startActivity(intent);
                                    }
                                }
                            });
                            builder.setIcon(android.R.drawable.ic_dialog_info);

                            final AlertDialog dialog = builder.create();
                            dialog.show();

                            pb.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(contexto, "No hay datos nuevos disponibles, por favor intente nuevamente", Toast.LENGTH_LONG).show();
                            pb.setVisibility(View.GONE);
                            /*SuperActivityToast.create(contexto, new Style(), Style.TYPE_STANDARD)
                                    .setProgressBarColor(Color.WHITE)
                                    .setText(" No hay datos nuevos disponibles,\npor favor intente nuevamente")
                                    //.setColor(contexto.getResources().getColor(android.R.color.background_light))
                                    //.setTextColor(Color.WHITE)
                                    .setIconPosition(Style.ICONPOSITION_LEFT)
                                    .setIconResource(android.R.drawable.ic_dialog_info)
                                    .setDuration(Style.DURATION_LONG)
                                    .setFrame(Style.FRAME_KITKAT)
                                    .setColor(PaletteUtils.getTransparentColor(PaletteUtils.MATERIAL_INDIGO))
                                    .setAnimations(Style.ANIMATIONS_SCALE).show();*/
                        }
                    }
                } else {
                    Toast.makeText(contexto, "Los datos ingresados son incorrectos, por favor intente nuevamente", Toast.LENGTH_LONG).show();
                    pb.setVisibility(View.GONE);
                }
            }
            else {
                Toast.makeText(contexto, "No hay conexión con el servidor, los datos no fueron cargados", Toast.LENGTH_LONG).show();
                pb.setVisibility(View.GONE);
            }
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
            //Toast.makeText(contexto, e.toString(), Toast.LENGTH_LONG).show();
            pb.setVisibility(View.GONE);
        }

        super.onPostExecute(result);
    }
}