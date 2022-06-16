package com.zionjr.policeticket.cloud_functions.response_models;

import com.zionjr.policeticket.model.entities.PenaltyRule;

import java.util.List;

public class PenaltyRuleResponse {

    private int code;
    private String message;
    private List<PenaltyRule> result;

    public PenaltyRuleResponse(int code, String message, List<PenaltyRule> result) {
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

    public List<PenaltyRule> getResult() {
        return result;
    }

    public void setResult(List<PenaltyRule> result) {
        this.result = result;
    }
}
