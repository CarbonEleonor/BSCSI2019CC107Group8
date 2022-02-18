package com.omnicron.avax.prompts;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.omnicron.avax.R;


public class BasicInfoPrompt extends Dialog implements View.OnClickListener {
    public Activity activity;
    public Dialog dialog;
    public Button accept, reject;


    public BasicInfoPrompt(@NonNull Context context) {
        super(context);
        this.activity = (Activity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.modal_require_info);
        accept = findViewById(R.id.accept_add_info);
        reject = findViewById(R.id.reject_add_info);
        accept.setOnClickListener((View.OnClickListener) this);
        reject.setOnClickListener((View.OnClickListener) this);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.accept_add_info:
                Toast.makeText(activity, "It should be redirected to profile page.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.reject_add_info:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
