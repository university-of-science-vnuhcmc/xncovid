package com.hcdc.xncovid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hcdc.xncovid.model.GetStaffConfigReq;
import com.hcdc.xncovid.model.GetStaffConfigRes;
import com.hcdc.xncovid.model.UserInfo;
import com.hcdc.xncovid.util.Caller;
import com.hcdc.xncovid.util.ICallback;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            UserInfo userInfo = ((MyApplication)getApplication()).getUserInfo();
            if(userInfo == null){
                getUserInfo();
                userInfo = ((MyApplication)getApplication()).getUserInfo();
                if(userInfo == null){
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    return;
                }
            }

            if(userInfo.Role.equals("Leader")){
                Intent intent = new Intent(this, MainLeaderActivity.class);
                startActivity(intent);
                finish();
            } else {
                getStaffConfig();
                Intent intent = new Intent(this, MainStaffActivity.class);
                startActivity(intent);
                finish();
            }
        } catch (Exception ex){
            Log.w("MainActivity", ex.toString());
            new android.app.AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
    private void getStaffConfig(){
        Caller caller = new Caller();
        GetStaffConfigReq req = new GetStaffConfigReq();
        caller.call(this, "getstaffconfig", req, GetStaffConfigRes.class, new ICallback() {
            @Override
            public void callback(Object response) {
                GetStaffConfigRes res = (GetStaffConfigRes) response;
                if(res.ReturnCode != 1){
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("Lỗi: " + res.ReturnCode)
                            .setNegativeButton(android.R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }
                MyApplication myapp = ((MyApplication) getApplication());
                myapp.setDomain(res.Domain);
                myapp.setForm(res.Form);
                myapp.setUrl(res.Url);
                myapp.setId(res.Id);
            }
        }, null, Request.Method.POST);
    }
    private void getUserInfo(){
        try {
            FileInputStream fis = new FileInputStream(this.getFilesDir() + "/userinfo");
            InputStreamReader inputStreamReader =
                    new InputStreamReader(fis);
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line).append('\n');
                    line = reader.readLine();
                }
            } catch (IOException e) {
                // Error occurred when opening raw file for reading.
            } finally {
                String strUserInfo = stringBuilder.toString();
                if(strUserInfo == null || strUserInfo == ""){
                    return;
                }
                UserInfo userInfo;
                try {
                    userInfo = new Gson().fromJson(strUserInfo, UserInfo.class);
                } catch (JsonSyntaxException ex){
                    return;
                }
                ((MyApplication) this.getApplication()).setUserInfo(userInfo);
            }
        } catch (FileNotFoundException ex){
        }
    }
}