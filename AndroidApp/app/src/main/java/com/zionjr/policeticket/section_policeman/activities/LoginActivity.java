package com.zionjr.policeticket.section_policeman.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zionjr.policeticket.activities.UserSelectionActivity;
import com.zionjr.policeticket.databinding.ActivityLoginPolicemanBinding;
import com.zionjr.policeticket.section_policeman.cloudfunctions.CloudFunctions;
import com.zionjr.policeticket.section_policeman.cloudfunctions.response_models.PolicemanResponse;
import com.zionjr.policeticket.section_policeman.interfaces.ILoginListener;
import com.zionjr.policeticket.section_policeman.prefs.Session;
import com.zionjr.policeticket.utils.StringUtils;

public class LoginActivity extends AppCompatActivity implements ILoginListener {

    private ActivityLoginPolicemanBinding binding;
    private CloudFunctions cloudFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginPolicemanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cloudFunctions = new CloudFunctions(this);
        hideLoading();
    }

    public void gotoSignupPage(View view) {

        startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        finish();
    }

    public void login(View view) {

        if (checkFields()) {

            String email = binding.editTextEmail.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString().trim();

            sendLoginRequest(email, password);
        }
    }

    private boolean checkFields() {

        boolean isValid = true;
        String email = binding.editTextEmail.getText().toString().trim();
        String password = binding.editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {

            binding.editTextEmail.setError("Email is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {

            binding.editTextPassword.setError("Password is required");
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
            binding.editTextPassword.setError("Password must be at least 6 characters long");
            isValid = false;

        }

        return isValid;
    }

    private void sendLoginRequest(String email, String password) {

        showLoading();
        cloudFunctions.sendLoginRequest(email, password);
    }

    @Override
    public void onLoginResponse(PolicemanResponse response, boolean error) {

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
        Session.setEmail(LoginActivity.this, email);
        Session.user = response.getPoliceman();

        // goto dashboard
        hideLoading();
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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

    public void gotoSelectUserPage(View view) {

        startActivity(new Intent(LoginActivity.this, UserSelectionActivity.class));
        finish();
    }
}