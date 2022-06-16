package com.zionjr.policeticket.section_policeman.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.zionjr.policeticket.databinding.ActivityScanLicenseAsPoliceBinding;
import com.zionjr.policeticket.model.users.Driver;
import com.zionjr.policeticket.section_driver.cloudfunctions.CloudFunctions;
import com.zionjr.policeticket.section_driver.cloudfunctions.response_models.DriverResponse;
import com.zionjr.policeticket.section_driver.interfaces.IDashboardListener;
import com.zionjr.policeticket.utils.LicenseExtractionUtils;

import java.io.IOException;
import java.util.HashMap;

public class ScanLicenseAsPoliceActivity extends AppCompatActivity
        implements IDashboardListener {

    private static final int REQUEST_CODE_CAMERA = 1000;
    private ActivityScanLicenseAsPoliceBinding binding;
    private CloudFunctions cloudFunctions;

    private Bitmap licenseImageBitmap;
    private String licenseNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanLicenseAsPoliceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cloudFunctions = new CloudFunctions(this);

        hideLoading();

        binding.textViewScanFailed.setVisibility(View.GONE);
        binding.textViewNotRegistered.setVisibility(View.GONE);
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

        sendLicenseVerifyRequest();
    }

    private void sendLicenseVerifyRequest() {

        showLoading();
        cloudFunctions.getDriverByLicenseNumber(licenseNumber);
    }

    @Override
    public void onGetDriverResponse(DriverResponse response, boolean error) {

        if (error) {

            hideLoading();
            Toast.makeText(this, "An error occurred. Check your internet connection",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (response.getCode() == 400) {

            binding.textViewNotRegistered.setVisibility(View.VISIBLE);
            hideLoading();
            return;
        }

        Driver driver = response.getDriver();
        Gson gson = new Gson();
        Intent intent;

        if (getIntent().getBooleanExtra("issue_ticket", false)) {

            intent = new Intent(ScanLicenseAsPoliceActivity.this,
                    IssueTicketActivity.class);

        } else {

            intent = new Intent(ScanLicenseAsPoliceActivity.this,
                    ShowDriverDetailsActivity.class);
        }

        intent.putExtra("driver", gson.toJson(driver));

        hideLoading();
        startActivity(intent);
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
                    binding.textViewScanFailed.setVisibility(View.GONE);
                    binding.textViewNotRegistered.setVisibility(View.GONE);

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
    }

    public void goBack(View view) {

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