package com.sw.tain.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by home on 2016/9/6.
 */
public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mResolved;
    private String mSuspect;

    public Crime() {

        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public Crime(UUID uuid){
        mId = uuid;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }
    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String title) {
        mTitle = title;
    }

    public boolean isResolved() {
        return mResolved;
    }

    public void setResolved(boolean resolved) {
        mResolved = resolved;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public String getPictureFileName(){
        return "IMG_" + mId.toString() + ".jpg";
    }
}
