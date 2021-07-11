package com.hcdc.xncovid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.hcdc.xncovid.model.GetLocateReq;
import com.hcdc.xncovid.model.GetLocateRes;
import com.hcdc.xncovid.model.LocateInfor;
import com.hcdc.xncovid.util.Caller;
import com.hcdc.xncovid.util.ICallback;

public class CreateSessionActivity extends AppCompatActivity implements IDatePicker, ITimePicker {
    private LocateInfor province;
    private LocateInfor district;
    private LocateInfor ward;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_session);

            Spinner spinnerProvince = findViewById(R.id.province);
            Spinner spinnerDistrict = findViewById(R.id.district);
            Spinner spinnerWard = findViewById(R.id.ward);
            getLocateInfor("", spinnerProvince);
            spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    province = (LocateInfor)parent.getItemAtPosition(position);
                    getLocateInfor(province.Code, spinnerDistrict);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    district = (LocateInfor)parent.getItemAtPosition(position);
                    getLocateInfor(district.Code, spinnerWard);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            spinnerWard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ward = (LocateInfor)parent.getItemAtPosition(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (Exception ex){
            Log.w("CreateSessionActivity", ex.toString());
            new android.app.AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    private void getLocateInfor(String code, Spinner spinner){
        Caller caller = new Caller();
        GetLocateReq req = new GetLocateReq();
        req.Value = code;
        caller.call(this, "getlocate", req, GetLocateRes.class, new ICallback() {
            @Override
            public void callback(Object response) {
                GetLocateRes res = (GetLocateRes) response;
                if(res.ReturnCode != 1){
                    new AlertDialog.Builder(CreateSessionActivity.this)
                            .setMessage("Lỗi: " + res.ReturnCode)
                            .setNegativeButton(android.R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }
                ArrayAdapter<LocateInfor> adapter = new ArrayAdapter(CreateSessionActivity.this,
                        R.layout.spinner_item, res.LocateInfors);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }
        }, null, Request.Method.POST);
    }

    public void createSession(View v) {
        try {
            EditText sessionName = findViewById(R.id.sessionName);
            EditText address = findViewById(R.id.address);
            EditText cause = findViewById(R.id.cause);
            TextView time = findViewById(R.id.chooseTime);
            TextView date = findViewById(R.id.chooseDate);

            boolean valid = true;
            if(sessionName.getText().toString().trim().length() == 0){
                sessionName.setError("Thiếu tên phiên.");
                valid = false;
            }
            if(address.getText().toString().trim().length() == 0){
                address.setError("Thiếu địa chỉ cụ thể.");
                valid = false;
            }
            if(cause.getText().toString().trim().length() == 0){
                cause.setError("Thiếu lý do.");
                valid = false;
            }
            if(time.getText().length() == 0){
                time.setError("Thiếu thời gian.");
                valid = false;
            }
            if(date.getText().length() == 0){
                date.setError("Thiếu ngày tháng.");
                valid = false;
            }
            if(province == null){
                new AlertDialog.Builder(this)
                        .setMessage("Vui lòng chọn Tỉnh/Thành phố.")
                        .setNegativeButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                valid = false;
            }
            if(district == null){
                new AlertDialog.Builder(this)
                        .setMessage("Vui lòng chọn Quận/Huyện.")
                        .setNegativeButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                valid = false;
            }
            if(ward == null){
                new AlertDialog.Builder(CreateSessionActivity.this)
                        .setMessage("Vui lòng chọn Xã/Phường.")
                        .setNegativeButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                valid = false;
            }
            if(!valid){
                return;
            }

            Intent intent = new Intent(this, ConfirmSessionActivity.class);
            intent.putExtra("SessionName", sessionName.getText().toString());
            intent.putExtra("Address", address.getText().toString());
            intent.putExtra("Cause", cause.getText().toString());
            intent.putExtra("Year", getYear());
            intent.putExtra("Month", getMonth());
            intent.putExtra("Day", getDay());
            intent.putExtra("Hour", getHour());
            intent.putExtra("Minute", getMinute());
            intent.putExtra("Province", new Gson().toJson(province));
            intent.putExtra("District", new Gson().toJson(district));
            intent.putExtra("Ward", new Gson().toJson(ward));
            startActivity(intent);
        } catch (Exception ex){
            Log.w("createSession", ex.toString());
            new android.app.AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    public void showTimePickerDialog(View v) {
        try {
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getSupportFragmentManager(), "timePicker");
        } catch (Exception ex){
            Log.w("showTimePickerDialog", ex.toString());
            new android.app.AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
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
        try {
            this.year = year;
            this.month = month;
            this.day = day;
            TextView chooseDate = findViewById(R.id.chooseDate);
            chooseDate.setText(String.format("%02d/%02d/%04d", day, month, year));
        } catch (Exception ex){
            Log.w("setDate", ex.toString());
            new android.app.AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    public void showDatePickerDialog(View v) {
        try {
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getSupportFragmentManager(), "datePicker");
        } catch (Exception ex){
            Log.w("showDatePickerDialog", ex.toString());
            new android.app.AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
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
        try {
            this.hour = hour;
            this.minute = minute;
            TextView chooseTime = findViewById(R.id.chooseTime);
            chooseTime.setText(String.format("%02d:%02d", hour, minute));
        } catch (Exception ex){
            Log.w("setTime", ex.toString());
            new android.app.AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}