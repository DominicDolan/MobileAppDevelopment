package com.dolan.dominic.dublinbikes.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dolan.dominic.dublinbikes.R;
import com.dolan.dominic.dublinbikes.activities.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, OnCompleteListener<AuthResult> {

    AutoCompleteTextView emailInput;
    EditText passwordInput;
    Button signInButton, registerButton;
    TextView skip;

    FirebaseAuth firebaseAuth;

    boolean registeringUser = false;
    boolean signingInUser = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null){
            startApp();
        }

        emailInput = (AutoCompleteTextView) findViewById(R.id.email);
        passwordInput = (EditText) findViewById(R.id.password);

        signInButton = (Button) findViewById(R.id.email_sign_in_button);
        registerButton = (Button) findViewById(R.id.email_register_button);
        skip = (TextView) findViewById(R.id.skip);

        signInButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        skip.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
    }

    private void startApp(){
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onClick(View v) {

        String email = emailInput.getText().toString().trim();
        String password  = passwordInput.getText().toString().trim();

        if (v == signInButton){
            validateInput(email, password);
            signingInUser = true;
            progressDialog.setMessage("Signing In Please Wait...");
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, this);
        } else if(v == registerButton){
            validateInput(email, password);
            registeringUser = true;
            progressDialog.setMessage("Registering Please Wait...");
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, this);
        } else if (v == skip){
            startApp();
        }
    }

    private void validateInput(String email, String password){

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        progressDialog.dismiss();

        if (signingInUser){
            signingInUser = false;
            if (task.isSuccessful()) {
                //display some message here
                Toast.makeText(this, "Successfully Signed In", Toast.LENGTH_LONG).show();
                startApp();
            } else {
                //display some message here
                Toast.makeText(this, "Sign In Error", Toast.LENGTH_LONG).show();
            }
        }

        if (registeringUser) {
            registeringUser = false;
            if (task.isSuccessful()) {
                //display some message here
                Toast.makeText(this, "Successfully registered", Toast.LENGTH_LONG).show();
                startApp();
            } else {
                //display some message here
                Toast.makeText(this, "Registration Error", Toast.LENGTH_LONG).show();
            }
        }
    }
}
