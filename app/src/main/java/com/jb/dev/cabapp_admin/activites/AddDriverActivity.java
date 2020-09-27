package com.jb.dev.cabapp_admin.activites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.jb.dev.cabapp_admin.R;
import com.jb.dev.cabapp_admin.helper.Constants;
import com.jb.dev.cabapp_admin.helper.Helper;

import java.util.HashMap;
import java.util.Map;

public class AddDriverActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText editTextFirstName, editTextAddress, editTextPhoneNumber, editTextEmail, editTextPassword;
    private Button buttonOk, buttonCancle;
    private String TAG = "ADD Driver Activity";
    private Helper validation;
    String mFirstName;
    String mAddress;
    String mPhoneNumber;
    String mEmail;
    String mPassword;
    FirebaseFirestore mFirebaseFirestore;
    CollectionReference mDriverRef;
    View parent_view;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_driver);

        init();
        initListener();
        initObject();
    }

    private void init() {
        editTextAddress = findViewById(R.id.driver_et_Address);
        editTextEmail = findViewById(R.id.driver_et_Email);
        editTextFirstName = findViewById(R.id.driver_et_name);
        editTextPhoneNumber = findViewById(R.id.driver_et_Phone_Number);
        editTextPassword = findViewById(R.id.driver_et_Password);
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        buttonCancle = findViewById(R.id.driver_btn_Cencle);
        buttonOk = findViewById(R.id.driver_btn_Ok);
        progressBar = findViewById(R.id.progress_circular);
        mDriverRef = mFirebaseFirestore.collection(Constants.DRIVER_COLLECTION_REFERENCE_KEY);
        parent_view = findViewById(android.R.id.content);
        isClear();
        editTextFirstName.setText("Jaimin");
        editTextAddress.setText("adadadd");
        editTextPhoneNumber.setText("8989898989");
        editTextPassword.setText("23456234");
        editTextEmail.setText("jaimin@driver.com");
    }

    private void initListener() {
        buttonOk.setOnClickListener(this);
        buttonCancle.setOnClickListener(this);
    }

    private void initObject() {
        validation = new Helper(this);
    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.driver_btn_Ok:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(buttonOk.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                addDriverDataToFirebase();
                break;
            case R.id.driver_btn_Cencle:
                this.finish();
                break;
        }
    }

    private void isClear() {
        editTextPassword.getText().clear();
        editTextEmail.getText().clear();
        editTextPhoneNumber.getText().clear();
        editTextAddress.getText().clear();
        editTextFirstName.getText().clear();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    //Add DriverData To Firebase
    private void addDriverDataToFirebase() {
        mFirstName = editTextFirstName.getText().toString();
        mAddress = editTextAddress.getText().toString();
        mPhoneNumber = editTextPhoneNumber.getText().toString();
        mEmail = editTextEmail.getText().toString();
        mPassword = editTextPassword.getText().toString();
        if (!Helper.isValidText(mFirstName)) {
            editTextFirstName.setError(getString(R.string.txt_enter_first_name));
            editTextFirstName.setFocusable(true);
        } else if (!Helper.isValidAddress(mAddress)) {
            editTextAddress.setError(getString(R.string.txt_enter_address));
            editTextAddress.setFocusable(true);
        } else if (!Helper.validPhoneNumber(mPhoneNumber)) {
            editTextPhoneNumber.setError(getString(R.string.txt_enter_phone_number));
            editTextPhoneNumber.setFocusable(true);
        } else if (!Helper.validDriverEmail(mEmail)) {
            editTextEmail.setError(getString(R.string.txt_enter_email));
            editTextEmail.setFocusable(true);
        } else if (!Helper.validPassword(mPassword)) {
            editTextPassword.setError(getString(R.string.txt_enter_password));
            editTextPassword.setFocusable(true);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            Query query = mDriverRef.whereEqualTo(Constants.DRIVER_EMAIL_KEY, mEmail);
            final Query queryPhoneNumber = mDriverRef.whereEqualTo(Constants.DRIVER_PHONE_NUMBER_KEY, mPhoneNumber);
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Snackbar.make(parent_view, getString(R.string.txt_driver_already_exist), Snackbar.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    } else {
                        queryPhoneNumber.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    Snackbar.make(parent_view, getString(R.string.txt_phone_already_exist), Snackbar.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    Map<String, Object> NewDriver = new HashMap<>();
                                    NewDriver.put(Constants.DRIVER_NAME_KEY, mFirstName);
                                    NewDriver.put(Constants.DRIVER_ADDRESS_KEY, mAddress);
                                    NewDriver.put(Constants.DRIVER_PHONE_NUMBER_KEY, mPhoneNumber);
                                    NewDriver.put(Constants.DRIVER_EMAIL_KEY, mEmail);
                                    NewDriver.put(Constants.DRIVER_PASSWORD_KEY, mPassword);
                                    mFirebaseFirestore.collection(Constants.DRIVER_COLLECTION_REFERENCE_KEY).document().set(NewDriver)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressBar.setVisibility(View.GONE);
                                                    Snackbar.make(parent_view, getString(R.string.txt_success_register), Snackbar.LENGTH_SHORT).show();
                                                    Intent intent = getIntent();
                                                    setResult(Constants.DRIVER_REFRESH_RESULT_CODE);
                                                    finish();
//                                        navigateUpTo(new Intent(AddDriverActivity.this, CabFragment.class));
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Snackbar.make(parent_view, getString(R.string.something_want_wrong), Snackbar.LENGTH_SHORT).show();
                                                    progressBar.setVisibility(View.GONE);
                                                }
                                            });
                                }
                            }
                        });
                    }
                }
            });
        }
    }
}