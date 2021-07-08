package com.hcdc.xncovid.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class LoginUserRes {
    @SerializedName("Token")
    private String token;

    @SerializedName("Url")
    private String urlKBYTAPI;

    @SerializedName("Domain")
    private String domainKBYT;

    @SerializedName("Form")
    private  String regexUserInfo;

    @SerializedName("Id")
    private  String regexKBYTId;

    @SerializedName("Role")
    private  String Role;

    @SerializedName("returnCode")
    private  int returncode;

    @SerializedName("returnMess")
    private  String returnmess;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String getToken() {
        return token;
    }

    public String getUrlKBYTAPI() {
        return urlKBYTAPI;
    }

    public String getDomainKBYT() {
        return domainKBYT;
    }

    public String getRegexUserInfo() {
        return regexUserInfo;
    }

    public String getRegexKBYTId() {
        return regexKBYTId;
    }

    public String getRole() {
        return Role;
    }

    public int getReturncode() {
        return returncode;
    }

    public String getReturnmess() {
        return returnmess;
    }
}
