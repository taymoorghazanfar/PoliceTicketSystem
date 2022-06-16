package com.zionjr.policeticket.section_policeman.cloudfunctions.response_models;

public class IssueTicketResponse {

    private int code;
    private String ticketId;
    private String dateIssued;
    private String dateDue;

    public IssueTicketResponse(int code, String ticketId, String dateIssued, String dateDue) {
        this.code = code;
        this.ticketId = ticketId;
        this.dateIssued = dateIssued;
        this.dateDue = dateDue;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
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
}
