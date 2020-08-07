package com.itp.sigedindocentes;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnidadesActivity extends AppCompatActivity {
    private AdminSQLiteOpenHelper admin;
    private SQLiteDatabase bd;
    private Cursor fila;
    private ListView lv;
    private Calendar fecha;
    private String periodo, pro = "", sem_gru = "", uni = "", gru = "", s = "", g = "";
    private TableRow tr[], tr2[];
    private DynamicControlsClassUnidades tvc[];
    private ViewGroup layout;
    private ScrollView sv;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unidades);

        layout = (ViewGroup) findViewById(R.id.layout);
        sv = (ScrollView) findViewById(R.id.scrollView);

        pb = (ProgressBar) findViewById(R.id.progressBar3);
        pb.setVisibility(View.GONE);

        admin = new AdminSQLiteOpenHelper(this, vars.bd, null, vars.version);
        bd = admin.getWritableDatabase();
        fila = bd.rawQuery("SELECT iddoc, nomdoc FROM datos LIMIT 1", null);
        if (fila.moveToFirst()) {
            vars.idusu = fila.getString(0);
            String docente[] = fila.getString(1).split(" ");
            this.setTitle(MethodsClass.textCapWords(docente[0] + " " + docente[1] + " " + docente[2]));
        }
        bd.close();

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

        lv = (ListView) findViewById(R.id.listView);
        cargar_lista();
    }

    private void cargar_lista() {
        LayoutInflater inflater = LayoutInflater.from(this);
        int id = R.layout.layout_dinamico;
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(id, null, false);
        layout.removeAllViewsInLayout();
        sv.scrollTo(0, 0);

        //Cantidad de programas
        bd = admin.getWritableDatabase();
        fila = bd.rawQuery("SELECT DISTINCT programa FROM datos " +
                "WHERE periodo = '"+periodo+"' ORDER BY programa DESC", null);
        int c = 0;
        while (fila.moveToNext()) {
            c++;
        }
        bd.close();

        tvc = new DynamicControlsClassUnidades[c];
        tr = new TableRow[c];
        tr2 = new TableRow[c];

        //Crear arreglos de objetos y filas por cada programa
        bd = admin.getWritableDatabase();
        fila = bd.rawQuery("SELECT DISTINCT programa FROM datos " +
                "WHERE periodo = '"+periodo+"' ORDER BY programa DESC", null);
        int i = 0;
        while (fila.moveToNext()) {
            pro = fila.getString(0);
            tvc[i] = new DynamicControlsClassUnidades(this, pro);
            tr[i] = new TableRow(this);
            tr2[i] = new TableRow(this);
            i++;
        }
        bd.close();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = (int) (metrics.widthPixels * 0.92);
        //Toast.makeText(this, ""+width, Toast.LENGTH_LONG).show();

        TableRow.LayoutParams param[] = new TableRow.LayoutParams[15];
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams p = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        param[i] = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        for (i = 0; i < param.length; i++) {
            param[i] = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        }
        for(i=0; i < c; i++) {
            tr[i].addView(tvc[i].getT(), p);
            tvc[i].getT().setWidth(width);
            if (tvc[i].getT().getText().toString().indexOf("AMBIENTAL") != -1) {
                tvc[i].getT().setTextColor(Color.parseColor("#589b32"));
                tr[i].addView(tvc[i].getIb(), p);
                tvc[i].getIb().setImageResource(R.drawable.ic_ambiental);
            }
            else if (tvc[i].getT().getText().toString().indexOf("SISTEMAS") != -1 ||
                    tvc[i].getT().getText().toString().indexOf("SOFTWARE") != -1) {
                tvc[i].getT().setTextColor(Color.parseColor("#ed1b34"));
                tr[i].addView(tvc[i].getIb(), p);
                tvc[i].getIb().setImageResource(R.drawable.ic_sistemas);
            }
            else if (tvc[i].getT().getText().toString().indexOf("CIVIL") != -1 ||
                    tvc[i].getT().getText().toString().indexOf("OBRAS") != -1) {
                tvc[i].getT().setTextColor(Color.parseColor("#f08523"));
                tr[i].addView(tvc[i].getIb(), p);
                tvc[i].getIb().setImageResource(R.drawable.ic_civil);
            }
            else if (tvc[i].getT().getText().toString().indexOf("FORESTAL") != -1) {
                tvc[i].getT().setTextColor(Color.parseColor("#78866B"));
                tr[i].addView(tvc[i].getIb(), p);
                tvc[i].getIb().setImageResource(R.drawable.ic_forestal);
            }
            else if (tvc[i].getT().getText().toString().indexOf("AGRO") != -1) {
                tvc[i].getT().setTextColor(Color.parseColor("#768a95"));
                tr[i].addView(tvc[i].getIb(), p);
                tvc[i].getIb().setImageResource(R.drawable.ic_agro);
            }
            else if (tvc[i].getT().getText().toString().indexOf("EMPRESA") != -1) {
                tvc[i].getT().setTextColor(Color.parseColor("#3959ae"));
                tr[i].addView(tvc[i].getIb(), p);
                tvc[i].getIb().setImageResource(R.drawable.ic_admin);
            }
            else if (tvc[i].getT().getText().toString().indexOf("CONTA") != -1) {
                tvc[i].getT().setTextColor(Color.parseColor("#3959ae"));
                tr[i].addView(tvc[i].getIb(), p);
                tvc[i].getIb().setImageResource(R.drawable.ic_conta);
            }
            else if (tvc[i].getT().getText().toString().indexOf("NEGOCIOS") != -1) {
                tvc[i].getT().setTextColor(Color.parseColor("#89490c"));
                tr[i].addView(tvc[i].getIb(), p);
                tvc[i].getIb().setImageResource(R.drawable.ic_negocios);
            }
            tr2[i].addView(tvc[i].getL(), param[i]);

            //Llenar el ArrayList para cargar el adaptador
            ArrayList<String> unidades = new ArrayList<>();
            bd = admin.getWritableDatabase();
            fila = bd.rawQuery("SELECT DISTINCT programa, semestre, unidad, grupo FROM datos " +
                    "WHERE periodo = '"+periodo+"' AND programa = '"+tvc[i].getT().getText().toString()+"' ORDER BY programa DESC", null);
            while (fila.moveToNext()) {
                unidades.add(fila.getString(2) + " " + fila.getString(1) + fila.getString(3));
            }
            bd.close();

            //Cambiar el tamaño de texto del ListView
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (getApplicationContext(), android.R.layout.simple_list_item_1, unidades) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent){
                    View view = super.getView(position, convertView, parent);
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);
                    return view;
                }
            };

            tvc[i].getL().setAdapter(adapter);
            ListAdapter listAdapter = adapter;
            int totalHeight = 0;
            for (int j = 0; j < listAdapter.getCount(); j++) {
                View listItem = listAdapter.getView(j, null, tvc[j].getL());
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = tvc[i].getL().getLayoutParams();
            params.height = totalHeight + (tvc[i].getL().getDividerHeight() * (listAdapter.getCount())) + 50;
            tvc[i].getL().setLayoutParams(params);
            tvc[i].getL().requestLayout();

            tvc[i].getL().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String elem = adapterView.getItemAtPosition(i).toString();

                    uni = "";
                    String[] datos = elem.split(" ");
                    for (i = 0; i < datos.length - 1; i++) {
                        uni += datos[i] + " ";
                    }
                    sem_gru = datos[datos.length-1];
                    if(sem_gru.length() == 2) {
                        s = sem_gru.substring(0, 1);
                        g = sem_gru.substring(1, 2);
                    }
                    else {
                        s = sem_gru.substring(0, 2);
                        g = sem_gru.substring(2, 3);
                    }

                    bd = admin.getWritableDatabase();
                    fila = bd.rawQuery("SELECT DISTINCT programa FROM datos WHERE periodo = '"+periodo+"'" +
                            " AND semestre = '"+s+"' AND unidad = '"+uni.trim()+"' AND grupo = '"+g+"' ORDER BY programa DESC", null);
                    if (fila.moveToFirst()) {
                        pro = fila.getString(0);
                    }
                    bd.close();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("programa", pro);
                    intent.putExtra("unidad", uni.trim());
                    intent.putExtra("semestre", s);
                    intent.putExtra("grupo", g);
                    startActivity(intent);
                }
            });

            layout.addView(tr[i], lp);
            layout.addView(tr2[i], lp);
        }
        layout.addView(relativeLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_unidades, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.idlog) {
            Intent intent = new Intent().setClass(UnidadesActivity.this, LogActivity.class);
            startActivity(intent);
        }

        if (id == R.id.idupdate) {
            pb.setProgress(0);
            pb.setVisibility(View.VISIBLE);
            UnidadesActivity.this.setProgress(pb.getProgress() * 1000);
            pb.incrementProgressBy(pb.getProgress());
            CargarDatos cd = new CargarDatos(this, pb, 2, 1);
            cd.execute();
        }

        /*if (id == R.id.backup) {
            pb.setProgress(0);
            pb.setVisibility(View.VISIBLE);
            UnidadesActivity.this.setProgress(pb.getProgress() * 1000);
            pb.incrementProgressBy(pb.getProgress());
            Backup bk = new Backup(this, pb);
            bk.execute();
        }*/

        return super.onOptionsItemSelected(item);
    }

    //Confirmación para cerrar la aplicación
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Información")
                    .setMessage("¿Está seguro que desea salir de la aplicación?")
                    .setNegativeButton("Cancelar", null)
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UnidadesActivity.this.finish();
                        }
                    })
                    .show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargar_lista();
    }
}