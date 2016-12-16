package com.sw.tain.criminalintent;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.StaticLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.sw.tain.criminalintent.DBUtil.ImageUtil;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by home on 2016/11/14.
 */

public class CrimePhotoViewDialog extends DialogFragment {
    public static final String EXTRA_CRIME_ID = "crime id";

    private ImageView mPhotoView;
    private Crime mCrime;
    private File mPhotoFile;

    public static CrimePhotoViewDialog newInstance(UUID uuid){
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_CRIME_ID, uuid);

        CrimePhotoViewDialog fragment = new CrimePhotoViewDialog();
        fragment.setArguments(bundle);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        UUID uuid = (UUID)args.getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.getCrimeLab(getActivity()).getCrime(uuid);
        mPhotoFile = CrimeLab.getCrimeLab(getActivity()).getPictureFile(mCrime);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_view_photo, null);
        mPhotoView = (ImageView) view.findViewById(R.id.crime_photo_imageView);
        updatePhotoView();

        return new AlertDialog.Builder(getActivity()).
                setView(view).
                setTitle("Crime Photo").
                setPositiveButton(android.R.string.ok, null).
                create();
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
}
