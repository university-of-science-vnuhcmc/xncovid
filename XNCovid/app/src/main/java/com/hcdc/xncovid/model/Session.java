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
    public String Purpose; //XN Giam sat = Ly do, XN chi dinh = doi tuong lien quan neu co
    public String Account;
    public  String CovidTestingSessionTypeName; //ten loai
    public  String CovidTestingSessionObjectName; // XN chi dinh = doi tuong
    public  String DesignatedReasonName; // XN chi dinh = ly do
    public  long CovidTestingSessionTypeID; // 1 là giám sát, 2 là chỉ định
    public  long CovidTestingSessionObjectID; // ID doi tuong cu xn chi dinh
    public  long DesignatedReasonID; // ID ly do cua XN chi dinh

    public  Date getTestingDate(){
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmm");
        Date parsedDate = null;
        try {
            parsedDate = inputFormat.parse(TestingDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  parsedDate;
    }
}
