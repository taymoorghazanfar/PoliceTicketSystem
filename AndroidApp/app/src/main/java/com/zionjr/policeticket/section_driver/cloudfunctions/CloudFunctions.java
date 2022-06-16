package com.zionjr.policeticket.section_driver.cloudfunctions;

import android.util.Log;

import com.google.firebase.functions.FirebaseFunctions;
import com.zionjr.policeticket.section_driver.cloudfunctions.response_models.DriverResponse;
import com.zionjr.policeticket.section_driver.cloudfunctions.response_models.LicenseVerifyResponse;
import com.zionjr.policeticket.section_driver.interfaces.IDashboardListener;
import com.zionjr.policeticket.section_driver.interfaces.ILoginListener;
import com.zionjr.policeticket.section_driver.interfaces.ISignupListener;
import com.zionjr.policeticket.section_driver.interfaces.IVerifyLicenseListener;
import com.zionjr.policeticket.utils.ParseUtils;

import java.util.HashMap;

public class CloudFunctions {

    private IVerifyLicenseListener verifyLicenseListener;
    private ISignupListener signupListener;
    private ILoginListener loginListener;
    private IDashboardListener dashboardListener;

    public CloudFunctions(IVerifyLicenseListener verifyLicenseListener) {
        this.verifyLicenseListener = verifyLicenseListener;
    }

    public CloudFunctions(ISignupListener signupListener) {

        this.signupListener = signupListener;
    }

    public CloudFunctions(ILoginListener loginListener) {
        this.loginListener = loginListener;
    }

    public CloudFunctions(IDashboardListener dashboardListener) {
        this.dashboardListener = dashboardListener;
    }

    public void verifyLicense(String licenseNumber) {

        HashMap<String, Object> data = new HashMap<>();
        data.put("licenseNumber", licenseNumber);

        FirebaseFunctions
                .getInstance()
                .getHttpsCallable("driver-verify_license")
                .call(data)
                .addOnSuccessListener(httpsCallableResult -> {

                    LicenseVerifyResponse response = ParseUtils
                            .parseLicenseVerifyResponse(httpsCallableResult.getData());

                    verifyLicenseListener.onVerifyLicenseResponse(response, false);

                })
                .addOnFailureListener(e ->
                        verifyLicenseListener.onVerifyLicenseResponse(null, true));

    }

    public void sendSignupRequest(String licenseNumber, String plateNumber,
                                  String licenseExpiry, String name,
                                  String email, String password) {

        HashMap<String, Object> data = new HashMap<>();
        data.put("licenseNumber", licenseNumber);
        data.put("plateNumber", plateNumber);
        data.put("licenseExpiry", licenseExpiry);
        data.put("name", name);
        data.put("email", email);
        data.put("password", password);

        FirebaseFunctions
                .getInstance()
                .getHttpsCallable("driver-signup")
                .call(data)
                .addOnSuccessListener(httpsCallableResult -> {

                    DriverResponse response = ParseUtils
                            .parseDriverResponse(httpsCallableResult.getData());

                    signupListener.onSignupResponse(response, false);

                })
                .addOnFailureListener(e ->
                        signupListener.onSignupResponse(null, true));
    }

    public void sendLoginRequest(String email, String password) {

        HashMap<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("password", password);

        FirebaseFunctions
                .getInstance()
                .getHttpsCallable("driver-login")
                .call(data)
                .addOnSuccessListener(httpsCallableResult -> {

                    DriverResponse response = ParseUtils
                            .parseDriverResponse(httpsCallableResult.getData());

                    loginListener.onLoginResponse(response, false);

                })
                .addOnFailureListener(e ->
                        loginListener.onLoginResponse(null, true));
    }

    public void getDriver(String email) {

        Log.d("on_error", "getDriver: " + email);

        HashMap<String, Object> data = new HashMap<>();
        data.put("email", email);

        FirebaseFunctions
                .getInstance()
                .getHttpsCallable("driver-get_driver")
                .call(data)
                .addOnSuccessListener(httpsCallableResult -> {

                    DriverResponse response = ParseUtils
                            .parseDriverResponse(httpsCallableResult.getData());

                    dashboardListener.onGetDriverResponse(response, false);

                })
                .addOnFailureListener(e -> {

                    Log.d("on_error", "getDriver: " + e.getMessage() + ", " + e.toString());
                    dashboardListener.onGetDriverResponse(null, true);
                });
    }

    public void getDriverByLicenseNumber(String licenseNumber) {

        HashMap<String, Object> data = new HashMap<>();
        data.put("license_number", licenseNumber);

        FirebaseFunctions
                .getInstance()
                .getHttpsCallable("driver-get_driver_by_license_number")
                .call(data)
                .addOnSuccessListener(httpsCallableResult -> {

                    DriverResponse response = ParseUtils
                            .parseDriverResponse(httpsCallableResult.getData());

                    dashboardListener.onGetDriverResponse(response, false);

                })
                .addOnFailureListener(e -> {

                    Log.d("on_error", "getDriver: " + e.getMessage() + ", " + e.toString());
                    dashboardListener.onGetDriverResponse(null, true);
                });
    }

    public void getDriverByPlateNumber(String plateNumber) {

        HashMap<String, Object> data = new HashMap<>();
        data.put("plate_number", plateNumber);

        FirebaseFunctions
                .getInstance()
                .getHttpsCallable("driver-get_driver_by_plate_number")
                .call(data)
                .addOnSuccessListener(httpsCallableResult -> {

                    DriverResponse response = ParseUtils
                            .parseDriverResponse(httpsCallableResult.getData());

                    dashboardListener.onGetDriverResponse(response, false);

                })
                .addOnFailureListener(e -> {

                    Log.d("on_error", "getDriver: " + e.getMessage() + ", " + e.toString());
                    dashboardListener.onGetDriverResponse(null, true);
                });
    }
}
