package com.sw.tain.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by home on 2016/11/1.
 */

public class CrimeDatePickerFragment extends DialogFragment {

    DatePicker mDatePicker;

    public static final String DATE_ARGS_KEY = "date_rgs_key";
    public static final String DATE_INTENT = "date";
       @NonNull
       @Override
       public Dialog onCreateDialog(Bundle savedInstanceS) {
           View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_crime_date_picker, null);
           mDatePicker = (DatePicker)v.findViewById(R.id.crime_date_picker);

           Bundle args = getArguments();
           if(args!=null){
               Date date = (Date)args.getSerializable(DATE_ARGS_KEY);
               Calendar calendar = Calendar.getInstance();
               calendar.setTime(date);
               mDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), null);
           }

           return new AlertDialog.Builder(getActivity())
                   .setTitle(R.string.date_picker_title)
                   .setView(v)
                   .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           SendResult();
                       }

                       private void SendResult() {
                           Fragment fragment = getTargetFragment();
                           if(fragment==null) return;
                           int year = mDatePicker.getYear();
                           int month = mDatePicker.getMonth();
                           int day = mDatePicker.getDayOfMonth();
                           Date date = new GregorianCalendar(year, month, day).getTime();
                           Intent intent = new Intent();
                           intent.putExtra(DATE_INTENT, date);
                           fragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                       }
                   }).create();

       }

    public static CrimeDatePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(DATE_ARGS_KEY, date);

        CrimeDatePickerFragment fragment = new CrimeDatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
/*
    Button mButtonOK;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dialog_crime_date_picker_for_activity, null);
        mDatePicker = (DatePicker) v.findViewById(R.id.crime_date_picker2);

        Bundle args = getArguments();
        if (args != null) {
            Date date = (Date) args.getSerializable(DATE_ARGS_KEY);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            mDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), null);
        }

        mButtonOK = (Button)v.findViewById(R.id.button_date_picker_ok);
        mButtonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendResult();
                getActivity().finish();
            }

            private void SendResult() {
                int year = mDatePicker.getYear();
                int month = mDatePicker.getMonth();
                int day = mDatePicker.getDayOfMonth();
                Date date = new GregorianCalendar(year, month, day).getTime();
                Intent intent = new Intent();
                intent.putExtra(DATE_INTENT, date);
                getActivity().setResult(Activity.RESULT_OK, intent);
            }
        });

        return v;
    }
*/
}
