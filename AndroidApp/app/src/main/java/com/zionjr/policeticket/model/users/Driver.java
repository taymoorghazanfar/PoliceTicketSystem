package com.zionjr.policeticket.model.users;

import com.zionjr.policeticket.model.entities.Ticket;

import java.util.List;

public class Driver {

    private String licenseNumber;
    private String plateNumber;

    private String licenseExpiry;
    private String name;
    private String email;
    private String password;
    private List<Ticket> tickets;

    public Driver(String licenseNumber, String plateNumber,
                  String licenseExpiry, String name,
                  String email, String password, List<Ticket> tickets) {

        this.licenseNumber = licenseNumber;
        this.plateNumber = plateNumber;
        this.licenseExpiry = licenseExpiry;
        this.name = name;
        this.email = email;
        this.password = password;
        this.tickets = tickets;
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

    public String getLicenseExpiry() {
        return licenseExpiry;
    }

    public void setLicenseExpiry(String licenseExpiry) {
        this.licenseExpiry = licenseExpiry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "licenseNumber='" + licenseNumber + '\'' +
                ", plateNumber='" + plateNumber + '\'' +
                ", licenseExpiry='" + licenseExpiry + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", tickets=" + tickets +
                '}';
    }
}
