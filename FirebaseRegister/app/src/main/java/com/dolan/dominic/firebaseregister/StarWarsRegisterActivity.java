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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class StarWarsRegisterActivity extends AppCompatActivity implements View.OnClickListener, OnCompleteListener {

    FirebaseAuth firebaseAuth;

    EditText emailTxt;
    EditText passwordTxt;
    EditText confirmPasswordTxt;
    Button registerButton;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(this, FavouriteActivity.class));
        }
        emailTxt = (EditText) findViewById(R.id.emailBox);
        passwordTxt = (EditText) findViewById(R.id.password);
        confirmPasswordTxt = (EditText) findViewById(R.id.confirmPassword);
        registerButton = (Button) findViewById(R.id.register);

        registerButton.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View v) {
        String email = emailTxt.getText().toString();
        String password = passwordTxt.getText().toString();

        if (v == registerButton && validateTextFields()){
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, this);

        }
    }

    @Override
    public void onComplete(@NonNull Task task) {
        progressDialog.dismiss();

        if (task.isSuccessful()) {
            Toast.makeText(this, "Successfully Registered", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            Toast.makeText(this, "Registration Error", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateTextFields(){
        String email = emailTxt.getText().toString();
        String password = passwordTxt.getText().toString();
        String confirmPassword = confirmPasswordTxt.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return false;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return false;
        }
        if(TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(this,"Please enter confirm password",Toast.LENGTH_LONG).show();
            return false;
        }
        if (!password.equals(confirmPassword)){
            Toast.makeText(this,"Passwords do not match",Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}
