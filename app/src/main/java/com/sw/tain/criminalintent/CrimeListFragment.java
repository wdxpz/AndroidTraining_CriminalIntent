package com.sw.tain.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by home on 2016/10/26.
 */

public class CrimeListFragment extends Fragment {
    protected RecyclerView mRecyclerView;
    protected CrimeAdapter mAdapter;
    protected TextView mEmptyCrimeTextView;
    protected Button mAddFirstCrimeButton;

    private static final int REQUEST_CRIME = 1;
    private static final String SHOW_HIDE_MENU_STAT = "showhidemenu";
    private int mCurrentPos;
    private boolean mTitleVisible;
    private Callbacks mCallback;

    public interface Callbacks{
        void onClickCrimeItem(Crime crime);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        CrimeLab crimeLab = CrimeLab.getCrimeLab(getActivity());
        int crimeCount = crimeLab.getCrimes().size();

        View view;
        view =  inflater.inflate(R.layout.fragment_empty_crime_list, container, false);
        mEmptyCrimeTextView = (TextView) view.findViewById(R.id.textView_empty_crime_text);
        mAddFirstCrimeButton = (Button)view.findViewById(R.id.button_add_first_crime);
        mAddFirstCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crime crime = new Crime();
                CrimeLab.getCrimeLab(getActivity()).addCrime(crime);
//                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
//                startActivity(intent);
                mCallback.onClickCrimeItem(crime);
            }
        });
        if(crimeCount>0){
            mEmptyCrimeTextView.setVisibility(View.INVISIBLE);
            mAddFirstCrimeButton.setVisibility(View.INVISIBLE);
        }

        mRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(savedInstanceState!=null) {
            mTitleVisible = savedInstanceState.getBoolean(SHOW_HIDE_MENU_STAT);
        }
        setHasOptionsMenu(true);
        updateUI();
        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crime_list_option_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.optionmenu_show_hide_title);
        if(mTitleVisible)
            menuItem.setTitle(R.string.option_menu_hide_title);
        else
            menuItem.setTitle(R.string.option_menu_show_title);
    }

    private void updateTitle() {
        CrimeLab crimeLab = CrimeLab.getCrimeLab(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
     //   String title = getString(R.string.crime_tilt_string_format, crimeCount);
        String title = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);
        if(!mTitleVisible) title = null;
        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        appCompatActivity.getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.optionmenu_add_new_crime:
                Crime crime = new Crime();
                CrimeLab.getCrimeLab(getActivity()).addCrime(crime);
//                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
//                startActivity(intent);
                mCallback.onClickCrimeItem(crime);
                return true;
            case R.id.optionmenu_show_hide_title:
                mTitleVisible = !mTitleVisible;
                getActivity().invalidateOptionsMenu();
                updateTitle();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.getCrimeLab(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if(crimes.size()>0){
            mEmptyCrimeTextView.setVisibility(View.INVISIBLE);
            mAddFirstCrimeButton.setVisibility(View.INVISIBLE);
        }

            if(mAdapter==null) {
                mAdapter = new CrimeAdapter(crimes);
                mRecyclerView.setAdapter(mAdapter);
            }else {
                mAdapter.updateCrime(crimes);
                mAdapter.notifyDataSetChanged();
                //mAdapter.notifyItemChanged(mCurrentPos);
             }

        updateTitle();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==REQUEST_CRIME){

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SHOW_HIDE_MENU_STAT, mTitleVisible);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;
        private Crime mCrime;



        public CrimeHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_crime_title_textview);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_crime_date_textview);
            mSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_crime_solved_checkbox);
            itemView.setOnClickListener(this);
        }
        public void BindCrime(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            CharSequence date = DateFormat.format("EEE, MMM-dd-yyyy, hh:mm aaa", mCrime.getDate());
            mDateTextView.setText(date.toString());
            mSolvedCheckBox.setChecked(mCrime.isResolved());
        }

        @Override
        public void onClick(View v) {
  //          Toast.makeText(getActivity(), mCrime.getTitle()+ " Clicked!", Toast.LENGTH_SHORT).show();
            mCurrentPos = mRecyclerView.getLayoutManager().getPosition(v);
//            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
//            startActivity(intent);
            mCallback.onClickCrimeItem(mCrime);

        }

    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
        private List<Crime> mCrimes;
        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(view);
        }



        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            holder.BindCrime(mCrimes.get(position));
        }

        public void updateCrime(List<Crime> crimes){
            mCrimes = crimes;
        }
    }
}
