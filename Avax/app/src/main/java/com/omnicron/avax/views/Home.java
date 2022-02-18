package com.omnicron.avax.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.omnicron.avax.prompts.BasicInfoPrompt;
import com.omnicron.avax.R;
import com.omnicron.avax.helpers.SessionLibrary;
import com.omnicron.avax.helpers.UserCredential;

public class Home extends AppCompatActivity {

    UserCredential user;
    TextView retr;
    Button logoutBtn;
    SwipeRefreshLayout refresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        refresh = findViewById(R.id.refresh_home);
        final SessionLibrary session = new SessionLibrary(this);
        user = UserCredential.initExtra(Home.this);

        BasicInfoPrompt prompt = new BasicInfoPrompt(Home.this);
        prompt.show();

        retr =  findViewById(R.id.retriever);
        logoutBtn = findViewById(R.id.logoutBtn);

        retr.setText(user.getInstitutionID());

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               if (user.getAccessKey().isEmpty()||SessionLibrary.isTokenExpired(user.getAccessKey()))
                    session.getAccessKey(user);

               refresh.setRefreshing(false);
            }
        });
        logoutBtn.setOnClickListener(v->{
            session.requestSignout(user, new ProgressDialog(Home.this));
        });
    }
}