package com.hcdc.xncovid;

public interface IDatePicker {
    public int getYear();
    public int getMonth();
    public int getDay();
    public void setDate(int year, int month, int day);
}
