package com.zionjr.policeticket.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.zionjr.policeticket.cloud_functions.response_models.CollectionCenterResponse;
import com.zionjr.policeticket.cloud_functions.response_models.PenaltyRuleResponse;
import com.zionjr.policeticket.section_driver.cloudfunctions.response_models.DriverResponse;
import com.zionjr.policeticket.section_driver.cloudfunctions.response_models.LicenseVerifyResponse;
import com.zionjr.policeticket.section_policeman.cloudfunctions.response_models.IssueTicketResponse;
import com.zionjr.policeticket.section_policeman.cloudfunctions.response_models.PolicemanResponse;

public class ParseUtils {

    public static PolicemanResponse parsePolicemanResponse(Object data) {

        Gson gson = new Gson();
        JsonElement jsonElement;

        jsonElement = gson.toJsonTree(data);

        return gson.fromJson(jsonElement, PolicemanResponse.class);
    }

    public static DriverResponse parseDriverResponse(Object data) {

        Gson gson = new Gson();
        JsonElement jsonElement;

        jsonElement = gson.toJsonTree(data);

        return gson.fromJson(jsonElement, DriverResponse.class);
    }

    public static LicenseVerifyResponse parseLicenseVerifyResponse(Object data) {

        Gson gson = new Gson();
        JsonElement jsonElement;

        jsonElement = gson.toJsonTree(data);

        return gson.fromJson(jsonElement, LicenseVerifyResponse.class);
    }

    public static PenaltyRuleResponse parsePenaltyRuleResponse(Object data) {

        Gson gson = new Gson();
        JsonElement jsonElement;

        jsonElement = gson.toJsonTree(data);

        return gson.fromJson(jsonElement, PenaltyRuleResponse.class);
    }

    public static CollectionCenterResponse parseCollectionCenterResponse(Object data) {

        Gson gson = new Gson();
        JsonElement jsonElement;

        jsonElement = gson.toJsonTree(data);

        return gson.fromJson(jsonElement, CollectionCenterResponse.class);
    }

    public static IssueTicketResponse parseIssueTicketResponse(Object data) {

        Gson gson = new Gson();
        JsonElement jsonElement;

        jsonElement = gson.toJsonTree(data);

        return gson.fromJson(jsonElement, IssueTicketResponse.class);
    }
}
