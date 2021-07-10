package com.hcdc.xncovid.model;

public class GroupedUserInfo {

    private String uid;

    private boolean isOnline;

    private String fullname;


    private String birthYear;


    private String phone;

    private  String cccd;


    private  int gent;

    private  String address;

    private String  district;

    private String  ward;

    private String  province;

    private String  districtID;

    private String  wardID;

    private String  provinceID;

    public GroupedUserInfo(String uid, boolean isOnline) {
        this.uid = uid;
        this.isOnline = isOnline;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrictID() {
        return districtID;
    }

    public void setDistrictID(String districtID) {
        this.districtID = districtID;
    }

    public String getWardID() {
        return wardID;
    }

    public void setWardID(String wardID) {
        this.wardID = wardID;
    }

    public String getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(String provinceID) {
        this.provinceID = provinceID;
    }
}
