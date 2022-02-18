package com.omnicron.avax.helpers;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

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
import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import com.omnicron.avax.BuildConfig;
import com.omnicron.avax.R;
import com.omnicron.avax.views.SignupAdditionalInfo;
import com.omnicron.avax.views.Startup;

import org.json.JSONException;
import org.json.JSONObject;


import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class SessionLibrary{
    private final SharedPreferences.Editor editor;
    private final Context ctx;
    private final Activity mActivity;
    private ErrorMessage errorMessage;

    public SessionLibrary(Context ctx){
        this.ctx = ctx;
        this.mActivity = (Activity) ctx;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        editor = pref.edit();
    }

    public void createAccount(UserCredential user, ProgressDialog loading){
        loading.show();
        loading.setContentView(R.layout.loading_layout);
        loading.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        loading.setCancelable(false);

        JSONObject credential = new JSONObject();
        try {
            credential.put("institution_id", user.getInstitutionID());
            credential.put("email", user.getEmail());
            credential.put("password", user.getPassword());
            credential.put("givenName", user.getGivenName());
            credential.put("middleName", user.getMiddleName());
            credential.put("familyName", user.getFamilyName());
            credential.put("suffix", user.getSuffix());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String rootURL = BuildConfig.ROOT_URL;
        final String url = rootURL +"/auth/signup";

        JsonObjectRequest signupRequest = new JsonObjectRequest(Request.Method.POST, url, credential,
                response -> {
                    String refreshToken = "";
                    String accessToken = "";
                    String uid = "";
                    try {
                        loading.dismiss();
                        refreshToken = response.getString("refreshToken");
                        accessToken = response.getString("accessToken");
                        uid = response.getString("uid");
                        user.setAccessKey(accessToken);
                        user.setRefreshKey(refreshToken);
                        user.setUid(uid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    saveSession(user);
                    Intent toBasicInfo = new Intent(ctx, SignupAdditionalInfo.class);
                    toBasicInfo.putExtra("User", user);
                    ctx.startActivity(toBasicInfo);
                    mActivity.finish();
                },
                error -> {
                    loading.dismiss();
                    if(error instanceof ServerError){
                        ErrorMessage errMessage = new ErrorMessage(error.networkResponse);
                        int code = errMessage.getCode();

                        if(code == 400){
                            Toast.makeText(ctx.getApplicationContext(), errMessage.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        else{
                            StringBuilder message = new StringBuilder();
                            message.append("Error: ").append(code).append(" "+ errMessage.getMessage());
                            Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
                        }
                    }
                    else if(error instanceof TimeoutError || error instanceof NoConnectionError){
                        Toast.makeText(mActivity, "Please check your connection!", Toast.LENGTH_LONG).show();
                    }
                    else if (error instanceof NetworkError) {
                        Toast.makeText(mActivity, "Please check your connection!", Toast.LENGTH_LONG).show();
                    }
                    else if (error instanceof ParseError) {
                        Toast.makeText(mActivity, "Please try again", Toast.LENGTH_LONG).show();
                    }
                }
        );
        int TIMEOUT_MS = 5000;
        signupRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        VolleyController.getInstance(ctx.getApplicationContext()).addToRequestQueue(signupRequest);
    }
    public void authenticateSession(UserCredential user, @Nullable Class<?> redirectTo){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        String refreshKey = pref.getString("refresh_key", "");

        String[] safePages = { "Login", "Startup", "Signup", "SignupAdditionalInfo", "SignupBasicInfo",
        "VerifyStudent"};
        String contextName = this.ctx.getClass().getSimpleName();

        if(!refreshKey.isEmpty()){
            JWT refreshJWT = null;
            try{
                refreshJWT = new JWT(refreshKey);
            }
            catch(JwtException e ){
                e.printStackTrace();
            }
            String receivedAccessToken = getAccessToken(refreshKey);

            if(errorMessage!=null && errorMessage.getCode() >= 400){
                // if not in safe space
                if(!Arrays.asList(safePages).contains(contextName))
                    redirectStartup();
            }else{
                editor.putString("access_key", receivedAccessToken);
                editor.apply();
                user.setAccessKey(receivedAccessToken);

                assert refreshJWT != null;
                user.setInstitutionID(parseJWT(refreshJWT, "institution_id"));
                user.setEmail(parseJWT(refreshJWT, "email"));
                user.setUid(parseJWT(refreshJWT, "uid"));

                if(redirectTo != null) {
                    if(!Arrays.asList(safePages).contains(contextName)){
                        redirectStartup();
                    }else{
                        Intent to = new Intent(this.ctx, redirectTo);
                        to.putExtra("User", user);
                        Activity activity = (Activity) this.ctx;
                        this.ctx.startActivity(to);
                        activity.finish();
                    }
                }
            }
        }
    }

    public void getAccessKey(UserCredential user) {
        final String url = BuildConfig.ROOT_URL + "/auth/createSession";

        JSONObject body = new JSONObject();
        try{
            body.put("refreshToken", user.getRefreshKey());
        }catch(JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest getAccessQueue = new JsonObjectRequest(Request.Method.POST, url, body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String accessToken = "";
                        try {
                            accessToken = response.getString("accessToken");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        user.setAccessKey(accessToken);
                        saveSession(user);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error instanceof ServerError){
                            final ErrorMessage err = new ErrorMessage(error.networkResponse);
                            if(err.getCode() >= 400){
                                Toast.makeText(ctx, err.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if(error instanceof TimeoutError || error instanceof NoConnectionError){
                            Toast.makeText(mActivity, "Please check your connection!", Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof NetworkError) {
                            Toast.makeText(mActivity, "Please check your connection!", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(mActivity, "Please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        int TIMEOUT_MS = 5000;
        getAccessQueue.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        VolleyController.getInstance(ctx.getApplicationContext()).addToRequestQueue(getAccessQueue);
    }
    public void clearSession(){
        editor.clear().apply();
        this.ctx.startActivity(new Intent(this.ctx, Startup.class));
        mActivity.finish();
    }
    public void requestSignout(UserCredential user, ProgressDialog loading){
        loading.show();
        loading.setContentView(R.layout.loading_layout);
        loading.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        loading.setCancelable(false);


        String url = BuildConfig.ROOT_URL + "/auth/sessionLogout";
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.ctx);
        String refreshKey = pref.getString("refresh_key","");

        JSONObject body = new JSONObject();
        try {
            body.put("refreshToken", refreshKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest signoutRequest = new JsonObjectRequest(Request.Method.POST, url, body,
                response -> {
                    redirectStartup();
                    user.reset();
                    this.clearSession();
                },
                error -> {
                    loading.dismiss();

                    if(error instanceof ServerError){
                        errorMessage = new ErrorMessage(error.networkResponse);
                        Toast.makeText(mActivity, errorMessage.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    else if(error instanceof TimeoutError || error instanceof NoConnectionError){
                        Toast.makeText(mActivity, "Please check your connection!", Toast.LENGTH_LONG).show();
                    }
                    else if (error instanceof NetworkError) {
                        Toast.makeText(mActivity, "Please check your connection!", Toast.LENGTH_LONG).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(mActivity, "Please try again", Toast.LENGTH_LONG).show();
                    }
                    redirectStartup();
                    user.reset();
                    this.clearSession();
                }

        );
        int TIMEOUT_MS = 5000;
        signoutRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleyController.getInstance(ctx.getApplicationContext()).addToRequestQueue(signoutRequest);
    }
    public void saveSession(UserCredential user){
        String returnedAccessKey = user.getAccessKey();
        String returnedRefreshKey = user.getRefreshKey();

        editor.putString("refresh_key", returnedRefreshKey);
        editor.putString("access_key", returnedAccessKey);

        editor.apply();
    }

    private String getAccessToken(String refreshKey) {
        final String[] receivedToken = {""};

        final String url = BuildConfig.ROOT_URL + "/auth/createSession";

        JsonObjectRequest getAccessQueue = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String accessToken = "";
                        try {
                            accessToken = response.getString("accessToken");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        receivedToken[0] = accessToken;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error instanceof ServerError){
                            errorMessage = new ErrorMessage(error.networkResponse);
                        }
                        else if(error instanceof TimeoutError || error instanceof NoConnectionError){
                            Toast.makeText(mActivity, "Please check your connection!", Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof NetworkError) {
                            Toast.makeText(mActivity, "Please check your connection!", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(mActivity, "Please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                }){
            @Override
            public Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<String, String>();
                params.put("refreshToken", refreshKey);
                return params;
            }
        };
        int TIMEOUT_MS = 5000;
        getAccessQueue.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        VolleyController.getInstance(ctx.getApplicationContext()).addToRequestQueue(getAccessQueue);
        return receivedToken[0];
    }
    private String parseJWT(JWT jwt, String key){

        Claim subsMetaData = jwt.getClaim(key);
        String parsedValue = subsMetaData.asString();

        return parsedValue;
    }
    public static String parseJWT(String token, String key){
        if(token.isEmpty()) return "";

        JWT jwt = new JWT(token);
        Claim subsMetaData = jwt.getClaim(key);
        String parsedValue = subsMetaData.asString();

        return parsedValue;
    }
    public static boolean isTokenExpired(String jwt){
        if(jwt.isEmpty()) return true;

        JWT token = new JWT(jwt);
        return token.isExpired(0);
    }

    public void redirectStartup(){
        Intent toStartup = new Intent(this.ctx, Startup.class);
        clearSession();
        toStartup.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.ctx.startActivity(toStartup);
        mActivity.finish();
    }

    public static String createVerificationJWT(UserCredential user) {
        try{
            SecretKey key = Keys.hmacShaKeyFor(BuildConfig.VERIFICATION_TOKEN_KEY
                    .getBytes(StandardCharsets.UTF_8));
            long now = new Date().getTime();
            long expiresAt = now + 60*60*5*1000; // 5 minutes

            String token = Jwts.builder()
                    .claim("institution_id", user.getInstitutionID())
                    .claim("givenName" , user.getGivenName().toLowerCase())
                    .claim("middleName", user.getMiddleName().toLowerCase())
                    .claim("familyName", user.getFamilyName().toLowerCase())
                    .claim("email", user.getEmail().toLowerCase())
                    .claim("suffix", user.getSuffix().toLowerCase())
                    .setExpiration(new Date(expiresAt))
                    .signWith(key)
                    .compact();
            Log.d("token", token);
            return token;
        }catch(JWTCreationException e){
            e.printStackTrace();
        }
        return null;
    }
    public static String createCodeJWT(String code, String email) {
        try{
            SecretKey key = Keys.hmacShaKeyFor(BuildConfig.VERIFICATION_TOKEN_KEY
                    .getBytes(StandardCharsets.UTF_8));
            long now = new Date().getTime();
            long expiresAt = now + 60*60*5*1000; // 5 minutes

            String token = Jwts.builder()
                    .claim("code", code)
                    .claim("email", email)
                    .setExpiration(new Date(expiresAt))
                    .signWith(key)
                    .compact();
            Log.d("token", token);
            return token;
        }catch(JWTCreationException e){
            e.printStackTrace();
        }
        return null;
    }
    public void refresh(UserCredential user){
        if (user.getAccessKey().isEmpty()|| SessionLibrary.isTokenExpired(user.getAccessKey()))
            this.getAccessKey(user);
    }
}
