package com.hcdc.xncovid.model;

import java.util.Calendar;
import java.util.Date;

public class Session {
    public long SessionID;
    public String SessionName;
    public String ProvinceName;
    public String DistrictName;
    public String WardName;
    public String Address;
    public Date TestingDate;
    public String Purpose;
    public String Account;

    public String getFullAddress(){
        return Address + ", " + WardName + ", " + DistrictName +  ", " + ProvinceName;
    }
}
