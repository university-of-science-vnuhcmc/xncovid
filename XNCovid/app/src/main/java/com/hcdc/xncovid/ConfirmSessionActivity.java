package com.hcdc.xncovid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import com.hcdc.xncovid.model.KeyValue;
import com.hcdc.xncovid.model.LocateInfor;
import com.hcdc.xncovid.model.Reason;
import com.hcdc.xncovid.model.Session;
import com.hcdc.xncovid.util.Caller;
import com.hcdc.xncovid.util.ICallback;

import java.text.SimpleDateFormat;

public class ConfirmSessionActivity extends AppCompatActivity {
    private View mLoading;
    private KeyValue type;
    private KeyValue target1;
    private Reason cause2;
    private KeyValue target2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_confirm_session);
            setUIRef();
            Bundle bundle = getIntent().getExtras();
            String sessionName = bundle.getString("SessionName");
            String address = bundle.getString("Address");
            String note = bundle.getString("Note");
            Integer year = bundle.getInt("Year");
            Integer month = bundle.getInt("Month");
            Integer day = bundle.getInt("Day");
            Integer hour = bundle.getInt("Hour");
            Integer minute = bundle.getInt("Minute");
            LocateInfor province = new Gson().fromJson(bundle.getString("Province"), LocateInfor.class);
            LocateInfor district = new Gson().fromJson(bundle.getString("District"), LocateInfor.class);
            LocateInfor ward = new Gson().fromJson(bundle.getString("Ward"), LocateInfor.class);
            type = new Gson().fromJson(bundle.getString("Type"), KeyValue.class);
            target1 = new Gson().fromJson(bundle.getString("Target1"), KeyValue.class);
            cause2 = new Gson().fromJson(bundle.getString("Cause2"), Reason.class);
            target2 = new Gson().fromJson(bundle.getString("Target2"), KeyValue.class);

            TextView viewSessionName = findViewById(R.id.sessionName);
            viewSessionName.setText(sessionName);
            TextView viewAddress = findViewById(R.id.address);
            viewAddress.setText(address);
            TextView viewCause = findViewById(R.id.cause1);
            viewCause.setText(note);
            TextView viewRelativeTarget = findViewById(R.id.relativeTarget);
            viewRelativeTarget.setText(note);
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
            TextView viewType = findViewById(R.id.type);
            viewType.setText(type.Name);
            if(type.ID == 1){
                findViewById(R.id.type1).setVisibility(View.VISIBLE);
                findViewById(R.id.type2).setVisibility(View.GONE);
                TextView viewTarget1 = findViewById(R.id.target1);
                viewTarget1.setText(target1.Name);
            } else {
                findViewById(R.id.type1).setVisibility(View.GONE);
                findViewById(R.id.type2).setVisibility(View.VISIBLE);
                TextView viewCause2 = findViewById(R.id.cause2);
                viewCause2.setText(cause2.Name);
                TextView viewTarget2 = findViewById(R.id.target2);
                viewTarget2.setText(target2.Name);
            }

            LinearLayout next = findViewById(R.id.next);
            next.setOnClickListener(new View.OnClickListener() {
                public void onClick (View v) {
                    try {
                        CreateTestSessionReq req = new CreateTestSessionReq();
                        req.SessionName = sessionName;
                        req.Note = note;
                        req.TestingDate = String.format("%04d%02d%02d%02d%02d", year, month, day, hour, minute);
                        req.FullLocation = address + ", " + ward.Name + ", " + district.Name + ", " + province.Name;
                        req.ApartmentNo = address;
                        req.WardID = ward.ID;
                        req.DistrictID = district.ID;
                        req.ProvinceID = province.ID;
                        req.AccountID = ((MyApplication)getApplication()).getUserInfo().AccountID;
                        req.CovidTestingSessionTypeID = type.ID;
                        if(type.ID == 1){
                            req.CovidTestingSessionObjectID = target1.ID;
                        } else {
                            req.DesignatedReasonID = cause2.ID;
                            req.CovidTestingSessionObjectID = target2.ID;
                        }
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

    private void createSession(CreateTestSessionReq req){
        Caller caller = new Caller();
        showLoading();
        caller.call(this, "createtestsession", req, CreateTestSessionRes.class, new ICallback() {
            @Override
            public void callback(Object response) {
                try {
                    hideLoading();
                    CreateTestSessionRes res = (CreateTestSessionRes) response;
                    if(res.ReturnCode != 1){
                        new AlertDialog.Builder(ConfirmSessionActivity.this)
                                .setMessage("Lỗi: " + res.ReturnCode)
                                .setNegativeButton(android.R.string.ok, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return;
                    }
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    Intent intent = new Intent(ConfirmSessionActivity.this, QRSessionActivity.class);
                    Session session = new Session();
                    session.SessionID = res.SessionID;
                    session.SessionName = req.SessionName;
                    session.Address = req.FullLocation;
                    session.TestingDate = req.TestingDate;
                    session.CovidTestingSessionTypeID = type.ID;
                    session.CovidTestingSessionTypeName = type.Name;
                    session.Account = ((MyApplication)getApplication()).getUserInfo().Name;
                    session.Purpose = req.Note;
                    session.CovidTestingSessionObjectName = type.ID == 1 ? target1.Name : target2.Name;
                    session.DesignatedReasonName = type.ID == 1 ? "" : cause2.Name;

                    intent.putExtra("Session", new Gson().toJson(session));
                    startActivity(intent);
                    finish();
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
    private void setUIRef()
    {
        //Create a Instance of the Loading Layout
        mLoading = findViewById(R.id.my_loading_layout);
    }

    private void showLoading()
    {
        /*Call this function when you want progress dialog to appear*/
        if (mLoading != null)
        {
            mLoading.setVisibility(View.VISIBLE);
        }
    }

    private void hideLoading()
    {
        /*Call this function when you want progress dialog to disappear*/
        if (mLoading != null)
        {
            mLoading.setVisibility(View.GONE);
        }
    }

    public void back(View v){
        onBackPressed();
    }
}