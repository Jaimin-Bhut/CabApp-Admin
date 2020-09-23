package com.jb.dev.cabapp_admin.activites;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jb.dev.cabapp_admin.R;
import com.jb.dev.cabapp_admin.helper.Constants;
import com.jb.dev.cabapp_admin.helper.Helper;

public class UpdateCabActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UpdateCabActivity";
    private EditText editTextCabName, editTextCabNumber, editTextPerCapacity, editTextLaugageCapacity;
    private MaterialButton buttonUpdate, buttonCancle;
    private Helper helper;
    SharedPreferences sharedPreferences;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String mId, mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_cab);

        Intent intent = getIntent();
        mId = intent.getStringExtra("id");
        mPath = intent.getStringExtra("path");
        init();
        initListener();
        initObject();
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void init() {
        editTextCabName = findViewById(R.id.update_cab_et_cabname);
        editTextCabNumber = findViewById(R.id.update_cab_et_cabnumber);
        editTextCabNumber.setEnabled(false);
        editTextPerCapacity = findViewById(R.id.update_cab_et_per_capacity);
        editTextLaugageCapacity = findViewById(R.id.update_cab_et_laugage_capacity);
        buttonCancle = findViewById(R.id.update_cab_btn_Cancel);
        buttonUpdate = findViewById(R.id.update_cab_btn_update);
        isDefaultData();
    }

    private void initListener() {
        buttonUpdate.setOnClickListener(this);
        buttonCancle.setOnClickListener(this);
    }

    private void initObject() {
        helper = new Helper(this);
    }

    private void updateData() {
        String mCabName = editTextCabName.getText().toString().toUpperCase();
        String mCabNumber = editTextCabNumber.getText().toString().toUpperCase();
        String mPerCapacity = editTextPerCapacity.getText().toString();
        String mLaugageCapacity = editTextLaugageCapacity.getText().toString();
        try {
            if (!Helper.isValidTextDigit(mCabName)) {
                editTextCabName.setError(getString(R.string.enter_valid_details));
                editTextCabName.setFocusable(true);
            } else if (!Helper.validNumberPlate(mCabNumber)) {
                editTextCabNumber.setError(getString(R.string.enter_valid_details));
                editTextCabNumber.setFocusable(true);
            } else if (!Helper.isValidPerCapacity(mPerCapacity)) {
                editTextPerCapacity.setError(getString(R.string.enter_valid_details));
                editTextPerCapacity.setFocusable(true);
            } else if (!Helper.isValidLaugage(mLaugageCapacity)) {
                editTextLaugageCapacity.setError(getString(R.string.enter_valid_details));
                editTextLaugageCapacity.setFocusable(true);
            } else {
                DocumentReference documentReference = db.collection("Cab_Data").document(mId);
                documentReference.update("CabName", mCabName);
                documentReference.update("CabNumber", mCabNumber);
                documentReference.update("CabPerCapacity", mPerCapacity);
                documentReference.update("CabLaugageCapacity", mLaugageCapacity)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UpdateCabActivity.this, getString(R.string.txt_success_update), Toast.LENGTH_LONG).show();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                setResult(Constants.CAB_REFRESH_RESULT_CODE);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UpdateCabActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

            }
        } catch (Exception e) {
            Log.e(TAG, "updateCabBlock" + e);
        }
    }

    private void isDefaultData() {
        sharedPreferences = getSharedPreferences("is_set", MODE_PRIVATE);
        String cabName = sharedPreferences.getString("cabname", "");
        String cabNumber = sharedPreferences.getString("cabnumber", "");
        String cabPerCapacity = sharedPreferences.getString("cabpercapacity", "");
        String cabLauCapacity = sharedPreferences.getString("cablagcapacity", "");
        String cabDriver = sharedPreferences.getString("cabdriver", "");
        String cabStatus = sharedPreferences.getString("cabstatus", "");
        editTextCabName.setText(cabName);
        editTextCabNumber.setText(cabNumber);
        editTextPerCapacity.setText(cabPerCapacity);
        editTextLaugageCapacity.setText(cabLauCapacity);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_cab_btn_update:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(buttonUpdate.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                updateData();
                break;
            case R.id.update_cab_btn_Cancel:
                this.finish();
                break;
        }
    }
}
