package com.zionjr.policeticket.section_driver.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zionjr.policeticket.databinding.ActivitySignupDriverBinding;
import com.zionjr.policeticket.section_driver.cloudfunctions.CloudFunctions;
import com.zionjr.policeticket.section_driver.cloudfunctions.response_models.DriverResponse;
import com.zionjr.policeticket.section_driver.interfaces.ISignupListener;
import com.zionjr.policeticket.section_driver.prefs.Session;
import com.zionjr.policeticket.utils.StringUtils;

public class SignupActivity extends AppCompatActivity implements ISignupListener {

    private ActivitySignupDriverBinding binding;
    private CloudFunctions cloudFunctions;

    private String licenseNumber;
    private String licenseExpiry;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupDriverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cloudFunctions = new CloudFunctions(this);

        hideLoading();
        getIntentData();

    }

    private void getIntentData() {

        licenseNumber = getIntent().getStringExtra("license_number");
        licenseExpiry = getIntent().getStringExtra("license_expiry");
        name = getIntent().getStringExtra("name");

        Log.d("license", "getIntentData: license: " + licenseNumber);
        Log.d("license", "getIntentData: expiry: " + licenseExpiry);
        Log.d("license", "getIntentData: name: " + name);
    }

    public void signup(View view) {

        if (checkFields()) {

            String plateNumber = binding.editTextPlateNumber.getText().toString().trim();
            String email = binding.editTextEmail.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString().trim();

            sendSignupRequest(licenseNumber, licenseExpiry, name, plateNumber, email, password);
        }
    }

    private boolean checkFields() {

        boolean isValid = true;
        String plateNumber = binding.editTextPlateNumber.getText().toString().trim();
        String email = binding.editTextEmail.getText().toString().trim();
        String password = binding.editTextPassword.getText().toString().trim();
        String confirmPassword = binding.editTextConfirmPassword.getText().toString().trim();

        // check fields are empty
        if (TextUtils.isEmpty(plateNumber)) {

            binding.editTextPlateNumber.setError("Plate number is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(email)) {

            binding.editTextEmail.setError("Email is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {

            binding.editTextPassword.setError("Password is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {

            binding.editTextConfirmPassword.setError("Password confirmation is required");
            isValid = false;
        }

        // if fields are empty
        if (!isValid) {

            return false;
        }

        // check email is valid
        if (!StringUtils.isEmailValid(email)) {

            binding.editTextEmail.setError("Email has invalid format");
            isValid = false;
        }

        // check password is 6 character long
        if (password.length() < 6) {
            binding.editTextPassword.setError("Password should be at least 6 characters long");
            isValid = false;

        }

        if (confirmPassword.length() < 6) {
            binding.editTextConfirmPassword.setError("Password should be at least 6 characters long");
            isValid = false;

        }

        // check if passwords do not match
        if (!password.equals(confirmPassword)) {

            binding.editTextConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }

        return isValid;
    }

    private void sendSignupRequest(String licenseNumber, String licenseExpiry,
                                   String name, String plateNumber,
                                   String email, String password) {

        showLoading();
        cloudFunctions.sendSignupRequest(licenseNumber, plateNumber,
                licenseExpiry, name, email, password);
    }

    @Override
    public void onSignupResponse(DriverResponse response, boolean error) {

        if (error) {

            hideLoading();
            Toast.makeText(this, "An error occurred. Check your internet connection",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (response.getCode() == 400) {

            Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
            hideLoading();
            return;
        }

        // save the user to prefs
        String email = response.getDriver().getEmail();
        Session.setEmail(SignupActivity.this, email);
        Session.user = response.getDriver();

        // goto dashboard
        hideLoading();
        Intent intent = new Intent(SignupActivity.this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void gotoLoginPage(View view) {

        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        finish();
    }

    public void showLoading() {

        binding.layoutContent.setVisibility(View.GONE);
        binding.layoutLoading.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {

        binding.layoutLoading.setVisibility(View.GONE);
        binding.layoutContent.setVisibility(View.VISIBLE);
    }
}