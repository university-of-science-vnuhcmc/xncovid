package com.hcdc.xncovid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hcdc.xncovid.model.LocateInfor;

public class ConfirmSessionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_session);
        Bundle bundle = getIntent().getExtras();
        String sessionName = bundle.getString("SessionName");
        String address = bundle.getString("Address");
        String cause = bundle.getString("Cause");
        Integer year = bundle.getInt("Year");
        Integer month = bundle.getInt("Month");
        Integer day = bundle.getInt("Day");
        Integer hour = bundle.getInt("Hour");
        Integer minute = bundle.getInt("Minute");
        LocateInfor province = new Gson().fromJson(bundle.getString("Province"), LocateInfor.class);
        LocateInfor district = new Gson().fromJson(bundle.getString("District"), LocateInfor.class);
        LocateInfor ward = new Gson().fromJson(bundle.getString("Ward"), LocateInfor.class);

        TextView viewSessionName = findViewById(R.id.sessionName);
        viewSessionName.setText(sessionName);
        TextView viewAddress = findViewById(R.id.address);
        viewAddress.setText(address);
        TextView viewCause = findViewById(R.id.cause);
        viewCause.setText(cause);
        TextView chooseTime = findViewById(R.id.chooseTime);
        chooseTime.setText(String.format("%02d:%02d", hour, minute));
        TextView chooseDate = findViewById(R.id.chooseDate);
        chooseDate.setText(String.format("%02d/%02d/%04d", day, month, year));
        TextView viewProvince = findViewById(R.id.province);
        viewProvince.setText(province.Name);
        TextView viewDistrict = findViewById(R.id.district);
        viewDistrict.setText(district.Name);
        TextView viewWard = findViewById(R.id.ward);
        viewWard.setText(ward.Name);
    }
}