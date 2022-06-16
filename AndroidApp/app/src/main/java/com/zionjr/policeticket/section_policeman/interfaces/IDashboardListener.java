package com.zionjr.policeticket.section_policeman.interfaces;

import com.zionjr.policeticket.section_policeman.cloudfunctions.response_models.PolicemanResponse;

public interface IDashboardListener {

    void onGetPolicemanResponse(PolicemanResponse response, boolean error);
}
