package com.hcdc.xncovid.model;

import com.google.gson.Gson;

public class GroupedUserInfo {

    private String uid;

    private boolean isOnline;

    private String fullname;


    private String birthYear;


    private String phone;

    private  String cccd;


    private  int gent;

    public GroupedUserInfo(String uid, boolean isOnline) {
        this.uid = uid;
        this.isOnline = isOnline;
    }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public int getGent() {
        return gent;
    }

    public void setGent(int gent) {
        this.gent = gent;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public String getUid() {
        return uid;
    }
}
