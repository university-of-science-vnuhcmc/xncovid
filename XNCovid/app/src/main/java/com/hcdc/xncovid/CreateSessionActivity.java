package com.hcdc.xncovid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.hcdc.xncovid.model.GetLocateReq;
import com.hcdc.xncovid.model.GetLocateRes;
import com.hcdc.xncovid.model.LocateInfor;
import com.hcdc.xncovid.util.APIResponse;
import com.hcdc.xncovid.util.Caller;
import com.hcdc.xncovid.util.ICallback;

public class CreateSessionActivity extends AppCompatActivity implements IDatePicker, ITimePicker {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);
        Spinner spinnerProvince = (Spinner) findViewById(R.id.province);
        getLocateInfor("", spinnerProvince);
        /*spinnerProvince.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                *//*LocateInfor item = allNews.get(position);
                String url = item.getUrl();
                Uri uri = Uri.parse(url);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));*//*
            }
        });*/
    }

    private void getLocateInfor(String code, Spinner spinner){
        Caller caller = new Caller();
        GetLocateReq req = new GetLocateReq();
        req.Value = code;caller.call(this, "getlocate", req, GetLocateRes.class, new ICallback() {
            @Override
            public void callback(APIResponse response) {
                GetLocateRes res = (GetLocateRes) response;
                ArrayAdapter<LocateInfor> adapter = new ArrayAdapter(getBaseContext(),
                        android.R.layout.simple_spinner_item, res.locateInfors);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }
        });
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
    private int year;
    @Override
    public int getYear() {
        return year;
    }

    private int month;
    @Override
    public int getMonth() {
        return month;
    }

    private int day;
    @Override
    public int getDay() {
        return day;
    }


    @Override
    public void setDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        TextView chooseDate = findViewById(R.id.chooseDate);
        chooseDate.setText(String.format("%02d/%02d/%04d", day, month, year));
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private int hour;
    @Override
    public int getHour() {
        return hour;
    }

    private int minute;
    @Override
    public int getMinute() {
        return minute;
    }

    @Override
    public void setTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        TextView chooseTime = findViewById(R.id.chooseTime);
        chooseTime.setText(String.format("%02d:%02d", hour, minute));
    }
}