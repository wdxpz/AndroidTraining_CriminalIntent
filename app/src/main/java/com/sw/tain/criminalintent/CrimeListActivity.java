package com.sw.tain.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by home on 2016/10/26.
 */

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks{
    @Override
    protected Fragment CreateFragment() {
        return new CrimeListFragment();
    }

    protected int getLayoutRes(){
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onClickCrimeItem(Crime crime) {
        if(findViewById(R.id.detail_fragment_container)==null){
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        }else{
            FragmentManager fm = getSupportFragmentManager();
            CrimeFragment fragment = CrimeFragment.newInstance(crime.getId());
            fm.beginTransaction().replace(R.id.detail_fragment_container, fragment).commit();
        }
    }

    @Override
    public void OnCrimeItemChanged() {
        FragmentManager fm = getSupportFragmentManager();
        CrimeListFragment crimeListFragment = (CrimeListFragment) fm.findFragmentById(R.id.fragment_container);
        crimeListFragment.updateUI();
    }

    @Override
    public void OnCrimeItemDeleted() {
        FragmentManager fm = getSupportFragmentManager();
        CrimeListFragment crimeListFragment = (CrimeListFragment) fm.findFragmentById(R.id.fragment_container);
        crimeListFragment.updateUI();
        CrimeFragment crimeFragment = (CrimeFragment)fm.findFragmentById(R.id.detail_fragment_container);
        fm.beginTransaction().remove(crimeFragment).commit();
    }
}
