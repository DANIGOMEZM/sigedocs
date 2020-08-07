package com.itp.sigedindocentes;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.muddzdev.styleabletoast.StyleableToast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {
    private AdminSQLiteOpenHelper admin;
    private SQLiteDatabase bd;
    private Cursor fila;
    private EditText t1, t2;
    private ProgressBar pb;
    private int band = 0;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);

        admin = new AdminSQLiteOpenHelper(this, vars.bd, null, vars.version);

        //Animation animation = AnimationUtils.loadAnimation(this, R.anim.animation);
        t1 = (EditText) findViewById(R.id.editText);
        //t1.startAnimation(animation);
        t2 = (EditText) findViewById(R.id.editText2);
        //t2.startAnimation(animation);
        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.GONE);
    }

    public void iniciarSesion(View view) {
        String usu = t1.getText().toString();
        String pass = t2.getText().toString();
        vars.idusu = usu;
        vars.pass = pass;

        if (!usu.equals("")) {
            if (!pass.equals("")) {
                if(isOnline(this)) {
                    bd = admin.getWritableDatabase();
                    fila = bd.rawQuery("SELECT COUNT(*) AS cant FROM datos", null);
                    if (fila.moveToFirst()) {
                        //Log.i("Cantidad", ""+fila.getInt(0));
                        //Base de datos local sin datos
                        if (fila.getInt(0) == 0) {
                            pb.setProgress(0);
                            pb.setVisibility(View.VISIBLE);
                            LoginActivity.this.setProgress(pb.getProgress() * 1000);
                            pb.incrementProgressBy(pb.getProgress());
                            Restaurar res = new Restaurar(this, pb);
                            res.execute();
                        }
                        else {
                            pb.setProgress(0);
                            pb.setVisibility(View.VISIBLE);
                            LoginActivity.this.setProgress(pb.getProgress() * 1000);
                            pb.incrementProgressBy(pb.getProgress());
                            CargarDatos cd = new CargarDatos(this, pb, 3, 0);
                            cd.execute();
                        }
                    }
                    bd.close();
                }
                else {
                    Toast.makeText(this, "Necesitas conexión a internet para iniciar sesión", Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(this, "Por favor, ingrese su contraseña", Toast.LENGTH_LONG).show();
                //StyleableToast.makeText(this, "Por favor, ingrese su Contraseña", Toast.LENGTH_LONG, R.style.mytoast).show();
            }
        }
        else {
            Toast.makeText(this, "Por favor, ingrese su ID", Toast.LENGTH_LONG).show();
        }
    }

    public static String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }
}