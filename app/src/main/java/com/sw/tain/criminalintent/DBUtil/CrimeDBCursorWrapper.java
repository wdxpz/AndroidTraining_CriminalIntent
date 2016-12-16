package com.sw.tain.criminalintent.DBUtil;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.sw.tain.criminalintent.Crime;
import com.sw.tain.criminalintent.DBUtil.CrimeDBSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by home on 2016/11/9.
 */

public class CrimeDBCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public CrimeDBCursorWrapper(Cursor cursor) {
        super(cursor);
    }


    public Crime getCrime(){
        String uuid = getString(getColumnIndex(CrimeTable.Col.UUID));
        String title = getString(getColumnIndex(CrimeTable.Col.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Col.DATE));
        int solved = getInt(getColumnIndex(CrimeTable.Col.SOLVED));
        String suspect = getString(getColumnIndex(CrimeTable.Col.SUSPECT));

        Crime crime = new Crime(UUID.fromString(uuid));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setResolved(solved==1?true:false);
        crime.setSuspect(suspect);

        return crime;
    }
}
