package com.omnicron.avax.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
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
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.material.textfield.TextInputLayout;
import com.omnicron.avax.BuildConfig;
import com.omnicron.avax.R;
import com.omnicron.avax.helpers.ErrorMessage;
import com.omnicron.avax.helpers.SessionLibrary;
import com.omnicron.avax.helpers.UserCredential;
import com.omnicron.avax.helpers.VolleyController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class Profile extends AppCompatActivity {
    ImageView back;
    AwesomeValidation awesomeValidation;
    UserCredential user;
    TextInputLayout institutionIdTIL, emailTIL,
            givenNameTIL, middleNameTIL, familyNameTIL,
            suffixTIL, genderTIL, birthdateTIL, phoneNumberTIL, address1TIL,
            address2TIL, cityTIL;
    TextView fullNameHolder, vaccineStatusHolder;
    Button updateBtn;
    SessionLibrary session;
    ProgressDialog loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        loading = new ProgressDialog(this);
        session = new SessionLibrary(this);
        user = UserCredential.initExtra(Profile.this);
        fetchProfile(user);

        initViews();
        initListener();
        initValidation();
    }

    private void initViews(){

        institutionIdTIL = findViewById(R.id.inst_id_til);
        emailTIL = findViewById(R.id.email_til);
        givenNameTIL = findViewById(R.id.gname_til);
        middleNameTIL = findViewById(R.id.mname_til);
        familyNameTIL = findViewById(R.id.fname_til);
        suffixTIL = findViewById(R.id.suff_til);
        genderTIL = findViewById(R.id.gender_til);
        birthdateTIL = findViewById(R.id.bdate_til);
        phoneNumberTIL = findViewById(R.id.contact_til);
        address1TIL = findViewById(R.id.address1_til);
        address2TIL = findViewById(R.id.address2_til);
        cityTIL = findViewById(R.id.city_til);
        fullNameHolder = findViewById(R.id.full_name_holder);
        vaccineStatusHolder = findViewById(R.id.vax_status_holder);

        Objects.requireNonNull(institutionIdTIL.getEditText()).setInputType(InputType.TYPE_NULL);
        Objects.requireNonNull(emailTIL.getEditText()).setInputType(InputType.TYPE_NULL);
        Objects.requireNonNull(givenNameTIL.getEditText()).setInputType(InputType.TYPE_NULL);
        Objects.requireNonNull(middleNameTIL.getEditText()).setInputType(InputType.TYPE_NULL);
        Objects.requireNonNull(familyNameTIL.getEditText()).setInputType(InputType.TYPE_NULL);
        Objects.requireNonNull(suffixTIL.getEditText()).setInputType(InputType.TYPE_NULL);

        updateBtn = findViewById(R.id.updateProfile);
        back = findViewById(R.id.back_btn);
    }
    private void initListener(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        updateBtn.setOnClickListener(v-> {
            if(awesomeValidation.validate()){
                try {
                    updateRequest();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    private void initValidation(){
        awesomeValidation = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        String nameRegex = "^[\\w]{2,25}$";
        Pattern suffixRegex = Pattern.compile("^jr\\.$|^sr\\.$|^([ixv]{0,4})$", Pattern.CASE_INSENSITIVE);
        String genderRegex = "^Male$|^Female$";
        String bdateRegex = "^\\d{4}\\/\\d{2}\\/\\d{2}";
        String mobilePhoneRegex = "^09\\d{9}$";
        String address1Regex = "^[a-zA-Z0-9\\.\\,\\-\\#\\s]{2,50}$";
        String address2Regex = "^[a-zA-Z0-9\\.\\,\\-\\#\\s]{0,50}$";
        String cityRegex = "^[a-zA-Z\\s]{2,}$";

        awesomeValidation.addValidation(this, R.id.gname_til, nameRegex, R.string.err_name);
        awesomeValidation.addValidation(this, R.id.mname_til, nameRegex, R.string.err_name);
        awesomeValidation.addValidation(this, R.id.fname_til, nameRegex, R.string.err_name);
        awesomeValidation.addValidation(this, R.id.suff_til, suffixRegex, R.string.err_suffix);

        awesomeValidation.addValidation(this, R.id.bdate_til, bdateRegex, R.string.err_bdate);
        awesomeValidation.addValidation(this, R.id.gender_act, genderRegex, R.string.err_gender);
        awesomeValidation.addValidation(this, R.id.contact_til, mobilePhoneRegex, R.string.err_mobilePhone);
        awesomeValidation.addValidation(this, R.id.address1_til, address1Regex, R.string.err_address1);
        awesomeValidation.addValidation(this, R.id.address2_til, address2Regex, R.string.err_address1);
        awesomeValidation.addValidation(this, R.id.city_til, cityRegex, R.string.err_city);

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void populateFields(){
        String inst_id = user.getInstitutionID();
        String email = user.getEmail();
        String givenName = user.getGivenName();
        String middleName = user.getMiddleName();
        String familyName = user.getFamilyName();
        String suffix = user.getSuffix();
        String gender = user.getGender();
        String birthdate = user.getBirthdate();
        String phoneNumber = user.getPhoneNumber();
        String address1 = user.getAddress1();
        String address2 = user.getAddress2();
        String city = user.getCity();
        String vaccinationStatus = user.getVaccintionStatus()!=null? user.getVaccintionStatus() : "Not set";
        String healthStatus = user.getHealthStatus() != null ? user.getHealthStatus() : "Not set";

        String fullname = String.format("%s %s %s %s", givenName, middleName, familyName, suffix).trim();

        // populate
        Objects.requireNonNull(institutionIdTIL.getEditText()).setText(inst_id);
        Objects.requireNonNull(emailTIL.getEditText()).setText(email);
        Objects.requireNonNull(givenNameTIL.getEditText()).setText(givenName);
        Objects.requireNonNull(middleNameTIL.getEditText()).setText(middleName);
        Objects.requireNonNull(familyNameTIL.getEditText()).setText(familyName);
        Objects.requireNonNull(suffixTIL.getEditText()).setText(suffix);
        Objects.requireNonNull(genderTIL.getEditText()).setText(gender);
        Objects.requireNonNull(birthdateTIL.getEditText()).setText(birthdate);
        Objects.requireNonNull(phoneNumberTIL.getEditText()).setText(phoneNumber);
        Objects.requireNonNull(address1TIL.getEditText()).setText(address1);
        Objects.requireNonNull(address2TIL.getEditText()).setText(address2);
        Objects.requireNonNull(cityTIL.getEditText()).setText(city);
        vaccineStatusHolder.setText(vaccinationStatus);
        fullNameHolder.setText(fullname);
    }
    public void fetchProfile(UserCredential user){
        loading.show();
        loading.setContentView(R.layout.loading_layout);
        loading.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        loading.setCancelable(false);
        if (SessionLibrary.isTokenExpired(user.getAccessKey()))
            session.getAccessKey(user);

        String url = BuildConfig.ROOT_URL+"/user/"+user.getUid();
        JsonObjectRequest fetch = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        JSONArray data = null;
                        JSONObject result = null;
                        try {
                            data = response.getJSONArray("result");
                            result = data.getJSONObject(0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String uid = "" ;
                        String institutionId = "";
                        String email = "";
                        String givenName = "";
                        String middleName = "";
                        String familyName = "";
                        String suffix = "";
                        String gender = "";
                        String birthdate = "";
                        String phoneNumber = "";
                        String address1 = "";
                        String address2 = "";
                        String city = "";
                        String vaccinationStatus = "";
                        String healthStatus = "";


                        try {
                            uid = result.getString("uid");
                            institutionId = result.getString("institution_id");
                            email = result.getString("email");
                            givenName = result.getString("givenName");
                            middleName = result.getString("middleName");
                            familyName = result.getString("familyName");
                            suffix = result.getString("suffix");
                            gender = result.getString("gender");
                            birthdate = result.getString("birthdate");
                            phoneNumber = result.getString("phoneNumber");
                            address1 = result.getString("addressLine1");
                            address2 = result.getString("addressLine2");
                            city = result.getString("city");
                            vaccinationStatus = result.getString("vaccinationStatus");
                            healthStatus = result.getString("healthStatus");
                        }
                        catch(JSONException e){
                            e.printStackTrace();
                        }
                        user.setUid(uid);
                        user.setInstitutionID(institutionId);
                        user.setEmail(email);
                        user.setGivenName(givenName);
                        user.setMiddleName(middleName);
                        user.setFamilyName(familyName);
                        user.setSuffix(suffix);
                        user.setGender(gender);
                        user.setBirthdate(birthdate);
                        user.setPhoneNumber(phoneNumber);
                        user.setAddress1(address1);
                        user.setAddress2(address2);
                        user.setCity(city);
                        user.setVaccintionStatus(vaccinationStatus);
                        user.setHealthStatus(healthStatus);
                        populateFields();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        if(error instanceof ServerError){
                            final ErrorMessage err = new ErrorMessage(error.networkResponse);
                            if(err.getCode() >= 400){
                                Toast.makeText(Profile.this, err.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if(error instanceof TimeoutError || error instanceof NoConnectionError){
                            Toast.makeText(Profile.this, "Please check your connection!", Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof NetworkError) {
                            Toast.makeText(Profile.this, "Please check your connection!", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(Profile.this, "Please try again", Toast.LENGTH_LONG).show();
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
        fetch.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        VolleyController.getInstance(getApplicationContext()).addToRequestQueue(fetch);
    }
    public void updateRequest() throws JSONException{
        loading.show();
        loading.setContentView(R.layout.loading_layout);
        loading.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        loading.setCancelable(false);
        if (SessionLibrary.isTokenExpired(user.getAccessKey()))
            session.getAccessKey(user);
        String gender = Objects.requireNonNull(genderTIL.getEditText()).getText().toString();
        String birthdate = Objects.requireNonNull(birthdateTIL.getEditText()).getText().toString();
        String phoneNumber = Objects.requireNonNull(phoneNumberTIL.getEditText()).getText().toString();
        String address1 = Objects.requireNonNull(address1TIL.getEditText()).getText().toString();
        String address2 = Objects.requireNonNull(address2TIL.getEditText()).getText().toString();
        String city = Objects.requireNonNull(cityTIL.getEditText()).getText().toString();

        JSONObject payload = new JSONObject();

        payload.put("institution_id", user.getInstitutionID());
        payload.put("email", user.getEmail());
        payload.put("givenName", user.getGivenName());
        payload.put("middleName", user.getMiddleName());
        payload.put("familyName", user.getFamilyName());
        payload.put("suffix", user.getSuffix());
        payload.put("gender", gender);
        payload.put("phoneNumber", phoneNumber);
        payload.put("birthdate", birthdate);
        payload.put("addressLine1", address1);
        payload.put("addressLine2", address2);
        payload.put("city", city);

        String url = BuildConfig.ROOT_URL+"/user/information/"+user.getUid();
        JsonObjectRequest updateReq = new JsonObjectRequest(Request.Method.PUT, url, payload,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        user.setGender(gender);
                        user.setBirthdate(birthdate);
                        user.setPhoneNumber(phoneNumber);
                        user.setAddress1(address1);
                        user.setAddress2(address2);
                        user.setCity(city);

                        populateFields();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        if(error instanceof ServerError){
                            final ErrorMessage err = new ErrorMessage(error.networkResponse);
                            if(err.getCode() >= 400){
                                Toast.makeText(getApplicationContext(), err.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if(error instanceof TimeoutError || error instanceof NoConnectionError){
                            Toast.makeText(getApplicationContext(), "Please check your connection!", Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(), "Please check your connection!", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
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
        updateReq.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        VolleyController.getInstance(getApplicationContext()).addToRequestQueue(updateReq);
    }
}