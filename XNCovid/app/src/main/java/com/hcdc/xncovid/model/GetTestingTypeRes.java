package com.hcdc.xncovid.model;

import com.hcdc.xncovid.util.APIResponse;

public class GetTestingTypeRes extends APIResponse {
    public KeyValue[] TestingTypes;
    public Reason[] Reasons;
    public KeyValue[] TestingObjects;
}
