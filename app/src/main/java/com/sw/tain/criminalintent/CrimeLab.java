package com.sw.tain.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.sw.tain.criminalintent.DBUtil.CrimeDBCursorWrapper;
import com.sw.tain.criminalintent.DBUtil.CrimeDBOpenHelper;
import com.sw.tain.criminalintent.DBUtil.CrimeDBSchema.CrimeTable;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by home on 2016/10/26.
 */

public class CrimeLab {
    protected static CrimeLab sCrimeLab;
    private SQLiteDatabase mDatabase;
    private Context mContext;


    protected CrimeLab(Context context) {
        mContext = context.getApplicationContext();

        CrimeDBOpenHelper dbOpenHelper = new CrimeDBOpenHelper(context);
        mDatabase = dbOpenHelper.getWritableDatabase();

    }
   public static CrimeLab getCrimeLab(Context context){
        if(sCrimeLab==null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();

        CrimeDBCursorWrapper cursor = queryCrime(null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Crime crime = cursor.getCrime();
            crimes.add(crime);
            cursor.moveToNext();
        }
        cursor.close();

        return crimes;
    }

    public Crime getCrime(UUID id){
        CrimeDBCursorWrapper cursor = queryCrime(CrimeTable.Col.UUID+"=?", new String[]{id.toString()});
        if(cursor.getCount()==0)
            return null;
        cursor.moveToFirst();
        Crime crime = cursor.getCrime();
        cursor.close();
        return crime;
    }

    public void addCrime(Crime crime){
        ContentValues values = getContentValues(crime);
        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    public void updateCrime(Crime crime){
        ContentValues values = getContentValues(crime);
        mDatabase.update(CrimeTable.NAME, values, CrimeTable.Col.UUID+"=?", new String[]{crime.getId().toString()});
    }

    public void deleteCrime(Crime crime){
        mDatabase.delete(CrimeTable.NAME, CrimeTable.Col.UUID+"=?", new String[]{crime.getId().toString()});
    }

    public File getPictureFile(Crime crime){
        File externalFileDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(externalFileDir==null) return null;
        return new File(externalFileDir, crime.getPictureFileName());
    }

    private ContentValues getContentValues(Crime crime){
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Col.UUID, crime.getId().toString());
        values.put(CrimeTable.Col.TITLE, crime.getTitle());
        values.put(CrimeTable.Col.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Col.SOLVED, crime.isResolved()?1:0);
        values.put(CrimeTable.Col.SUSPECT, crime.getSuspect());
        return values;
    }

    private CrimeDBCursorWrapper queryCrime(String whereClause, String[]whereArgs) {
        Cursor cursor = mDatabase.query(CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null, null, null);
        return new CrimeDBCursorWrapper(cursor);
    }
}
