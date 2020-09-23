package com.jb.dev.cabapp_admin.activites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import com.jb.dev.cabapp_admin.R;
import com.jb.dev.cabapp_admin.helper.Constants;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        loader();
    }

    void loader() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAdminLogin()) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        }, 2000);
    }

    public boolean isAdminLogin() {
        SharedPreferences sp;
        sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return sp.getBoolean(Constants.IS_ADMIN_EMAIL, false);
    }

}