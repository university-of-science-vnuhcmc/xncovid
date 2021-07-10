package com.hcdc.xncovid.model;

import com.hcdc.xncovid.util.APIRequest;

import java.util.ArrayList;

public class GroupTestReq extends APIRequest {
    public String CovidSpecimenCode ;
    public long CovidTestingSessionID;
    public String SpecimenAmount;
    public long AccountID;
    public String Note ;
    public ArrayList<CitizenInfor> CitizenInfor;
}


