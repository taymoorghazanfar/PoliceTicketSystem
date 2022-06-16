package com.zionjr.policeticket.model.entities;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Ticket {

    private String id;
    private String dateIssued;
    private String dateDue;
    private boolean isPayed;
    private Violator violator;
    private Issuer issuer;
    private List<PenaltyRule> penalties;

    public Ticket(Violator violator, Issuer issuer, List<PenaltyRule> penalties) {

        this.isPayed = false;
        this.violator = violator;
        this.issuer = issuer;
        this.penalties = penalties;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(String dateIssued) {
        this.dateIssued = dateIssued;
    }

    public String getDateDue() {
        return dateDue;
    }

    public void setDateDue(String dateDue) {
        this.dateDue = dateDue;
    }

    public boolean isPayed() {
        return isPayed;
    }

    public void setPayed(boolean payed) {
        isPayed = payed;
    }

    public Violator getViolator() {
        return violator;
    }

    public void setViolator(Violator violator) {
        this.violator = violator;
    }

    public Issuer getIssuer() {
        return issuer;
    }

    public void setIssuer(Issuer issuer) {
        this.issuer = issuer;
    }

    public List<PenaltyRule> getPenalties() {
        return penalties;
    }

    public void setPenalties(List<PenaltyRule> penalties) {
        this.penalties = penalties;
    }

    public static class Violator {

        private String name;
        private String licenseNumber;
        private String plateNumber;

        public Violator(String name, String licenseNumber, String plateNumber) {
            this.name = name;
            this.licenseNumber = licenseNumber;
            this.plateNumber = plateNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLicenseNumber() {
            return licenseNumber;
        }

        public void setLicenseNumber(String licenseNumber) {
            this.licenseNumber = licenseNumber;
        }

        public String getPlateNumber() {
            return plateNumber;
        }

        public void setPlateNumber(String plateNumber) {
            this.plateNumber = plateNumber;
        }
    }

    public static class Issuer {

        private String name;
        private String badgeNumber;

        public Issuer(String name, String badgeNumber) {
            this.name = name;
            this.badgeNumber = badgeNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBadgeNumber() {
            return badgeNumber;
        }

        public void setBadgeNumber(String badgeNumber) {
            this.badgeNumber = badgeNumber;
        }
    }

    public JSONObject toJson() {

        Gson gson = new Gson();
        String jsonString = gson.toJson(this);

        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            return null;
        }
    }
}
