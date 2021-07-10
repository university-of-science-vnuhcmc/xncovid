package com.hcdc.xncovid.model;

public class SessionInfo {
    public long ID;
    public String Name;
    public LocateInfor Province;
    public LocateInfor District;
    public LocateInfor Ward;
    public String Address;
    public String FullAddress;
    public int Year;
    public int Month;
    public int Day;
    public int Hour;
    public int Minute;
    public String Cause;
    public UserInfo Leader;
    public UserInfo[] Participants;
}
