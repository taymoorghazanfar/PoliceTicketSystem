package com.zionjr.policeticket.model.entities;

public class PenaltyRule {

    private String id;
    private String title;
    private String description;
    private double amount;
    private String dateCreated;

    public PenaltyRule(String id, String title, String description, double amount, String dateCreated) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.dateCreated = dateCreated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
