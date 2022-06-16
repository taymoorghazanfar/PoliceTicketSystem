package com.zionjr.policeticket.model.entities;

public class CollectionCenter {

    private String id;
    private String name;
    private String phone;
    private double lat;
    private double lng;
    private String dateCreated;

    public CollectionCenter(String id, String name,
                            String phone, double lat, double lng, String dateCreated) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.lat = lat;
        this.lng = lng;
        this.dateCreated = dateCreated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
