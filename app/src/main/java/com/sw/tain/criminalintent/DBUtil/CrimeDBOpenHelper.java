package com.sw.tain.criminalintent.DBUtil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sw.tain.criminalintent.DBUtil.CrimeDBSchema.CrimeTable;

/**
 * Created by home on 2016/11/9.
 */

public class CrimeDBOpenHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASENAME = "crimes.db";

    public CrimeDBOpenHelper(Context context) {
        super(context, DATABASENAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CrimeTable.NAME + "( " +
                "_id integer primary key autoincrement, " +
                CrimeTable.Col.UUID + ", " +
                CrimeTable.Col.TITLE + ", " +
                CrimeTable.Col.DATE + ", " +
                CrimeTable.Col.SOLVED + ", " +
                CrimeTable.Col.SUSPECT + " )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
