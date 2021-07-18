package com.hcdc.xncovid.model;

import com.hcdc.xncovid.util.APIRequest;

public class Reason extends APIRequest {
    public long ID;
    public String Name;
    public KeyValue[] Objects;

    @Override
    public String toString() {
        return Name;
    }
}
