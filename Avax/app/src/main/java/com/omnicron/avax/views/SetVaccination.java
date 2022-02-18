package com.omnicron.avax.views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.omnicron.avax.BuildConfig;
import com.omnicron.avax.R;
import com.omnicron.avax.helpers.ErrorMessage;
import com.omnicron.avax.helpers.SessionLibrary;
import com.omnicron.avax.helpers.UserCredential;
import com.omnicron.avax.helpers.VolleyController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SetVaccination extends AppCompatActivity {
    AutoCompleteTextView vaccinationACT;
    Button submitBtn;
    ImageView backBtn;
    SessionLibrary session;
    UserCredential user;
    ProgressDialog loading;
    AwesomeValidation awesomeValidation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_vaccination);
        session = new SessionLibrary(this);
        user = UserCredential.initExtra(this);
        if (user.getAccessKey().isEmpty() || SessionLibrary.isTokenExpired(user.getAccessKey())) {
            session.getAccessKey(user);
        }

        initViews();
        initListeners();
        initVaccinationSpinner();
        initValidation();
    }

    private void initViews(){
        loading = new ProgressDialog(SetVaccination.this);
        vaccinationACT = findViewById(R.id.vax_status_act);
        submitBtn = findViewById(R.id.button_submit);
        backBtn = findViewById(R.id.back_btn);
    }
    private void initVaccinationSpinner(){
        ArrayAdapter<CharSequence> arrAdapter = ArrayAdapter.createFromResource(
                SetVaccination.this, R.array.vax_status_arr,
                android.R.layout.simple_dropdown_item_1line);
        arrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vaccinationACT.setAdapter(arrAdapter);
        vaccinationACT.setThreshold(1);
        String current = user.getVaccintionStatus()!=null? user.getVaccintionStatus(): "";
        vaccinationACT.setText(current, false);
    }
    private void initListeners(){
      submitBtn.setOnClickListener(v -> {
          try {
              if(awesomeValidation.validate())
                  if(user.getVaccintionStatus()==null) submit();
                  else update();
          } catch (JSONException e) {
              e.printStackTrace();
          }
      });
      backBtn.setOnClickListener(V->finish());
    }
    private void initValidation(){
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        String vaxREg = "^vaccinated$|^unvaccinated$|^boostered$";
        awesomeValidation.addValidation(this, R.id.vax_status_act, vaxREg, R.string.err_vax_status);
    }
    private void submit() throws JSONException {
        if (SessionLibrary.isTokenExpired(user.getAccessKey()))
            session.getAccessKey(user);
        loading.show();
        loading.setContentView(R.layout.loading_layout);
        loading.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        loading.setCancelable(false);
        JSONObject payload = new JSONObject();
        payload.put("status", vaccinationACT.getText().toString());
        String url = BuildConfig.ROOT_URL+"/user/vaccination/"+user.getUid();
        JsonObjectRequest sendStatus = new JsonObjectRequest(Request.Method.POST, url, payload,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        Toast.makeText(SetVaccination.this, "Submitted!", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        if(error instanceof ServerError){
                            final ErrorMessage err = new ErrorMessage(error.networkResponse);
                            if(err.getCode() >= 400){
                                Toast.makeText(SetVaccination.this, err.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if(error instanceof TimeoutError || error instanceof NoConnectionError){
                            Toast.makeText(SetVaccination.this, "Please check your connection!", Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof NetworkError) {
                            Toast.makeText(SetVaccination.this, "Please check your connection!", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(SetVaccination.this, "Please try again", Toast.LENGTH_LONG).show();
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
        sendStatus.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        VolleyController.getInstance(getApplicationContext()).addToRequestQueue(sendStatus);
    }
    private void update() throws JSONException {
        if (SessionLibrary.isTokenExpired(user.getAccessKey()))
            session.getAccessKey(user);
        loading.show();
        loading.setContentView(R.layout.loading_layout);
        loading.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        loading.setCancelable(false);
        JSONObject payload = new JSONObject();
        payload.put("status", vaccinationACT.getText().toString());
        String url = BuildConfig.ROOT_URL+"/user/vaccination/"+user.getUid();
        JsonObjectRequest sendStatus = new JsonObjectRequest(Request.Method.PUT, url, payload,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        Toast.makeText(SetVaccination.this, "Submitted!", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        if(error instanceof ServerError){
                            final ErrorMessage err = new ErrorMessage(error.networkResponse);
                            if(err.getCode() >= 400){
                                Toast.makeText(SetVaccination.this, err.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if(error instanceof TimeoutError || error instanceof NoConnectionError){
                            Toast.makeText(SetVaccination.this, "Please check your connection!", Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof NetworkError) {
                            Toast.makeText(SetVaccination.this, "Please check your connection!", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(SetVaccination.this, "Please try again", Toast.LENGTH_LONG).show();
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
        sendStatus.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        VolleyController.getInstance(getApplicationContext()).addToRequestQueue(sendStatus);
    }
}