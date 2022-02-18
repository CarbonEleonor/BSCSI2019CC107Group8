package com.omnicron.avax.views;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import com.omnicron.avax.R;
import com.omnicron.avax.helpers.UserCredential;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;
import java.util.regex.Pattern;

public class SignupBasicInfo extends AppCompatActivity {
    AwesomeValidation awesomeValidation;
    Button verifyBtn;
    TextInputLayout gnameTil, mnameTil, fnameTil, suffTil;
    UserCredential user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_basic_info);

        user = UserCredential.initExtra(this);

        initViews();
        initListeners();
        initValidation();
    }

    private void toVerifyBtn(){
        if(awesomeValidation.validate()){
            String givenName = Objects.requireNonNull(gnameTil.getEditText()).getText().toString();
            String middleName = Objects.requireNonNull(mnameTil.getEditText()).getText().toString();
            String familyName = Objects.requireNonNull(fnameTil.getEditText()).getText().toString();
            String suffix = Objects.requireNonNull(suffTil.getEditText()).getText().toString();

            user.setGivenName(givenName);
            user.setMiddleName(middleName);
            user.setFamilyName(familyName);
            user.setSuffix(suffix);

            redirectTo(VerifyStudent.class, false, user);
        }

    }

    private void initViews(){
        gnameTil = findViewById(R.id.gname_til);
        mnameTil = findViewById(R.id.mname_til);
        fnameTil = findViewById(R.id.fname_til);
        suffTil = findViewById(R.id.suff_til);
        verifyBtn = findViewById(R.id.toVerify);
    }
    private void initListeners(){
       verifyBtn.setOnClickListener(v->toVerifyBtn());
    }
    private void initValidation(){
        awesomeValidation = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);

        String nameRegex = "^[\\w]{2,25}$";
        Pattern suffixRegex = Pattern.compile("^jr\\.$|^sr\\.$|^([ixv]{0,4})$", Pattern.CASE_INSENSITIVE);
        awesomeValidation.addValidation(this, R.id.gname_til, nameRegex, R.string.err_name);
        awesomeValidation.addValidation(this, R.id.mname_til, nameRegex, R.string.err_name);
        awesomeValidation.addValidation(this, R.id.fname_til, nameRegex, R.string.err_name);
        awesomeValidation.addValidation(this, R.id.suff_til, suffixRegex, R.string.err_suffix);
    }
    private void redirectTo(Class page, boolean closeAfterRedirect, @Nullable UserCredential user){
        Intent intent = new Intent(this, page);
        if(user != null)
            intent.putExtra("User", user);

        startActivity(intent);

        if(closeAfterRedirect)
            finish();
    }
}