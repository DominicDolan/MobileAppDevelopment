package com.dolan.dominic.modernartui;

import android.graphics.Color;
import android.view.View;

/**
 * Created by domin on 21 Aug 2017.
 */

public class ColorPanel {
    private View view;
    private float startRed, startGreen, startBlue;
    private float endRed, endGreen, endBlue;

    public ColorPanel(View view, int startColor, int endColor) {
        this.view = view;

        //Separate the hex code into the corresponding red, green and blue components
        startRed = Color.red(startColor);
        startGreen = Color.green(startColor);
        startBlue = Color.blue(startColor);

        endRed = Color.red(endColor);
        endGreen = Color.green(endColor);
        endBlue = Color.blue(endColor);

    }

    //alterColor() will be called by the seek bar listener which will pass in the seek bar progress
    //as the percent. Percent describes the percentage towards the end color. 0% means the view will
    //be at its default value and 100% will be at the value defined by the end colors.
    public void alterColor(int percent){
        //Alter the background color of the view based on the value passed in as percent,
        //each of red, green and blue will be interpolated based off the start color,
        //the end color and the percent.
        int red = (int) (startRed + ((endRed - startRed)/100f)*(float)percent);
        int green = (int)(startGreen + ((endGreen - startGreen)/100f)*(float)percent);
        int blue = (int) (startBlue + ((endBlue - startBlue)/100f)*(float)percent);

        //Convert the colors back into a hex view and send the to the view.
        view.setBackgroundColor(Color.argb(255, red, green, blue));
    }

}
