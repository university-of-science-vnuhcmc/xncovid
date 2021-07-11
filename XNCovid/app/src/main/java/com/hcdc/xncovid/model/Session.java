package com.hcdc.xncovid.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Session {
    public long SessionID;
    public String SessionName;
    public String ProvinceName;
    public String DistrictName;
    public String WardName;
    public String Address;
    public String TestingDate;
    public String Purpose;
    public String Account;

    public  Date getTestingDate(){
        SimpleDateFormat inputFormat = new SimpleDateFormat("HHmmssddMMyyyy");
        Date parsedDate = null;
        try {
            parsedDate = inputFormat.parse(TestingDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  parsedDate;
    }
}
