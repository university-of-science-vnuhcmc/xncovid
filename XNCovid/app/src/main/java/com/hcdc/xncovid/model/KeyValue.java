package com.hcdc.xncovid.model;

import com.hcdc.xncovid.util.APIRequest;

public class KeyValue extends APIRequest {
    public long ID;
    public String Name;

    @Override
    public String toString() {
        return Name;
    }
}
