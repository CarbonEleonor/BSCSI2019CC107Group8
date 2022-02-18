package com.omnicron.avax.views;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
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
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignupAdditionalInfo extends AppCompatActivity {
    DatePickerDialog dateDialog;
    UserCredential user;
    TextInputLayout bdateTil;
    TextInputLayout mobilePhoneTil;
    TextInputLayout address1Til;
    TextInputLayout address2Til;
    TextInputLayout cityTil;
    AutoCompleteTextView genderSpinner;

    AwesomeValidation bStyleValidation;
    AwesomeValidation tilStyleValidation;

    Button addUserBtn;

    SessionLibrary session;

    ProgressDialog loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_additional_info);

        session = new SessionLibrary(this);
        user = UserCredential.initExtra(this);
        initViews();
        initListeners();
        initValidation();
    }



    private void initViews(){
        loading = new ProgressDialog(SignupAdditionalInfo.this);
        bdateTil = findViewById(R.id.bdate_til);
        Objects.requireNonNull(bdateTil.getEditText()).setInputType(InputType.TYPE_NULL);
        genderSpinner = findViewById(R.id.gender_act);
        addUserBtn = findViewById(R.id.toCreateAddInfo);
        mobilePhoneTil = findViewById(R.id.contact_til);
        address1Til = findViewById(R.id.address1_til);
        address2Til = findViewById(R.id.address2_til);
        cityTil = findViewById(R.id.city_til);
        Objects.requireNonNull(mobilePhoneTil.getEditText()).setText("09");
        initGenderSpinner();
    }
    private void initGenderSpinner(){
        ArrayAdapter<CharSequence> genderArrAdapter = ArrayAdapter.createFromResource(
                SignupAdditionalInfo.this, R.array.genders_arr,
                android.R.layout.simple_dropdown_item_1line);
        genderArrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderArrAdapter);
        genderSpinner.setThreshold(1);
    }
    private void initListeners() {
        Objects.requireNonNull(bdateTil.getEditText()).setOnClickListener(v -> {
            initDatePicker();
            dateDialog.show();
        });
        addUserBtn.setOnClickListener(v-> {
            String gender = genderSpinner.getText().toString();
            String bdate = Objects.requireNonNull(bdateTil.getEditText()).getText().toString();
            String phoneNumber = Objects.requireNonNull(mobilePhoneTil.getEditText()).getText().toString();
            String address1 = Objects.requireNonNull(address1Til.getEditText()).getText().toString();
            String address2 = Objects.requireNonNull(address2Til.getEditText()).getText().toString();
            String city = Objects.requireNonNull(cityTil.getEditText()).getText().toString();

            user.setGender(gender);
            user.setBirthdate(bdate);
            user.setPhoneNumber(phoneNumber);
            user.setAddress1(address1);
            user.setAddress2(address2);
            user.setCity(city);

            if(bStyleValidation.validate() && tilStyleValidation.validate()) {
                Toast.makeText(this, "Working here", Toast.LENGTH_SHORT).show();
                try {
                    updateBasicInformation();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        genderSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                user.setGender(text);
            }
        });
        Objects.requireNonNull(mobilePhoneTil.getEditText()).setOnFocusChangeListener(
                new View.OnFocusChangeListener(){

                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(hasFocus && mobilePhoneTil.getEditText().getText().toString().length() < 3){
                            Objects.requireNonNull(mobilePhoneTil.getEditText()).setText("09");
                        }
                    }
                });
    }

    private void initValidation(){

        bStyleValidation = new AwesomeValidation(ValidationStyle.BASIC);
        tilStyleValidation = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);

        String genderRegex = "^Male$|^Female$";
        String bdateRegex = "^\\d{4}\\/\\d{2}\\/\\d{2}";
        String mobilePhoneRegex = "^09\\d{9}$";
        String address1Regex = "^[a-zA-Z0-9\\.\\,\\-\\#\\s]{2,50}$";
        String address2Regex = "^[a-zA-Z0-9\\.\\,\\-\\#\\s]{0,50}$";
        String cityRegex = "^[a-zA-Z\\s]{2,}$";

        bStyleValidation.addValidation(this, R.id.bdate_til, bdateRegex, R.string.err_bdate);
        bStyleValidation.addValidation  (this, R.id.gender_act, genderRegex, R.string.err_gender);

        tilStyleValidation.addValidation(this, R.id.contact_til, mobilePhoneRegex, R.string.err_mobilePhone);
        tilStyleValidation.addValidation(this, R.id.address1_til, address1Regex, R.string.err_address1);
        tilStyleValidation.addValidation(this, R.id.address2_til, address2Regex, R.string.err_address1);
        tilStyleValidation.addValidation(this, R.id.city_til, cityRegex, R.string.err_city);

    }
    private void initDatePicker(){
        final Calendar calendar = Calendar.getInstance();
        int day, month, year;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        dateDialog = new DatePickerDialog(SignupAdditionalInfo.this,
                (view, thisYear, monthOfYear, dayOfMonth) -> {
                    String m = "", d = ""; // month and day
                    if(monthOfYear<10) m += "0"+(monthOfYear+1);
                    else m += monthOfYear;
                    if(dayOfMonth<10) d += "0"+dayOfMonth;
                    else d += dayOfMonth;

                    StringBuilder date = new StringBuilder()
                                        .append(thisYear)
                                        .append("/")
                                        .append(m)
                                        .append("/")
                                        .append(d);
                    Objects.requireNonNull(bdateTil.getEditText()).setText(date.toString());
                }, year, month, day);
    }

    private void updateBasicInformation() throws JSONException {
        loading.show();
        loading.setContentView(R.layout.loading_layout);
        loading.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        loading.setCancelable(false);

        if(SessionLibrary.isTokenExpired(user.getAccessKey()))
            session.authenticateSession(user, null);
        // create token
        String url = BuildConfig.ROOT_URL+"/user/information/"+user.getUid();


        JSONObject payload = new JSONObject();

            payload.put("uid", user.getUid());
            payload.put("institution_id", user.getInstitutionID());
            payload.put("email", user.getEmail());
            payload.put("givenName", user.getGivenName());
            payload.put("middleName", user.getMiddleName());
            payload.put("familyName", user.getFamilyName());
            payload.put("suffix", user.getSuffix());
            payload.put("gender", user.getGender());
            payload.put("birthdate", user.getBirthdate());
            payload.put("phoneNumber", user.getPhoneNumber());
            payload.put("addressLine1", user.getAddress1());
            payload.put("addressLine2", user.getAddress2());
            payload.put("city", user.getCity());

        JsonObjectRequest updateInfo = new JsonObjectRequest(Request.Method.PUT, url, payload,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        Toast.makeText(SignupAdditionalInfo.this, "Welcome to Avax!", Toast.LENGTH_SHORT).show();
                        SignupAdditionalInfo.this.redirectTo(Home.class, true, user, Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        ErrorMessage err = new ErrorMessage(error.networkResponse);
                        if (err.getCode() >= 400) {
                            Toast.makeText(SignupAdditionalInfo.this, err.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("authorization", user.getAccessKey());
                        return headers;
                    }
                };
        int TIMEOUT_MS = 5000;
        updateInfo.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        VolleyController.getInstance(getApplicationContext()).addToRequestQueue(updateInfo);
    }

    private void redirectTo(Class<?> page, boolean closeAfterRedirect, @Nullable UserCredential user, int flag){
        Intent intent = new Intent(this, page);
        if(user != null)
            intent.putExtra("User", user);

        intent.addFlags(flag);
        startActivity(intent);

        if(closeAfterRedirect)
            finish();
    }
}