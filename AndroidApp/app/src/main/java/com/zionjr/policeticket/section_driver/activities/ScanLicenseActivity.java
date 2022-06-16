package com.zionjr.policeticket.section_driver.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.zionjr.policeticket.databinding.ActivityScanLicenseDriverBinding;
import com.zionjr.policeticket.section_driver.cloudfunctions.CloudFunctions;
import com.zionjr.policeticket.section_driver.cloudfunctions.response_models.LicenseVerifyResponse;
import com.zionjr.policeticket.section_driver.interfaces.IVerifyLicenseListener;
import com.zionjr.policeticket.utils.LicenseExtractionUtils;

import java.io.IOException;
import java.util.HashMap;

public class ScanLicenseActivity extends AppCompatActivity implements IVerifyLicenseListener {

    private static final int REQUEST_CODE_CAMERA = 1000;
    private ActivityScanLicenseDriverBinding binding;
    private CloudFunctions cloudFunctions;

    private Bitmap licenseImageBitmap;
    private String licenseNumber;
    private String licenseExpiry;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanLicenseDriverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cloudFunctions = new CloudFunctions(this);

        hideLoading();

        binding.textViewScanSuccess.setVisibility(View.GONE);
        binding.textViewScanFailed.setVisibility(View.GONE);
    }

    public void scanLicense(View view) {

        ImagePicker.with(this)
                .crop(27, 17)
                .start(REQUEST_CODE_CAMERA);
    }

    private void sendLicenseScanRequest() {

        if (licenseImageBitmap == null) {

            binding.textViewScanFailed.setVisibility(View.VISIBLE);
            resetViews();
            hideLoading();
            return;
        }

        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        InputImage image = InputImage.fromBitmap(licenseImageBitmap, 0);

        recognizer.process(image)
                .addOnSuccessListener(visionText -> {

                    String[] words = visionText.getText().split("\n");

                    parseLicenseDetails(words);
                })
                .addOnFailureListener(
                        e -> {
                            Log.d("on_error", "sendLicenseScanRequest: "
                                    + e.getMessage() + ", " + e.toString());
                            binding.textViewScanFailed.setVisibility(View.VISIBLE);
                            resetViews();
                            hideLoading();
                        });
    }

    private void parseLicenseDetails(String[] words) {

        HashMap<String, String> data = LicenseExtractionUtils.parseLicense(words);

        if (data == null) {

            binding.textViewScanFailed.setVisibility(View.VISIBLE);
            resetViews();
            hideLoading();
            return;
        }

        licenseNumber = data.get("license_number");
        licenseExpiry = data.get("license_expiry");
        name = data.get("name");

        binding.editTextLicenseNumber.setText(licenseNumber);
        binding.editTextExpiryDate.setText(licenseExpiry);
        binding.editTextName.setText(name);

        binding.textViewScanSuccess.setVisibility(View.VISIBLE);
        hideLoading();
    }

    public void continueSignup(View view) {

        binding.textViewScanSuccess.setVisibility(View.GONE);
        binding.textViewScanFailed.setVisibility(View.GONE);

        if (TextUtils.isEmpty(licenseNumber)
                || TextUtils.isEmpty(name) || TextUtils.isEmpty(licenseExpiry)) {

            Toast.makeText(this, "Scan you license card", Toast.LENGTH_SHORT).show();
            return;
        }

        sendLicenseVerifyRequest();
    }

    private void sendLicenseVerifyRequest() {

        showLoading();
        cloudFunctions.verifyLicense(licenseNumber);
    }

    @Override
    public void onVerifyLicenseResponse(LicenseVerifyResponse response, boolean error) {

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

        Intent intent = new Intent(ScanLicenseActivity.this, SignupActivity.class);
        intent.putExtra("license_number", licenseNumber);
        intent.putExtra("license_expiry", licenseExpiry);
        intent.putExtra("name", name);

        hideLoading();
        startActivity(intent);
    }

    public void gotoLoginPage(View view) {

        startActivity(new Intent(ScanLicenseActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {

            if (data != null) {

                Uri imageUri = data.getData();

                try {

                    showLoading();
                    resetViews();
                    binding.textViewScanSuccess.setVisibility(View.GONE);
                    binding.textViewScanFailed.setVisibility(View.GONE);

                    licenseImageBitmap = MediaStore.Images.Media
                            .getBitmap(this.getContentResolver(), imageUri);

                    sendLicenseScanRequest();

                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }
    }

    void resetViews() {

        licenseNumber = null;
        licenseExpiry = null;
        name = null;

        binding.editTextLicenseNumber.setText("");
        binding.editTextExpiryDate.setText("");
        binding.editTextName.setText("");
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