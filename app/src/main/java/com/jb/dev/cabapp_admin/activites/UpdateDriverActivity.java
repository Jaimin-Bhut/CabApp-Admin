package com.jb.dev.cabapp_admin.activites;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jb.dev.cabapp_admin.R;
import com.jb.dev.cabapp_admin.helper.Constants;
import com.jb.dev.cabapp_admin.helper.Helper;
import com.jb.dev.cabapp_admin.model.DriverModel;

public class UpdateDriverActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "UpdateDriverAcitivity";
    private EditText editTextDriverFirstName, editTextDriverAddress, editTextDriverPhoneNumber, editTextDriverEmail;
    private Button buttonUpdate, buttonCancle;
    private Helper helper;
    SharedPreferences sharedPreferences;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DriverModel driverModel;
    String mid, mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_driver);

        Intent intent = getIntent();
        mid = intent.getStringExtra("id");
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
        editTextDriverFirstName = findViewById(R.id.update_driver_et_Firstname);
        editTextDriverAddress = findViewById(R.id.update_driver_et_Address);
        editTextDriverPhoneNumber = findViewById(R.id.update_driver_et_Phone_Number);
        editTextDriverEmail = findViewById(R.id.update_driver_et_Email);
        editTextDriverEmail.setEnabled(false);
        buttonUpdate = findViewById(R.id.update_driver_btn_update);
        buttonCancle = findViewById(R.id.update_driver_btn_Cencle);
        isDefaultData();
    }

    private void initListener() {
        buttonUpdate.setOnClickListener(this);
        buttonCancle.setOnClickListener(this);
    }

    private void initObject() {
        helper = new Helper(this);
    }

    private void isDefaultData() {
        sharedPreferences = getSharedPreferences("is_set", MODE_PRIVATE);
        String driverFirstName = sharedPreferences.getString("firstName", "");
        String driverPhoneNumber = sharedPreferences.getString("phonenumber", "");
        String driverEmail = sharedPreferences.getString("email", "");
        String driverAddress = sharedPreferences.getString("address", "");
        editTextDriverFirstName.setText(driverFirstName);
        editTextDriverAddress.setText(driverAddress);
        editTextDriverEmail.setText(driverEmail);
        editTextDriverPhoneNumber.setText(driverPhoneNumber);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_driver_btn_update:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(buttonUpdate.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                updateDriverDataToDatabase();
                break;
            case R.id.update_driver_btn_Cencle:
                this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                this.finish();
                break;
        }
    }

    //for update driver data to database
    private void updateDriverDataToDatabase() {
        String mFirstName = editTextDriverFirstName.getText().toString();
        String mAddress = editTextDriverAddress.getText().toString();
        String mPhoneNumber = editTextDriverPhoneNumber.getText().toString();
        String mEmail = editTextDriverEmail.getText().toString();
        try {
            if (!Helper.isValidText(mFirstName)) {
                editTextDriverFirstName.setError(getString(R.string.enter_valid_details));
                editTextDriverFirstName.setFocusable(true);
            } else if (!Helper.isValidTextDigit(mAddress)) {
                editTextDriverAddress.setError(getString(R.string.enter_valid_details));
                editTextDriverAddress.setFocusable(true);
            } else if (!Helper.validPhoneNumber(mPhoneNumber)) {
                editTextDriverPhoneNumber.setError(getString(R.string.enter_valid_details));
                editTextDriverPhoneNumber.setFocusable(true);
            } else if (!Helper.validDriverEmail(mEmail)) {
                editTextDriverEmail.setError(getString(R.string.enter_valid_details));
                editTextDriverEmail.setFocusable(true);
            } else {
                DocumentReference documentReference = db.collection("Driver_Data").document(mid);
                documentReference.update("Name", mFirstName);
                documentReference.update("Address", mAddress);
                documentReference.update("Phone", mPhoneNumber)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UpdateDriverActivity.this, "Document Updated", Toast.LENGTH_LONG).show();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                setResult(Constants.DRIVER_REFRESH_RESULT_CODE);
                                UpdateDriverActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UpdateDriverActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        } catch (
                Exception e) {
            Log.e(TAG, "updateDriverMethod" + e);
        }
    }
}
