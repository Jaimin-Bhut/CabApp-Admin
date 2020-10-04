package com.jb.dev.cabapp_admin.activites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.jb.dev.cabapp_admin.R;
import com.jb.dev.cabapp_admin.helper.Constants;
import com.jb.dev.cabapp_admin.helper.Helper;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private String email, password;
    TextInputEditText edtEmail, edtPassword;
    MaterialButton btnLogin;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {
        view = findViewById(android.R.id.content);
        progressBar = findViewById(R.id.progress_circular);
        edtEmail = findViewById(R.id.email);
        edtPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);
        edtEmail.setText("jaimin@admin.com");
        edtPassword.setText("12345678");
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                        isLogin();
                    }
                }, 1500);
            }
        });
        if (!Helper.isNetworkConnected(this)) {
            Snackbar snackbar = Snackbar.make(view, getString(R.string.please_turn_on_internet), Snackbar.LENGTH_INDEFINITE)
                    .setAction("TURN ON", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
                            startActivity(intent);
                        }
                    });
            snackbar.setActionTextColor(Color.WHITE);
            snackbar.show();
        }
    }

    private void isLogin() {
        email = edtEmail.getText().toString();
        password = edtPassword.getText().toString();
        if (email.equals("jaimin@admin.com") && password.equals("12345678")) {
            storeEmailtoSharedPref(true);
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        } else {
            Snackbar.make(view, getString(R.string.enter_valid_details), Snackbar.LENGTH_INDEFINITE).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    public void storeEmailtoSharedPref(boolean sessionLogin) {
        SharedPreferences sp;
        sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Constants.IS_ADMIN_EMAIL, sessionLogin);
        editor.apply();
    }

}