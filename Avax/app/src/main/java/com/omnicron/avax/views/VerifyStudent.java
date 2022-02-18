package com.omnicron.avax.views;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.omnicron.avax.BuildConfig;
import com.omnicron.avax.R;
import com.omnicron.avax.helpers.ErrorMessage;
import com.omnicron.avax.helpers.SessionLibrary;
import com.omnicron.avax.helpers.UserCredential;
import com.omnicron.avax.helpers.VolleyController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import io.sentry.Sentry;

public class VerifyStudent extends AppCompatActivity {
    CountDownTimer cdt;
    UserCredential user;
    AwesomeValidation awesomeValidation;
    Button continueBtn, sendCodeBtn;
    EditText v_code;
    SessionLibrary session;
    ProgressDialog loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_student);

        session = new SessionLibrary(this);
        user = UserCredential.initExtra(this);

        initViews();
        initListener();
        initValidation();
    }

    private void initViews(){
        continueBtn = findViewById(R.id.toAdditionalInfo);
        sendCodeBtn = findViewById(R.id.send_verification_btn);
        v_code = findViewById(R.id.v_code_editText);
        loading = new ProgressDialog(VerifyStudent.this);

    }
    private void initListener(){
        sendCodeBtn.setOnClickListener(v->{
            loading.show();
            requestVerification();
        });
        continueBtn.setOnClickListener(v->toAdditionalInfo());
    }
    private void initValidation() {
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        String codeRegex = "^[\\d]{6}$";
        awesomeValidation.addValidation(this, R.id.v_code_editText, codeRegex, R.string.err_vcode_invalid);
    }
    private void toAdditionalInfo(){
        String vCode = v_code.getText().toString();

        if(awesomeValidation.validate()){
//            redirectTo(SignupAdditionalInfo.class, true, user);
            submitVerification();
        }
    }
    private void redirectTo(Class<?> page, boolean closeAfterRedirect, @Nullable UserCredential user){
        Intent intent = new Intent(this, page);
        if(user != null)
            intent.putExtra("User", user);

        startActivity(intent);

        if(closeAfterRedirect)
            finish();
    }

    private void submitVerification(){
        try {
            loading.show();
            loading.setContentView(R.layout.loading_layout);
            loading.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );
        }catch(Exception e){
            Sentry.captureMessage(e.getMessage());
        }


        String code = v_code.getText().toString();
        String url = BuildConfig.ROOT_URL+"/verify/validateCode";

        // create jwt token for code
        String token = SessionLibrary.createCodeJWT(code, user.getEmail());
        JSONObject payload = new JSONObject();
        try {
            payload.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest submitCodeReq = new JsonObjectRequest(Request.Method.POST, url, payload,
                response -> {
                    loading.dismiss();
                    session.createAccount(user, loading);
                },
                error -> {
                    loading.dismiss();
                    ErrorMessage err = new ErrorMessage(error.networkResponse);
                    if(err.getCode() >= 400){
                        Toast.makeText(getApplicationContext(), err.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        int TIMEOUT_MS = 5000;
        submitCodeReq.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        VolleyController.getInstance(getApplicationContext()).addToRequestQueue(submitCodeReq);
    }
    private void requestVerification(){
        // create token
        String token = SessionLibrary.createVerificationJWT(user);
        String url = BuildConfig.ROOT_URL+"/verify";
        RequestQueue requestMyCode = Volley.newRequestQueue(this);

        JSONObject payload = new JSONObject();
        try {
            payload.put("token", token);
        }catch (JSONException e){
            e.printStackTrace();
        }
        loading.show();
        loading.setContentView(R.layout.loading_layout);
        loading.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        loading.setCancelable(false);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload,
                response -> {
                    loading.dismiss();
                    String message = null;
                    try {
                        message = response.getString("message");
                    } catch (JSONException e) {
                        Sentry.captureMessage(Objects.requireNonNull(e.getMessage()));
                    }
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    try {
                        initTimer();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },
                error -> {
                    loading.dismiss();
                    error.printStackTrace();
                    ErrorMessage err = new ErrorMessage(error.networkResponse);
                    if(err.getCode() == 403){
                        Toast.makeText(this, R.string.err_prompt_accountNotAllowed, Toast.LENGTH_LONG).show();
                    }
                    if (err.getCode() == 400){
                        Toast.makeText(this, err.getMessage(), Toast.LENGTH_SHORT).show();
                    }
        });
        int TIMEOUT_MS = 5000;
        request.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        VolleyController.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
    private void initTimer(){
        sendCodeBtn.setClickable(false);
        long startTime = 60*5*1000;
        cdt = new CountDownTimer(startTime,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int) (millisUntilFinished / 1000);
                sendCodeBtn.setText(String.valueOf(secondsLeft));
            }
            @Override
            public void onFinish() {
                sendCodeBtn.setText(R.string.label_send_code);
                sendCodeBtn.setClickable(true);
            }
        }.start();
    }
}