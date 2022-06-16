package com.zionjr.policeticket.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.zionjr.policeticket.R;
import com.zionjr.policeticket.utils.PermissionUtils;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION = 3000;
    private static final int REQUEST_CODE_PERMISSION = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        checkPermissions();
    }

    private void checkPermissions() {

        if (!PermissionUtils.getRequestedPermissions(this).isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    PermissionUtils.getRequestedPermissions(this).toArray
                            (new String[0]), REQUEST_CODE_PERMISSION);
        } else {

            startSplash(selectActivity());
        }
    }

    private Intent selectActivity() {

        Intent intent;

        // if driver exist
        if (com.zionjr.policeticket
                .section_driver
                .prefs
                .Session.getEmail(SplashActivity.this) != null) {

            // goto driver dashboard
            intent = new Intent(SplashActivity.this, com.zionjr.policeticket
                    .section_driver
                    .activities
                    .DashboardActivity.class);

            // if policeman exist
        } else if (com.zionjr.policeticket
                .section_policeman
                .prefs
                .Session
                .getEmail(SplashActivity.this) != null) {

            // goto policeman dashboard
            intent = new Intent(SplashActivity.this, com.zionjr.policeticket
                    .section_policeman
                    .activities
                    .DashboardActivity.class);

            // goto user selection
        } else {

            intent = new Intent(SplashActivity.this,
                    UserSelectionActivity.class);
        }

        return intent;
    }

    private void startSplash(Intent intent) {

        Thread splashThread = new Thread() {

            @Override
            public void run() {
                try {
                    sleep(SPLASH_DURATION);

                    startActivity(intent);
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        splashThread.start();
    }

    private void showErrorDialog() {

        AlertDialog.Builder alert;
        alert = new AlertDialog.Builder(this);
        alert.setTitle("Permissions Denied");
        alert.setMessage("Requested permissions are required to use this app." +
                " Grant the required permissions & try again later");

        alert.setPositiveButton("OK", (dialog, whichButton) -> finishAffinity());

        alert.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSION) {

            boolean permissionsGranted = true;

            if (grantResults.length > 0) {

                for (int grantResult : grantResults) {

                    if (grantResult != PackageManager.PERMISSION_GRANTED) {

                        permissionsGranted = false;
                        break;
                    }
                }

                if (permissionsGranted) {

                    startSplash(selectActivity());

                } else {

                    showErrorDialog();
                }
            } else {

                showErrorDialog();
            }
        }
    }
}