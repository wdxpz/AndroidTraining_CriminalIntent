package com.sw.tain.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;

import java.util.Date;
import java.util.UUID;

/**
 * Created by home on 2016/11/1.
 */

public class CrimeDatePickerActivity extends SingleFragmentActivity {

    public static final String EXTRA_CRIME_DATE="date";
    @Override
    protected Fragment CreateFragment() {
        Intent intent = getIntent();
        if(intent!=null){
            Date date = (Date)intent.getSerializableExtra(EXTRA_CRIME_DATE);
            Fragment fragment = CrimeDatePickerFragment.newInstance(date);
            return fragment;
        }
        return null;
    }

    public static Intent newIntent(Context packageContext, Date date) {
        Intent intent = new Intent(packageContext, CrimeDatePickerActivity.class);
        intent.putExtra(EXTRA_CRIME_DATE, date);
        return intent;
    }
}
