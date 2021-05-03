package com.agyohora.mobileperitc.data.network;

import android.util.Log;

import com.agyohora.mobileperitc.BuildConfig;
import com.agyohora.mobileperitc.data.network.model.LoginRequest;
import com.agyohora.mobileperitc.data.network.model.LoginResponse;
import com.agyohora.mobileperitc.inappupdate.CheckForUpdates;
import com.agyohora.mobileperitc.inappupdate.InAppUpdater;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANResponse;

import org.json.JSONObject;

/**
 * Created by Invent on 2-1-18.
 * Do server requests
 */

public class AppApiHelper implements ApiHelper {
    private ApiHeader mApiHeader;

   /* public AppApiHelper(ApiHeader apiHeader) {
        mApiHeader = apiHeader;
    }*/

  /*  @Override
    public ApiHeader getApiHeader() {
        return mApiHeader;
    }*/

    @Override
    public void doServerLogin(LoginRequest.ServerLoginRequest request) {
        AndroidNetworking.post(ApiEndPoint.DATA_SERVER_ENDPOINT)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addBodyParameter(request)
                .build()
                .getAsJSONObject(new LoginResponse());
    }

    @Override
    public void doLogoutApiCall() {

    }

    @Override
    public void getDataApiCall() {

    }

    @Override
    public void checkAndInstallUpdate() {
        AndroidNetworking.get(ApiEndPoint.UPDATE_SERVER_ENDPOINT)
                .build()
                .getAsJSONArray(new InAppUpdater());
    }

    @Override
    public void checkForUpdates() {
        AndroidNetworking.get(ApiEndPoint.UPDATE_SERVER_ENDPOINT)
                .build()
                .getAsJSONArray(new CheckForUpdates());
    }

    @Override
    public ANResponse syncData(JSONObject jsonObject) {
        Log.e("JSON Object", "Before Sent to server" + jsonObject.toString());
        return AndroidNetworking.post(ApiEndPoint.DATA_SERVER_ENDPOINT)
                .addHeaders("Authorization", BuildConfig.API_KEY)
                .addJSONObjectBody(jsonObject)
                .build()
                .executeForOkHttpResponse();
    }
}
