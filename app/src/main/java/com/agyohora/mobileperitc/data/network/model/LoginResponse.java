package com.agyohora.mobileperitc.data.network.model;


import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

/**
 * Created by Invent on 2-1-18.
 */

public class LoginResponse implements JSONObjectRequestListener {

    @Override
    public void onResponse(JSONObject response) {

    }

    @Override
    public void onError(ANError anError) {

    }

    private String statusCode;
    private Long userId;
    private String accessToken;
    private String userName;
    private String userEmail;
    private String serverProfilePicUrl;
    private String message;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }


    public String getServerProfilePicUrl() {
        return serverProfilePicUrl;
    }

    public void setServerProfilePicUrl(String serverProfilePicUrl) {
        this.serverProfilePicUrl = serverProfilePicUrl;
    }
}

