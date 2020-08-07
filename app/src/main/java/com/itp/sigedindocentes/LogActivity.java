package com.itp.sigedindocentes;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class LogActivity extends AppCompatActivity {
    private AdminSQLiteOpenHelper admin;
    private SQLiteDatabase bd;
    private Cursor fila;
    private ListView lv;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_log);

        this.setTitle("Registros");

        lv = (ListView) findViewById(R.id.listView);
        ArrayList<String> logs = new ArrayList<>();

        admin = new AdminSQLiteOpenHelper(this, vars.bd, null, vars.version);
        bd = admin.getWritableDatabase();
        fila = bd.rawQuery("SELECT * FROM logs ORDER BY idlog DESC", null);
        while (fila.moveToNext()) {
            logs.add(fila.getString(1) + "\n" + MethodsClass.textCapWords(fila.getString(7)) + " " + fila.getString(8));
        }
        bd.close();

        final int n = logs.size();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, logs);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //String item = adapterView.getItemAtPosition(i).toString();
                //Toast.makeText(getApplicationContext(), ""+Math.abs(i-n), Toast.LENGTH_LONG).show();
                bd = admin.getWritableDatabase();
                fila = bd.rawQuery("SELECT * FROM logs WHERE idlog = '"+Math.abs(i-n)+"'", null);
                String texto = "";
                if (fila.moveToFirst()) {
                    texto = fila.getString(1) + "\n" + fila.getString(3) + "\nSEMESTRE: " +
                            fila.getString(4) + "\n" + fila.getString(5) + "\nGRUPO " +
                            fila.getString(6) + "\n" + fila.getString(7) + " " + fila.getString(8);

                    AlertDialog.Builder builder = new AlertDialog.Builder(LogActivity.this, R.style.CustomAlertDialog);
                    builder.setTitle("Información");
                    builder.setMessage(texto);
                    builder.setPositiveButton("Aceptar", null);
                    builder.setIcon(android.R.drawable.ic_dialog_info);

                    final AlertDialog dialog = builder.create();
                    //dialog.getWindow().setBackgroundDrawableResource(R.drawable.gradient);
                    dialog.show();
                    //Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    //b.setBackgroundColor(R.drawable.gradientbuttonprimary);
                    //b.setTextColor(Color.BLACK);
                }
                bd.close();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logs, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    public void onBackPressed() {
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
