package com.itp.sigedindocentes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(Context context, String nombre, CursorFactory factory, int version) {
        super(context, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE datos(iddatos INTEGER PRIMARY KEY AUTOINCREMENT, iddoc VARCHAR(12), nomdoc VARCHAR(50), "+
                "periodo VARCHAR(6), programa VARCHAR(60), semestre INTEGER(2), unidad VARCHAR(50), grupo CHAR(1), "+
                "idest VARCHAR(12), nomest VARCHAR(50), faltas TINYINT(2))");
        db.execSQL("CREATE TABLE logs(idlog INTEGER PRIMARY KEY AUTOINCREMENT, fecha VARCHAR(20), periodo VARCHAR(6), "+
                "programa VARCHAR(60), semestre INTEGER(2), unidad VARCHAR(50), grupo CHAR(1), estudiante VARCHAR(70), "+
                "faltas VARCHAR(2))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnte, int versionNue) {
        db.execSQL("DROP TABLE IF EXISTS datos");
        db.execSQL("DROP TABLE IF EXISTS logs");
        db.execSQL("CREATE TABLE datos(iddatos INTEGER PRIMARY KEY AUTOINCREMENT, iddoc VARCHAR(12), nomdoc VARCHAR(50), "+
                "periodo VARCHAR(6), programa VARCHAR(60), semestre INTEGER(2), unidad VARCHAR(50), grupo CHAR(1), "+
                "idest VARCHAR(12), nomest VARCHAR(50), faltas TINYINT(2))");
        db.execSQL("CREATE TABLE logs(idlog INTEGER PRIMARY KEY AUTOINCREMENT, fecha VARCHAR(20), periodo VARCHAR(6), "+
                "programa VARCHAR(60), semestre INTEGER(2), unidad VARCHAR(50), grupo CHAR(1), estudiante VARCHAR(70), "+
                "faltas VARCHAR(2))");
    }
}