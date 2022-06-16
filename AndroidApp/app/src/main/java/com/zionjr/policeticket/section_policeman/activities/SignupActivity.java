package com.zionjr.policeticket.section_policeman.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zionjr.policeticket.databinding.ActivitySignupPolicemanBinding;
import com.zionjr.policeticket.section_policeman.cloudfunctions.CloudFunctions;
import com.zionjr.policeticket.section_policeman.cloudfunctions.response_models.PolicemanResponse;
import com.zionjr.policeticket.section_policeman.interfaces.ISignupListener;
import com.zionjr.policeticket.section_policeman.prefs.Session;
import com.zionjr.policeticket.utils.StringUtils;

public class SignupActivity extends AppCompatActivity implements ISignupListener {

    private ActivitySignupPolicemanBinding binding;
    private CloudFunctions cloudFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupPolicemanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cloudFunctions = new CloudFunctions(this);
        hideLoading();
    }

    public void signup(View view) {

        if (checkFields()) {

            String badgeNumber = binding.editTextBadgeNumber.getText().toString().trim();
            String name = binding.editTextName.getText().toString().trim();
            String email = binding.editTextEmail.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString().trim();

            sendSignupRequest(badgeNumber, name, email, password);
        }
    }

    private boolean checkFields() {

        boolean isValid = true;
        String badgeNumber = binding.editTextBadgeNumber.getText().toString().trim();
        String name = binding.editTextName.getText().toString().trim();
        String email = binding.editTextEmail.getText().toString().trim();
        String password = binding.editTextPassword.getText().toString().trim();
        String confirmPassword = binding.editTextConfirmPassword.getText().toString().trim();

        // check fields are empty
        if (TextUtils.isEmpty(badgeNumber)) {

            binding.editTextBadgeNumber.setError("Badge number is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(name)) {

            binding.editTextName.setError("Name is required");
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

    private void sendSignupRequest(String badgeNumber, String name,
                                   String email, String password) {

        showLoading();
        cloudFunctions.sendSignupRequest(badgeNumber, name, email, password);
    }

    @Override
    public void onSignupResponse(PolicemanResponse response, boolean error) {

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
        String email = response.getPoliceman().getEmail();
        Session.setEmail(SignupActivity.this, email);
        Session.user = response.getPoliceman();

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