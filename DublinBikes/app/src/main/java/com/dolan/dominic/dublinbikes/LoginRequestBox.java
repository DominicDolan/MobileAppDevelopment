package com.dolan.dominic.dublinbikes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.dolan.dominic.dublinbikes.activities.LoginActivity;
import com.dolan.dominic.dublinbikes.activities.main.MainActivity;

/**
 * Created by domin on 24 Sep 2017.
 */

public class LoginRequestBox extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class to create the appropriate alert dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.login_box)
               .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       //Go to the Login screen when the user taps login
                       startActivity(new Intent(getActivity(), LoginActivity.class));
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

    public void show() {
        super.show(getFragmentManager(), "Login");
    }
}
