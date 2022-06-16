package com.zionjr.policeticket.section_driver.interfaces;

import com.zionjr.policeticket.section_driver.cloudfunctions.response_models.DriverResponse;

public interface ILoginListener {

    void onLoginResponse(DriverResponse response, boolean error);
}