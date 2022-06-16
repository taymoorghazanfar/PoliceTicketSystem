package com.zionjr.policeticket.section_driver.cloudfunctions.response_models;

import com.zionjr.policeticket.model.users.Driver;

public class DriverResponse {

    private int code;
    private String message;
    private Driver driver;

    public DriverResponse(int code, String message, Driver driver) {
        this.code = code;
        this.message = message;
        this.driver = driver;
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

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}
