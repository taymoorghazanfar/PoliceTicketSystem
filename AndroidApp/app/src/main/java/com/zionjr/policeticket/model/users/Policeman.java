package com.zionjr.policeticket.model.users;

import com.zionjr.policeticket.model.entities.Ticket;

import java.util.HashMap;
import java.util.List;

public class Policeman {

    private String badgeNumber;

    private String name;
    private String email;
    private String password;
    private List<Ticket> ticketsIssued;

    public Policeman(String badgeNumber, String name,
                     String email, String password,
                     List<Ticket> ticketsIssued) {

        this.badgeNumber = badgeNumber;
        this.name = name;
        this.email = email;
        this.password = password;
        this.ticketsIssued = ticketsIssued;
    }

    public String getBadgeNumber() {
        return badgeNumber;
    }

    public void setBadgeNumber(String badgeNumber) {
        this.badgeNumber = badgeNumber;
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

    public List<Ticket> getTicketsIssued() {
        return ticketsIssued;
    }

    public void setTicketsIssued(List<Ticket> ticketsIssued) {
        this.ticketsIssued = ticketsIssued;
    }
}
