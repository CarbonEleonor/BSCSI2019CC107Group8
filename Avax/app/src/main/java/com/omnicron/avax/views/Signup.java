package com.omnicron.avax.views;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.omnicron.avax.R;
import com.omnicron.avax.helpers.UserCredential;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class Signup extends AppCompatActivity {

    TextView toLogin;
    TextInputLayout iid_til, email_til, password_til;
    Button toBasicInfo;
    AwesomeValidation awesomeValidation = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initViews();
        initListeners();
        initValidation();
    }

    private void toBasicInfoClick(){
        UserCredential user = new UserCredential();
        String userIID = Objects.requireNonNull(iid_til.getEditText()).getText().toString();
        String userEmail = Objects.requireNonNull(email_til.getEditText()).getText().toString();
        String userPassw = Objects.requireNonNull(password_til.getEditText()).getText().toString();

        // set credentials need for login
        user.setInstitutionID(userIID);
        user.setEmail(userEmail);
        user.setPassword(userPassw);

        if(awesomeValidation.validate()){
            redirectTo(SignupBasicInfo.class, false, user);
        }
    }

    private void initListeners(){
        toLogin.setOnClickListener(v->redirectTo(Login.class, false, null));
        toBasicInfo.setOnClickListener(v->toBasicInfoClick());
    }
    private void initViews(){
        iid_til = findViewById(R.id.inst_id_til);
        email_til = findViewById(R.id.email_til);
        password_til = findViewById(R.id.passw_til);

        toBasicInfo = findViewById(R.id.toBasicInfo);
        toLogin = findViewById(R.id.toLogin);
    }
    private void initValidation(){
        String iidRegex = "^(\\d{2}-\\d{5})$";
        String emailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_"+
                "`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-"+
                "\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z"+
                "0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|"+
                "[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9]"+
                "[0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\"+
                "x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        String emailRegexForProviders = ".*@(gmail|google|yahoo|protonmail|outlook|icloud)\\.\\w{2,3}";
        String passRegex = "^\\w{12,}$";

        // iid validation
        awesomeValidation.addValidation(this, R.id.inst_id_til, iidRegex, R.string.err_iid);

        // email validation
        awesomeValidation.addValidation(this,  R.id.email_til, emailRegex, R.string.err_email);
        awesomeValidation.addValidation(this, R.id.email_til, emailRegexForProviders, R.string.err_emailProviders);

        // password validation
        awesomeValidation.addValidation(this, R.id.passw_til, passRegex, R.string.err_passw);

    }
    private void redirectTo(Class<?> page, boolean closeAfterRedirect, @Nullable UserCredential user){
        Intent intent = new Intent(this, page);
        if(user != null)
            intent.putExtra("User", user);
        startActivity(intent);
        if(closeAfterRedirect)
            finish();
    }
}
