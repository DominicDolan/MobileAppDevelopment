package com.dolan.dominic.modernartui;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    private final static String TAG="modernUI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set the action bar defined in the XML view
        //to be a support Action Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Create the panels using a method defined below
        ColorPanel[] panels = createColorPanelArray();


        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        //The seek bar takes a listener class which is a nested class
        //defined below. It takes the panels array as a constructor
        seekBar.setOnSeekBarChangeListener(new SeekBarListener(panels));

    }

    public ColorPanel[] createColorPanelArray(){
        final ColorPanel[] panels = new ColorPanel[5];
        //Create each panel by passing in the appropriate views and
        //colors, which are defined in XML.
        panels[0] = createPanel(R.id.view11, R.color.endColor1);
        panels[1] = createPanel(R.id.view12, R.color.endColor2);
        panels[2] = createPanel(R.id.view21, R.color.endColor3);
        panels[3] = createPanel(R.id.view22, R.color.endColor4);
        panels[4] = createPanel(R.id.view23, R.color.endColor5);

        return panels;
    }

    public ColorPanel createPanel(int viewID, int endColorID){
        //Get the view from the viewID parameter
        View view = findViewById(viewID);

        //Use ColorDrawable so that the background color of the view can be obtained
        ColorDrawable drawable = (ColorDrawable) view.getBackground();

        //The ColorPanel constructor takes the view, the start color and the end color as parameters
        //the start color is set to the current color of the view, the end color will be the color
        //of the panel when the seek bar is at its max value.
        return new ColorPanel(view, drawable.getColor(), getResources().getColor(endColorID, getTheme()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu from XML. The menu list only contains one item: More Information
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Although there should only be one item that can be selected,
        //it is still could practice to make sure that the selected item
        //is the one that is expected
        int id = item.getItemId();

        //if the expected itam has been selected then show the dialog box
        if (id == R.id.action_info) {
            showDialogBox();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDialogBox(){
        //Use the dialog box defined by the MoreInformationBox class.
        MoreInformationBox box = new MoreInformationBox();
        box.show(getFragmentManager(), "dialog_fragment");
    }

    //The SeekBarListener class takes care of the input from the seek bar,
    //the only variable it needs to work with is the ColorPanels array.
    private class SeekBarListener implements SeekBar.OnSeekBarChangeListener{
        ColorPanel[] panels;

        SeekBarListener(ColorPanel[] panels){
            this.panels = panels;
        }

        //Check that neither the array nor the individual panels are null and then alter
        //the color of the panel based on the progress of the seek bar
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (panels != null) {
                for (ColorPanel panel : panels) {
                    if (panel != null) {
                        panel.alterColor(progress);
                    }
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
