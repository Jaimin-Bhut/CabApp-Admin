package com.jb.dev.cabapp_admin.activites;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jb.dev.cabapp_admin.R;
import com.jb.dev.cabapp_admin.helper.Constants;
import com.jb.dev.cabapp_admin.helper.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddCabActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "Add Cab Activity";
    private EditText editTextCabName, editTextCabNumber, editTextPerCapacity, editTextLaugageCapacity;
    private Button buttonOk, buttonCancel;
    private Spinner spinnerDriver;
    private Spinner spinnerCabArea;
    FirebaseFirestore mFirebaseFirestore;
    CollectionReference mDriverRef;
    CollectionReference mCabRef;
    String mCabName;
    String mCabNumber;
    String mPerCapacity;
    String mLauageCapacity;
    String mAssignDriver;
    String mCabArea;
    private View parent_view;
    ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cab);

        init();
        initListener();
        loadSpinnerData();
    }

    private void init() {
        editTextCabName = findViewById(R.id.cab_et_cabname);
        editTextCabNumber = findViewById(R.id.cab_et_cabnumber);
        editTextPerCapacity = findViewById(R.id.cab_et_per_capacity);
        editTextLaugageCapacity = findViewById(R.id.cab_et_laugage_capacity);
        buttonOk = findViewById(R.id.cab_btn_Ok);
        buttonCancel = findViewById(R.id.cab_btn_Cancel);
        spinnerDriver = findViewById(R.id.cab_spinner_driver);
        spinnerCabArea = findViewById(R.id.cab_spinner_area);
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        adapter = ArrayAdapter.createFromResource(this, R.array.Area, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCabArea.setAdapter(adapter);
        mDriverRef = mFirebaseFirestore.collection(Constants.DRIVER_COLLECTION_REFERENCE_KEY);
        mCabRef = mFirebaseFirestore.collection(Constants.CAB_COLLECTION_REFERENCE_KEY);
        parent_view = findViewById(android.R.id.content);
        editTextCabName.setText("Jaimin");
        editTextCabNumber.setText("GJ09PG9090");
        editTextPerCapacity.setText("3");
        editTextLaugageCapacity.setText("32");
    }

    private void initListener() {
        buttonOk.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
        spinnerDriver.setOnItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cab_btn_Ok:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(buttonOk.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                addCabDataToFirebase();
                break;
            case R.id.cab_btn_Cancel:
                this.finish();
                break;
        }
    }


    private void loadSpinnerData() {
        final List<String> list_driver = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, list_driver);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDriver.setAdapter(adapter);
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //Add CabData To Firebase
    private void addCabDataToFirebase() {
        mCabName = editTextCabName.getText().toString();
        mCabNumber = editTextCabNumber.getText().toString().toUpperCase();
        mPerCapacity = editTextPerCapacity.getText().toString();
        mLauageCapacity = editTextLaugageCapacity.getText().toString();
        mAssignDriver = spinnerDriver.getSelectedItem().toString();
        mCabArea = spinnerCabArea.getSelectedItem().toString();
        if (!Helper.isValidTextDigit(mCabName)) {
            editTextCabName.setError(getString(R.string.txt_enter_cab_name));
            editTextCabName.setFocusable(true);
        } else if (!Helper.validNumberPlate(mCabNumber)) {
            editTextCabNumber.setError(getString(R.string.enter_valid_details));
            editTextCabNumber.setFocusable(true);
        } else if (!Helper.isValidPerCapacity(mPerCapacity)) {
            editTextPerCapacity.setError(getString(R.string.enter_valid_details));
            editTextPerCapacity.setFocusable(true);
        } else if (!Helper.isValidLaugage(mLauageCapacity)) {
            editTextLaugageCapacity.setError(getString(R.string.enter_valid_details));
            editTextLaugageCapacity.setFocusable(true);
        } else {
            Query query = mCabRef.whereEqualTo(Constants.CAB_NUMBER_KEY, mCabNumber).whereEqualTo(Constants.CAB_DRIVER_KEY, mAssignDriver);
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Map<String, Object> NewCab = new HashMap<>();
                        NewCab.put(Constants.CAB_NAME_KEY, mCabName);
                        NewCab.put(Constants.CAB_NUMBER_KEY, mCabNumber);
                        NewCab.put(Constants.CAB_PERSON_CAPACITY_KEY, mPerCapacity);
                        NewCab.put(Constants.CAB_LAUGAGE_CAPACITY_KEY, mLauageCapacity);
                        NewCab.put(Constants.CAB_DRIVER_KEY, mAssignDriver);
                        NewCab.put(Constants.CAB_AREA_KEY, mCabArea);
                        NewCab.put(Constants.CAB_STATUS_KEY, getString(R.string.available));
                        mFirebaseFirestore.collection(Constants.CAB_COLLECTION_REFERENCE_KEY).document().set(NewCab)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Snackbar.make(parent_view, getString(R.string.txt_success_register), Snackbar.LENGTH_SHORT).show();
                                        setResult(Constants.CAB_REFRESH_RESULT_CODE);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(parent_view, getString(R.string.something_want_wrong), Snackbar.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        Snackbar.make(parent_view, mCabNumber + " " + getString(R.string.txt_cab_already_exist), Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
