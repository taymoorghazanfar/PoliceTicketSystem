package com.zionjr.policeticket.section_driver.interfaces;

import com.zionjr.policeticket.section_driver.cloudfunctions.response_models.LicenseVerifyResponse;

public interface IVerifyLicenseListener {

    void onVerifyLicenseResponse(LicenseVerifyResponse response, boolean error);
}
