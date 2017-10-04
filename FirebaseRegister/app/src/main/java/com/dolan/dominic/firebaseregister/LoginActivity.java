package com.dolan.dominic.firebaseregister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, OnCompleteListener {

    FirebaseAuth firebaseAuth;

    EditText emailTxt;
    EditText passwordTxt;
    Button loginButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        firebaseAuth = FirebaseAuth.getInstance();

        emailTxt = (EditText) findViewById(R.id.emailBoxLogin);
        passwordTxt = (EditText) findViewById(R.id.passwordLogin);
        loginButton = (Button) findViewById(R.id.login);

        loginButton.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View v) {
        if (v == loginButton && validateTextFields()){
            String email = emailTxt.getText().toString();
            String password = passwordTxt.getText().toString();

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, this);

        }
    }

    @Override
    public void onComplete(@NonNull Task task) {
        progressDialog.dismiss();

        if (task.isSuccessful()) {
            Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, FavouriteActivity.class));
        } else {
            Toast.makeText(this, "Log In Error", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateTextFields(){
        String email = emailTxt.getText().toString();
        String password = passwordTxt.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return false;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
