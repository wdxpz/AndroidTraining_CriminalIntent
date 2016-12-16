package com.sw.tain.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.sw.tain.criminalintent.DBUtil.ImageUtil;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;


/**
 * Created by home on 2016/10/25.
 */

public class CrimeFragment extends Fragment {
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallButton;
    private ImageView mPhotoView;
    private ImageButton mPhoneButton;


    private static final String EXTRA_CRIME_ID = "crime_id";

    private static final String DATE_DIALOG_TAG = "date_dialog";
    private static final String VIEW_PHOTO__DIALOG_TAG = "view_photo_dialog";
    private static final int DATE_DIALOG_REQUEST = 0;
    private static final int CONTACT_REQUEST = 1;
    private static final int PHOTO_REQUEST = 2;

    private File mPhotoFile;
    private Callbacks mCallback;

    public interface Callbacks{
        public void OnCrimeItemChanged();
        public void OnCrimeItemDeleted();
    }

    public static CrimeFragment newInstance(UUID crimeid){
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_CRIME_ID, crimeid);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(bundle);

        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mCrime = new Crime();

        setHasOptionsMenu(true);

        Bundle args = getArguments();
        UUID uuid = (UUID)args.getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.getCrimeLab(getActivity()).getCrime(uuid);
        mPhotoFile = CrimeLab.getCrimeLab(getActivity()).getPictureFile(mCrime);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               mCrime.setTitle(s.toString());
                CrimeLab.getCrimeLab(getActivity()).updateCrime(mCrime);
                mCallback.OnCrimeItemChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button)v.findViewById(R.id.crime_date);
        updateDate();
        final FragmentManager fm = getFragmentManager();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //start dialog fragment

                CrimeDatePickerFragment dialog = CrimeDatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, DATE_DIALOG_REQUEST);
                dialog.show(fm, DATE_DIALOG_TAG);

//                //start activity for fragment
//                Intent intent = CrimeDatePickerActivity.newIntent(getActivity(), mCrime.getDate());
//                startActivityForResult(intent, DATE_DIALOG_REQUEST);

            }
        });

        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isResolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setResolved(isChecked);
                CrimeLab.getCrimeLab(getActivity()).updateCrime(mCrime);
                mCallback.OnCrimeItemChanged();
            }
        });

        
        mReportButton = (Button) v.findViewById(R.id.crime_report_button);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = ShareCompat.IntentBuilder.from(getActivity()).
                        setType("text/plain").
                        setText(getCrimeReport()).
                        setChooserTitle(getString(R.string.send_report)).
                        createChooserIntent();
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("text/plain");
//                intent.addCategory(Intent.CATEGORY_DEFAULT);
//                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
//                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
//                intent = Intent.createChooser(intent, getString(R.string.send_report));
                startActivity(intent);


            }
        });

        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect_button);
        final Intent contactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(contactIntent, PackageManager.MATCH_DEFAULT_ONLY)==null) mSuspectButton.setEnabled(false);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(contactIntent, CONTACT_REQUEST);
            }
        });
        if(mCrime.getSuspect()!=null) mSuspectButton.setText(mCrime.getSuspect());

        mCallButton = (Button) v.findViewById(R.id.crime_call_button);
        if(mCrime.getSuspect()==null) mCallButton.setEnabled(false);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String suspectNumber = getSuspectNumber();
                Uri uri = Uri.parse("tel: " + suspectNumber);
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
            }
        });

        mPhoneButton = (ImageButton)v.findViewById(R.id.crime_camera_button);

        final Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canPhoto = mPhotoFile!=null && photoIntent.resolveActivity(packageManager)!=null;
        mPhoneButton.setEnabled(canPhoto);


        mPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.fromFile(mPhotoFile);
                photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(photoIntent, PHOTO_REQUEST);
            }
        });

        mPhotoView = (ImageView)v.findViewById(R.id.crime_imageView);
        updatePhotoView();
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrimePhotoViewDialog dialog = CrimePhotoViewDialog.newInstance(mCrime.getId());
                dialog.show(fm, VIEW_PHOTO__DIALOG_TAG);
            }
        });
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (Callbacks)context;
    }

    private void updateDate() {
        CharSequence sDate = DateFormat.format("EEE, MMM-dd-yyyy, hh:mm aaa", mCrime.getDate());
        mDateButton.setText(sDate.toString());
    }

    private void updatePhotoView(){

        if(mPhotoFile==null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        }else{
            try {
                mPhotoView.setImageBitmap(ImageUtil.getScaledBitmap(mPhotoFile.getCanonicalPath(), getActivity()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getCrimeReport(){
        String solvedString = null;
        if(mCrime.isResolved())
            solvedString = getString(R.string.crime_report_solved);
        else
            solvedString = getString(R.string.crime_report_unsolved);

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if(suspect == null)
            suspect = getString(R.string.crime_report_no_suspect);
        else
            suspect = getString(R.string.crime_report_suspect, suspect);

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!= Activity.RESULT_OK) return;
        if(requestCode ==DATE_DIALOG_REQUEST) //for dialog fragment return;
        {
            Date date = (Date)data.getSerializableExtra(CrimeDatePickerFragment.DATE_INTENT);
            mCrime.setDate(date);
            updateDate();
            CrimeLab.getCrimeLab(getActivity()).updateCrime(mCrime);
            mCallback.OnCrimeItemChanged();
        }else if(requestCode == CONTACT_REQUEST && data != null){
            Uri uri = data.getData();
            //specify which fields you want to return values
            String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
            //perform query - the contactUri is like a "where" clause
            Cursor cursor = getActivity().getContentResolver().query(uri, queryFields, null, null, null);
            try{
                if(cursor.getCount()==0) return;
                cursor.moveToFirst();
                String suspect = cursor.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            }finally {
                cursor.close();
            }
        }else if(requestCode == PHOTO_REQUEST){
            updatePhotoView();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.getCrimeLab(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crime_page_option_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.optionmenu_delete_crime:
                AlertDialog dialog = new AlertDialog.Builder(getActivity()).setTitle("Delete Crime").
                        setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CrimeLab.getCrimeLab(getActivity()).deleteCrime(mCrime);
                                mCallback.OnCrimeItemDeleted();
//                                getActivity().finish();
                            }
                        }).
                        setNegativeButton(android.R.string.cancel, null).create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private String getSuspectNumber() {
        String result="";
        String logString="";
        if (ContextCompat.checkSelfPermission(getActivity(),android.Manifest.permission.READ_CONTACTS)
                !=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.READ_CONTACTS},
                    1);
        }
        Cursor cursor = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        try{
            if(cursor.getCount()==0) return result;
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                int id =  cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                logString = "Contact " + name + " , ";
                if(name.equals(mCrime.getSuspect())){
                    Cursor phoneCursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id,
                            null, null);
                    if(phoneCursor.getCount()>0){
                        phoneCursor.moveToFirst();
                        result = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        int type = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        logString += "(phone number: " + result + " , phone type: " + type + " )";
                        phoneCursor.moveToNext();
                        }
                        phoneCursor.close();
                    }
                cursor.moveToNext();
                }
        }finally {
            cursor.close();
        }
        Log.d("CriminalIntent", logString);
        return result;
    }
}
