package com.itp.sigedindocentes;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private AdminSQLiteOpenHelper admin;
    private SQLiteDatabase bd;
    private ContentValues registro;
    private Cursor fila;
    private MatrixCursor extras;
    private SimpleCursorAdapter adapter;
    private TableRow tr[];
    private DynamicControlsClass tvc[];
    private Calendar fecha;
    private String periodo, nomest = "", progr = "", unid = "", sem_gru = "", s = "", g = "";
    private TextView t1;
    private ViewGroup layout;
    private ScrollView sv;
    private ProgressBar pb;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        progr = getIntent().getStringExtra("programa");
        unid = getIntent().getStringExtra("unidad");
        s = getIntent().getStringExtra("semestre");
        g = getIntent().getStringExtra("grupo");

        admin = new AdminSQLiteOpenHelper(this, vars.bd, null, vars.version);
        bd = admin.getWritableDatabase();
        fila = bd.rawQuery("SELECT iddoc, nomdoc FROM datos LIMIT 1", null);
        if (fila.moveToFirst()) {
            vars.idusu = fila.getString(0);
            String docente[] = fila.getString(1).split(" ");
            this.setTitle(MethodsClass.textCapWords(docente[0] + " " + docente[1] + " " + docente[2]));
        }
        bd.close();

        layout = (ViewGroup) findViewById(R.id.layout);
        sv = (ScrollView) findViewById(R.id.scrollView);
        pb = (ProgressBar) findViewById(R.id.progressBar2);
        pb.setVisibility(View.GONE);

        t1 = (TextView) findViewById(R.id.textView);

        fecha = Calendar.getInstance();
        int a = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH) + 1;

        //Recordar que el semestre va de enero a julio
        if (mes >= 2 && mes <= 8) {
            periodo = a + "-1";
        }
        else {
            periodo = a + "-2";
        }

        cargarEstudiantes(periodo, progr, s, unid, g);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarEstudiantes(periodo, progr, s, unid, g);
    }

    public void cargarEstudiantes(String per, String pro, String sem, String uni, String gru) {
        //Toast.makeText(this, per+"\n"+pro+"\n"+sem+"\n"+uni+"\n"+gru, Toast.LENGTH_LONG).show();

        if (pro.length() < 45) {
            t1.setText(pro + " - " + sem + "\n" + uni + " - " + gru);
        }
        else {
            t1.setText(pro + " - " + sem + " - " + uni + " - " + gru);
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        int id = R.layout.layout_dinamico;
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(id, null, false);
        layout.removeAllViewsInLayout();
        sv.scrollTo(0, 0);


        bd = admin.getWritableDatabase();
        fila = bd.rawQuery("SELECT COUNT(iddatos) FROM datos WHERE periodo = '"+per+"' AND programa = '"+pro+"'"+
                "AND semestre = '"+sem+"' AND unidad = '"+uni+"' AND grupo = '"+gru+"'", null);
        int c = 0;
        if (fila.moveToFirst()) {
            c = fila.getInt(0);
        }
        bd.close();

        tvc = new DynamicControlsClass[c];
        tr = new TableRow[c];

        bd = admin.getWritableDatabase();
        fila = bd.rawQuery("SELECT iddatos, idest, nomest, faltas FROM datos WHERE periodo = '"+per+"'"+
                "AND programa = '"+pro+"' AND semestre = '"+sem+"' AND unidad = '"+uni+"' AND grupo = '"+gru+"' "+
                "ORDER BY nomest", null);
        int iddatos = 0, i = 0, faltas = 0;
        String idest = "";
        while (fila.moveToNext()) {
            iddatos = fila.getInt(0);
            idest = fila.getString(1);
            nomest = fila.getString(2);
            faltas = fila.getInt(3);
            tvc[i] = new DynamicControlsClass(iddatos, this, nomest, faltas, i+1);
            tr[i] = new TableRow(this);
            i++;
        }
        bd.close();

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams param = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        for(i=0; i<c; i++) {
            tr[i].addView(tvc[i].getT2(), param);
            tr[i].addView(tvc[i].getIb1(), param);
            tr[i].addView(tvc[i].getE(), param);
            tr[i].addView(tvc[i].getIb2(), param);
            tr[i].addView(tvc[i].getT1(), param);
            setOnClick(tvc[i].getIb1(), tvc[i].getIb2(), tvc[i].getE(), tvc[i].getT1(), i, per, pro, sem, uni, gru);
            layout.addView(tr[i], lp);
        }
        layout.addView(relativeLayout);
    }

    public void setOnClick(final ImageButton b1, final ImageButton b2, final EditText e, final TextView t, final int i,
                           final String pe, final String pr, final String s, final String u, final String g) {

        //iv.setVisibility(View.GONE);
        String fh = "yyyy-MM-dd HH:mm:ss";
        final Date date = fecha.getTime();
        final SimpleDateFormat sdf = new SimpleDateFormat(fh);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!e.getText().toString().equals("0")) {
                    int f = Integer.parseInt(e.getText().toString()) - 1;
                    e.setText("" + f);
                    bd = admin.getWritableDatabase();
                    registro = new ContentValues();
                    registro.put("faltas", f);
                    bd.update("datos", registro, "iddatos="+e.getId(), null);
                    bd.close();

                    bd = admin.getWritableDatabase();
                    registro = new ContentValues();
                    registro.put("fecha", sdf.format(date));
                    registro.put("periodo", pe);
                    registro.put("programa", pr);
                    registro.put("semestre", s);
                    registro.put("unidad", u);
                    registro.put("grupo", g);
                    registro.put("estudiante", t.getText().toString());
                    registro.put("faltas", "-1");
                    long reg = bd.insert("logs", null, registro);
                    bd.close();
                }
                else {
                    e.setText("0");
                }
                Backup bk = new Backup(getApplicationContext());
                bk.execute();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!e.getText().toString().equals("25")) {
                    int f = Integer.parseInt(e.getText().toString()) + 1;
                    e.setText("" + f);
                    bd = admin.getWritableDatabase();
                    registro = new ContentValues();
                    registro.put("faltas", f);
                    bd.update("datos", registro, "iddatos="+e.getId(), null);
                    bd.close();

                    bd = admin.getWritableDatabase();
                    registro = new ContentValues();
                    registro.put("fecha", sdf.format(date));
                    registro.put("periodo", pe);
                    registro.put("programa", pr);
                    registro.put("semestre", s);
                    registro.put("unidad", u);
                    registro.put("grupo", g);
                    registro.put("estudiante", t.getText().toString());
                    registro.put("faltas", "+1");
                    long reg = bd.insert("logs", null, registro);
                    bd.close();
                }
                else {
                    e.setText("25");
                }
                Backup bk = new Backup(getApplicationContext());
                bk.execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        if (id == R.id.idlog) {
            Intent intent = new Intent().setClass(MainActivity.this, LogActivity.class);
            startActivity(intent);
        }

        if (id == R.id.idupdate) {
            pb.setProgress(0);
            pb.setVisibility(View.VISIBLE);
            MainActivity.this.setProgress(pb.getProgress() * 1000);
            pb.incrementProgressBy(pb.getProgress());
            CargarDatos cd = new CargarDatos(this, pb, 2, 2);
            cd.execute();
        }

        return super.onOptionsItemSelected(item);
    }
}