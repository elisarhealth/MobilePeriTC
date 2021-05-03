package com.agyohora.mobileperitc.data.network;

import com.agyohora.mobileperitc.data.network.model.LoginRequest;
import com.androidnetworking.common.ANResponse;

import org.json.JSONObject;

/**
 * Created by Invent on 2-1-18.
 */

interface ApiHelper {

    //ApiHeader getApiHeader();

    void doServerLogin(LoginRequest.ServerLoginRequest request);

    void doLogoutApiCall();

    void getDataApiCall();

    void checkAndInstallUpdate();

    void checkForUpdates();

    ANResponse syncData(JSONObject jsonObject);

}
