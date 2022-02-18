package com.omnicron.avax.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.omnicron.avax.R;
import com.omnicron.avax.helpers.MainAdapter;

public class News extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageView btMenu;
    RecyclerView recyclerView;
    MainAdapter adapter;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        initViews();
        initListeners();
    }

    private void initViews(){
        drawerLayout = findViewById(R.id.drawer_layout);
        btMenu = findViewById(R.id.bt_menu);
        recyclerView = findViewById(R.id.recycler_view);

        adapter = new MainAdapter(this, MainActivity.arrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.News);
    }
    private void initListeners(){
        btMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        bottomNavigationView.setOnItemSelectedListener (
                item -> {
                    switch (item.getItemId()){
                        case R.id.Home:
                            startActivity(new Intent(getApplicationContext()
                                    ,MainActivity.class));
                            overridePendingTransition(0,0);
                            return true;
                        case R.id.News:
                            return true;
                        case R.id.Vaccination:
                            startActivity(new Intent(getApplicationContext()
                                    ,vaccination.class));
                            overridePendingTransition(0,0);
                            return true;
                        case R.id.Infographic:
                            startActivity(new Intent(getApplicationContext()
                                    ,Infographic.class));
                            overridePendingTransition(0,0);
                            return true;
                    }
                    return false;
                });

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
}