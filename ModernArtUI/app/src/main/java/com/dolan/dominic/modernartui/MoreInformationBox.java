package com.dolan.dominic.modernartui;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created by domin on 22 Aug 2017.
 */

public class MoreInformationBox extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class to create the appropriate alert dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.info_box)
               .setPositiveButton("Visit MOMA", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       //Go to the MoMA website if the user selects 'visits MOMA'
                       Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.moma.org"));
                       startActivity(i);
                   }
               })
               .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        //do nothing to cancel the dialog box when the user selects 'Not Now'
                   }
               });
        // Create the AlertDialog object and return it to the Fragment Manager
        return builder.create();
    }
}
