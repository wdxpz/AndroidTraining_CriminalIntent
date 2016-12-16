package com.sw.tain.criminalintent.DBUtil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import java.io.File;

/**
 * Created by home on 2016/11/14.
 */

public class ImageUtil {
    public static Bitmap getScaledBitmap(String imageFile, float dstWidth, float detHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int inSampleSize = 1;
        if(srcWidth>dstWidth || srcHeight>detHeight){
            if(srcWidth>srcHeight){
                inSampleSize = Math.round(srcHeight/detHeight);
            }else{
                inSampleSize = Math.round(srcWidth/detHeight);
            }
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        return BitmapFactory.decodeFile(imageFile, options);
    }

    public static Bitmap getScaledBitmap(String imageFile, Activity activity){
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return getScaledBitmap(imageFile, size.x, size.y);

    }
}
