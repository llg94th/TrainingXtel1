package com.llg94th.trainingxtel1.models;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;

public class AccountKitResult {
    private String authorization_code;
    private String access_token_key;
    private String service_code;
    private DevInfo devInfo;

    public String getAuthorization_code() {
        return authorization_code;
    }

    public void setAuthorization_code(String authorization_code) {
        this.authorization_code = authorization_code;
    }

    public String getService_code() {
        return service_code;
    }

    public void setService_code(String service_code) {
        this.service_code = service_code;
    }

    public DevInfo getDevInfo() {
        return devInfo;
    }

    public void setDevInfo(DevInfo devInfo) {
        this.devInfo = devInfo;
    }

    public AccountKitResult(String authorization_code,String access_token_key, String service_code, DevInfo devInfo) {

        this.authorization_code = authorization_code;
        this.access_token_key = access_token_key;
        this.service_code = service_code;
        this.devInfo = devInfo;
    }

    public AccountKitResult() {

    }

    public String getAccess_token_key() {
        return access_token_key;
    }

    public void setAccess_token_key(String access_token_key) {
        this.access_token_key = access_token_key;
    }

    public static AccountKitResult getInstant(Context context){
        AccountKitResult accountKitResult = new AccountKitResult();
        accountKitResult.devInfo = DevInfo.getInstant(context);
        accountKitResult.service_code = "PRK";
        return accountKitResult;

    }
    public String toJsonString(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    public JsonObject toJsonObject(){
        Gson gson = new Gson();
        JsonObject object = gson.fromJson(this.toJsonString(),JsonObject.class);
        return object;
    }
}
