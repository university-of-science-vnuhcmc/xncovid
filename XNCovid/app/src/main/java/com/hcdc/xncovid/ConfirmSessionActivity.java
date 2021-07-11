package com.hcdc.xncovid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.hcdc.xncovid.model.CreateTestSessionReq;
import com.hcdc.xncovid.model.CreateTestSessionRes;
import com.hcdc.xncovid.model.LocateInfor;
import com.hcdc.xncovid.util.Caller;
import com.hcdc.xncovid.util.ICallback;

public class ConfirmSessionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
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

            LinearLayout next = findViewById(R.id.next);
            next.setOnClickListener(new View.OnClickListener() {
                public void onClick (View v) {
                    try {
                        CreateTestSessionReq req = new CreateTestSessionReq();
                        req.SessionName = sessionName;
                        req.Note = cause;
                        req.TestingDate = String.format("%04d%02d%02d%02d%02d", year, month, day, hour, minute);
                        req.FullLocation = address + ", " + ward.Name + ", " + district.Name + ", " + province.Name;
                        req.ApartmentNo = address;
                        req.WardID = ward.ID;
                        req.DistrictID = district.ID;
                        req.ProvinceID = province.ID;
                        createSession(req);
                    } catch (Exception ex){
                        Log.w("next.setOnClickListener", ex.toString());
                        new android.app.AlertDialog.Builder(ConfirmSessionActivity.this)
                                .setMessage("Lỗi xử lý.")
                                .setNegativeButton("OK", null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }
            });
        } catch (Exception ex){
            Log.w("ConfirmSessionActivity", ex.toString());
            new android.app.AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
    private boolean flag = false;
    private void createSession(CreateTestSessionReq req){
        if(flag){
            return;
        } else {
            flag = true;
        }
        Caller caller = new Caller();
        caller.call(this, "createtestsession", req, CreateTestSessionRes.class, new ICallback() {
            @Override
            public void callback(Object response) {
                try {
                    flag = false;
                    CreateTestSessionRes res = (CreateTestSessionRes) response;
                    if(res.ReturnCode != 1){
                        new AlertDialog.Builder(ConfirmSessionActivity.this)
                                .setMessage("Lỗi: " + res.ReturnCode)
                                .setNegativeButton(android.R.string.ok, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return;
                    }
                    Intent intent = new Intent(ConfirmSessionActivity.this, QRSessionActivity.class);
                    intent.putExtra("SessionName", req.SessionName);
                    intent.putExtra("SessionID", res.SessionID);
                    intent.putExtra("IsNew", true);
                    startActivity(intent);
                } catch (Exception ex){
                    Log.w("createSession", ex.toString());
                    new android.app.AlertDialog.Builder(ConfirmSessionActivity.this)
                            .setMessage("Lỗi xử lý.")
                            .setNegativeButton("OK", null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        }, null, Request.Method.POST);
    }
}