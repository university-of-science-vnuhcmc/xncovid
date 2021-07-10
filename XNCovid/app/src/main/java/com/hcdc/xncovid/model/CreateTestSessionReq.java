package com.hcdc.xncovid.model;

import com.hcdc.xncovid.util.APIRequest;

public class CreateTestSessionReq extends APIRequest {
    public String SessionName;
    public String Purpose;
    public String TestingDate;
    public String FullLocation;
    public String ApartmentNo;
    public String StreetName;
    public long WardID;
    public long DistrictID;
    public long ProvinceID;
    public String Note;
}
