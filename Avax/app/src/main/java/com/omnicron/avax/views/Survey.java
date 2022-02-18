package com.omnicron.avax.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.omnicron.avax.BuildConfig;
import com.omnicron.avax.R;
import com.omnicron.avax.helpers.ErrorMessage;
import com.omnicron.avax.helpers.UserCredential;
import com.omnicron.avax.helpers.VolleyController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Survey extends AppCompatActivity {

    RadioGroup question1RG;
    RadioGroup question2RG;
    RadioGroup question3RG;
    Button submitSurvey;
    UserCredential user;
    ProgressDialog loading;
    ImageView back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        user = UserCredential.initExtra(this);

        initViews();
        initListeners();
    }
    public void initViews(){
        loading = new ProgressDialog(Survey.this);
        question1RG = findViewById(R.id.q1_rg);
        question2RG = findViewById(R.id.q2_rg);
        question3RG = findViewById(R.id.q3_rg);

        submitSurvey = findViewById(R.id.button_submit);
        back_btn = findViewById(R.id.back_btn);
    }
    public void initListeners(){
        submitSurvey.setOnClickListener(v-> {
            try {
                if(user.getHealthStatus()==null) submitAnswers(loading);
                else updateAnswers(loading);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        back_btn.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
    }
    private void submitAnswers(ProgressDialog loading) throws JSONException{
        String answer1 = new AnswerDefiner(Survey.this, question1RG.getCheckedRadioButtonId())
                .getAnswer();
        String answer2 = new AnswerDefiner(Survey.this, question2RG.getCheckedRadioButtonId())
                .getAnswer();
        String answer3 = new AnswerDefiner(Survey.this, question3RG.getCheckedRadioButtonId())
                .getAnswer();

        JSONObject payload = new JSONObject();
        JSONObject report = new JSONObject();
        report.put("question1", answer1);
        report.put("question2", answer2);
        report.put("question3", answer3);
        payload.put("report", report);

        loading.show();
        loading.setContentView(R.layout.loading_layout);
        loading.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        loading.setCancelable(false);

        String url = BuildConfig.ROOT_URL+"/user/health/"+user.getUid();
        JsonObjectRequest sendSurvey = new JsonObjectRequest(Request.Method.POST, url, payload,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        Toast.makeText(Survey.this, "Submitted", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        if(error instanceof ServerError){
                            final ErrorMessage err = new ErrorMessage(error.networkResponse);
                            if(err.getCode() >= 400){
                                Toast.makeText(Survey.this, err.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if(error instanceof TimeoutError || error instanceof NoConnectionError){
                            Toast.makeText(Survey.this, "Please check your connection!", Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof NetworkError) {
                            Toast.makeText(Survey.this, "Please check your connection!", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(Survey.this, "Please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("authorization", user.getAccessKey());
                        return headers;
                    }
                };
        int TIMEOUT_MS = 5000;
        sendSurvey.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        VolleyController.getInstance(getApplicationContext()).addToRequestQueue(sendSurvey);
    }
    private void updateAnswers(ProgressDialog loading) throws JSONException{
        String answer1 = new AnswerDefiner(Survey.this, question1RG.getCheckedRadioButtonId())
                .getAnswer();
        String answer2 = new AnswerDefiner(Survey.this, question2RG.getCheckedRadioButtonId())
                .getAnswer();
        String answer3 = new AnswerDefiner(Survey.this, question3RG.getCheckedRadioButtonId())
                .getAnswer();

        JSONObject payload = new JSONObject();
        JSONObject report = new JSONObject();
        report.put("question1", answer1);
        report.put("question2", answer2);
        report.put("question3", answer3);
        payload.put("report", report);

        loading.show();
        loading.setContentView(R.layout.loading_layout);
        loading.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        loading.setCancelable(false);

        String url = BuildConfig.ROOT_URL+"/user/health/"+user.getUid();
        JsonObjectRequest sendSurvey = new JsonObjectRequest(Request.Method.PUT, url, payload,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        Toast.makeText(Survey.this, "Submitted", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        if(error instanceof ServerError){
                            final ErrorMessage err = new ErrorMessage(error.networkResponse);
                            if(err.getCode() >= 400){
                                Toast.makeText(Survey.this, err.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if(error instanceof TimeoutError || error instanceof NoConnectionError){
                            Toast.makeText(Survey.this, "Please check your connection!", Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof NetworkError) {
                            Toast.makeText(Survey.this, "Please check your connection!", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(Survey.this, "Please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", user.getAccessKey());
                return headers;
            }
        };
        int TIMEOUT_MS = 5000;
        sendSurvey.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        VolleyController.getInstance(getApplicationContext()).addToRequestQueue(sendSurvey);
    }
}

class AnswerDefiner{
    private String answerString;
    private int answerId;
    private Activity activity;
    private static AnswerDefiner instance;

    public AnswerDefiner(Activity activity, int id){
        this.answerId = id;
        this.activity = activity;
    }

    public static synchronized AnswerDefiner getInstance(Activity activity, int answerId){
        if(instance == null){
            instance = new AnswerDefiner(activity, answerId);
        }
        return instance;
    }
    private Activity getActivity(){
        return this.activity;
    }
    private int getAnswerId(){
        return this.answerId;
    }
    public String getAnswer(){
        RadioButton choice = this.getActivity().findViewById(this.getAnswerId());
        String question = choice.getText().toString();
        return String.format("%d-%s",getPoint(), question);
    }

    private int getPoint(){
        int points = 0;
        switch(answerId){
            case R.id.q1_c1:
            case R.id.q3_c1:
            case R.id.q2_c1:
                points=0;
                break;
            case R.id.q1_c2:
            case R.id.q2_c2:
                points=2;
                break;
            case R.id.q3_c2:
                points=1;
            case R.id.q3_c3:
                points=2;
            break;
        }
        return points;
    }
}