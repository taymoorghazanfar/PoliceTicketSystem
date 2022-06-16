package com.zionjr.policeticket.section_policeman.cloudfunctions.response_models;

import com.zionjr.policeticket.model.users.Policeman;

public class PolicemanResponse {

    private int code;
    private String message;
    private Policeman policeman;

    public PolicemanResponse(int code, String message, Policeman policeman) {
        this.code = code;
        this.message = message;
        this.policeman = policeman;
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

    public Policeman getPoliceman() {
        return policeman;
    }

    public void setPoliceman(Policeman policeman) {
        this.policeman = policeman;
    }
}