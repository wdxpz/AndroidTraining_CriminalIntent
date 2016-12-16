package com.sw.tain.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.List;
import java.util.UUID;

/**
 * Created by home on 2016/11/1.
 */

public class CrimePagerActivity extends AppCompatActivity implements CrimeFragment.Callbacks{

    private static final String Extra_Crime_ID = "com.sw.tain.criminalintent.crime_id";
    private ViewPager mViewPager;
    private List<Crime> mCrimes;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);


        mCrimes = CrimeLab.getCrimeLab(this).getCrimes();

        FragmentManager fm = getSupportFragmentManager();
        mViewPager = (ViewPager)findViewById(R.id.activity_crime_pager);
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                CrimeFragment fragment = CrimeFragment.newInstance(mCrimes.get(position).getId());
                return fragment;
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        Intent intent = getIntent();
        UUID uuid = (UUID)intent.getSerializableExtra(Extra_Crime_ID);
        for(int i=0; i<mCrimes.size(); i++){
            if(mCrimes.get(i).getId().equals(uuid)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    public static Intent newIntent(Context packageContext, UUID crimeid){
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(Extra_Crime_ID, crimeid);
        return intent;
    }

    @Override
    public void OnCrimeItemChanged() {

    }

    @Override
    public void OnCrimeItemDeleted() {
        this.finish();
    }
}
