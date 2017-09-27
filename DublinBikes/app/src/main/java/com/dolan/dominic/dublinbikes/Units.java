package com.dolan.dominic.dublinbikes;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by domin on 1 Sep 2017.
 */

public class Units {

    public static float dp;
    public static float cm, cmY;
    public static int height, width;
    public static float inchesToCm = 2.54f;
    public static float cmToInches = 1/inchesToCm;
    public static float physicalWidth;
    public static float physicalHeight;

    public static void init(WindowManager manager){
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);

        dp = (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 1, dm );

        height = dm.heightPixels;
        width = dm.widthPixels;

        physicalWidth = (width/dm.xdpi)*inchesToCm;
        physicalHeight = (height/dm.ydpi)*inchesToCm;

        cm = physicalWidth/((float)width);
        cmY = physicalHeight/((float) height);
    }
}
