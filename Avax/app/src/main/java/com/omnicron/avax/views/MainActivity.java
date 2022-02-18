package com.omnicron.avax.views;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.Touch;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.omnicron.avax.BuildConfig;
import com.omnicron.avax.R;
import com.omnicron.avax.helpers.ErrorMessage;
import com.omnicron.avax.helpers.MainAdapter;
import com.omnicron.avax.helpers.SessionLibrary;
import com.omnicron.avax.helpers.Statistic;
import com.omnicron.avax.helpers.UserCredential;
import com.omnicron.avax.helpers.VolleyController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageView btMenu;
    RecyclerView recyclerView;
    static ArrayList<String> arrayList = new ArrayList<>();

    Statistic stats;
    MainAdapter adapter;
    BottomNavigationView bottomNavigationView;
    PieChart pieChart;
    Button button_to_survey, button_to_vax;
    SwipeRefreshLayout homeSwiper;
    UserCredential user;
    SessionLibrary session;
    ImageView indicator;
    TextView indicatorText;
    ProgressDialog loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new SessionLibrary(this);
        user = UserCredential.initExtra(this);
        stats = new Statistic();
        loading = new ProgressDialog(MainActivity.this);

        if (user.getAccessKey().isEmpty() || SessionLibrary.isTokenExpired(user.getAccessKey())) {
            session.getAccessKey(user);
        }
        fetchProfile(MainActivity.this);

        fetchVaccineStats(MainActivity.this);

        initViews();
        initListeners();
    }
    private void initListeners(){

        button_to_survey.setOnClickListener(v ->
                startActivity(new Intent(this, Survey.class ).putExtra("User", user)));
        button_to_vax.setOnClickListener(v ->
                startActivity(new Intent(this, SetVaccination.class ).putExtra("User", user)));

        btMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.Home:
                    return true;
                case R.id.News:
                    startActivity(new Intent(getApplicationContext(),
                            News.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.Vaccination:
                    startActivity(new Intent(getApplicationContext(),
                            vaccination.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.Infographic:
                    startActivity(new Intent(getApplicationContext(),
                            Infographic.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });

        homeSwiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchProfile(MainActivity.this);
                homeSwiper.setRefreshing(false);
            }
        });
    }
    private void initChart(){
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        PieEntry vaccinatedStat = new PieEntry(stats.getVaccinated(), "Vaccinated");
        PieEntry unvaccinatedStat = new PieEntry(stats.getUnvaccinated(), "Unvaccinaated");
        PieEntry boosteredStat = new PieEntry(stats.getBoostered(), "Boostered");

        pieEntries.add(vaccinatedStat);
        pieEntries.add(unvaccinatedStat);
        pieEntries.add(boosteredStat);

        PieDataSet pieDataSet = new PieDataSet(pieEntries,"");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setDrawValues(false);
        pieChart.setData(new PieData(pieDataSet));
        pieChart.animateXY(2000, 2000);
        pieChart.getDescription().setEnabled(false);
    }
    private void initViews(){

        pieChart = findViewById(R.id.pie_chart);
        homeSwiper = findViewById(R.id.home_swipe_refresh);
        drawerLayout = findViewById(R.id.drawer_layout);
        btMenu = findViewById(R.id.bt_menu);
        recyclerView = findViewById(R.id.recycler_view);
        button_to_survey = findViewById(R.id.button_to_survey);
        button_to_vax = findViewById(R.id.button_to_vax);

        arrayList.clear();

        arrayList.add("Profile");
        arrayList.add("About");
        arrayList.add("Logout");

        adapter = new MainAdapter(this, arrayList, user);
        recyclerView.setLayoutManager(new LinearLayoutManager (this));
        recyclerView.setAdapter(adapter);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.Home);

        indicator = findViewById(R.id.shield_indicator);
        indicatorText = findViewById(R.id.indicatorText);
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    public void initIndicator() {
        String status = user.getHealthStatus()!=null?user.getHealthStatus():"";
        indicatorText.setText(status.toUpperCase());
        switch(status){
            case "healthy":
                indicator.setImageDrawable(getResources().getDrawable(R.drawable.shield,
                        getApplicationContext().getTheme()));
                break;
            case "risk":
                indicator.setImageDrawable(getResources().getDrawable(R.drawable.shield_red,
                        getApplicationContext().getTheme()));
                break;
            default:
                indicator.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pending_actions_24,
                        getApplicationContext().getTheme()));
        }
    }
    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();

        closeDrawer(drawerLayout);
    }



    public void fetchProfile(Context ctx){
        loading.show();
        loading.setContentView(R.layout.loading_layout);
        loading.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        loading.setCancelable(false);
        SessionLibrary session = new SessionLibrary(ctx);
        if (SessionLibrary.isTokenExpired(user.getAccessKey()))
            session.getAccessKey(user);

        String url = BuildConfig.ROOT_URL+"/user/"+user.getUid();
        JsonObjectRequest fetch = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
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
                        initIndicator();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        if(error instanceof ServerError){
                            final ErrorMessage err = new ErrorMessage(error.networkResponse);
                            if(err.getCode() >= 400){
                                Toast.makeText(ctx, err.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if(error instanceof TimeoutError || error instanceof NoConnectionError){
                            Toast.makeText(ctx, "Please check your connection!", Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof NetworkError) {
                            Toast.makeText(ctx, "Please check your connection!", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(ctx, "Please try again", Toast.LENGTH_LONG).show();
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
                5,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        VolleyController.getInstance(ctx.getApplicationContext()).addToRequestQueue(fetch);
    }

    public void fetchVaccineStats(Context ctx){
        String url = BuildConfig.ROOT_URL+"/statistics/vaccine/";
        JsonObjectRequest fetch = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray result = null;
                        JSONObject data = null;
                        int vaccinated = 0, unvaccinated = 0, boostered = 0;
                        try {
                            result = response.getJSONArray("result");
                            Log.d("result", result.toString());
                            data = result.getJSONObject(0);

                            vaccinated = data.getInt("totalVaccinated");
                            unvaccinated = data.getInt("totalUnnvaccinated");
                            boostered = data.getInt("totalBoostered");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        stats.setVaccinated(vaccinated);
                        stats.setUnvaccinated(unvaccinated);
                        stats.setBoostered(boostered);

                        initChart();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error instanceof ServerError){
                            final ErrorMessage err = new ErrorMessage(error.networkResponse);
                            if(err.getCode() > 400){
                                Toast.makeText(ctx, err.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if(error instanceof TimeoutError || error instanceof NoConnectionError){
                            Toast.makeText(ctx, "Please check your connection!", Toast.LENGTH_LONG).show();
                        }
                        else if (error instanceof NetworkError) {
                            Toast.makeText(ctx, "Please check your connection!", Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(ctx, "Please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        int TIMEOUT_MS = 5000;
        fetch.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        VolleyController.getInstance(ctx.getApplicationContext()).addToRequestQueue(fetch);
    }

}