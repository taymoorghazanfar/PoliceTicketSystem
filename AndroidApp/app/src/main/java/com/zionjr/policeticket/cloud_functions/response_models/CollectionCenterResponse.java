package com.zionjr.policeticket.cloud_functions.response_models;

import com.zionjr.policeticket.model.entities.CollectionCenter;

import java.util.List;

public class CollectionCenterResponse {

    private int code;
    private String message;
    private List<CollectionCenter> result;

    public CollectionCenterResponse(int code, String message, List<CollectionCenter> result) {
        this.code = code;
        this.message = message;
        this.result = result;
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

    public List<CollectionCenter> getResult() {
        return result;
    }

    public void setResult(List<CollectionCenter> result) {
        this.result = result;
    }
}
