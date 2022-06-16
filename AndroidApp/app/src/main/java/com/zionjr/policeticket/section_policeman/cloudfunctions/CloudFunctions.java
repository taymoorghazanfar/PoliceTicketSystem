package com.zionjr.policeticket.section_policeman.cloudfunctions;

import com.google.firebase.functions.FirebaseFunctions;
import com.zionjr.policeticket.section_policeman.cloudfunctions.response_models.PolicemanResponse;
import com.zionjr.policeticket.section_policeman.interfaces.IDashboardListener;
import com.zionjr.policeticket.section_policeman.interfaces.ILoginListener;
import com.zionjr.policeticket.section_policeman.interfaces.ISignupListener;
import com.zionjr.policeticket.utils.ParseUtils;

import java.util.HashMap;

public class CloudFunctions {

    private ISignupListener signupListener;
    private ILoginListener loginListener;
    private IDashboardListener dashboardListener;

    public CloudFunctions(ISignupListener signupListener) {

        this.signupListener = signupListener;
    }

    public CloudFunctions(ILoginListener loginListener) {
        this.loginListener = loginListener;
    }

    public CloudFunctions(IDashboardListener dashboardListener) {
        this.dashboardListener = dashboardListener;
    }

    public void sendSignupRequest(String badgeNumber, String name,
                                  String email, String password) {

        HashMap<String, Object> data = new HashMap<>();
        data.put("badgeNumber", badgeNumber);
        data.put("name", name);
        data.put("email", email);
        data.put("password", password);

        FirebaseFunctions
                .getInstance()
                .getHttpsCallable("policeman-signup")
                .call(data)
                .addOnSuccessListener(httpsCallableResult -> {

                    PolicemanResponse response = ParseUtils
                            .parsePolicemanResponse(httpsCallableResult.getData());

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
                .getHttpsCallable("policeman-login")
                .call(data)
                .addOnSuccessListener(httpsCallableResult -> {

                    PolicemanResponse response = ParseUtils
                            .parsePolicemanResponse(httpsCallableResult.getData());

                    loginListener.onLoginResponse(response, false);

                })
                .addOnFailureListener(e ->
                        loginListener.onLoginResponse(null, true));
    }

    public void getPoliceman(String email) {

        HashMap<String, Object> data = new HashMap<>();
        data.put("email", email);

        FirebaseFunctions
                .getInstance()
                .getHttpsCallable("policeman-get_policeman")
                .call(data)
                .addOnSuccessListener(httpsCallableResult -> {

                    PolicemanResponse response = ParseUtils
                            .parsePolicemanResponse(httpsCallableResult.getData());

                    dashboardListener.onGetPolicemanResponse(response, false);

                })
                .addOnFailureListener(e ->
                        dashboardListener.onGetPolicemanResponse(null, true));
    }
}
