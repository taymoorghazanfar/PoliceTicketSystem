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
import com.zionjr.policeticket.databinding.ActivityScanPlateNumberAsPoliceBinding;
import com.zionjr.policeticket.model.users.Driver;
import com.zionjr.policeticket.section_driver.cloudfunctions.CloudFunctions;
import com.zionjr.policeticket.section_driver.cloudfunctions.response_models.DriverResponse;
import com.zionjr.policeticket.section_driver.interfaces.IDashboardListener;
import com.zionjr.policeticket.utils.PlateNumberExtractionUtils;

import java.io.IOException;

public class ScanPlateNumberAsPoliceActivity extends AppCompatActivity
        implements IDashboardListener {

    private static final int REQUEST_CODE_CAMERA = 1000;
    private ActivityScanPlateNumberAsPoliceBinding binding;
    private CloudFunctions cloudFunctions;

    private Bitmap plateImageBitmap;
    private String plateNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanPlateNumberAsPoliceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cloudFunctions = new CloudFunctions(this);

        hideLoading();

        binding.textViewScanFailed.setVisibility(View.GONE);
        binding.textViewNotRegistered.setVisibility(View.GONE);
    }

    public void scanLicense(View view) {

        ImagePicker.with(this)
                .crop(2, 1)
                .start(REQUEST_CODE_CAMERA);
    }

    private void sendScanRequest() {

        if (plateImageBitmap == null) {

            binding.textViewScanFailed.setVisibility(View.VISIBLE);
            resetViews();
            hideLoading();
            return;
        }

        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        InputImage image = InputImage.fromBitmap(plateImageBitmap, 0);

        recognizer.process(image)
                .addOnSuccessListener(visionText -> {

                    String[] words = visionText.getText().split("\n");

                    parseScanDetails(words);
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

    private void parseScanDetails(String[] words) {

        String data = PlateNumberExtractionUtils.parsePlateNumber(words);

        if (data == null) {

            binding.textViewScanFailed.setVisibility(View.VISIBLE);
            resetViews();
            hideLoading();
            return;
        }

        plateNumber = data;

        sendPlateNumberVerifyRequest();
    }

    private void sendPlateNumberVerifyRequest() {

        showLoading();
        cloudFunctions.getDriverByPlateNumber(plateNumber);
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

            intent = new Intent(ScanPlateNumberAsPoliceActivity.this,
                    IssueTicketActivity.class);

        } else {

            intent = new Intent(ScanPlateNumberAsPoliceActivity.this,
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

                    plateImageBitmap = MediaStore.Images.Media
                            .getBitmap(this.getContentResolver(), imageUri);

                    sendScanRequest();

                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }
    }

    void resetViews() {

        plateNumber = null;
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