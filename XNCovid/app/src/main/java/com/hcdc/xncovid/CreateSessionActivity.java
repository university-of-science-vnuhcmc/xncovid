package com.hcdc.xncovid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.hcdc.xncovid.model.GetLocateReq;
import com.hcdc.xncovid.model.GetLocateRes;
import com.hcdc.xncovid.model.GetTestingTypeReq;
import com.hcdc.xncovid.model.GetTestingTypeRes;
import com.hcdc.xncovid.model.KeyValue;
import com.hcdc.xncovid.model.LocateInfor;
import com.hcdc.xncovid.model.Reason;
import com.hcdc.xncovid.util.Caller;
import com.hcdc.xncovid.util.ICallback;

public class CreateSessionActivity extends AppCompatActivity implements IDatePicker, ITimePicker {
    private LocateInfor province;
    private LocateInfor district;
    private LocateInfor ward;

    private KeyValue type;
    private KeyValue target1;
    private Reason cause2;
    private KeyValue target2;

    private boolean isHourPadLeft = false;
    private boolean isMinutePadLeft = false;
    private boolean isDayPadLeft = false;
    private boolean isMonthPadLeft = false;

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
            getTestingType();
            Spinner spinnerType = findViewById(R.id.type);
            spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    type = (KeyValue)parent.getItemAtPosition(position);
                    if(type.ID == 1){
                        findViewById(R.id.type1).setVisibility(View.VISIBLE);
                        findViewById(R.id.type2).setVisibility(View.GONE);
                    } else {
                        findViewById(R.id.type2).setVisibility(View.VISIBLE);
                        findViewById(R.id.type1).setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            Spinner spinnerTarget1 = findViewById(R.id.target1);
            spinnerTarget1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    target1 = (KeyValue)parent.getItemAtPosition(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            Spinner spinnerCause2 = findViewById(R.id.cause2);
            Spinner spinnerTarget2 = findViewById(R.id.target2);
            spinnerCause2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    cause2 = (Reason) parent.getItemAtPosition(position);
                    ArrayAdapter<KeyValue> adapter = new ArrayAdapter(CreateSessionActivity.this,
                            R.layout.spinner_item, cause2.Objects);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTarget2.setAdapter(adapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            spinnerTarget2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    target2 = (KeyValue)parent.getItemAtPosition(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            EditText etHour = findViewById(R.id.hour);
            EditText etMinute = findViewById(R.id.minute);
            EditText etDay = findViewById(R.id.day);
            EditText etMonth = findViewById(R.id.month);
            EditText etYear = findViewById(R.id.year);
            etHour.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        int n = Integer.parseInt(s.toString());
                        if(n < 0 || n > 23){
                            etHour.setError("Giờ không hợp lệ.");
                            return;
                        }
                        if(s.length() == 2 && !isHourPadLeft){
                            etMinute.requestFocus();
                        }
                        isHourPadLeft = false;
                    } catch (Exception ex){

                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            etHour.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        try {
                            if(etHour.getText().length() == 0){
                                return;
                            }
                            int n = Integer.parseInt(etHour.getText().toString());
                            if(n < 0 || n > 23){
                                etHour.setError("Giờ không hợp lệ.");
                            }
                            isHourPadLeft = true;
                            etHour.setText(String.format("%02d", n));
                        } catch (Exception ex){

                        }
                    }
                }
            });
            etMinute.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        int n = Integer.parseInt(s.toString());
                        if(n < 0 || n > 59){
                            etMinute.setError("Phút không hợp lệ.");
                            return;
                        }
                        if(s.length() == 2 && !isMinutePadLeft){
                            etDay.requestFocus();
                        }
                        isMinutePadLeft = false;
                    } catch (Exception ex){

                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            etMinute.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        try {
                            if(etMinute.getText().length() == 0){
                                return;
                            }
                            int n = Integer.parseInt(etMinute.getText().toString());
                            if(n < 0 || n > 59){
                                etMinute.setError("Phút không hợp lệ.");
                            }
                            isMinutePadLeft = true;
                            etMinute.setText(String.format("%02d", n));
                        } catch (Exception ex){

                        }
                    }
                }
            });
            etDay.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        int n = Integer.parseInt(s.toString());
                        if(s.length() >= 2 && (n < 1 || n > 31)){
                            etDay.setError("Ngày không hợp lệ.");
                            return;
                        }
                        if(s.length() == 2 && !isDayPadLeft){
                            etMonth.requestFocus();
                        }
                        isDayPadLeft = false;
                    } catch (Exception ex){

                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            etDay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        try {
                            int n = Integer.parseInt(etDay.getText().toString());
                            if(n < 1 || n > 31){
                                etDay.setError("Ngày không hợp lệ.");
                            }
                            isDayPadLeft = true;
                            etDay.setText(String.format("%02d", n));
                        } catch (Exception ex){

                        }
                    }
                }
            });
            etMonth.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        int n = Integer.parseInt(s.toString());
                        if(s.length() >= 2 && (n < 1 || n > 12)){
                            etMonth.setError("Tháng không hợp lệ.");
                            return;
                        }
                        if(s.length() == 2 && !isMonthPadLeft){
                            etYear.requestFocus();
                        }
                        isMonthPadLeft = false;
                    } catch (Exception ex){

                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            etMonth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        try {
                            int n = Integer.parseInt(etMonth.getText().toString());
                            if(n < 1 || n > 12){
                                etMonth.setError("Tháng không hợp lệ.");
                            }
                            isMonthPadLeft = true;
                            etMonth.setText(String.format("%02d", n));
                        } catch (Exception ex){

                        }
                    }
                }
            });
            etYear.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        int n = Integer.parseInt(s.toString());
                        if(s.length() >= 4 && n < 2021){
                            etYear.setError("Năm không hợp lệ.");
                            return;
                        }
                    } catch (Exception ex){

                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            etYear.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        try {
                            int n = Integer.parseInt(etYear.getText().toString());
                            if(n < 2021){
                                etYear.setError("Năm không hợp lệ.");
                            }
                            etYear.setText(String.format("%04d", n));
                        } catch (Exception ex){

                        }
                    }
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
    private void getTestingType(){
        Caller caller = new Caller();
        GetTestingTypeReq req = new GetTestingTypeReq();
        caller.call(this, "gettestingtype", req, GetTestingTypeRes.class, new ICallback() {
            @Override
            public void callback(Object response) {
                GetTestingTypeRes res = (GetTestingTypeRes) response;
                if(res.ReturnCode != 1){
                    new AlertDialog.Builder(CreateSessionActivity.this)
                            .setMessage("Lỗi: " + res.ReturnCode)
                            .setNegativeButton(android.R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }
                Spinner spinnerType = findViewById(R.id.type);
                ArrayAdapter<KeyValue> adapter = new ArrayAdapter(CreateSessionActivity.this,
                        R.layout.spinner_item, res.TestingTypes);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerType.setAdapter(adapter);

                Spinner spinnerTarget1 = findViewById(R.id.target1);
                ArrayAdapter<KeyValue> adapter1 = new ArrayAdapter(CreateSessionActivity.this,
                        R.layout.spinner_item, res.TestingObjects);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTarget1.setAdapter(adapter1);

                Spinner spinnerCause2 = findViewById(R.id.cause2);
                ArrayAdapter<Reason> adapter2 = new ArrayAdapter(CreateSessionActivity.this,
                        R.layout.spinner_item, res.Reasons);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCause2.setAdapter(adapter2);
            }
        }, null, Request.Method.POST);
    }
    private int LAUNCH_SECOND_ACTIVITY = 1;
    public void createSession(View v) {
        try {
            EditText sessionName = findViewById(R.id.sessionName);
            EditText address = findViewById(R.id.address);

            boolean valid = true;
            if(sessionName.getText().toString().trim().length() == 0){
                sessionName.setError("Thiếu tên phiên.");
                valid = false;
            }
            if(address.getText().toString().trim().length() == 0){
                address.setError("Thiếu địa chỉ cụ thể.");
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
            if(type == null){
                new AlertDialog.Builder(CreateSessionActivity.this)
                        .setMessage("Vui lòng chọn Loại xét nghiệm.")
                        .setNegativeButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                valid = false;
            }
            if(type.ID == 1){
                if(target1 == null){
                    new AlertDialog.Builder(CreateSessionActivity.this)
                            .setMessage("Vui lòng chọn Đối tượng.")
                            .setNegativeButton(android.R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    valid = false;
                }
            } else {
                if(cause2 == null){
                    new AlertDialog.Builder(CreateSessionActivity.this)
                            .setMessage("Vui lòng chọn Lý do.")
                            .setNegativeButton(android.R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    valid = false;
                }
                if(target2 == null){
                    new AlertDialog.Builder(CreateSessionActivity.this)
                            .setMessage("Vui lòng chọn Đối tượng.")
                            .setNegativeButton(android.R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    valid = false;
                }
            }
            if(!valid){
                return;
            }

            hour = Integer.parseInt(((EditText)findViewById(R.id.hour)).getText().toString());
            minute = Integer.parseInt(((EditText)findViewById(R.id.minute)).getText().toString());
            day = Integer.parseInt(((EditText)findViewById(R.id.day)).getText().toString());
            month = Integer.parseInt(((EditText)findViewById(R.id.month)).getText().toString());
            year = Integer.parseInt(((EditText)findViewById(R.id.year)).getText().toString());

            Intent intent = new Intent(this, ConfirmSessionActivity.class);
            intent.putExtra("SessionName", sessionName.getText().toString());
            intent.putExtra("Address", address.getText().toString());
            if(type.ID == 1){
                intent.putExtra("Note", ((EditText)findViewById(R.id.cause1)).getText().toString());
            } else {
                intent.putExtra("Note", ((EditText)findViewById(R.id.relativeTarget)).getText().toString());
            }
            intent.putExtra("Year", getYear());
            intent.putExtra("Month", getMonth());
            intent.putExtra("Day", getDay());
            intent.putExtra("Hour", getHour());
            intent.putExtra("Minute", getMinute());
            intent.putExtra("Province", new Gson().toJson(province));
            intent.putExtra("District", new Gson().toJson(district));
            intent.putExtra("Ward", new Gson().toJson(ward));
            intent.putExtra("Type", new Gson().toJson(type));
            intent.putExtra("Target1", new Gson().toJson(target1));
            intent.putExtra("Cause2", new Gson().toJson(cause2));
            intent.putExtra("Target2", new Gson().toJson(target2));
            startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);
        } catch (Exception ex){
            Log.w("createSession", ex.toString());
            new android.app.AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                finish();
            }
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