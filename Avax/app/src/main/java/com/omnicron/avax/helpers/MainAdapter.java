package com.omnicron.avax.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.omnicron.avax.R;
import com.omnicron.avax.views.About;
import com.omnicron.avax.views.Home;
import com.omnicron.avax.views.Profile;

import java.util.ArrayList;


public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    Activity activity;
    ArrayList<String> arrayList;
    UserCredential user = new UserCredential();
    public MainAdapter(Activity activity, ArrayList<String> arrayList,@Nullable UserCredential user){
        this.activity = activity;
        this.arrayList = arrayList;
        if(user==null)
            this.user = user;
    }
    public MainAdapter(Activity activity, ArrayList<String> arrayList){
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_drawer_main,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textView.setText(arrayList.get(position));

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = activity;
                SessionLibrary session = new SessionLibrary(activity);
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);
                user.setAccessKey(pref.getString("access_key", ""));
                user.setRefreshKey(pref.getString("refresh_key", ""));
                int position = holder.getAdapterPosition();
                switch (position){
                        case 0:
                            activity.startActivity(new Intent(activity, Profile.class)
                                    .putExtra("User", user)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            break;
                        case 1:
                            activity.startActivity(new Intent(activity, About.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("User", user));
                            break;
                        case 2:
//                          LOGOUT
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Logout");
                            builder.setMessage("Are you sure you want to sign out?");
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    session.requestSignout(user, new ProgressDialog(ctx));
                                }
                            });
                            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                            break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {

        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_view);
        }
    }
}

