package com.omnicron.avax.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.omnicron.avax.R;
import com.omnicron.avax.helpers.SessionLibrary;
import com.omnicron.avax.helpers.UserCredential;

public class Startup extends AppCompatActivity {
    SessionLibrary session;
    final UserCredential user = new UserCredential();
    Button loginBtn, signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        initViews();
        initListeners();

    }



    private void initListeners(){
        loginBtn.setOnClickListener(v->onLoginClick());
        signupBtn.setOnClickListener(v->onSignUpClick());
    }
    private void initViews(){
        session = new SessionLibrary(Startup.this);
        session.authenticateSession(user, MainActivity.class);
        loginBtn = findViewById(R.id.goto_login);
        signupBtn = findViewById(R.id.goto_signup);
    }
    private void onLoginClick(){
        redirectTo(Login.class);
    }
    private void onSignUpClick(){
        redirectTo(Signup.class);
    }

    private void redirectTo(Class<?> page){
        Intent intent = new Intent(Startup.this, page);
        startActivity(intent);
        finish();
    }
}