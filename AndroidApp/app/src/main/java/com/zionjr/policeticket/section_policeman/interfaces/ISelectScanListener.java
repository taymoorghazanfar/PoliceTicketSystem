package com.zionjr.policeticket.section_policeman.interfaces;

public interface ISelectScanListener {

    void onScanLicenseSelected(boolean issueTicket);

    void onScanPlateNumberSelected(boolean issueTicket);
}
