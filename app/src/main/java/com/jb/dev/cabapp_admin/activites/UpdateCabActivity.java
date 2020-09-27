package com.jb.dev.cabapp_admin.activites;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jb.dev.cabapp_admin.R;
import com.jb.dev.cabapp_admin.helper.Constants;
import com.jb.dev.cabapp_admin.helper.Helper;

import java.util.ArrayList;
import java.util.List;

public class UpdateCabActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "UpdateCabActivity";
    private EditText editTextCabName, editTextCabNumber, editTextPerCapacity, editTextLaugageCapacity;
    private MaterialButton buttonUpdate, buttonCancle;
    private Helper helper;
    SharedPreferences sharedPreferences;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String mId, mPath;
    ArrayAdapter<CharSequence> adapter;
    CollectionReference mDriverRef;
    FirebaseFirestore mFirebaseFirestore;
    AutoCompleteTextView completeTextViewCab, completeTextViewDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_cab);

        Intent intent = getIntent();
        mId = intent.getStringExtra(Constants.CAB_ID);
        mPath = intent.getStringExtra("path");
        init();
        initListener();
        initObject();
        loadSpinnerData();
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
        completeTextViewDriver = findViewById(R.id.update_cab_spinner_driver);
        editTextPerCapacity = findViewById(R.id.update_cab_et_per_capacity);
        editTextLaugageCapacity = findViewById(R.id.update_cab_et_laugage_capacity);
        buttonCancle = findViewById(R.id.update_cab_btn_Cancel);
        buttonUpdate = findViewById(R.id.update_cab_btn_update);
        completeTextViewCab = findViewById(R.id.update_cab_spinner_area);
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mDriverRef = mFirebaseFirestore.collection(Constants.DRIVER_COLLECTION_REFERENCE_KEY);
        adapter = ArrayAdapter.createFromResource(this, R.array.Area, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        completeTextViewCab.setAdapter(adapter);
        isDefaultData();
    }

    private void initListener() {
        buttonUpdate.setOnClickListener(this);
        buttonCancle.setOnClickListener(this);
        completeTextViewDriver.setOnItemSelectedListener(this);
    }

    private void initObject() {
        helper = new Helper(this);
    }

    private void updateData() {
        String mCabName = editTextCabName.getText().toString().toUpperCase();
        String mCabNumber = editTextCabNumber.getText().toString().toUpperCase();
        String mPerCapacity = editTextPerCapacity.getText().toString();
        String mLaugageCapacity = editTextLaugageCapacity.getText().toString();
        String mAssignDriver = completeTextViewDriver.getText().toString();
        String mCabArea = completeTextViewCab.getText().toString();
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
            } else if (mAssignDriver.equals("")) {
                completeTextViewDriver.setError(getString(R.string.enter_valid_details));
            } else if (mCabArea.equals("")) {
                completeTextViewCab.setError(getString(R.string.enter_valid_details));
            } else {
                DocumentReference documentReference = db.collection(Constants.CAB_COLLECTION_REFERENCE_KEY).document(mId);
                documentReference.update(Constants.CAB_NAME_KEY, mCabName);
                documentReference.update(Constants.CAB_NUMBER_KEY, mCabNumber);
                documentReference.update(Constants.CAB_PERSON_CAPACITY_KEY, mPerCapacity);
                documentReference.update(Constants.CAB_LAUGAGE_CAPACITY_KEY, mLaugageCapacity);
                documentReference.update(Constants.CAB_AREA_KEY, mCabArea);
                documentReference.update(Constants.CAB_DRIVER_KEY, mAssignDriver)
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
        sharedPreferences = getSharedPreferences(Constants.CAB_DETAILS, MODE_PRIVATE);
        String cabName = sharedPreferences.getString(Constants.CAB_NAME_KEY, "");
        String cabNumber = sharedPreferences.getString(Constants.CAB_NUMBER_KEY, "");
        String cabPerCapacity = sharedPreferences.getString(Constants.CAB_PERSON_CAPACITY_KEY, "");
        String cabLauCapacity = sharedPreferences.getString(Constants.CAB_LAUGAGE_CAPACITY_KEY, "");
        String cabDriver = sharedPreferences.getString(Constants.CAB_DRIVER_KEY, "");
        String cabStatus = sharedPreferences.getString(Constants.CAB_STATUS_KEY, "");
        editTextCabName.setText(cabName);
        editTextCabNumber.setText(cabNumber);
        editTextPerCapacity.setText(cabPerCapacity);
        editTextLaugageCapacity.setText(cabLauCapacity);
    }

    private void loadSpinnerData() {
        final List<String> list_driver = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list_driver);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        completeTextViewDriver.setAdapter(adapter);
        mDriverRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        String driver = documentSnapshot.getString(Constants.DRIVER_EMAIL_KEY);
                        list_driver.add(driver);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
