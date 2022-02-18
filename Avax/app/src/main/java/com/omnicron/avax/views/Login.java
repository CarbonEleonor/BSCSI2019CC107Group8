package com.omnicron.avax.views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.omnicron.avax.BuildConfig;
import com.omnicron.avax.helpers.ErrorMessage;
import com.omnicron.avax.R;
import com.omnicron.avax.helpers.SessionLibrary;
import com.omnicron.avax.helpers.UserCredential;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    SessionLibrary session;
    final UserCredential user = new UserCredential();
    Button loginBtn;
    EditText input_id, input_passw;
    ProgressDialog loading;
    TextView toSignup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new SessionLibrary(Login.this);
        session.authenticateSession(user, MainActivity.class);

        loginBtn = findViewById(R.id.loginBtn);
        input_id = findViewById(R.id.inst_id_editText);
        input_passw = findViewById(R.id.passw_editText);
        toSignup = findViewById(R.id.toSignup);

        loading = new ProgressDialog(Login.this);

        loginBtn.setOnClickListener(v -> loginButtonClick());
        toSignup.setOnClickListener(V -> redirectTo(Signup.class));
    }

    private void loginButtonClick(){


        String userIID = input_id.getText().toString();
        String userPassw = input_passw.getText().toString();
        // set credentials need for login
        user.setInstitutionID(userIID);
        user.setPassword(userPassw);

        if(userIID.isEmpty() || userPassw.isEmpty()){
            Toast.makeText(this, "Empty fields!", Toast.LENGTH_SHORT).show();
        }
        else {
            sessionLogin(user, loading);
        }
    }

    private void sessionLogin(UserCredential user, ProgressDialog loading){

        loading.show();
        loading.setContentView(R.layout.loading_layout);
        loading.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        loading.setCancelable(false);
        final RequestQueue requestQueue;
//        int cacheCap = 1024*1024; // 1 MB
//        Cache cache = new DiskBasedCache(getCacheDir(), cacheCap);
//        Network network = new BasicNetwork(new HurlStack());

        JSONObject credential = new JSONObject();
        try {
            credential.put("institution_id", user.getInstitutionID());
            credential.put("password", user.getPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        requestQueue = Volley.newRequestQueue(Login.this);

        final String rootURL = BuildConfig.ROOT_URL;
        final String url = rootURL+"/auth/userLogin";

        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, url, credential,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String refreshToken = "";
                        String accessToken = "";
                        try {
                            loading.dismiss();
                            refreshToken = response.getString("refreshToken");
                            accessToken = response.getString("accessToken");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        user.setAccessKey(accessToken);
                        user.setRefreshKey(refreshToken);
                        session.saveSession(user);
                        Intent toHome = new Intent(Login.this, MainActivity.class);
                        toHome.putExtra("User", user);
                        startActivity(toHome);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        if(error instanceof ServerError){
                            ErrorMessage errMessage = new ErrorMessage(error.networkResponse);
                            int code = errMessage.getCode();

                            if(code == 400){
                                Toast.makeText(Login.this, errMessage.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            else{
                                StringBuilder message = new StringBuilder();
                                message.append("Error: ").append(code).append(" "+ errMessage.getMessage());
                                Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();
                            }
                        }
                        else if(error instanceof TimeoutError || error instanceof NoConnectionError){
                            Toast.makeText(Login.this, "Please check your connection!", Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof NetworkError) {
                            Toast.makeText(Login.this, "Please check your connection!", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(Login.this, "Please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
        requestQueue.add(loginRequest);
    }
    private void redirectTo(Class page){
        Intent intent = new Intent(Login.this, page);
        startActivity(intent);
        finish();
    }
}