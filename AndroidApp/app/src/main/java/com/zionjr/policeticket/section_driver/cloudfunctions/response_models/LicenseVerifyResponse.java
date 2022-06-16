package com.zionjr.policeticket.section_driver.cloudfunctions.response_models;

public class LicenseVerifyResponse {

    private int code;
    private String message;

    public LicenseVerifyResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
