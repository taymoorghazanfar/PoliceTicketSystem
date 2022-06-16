package com.zionjr.policeticket.section_driver.interfaces;

import com.zionjr.policeticket.section_driver.cloudfunctions.response_models.DriverResponse;

public interface IDashboardListener {

    void onGetDriverResponse(DriverResponse response, boolean error);
}
