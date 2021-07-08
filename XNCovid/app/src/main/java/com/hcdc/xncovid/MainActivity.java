package com.hcdc.xncovid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hcdc.xncovid.model.LoginReq;
import com.hcdc.xncovid.model.LoginRes;
import com.hcdc.xncovid.model.LoginUserRes;
import com.hcdc.xncovid.network.CoreCallBack;
import com.hcdc.xncovid.network.CoreNetworkProvider;
import com.hcdc.xncovid.util.APIRequest;
import com.hcdc.xncovid.util.APIResponse;
import com.hcdc.xncovid.util.Caller;
import com.hcdc.xncovid.util.ICallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String token = ((MyApplication) this.getApplication()).getToken();
        if(token == null || token == ""){
            getToken();
            token = ((MyApplication) this.getApplication()).getToken();
            if(token == ""){
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return;
            }
        }
        Caller caller = new Caller();
        LoginReq req = new LoginReq();
        req.email = "hoconghoai@gmail.com";
        req.tokenid = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjFiZjhhODRkM2VjZDc3ZTlmMmFkNWYwNmZmZDI2MDcwMWRkMDZkOTAiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIyMDIxNjQ3MzYyMDQtbGE0MjZrYWNudTZyOGF0azZuY3ZtNHR1bm5mbTBvNmcuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiIyMDIxNjQ3MzYyMDQtNmZ0bGluaWtxOXJwM21lbjVzYXA2dHA1ZWVycGU1N2guYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTgxNDcxMTkxMjU3NTg4MjQ5MjciLCJlbWFpbCI6ImhvY29uZ2hvYWlAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJIb8OgaSBI4buTIEPDtG5nIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hLS9BT2gxNEdqVkNhaUNfNS1sY3hYMVNmeFpEY3kxcWJ4YXNwZWRPVWNGa3c0ZVdRPXM5Ni1jIiwiZ2l2ZW5fbmFtZSI6Ikhvw6BpIiwiZmFtaWx5X25hbWUiOiJI4buTIEPDtG5nIiwibG9jYWxlIjoiZW4iLCJpYXQiOjE2MjU3MjYxMjcsImV4cCI6MTYyNTcyOTcyN30.L1OM9m3X3F67L1MyQ1ckpx6E5w8XewaSdCozoYJFUeSzfz7F4mxPc_ig_HpewjSASllVXnKiNlxW_6scr_6bfv43BMcrhQE1FG4y0wG_xXOWZTEjfgQT0s5bzPXXc7tA5cRJWt3fjH9flkmi1xVjp_vTWGWnhsA53LCrgby4g7LPCBpW5RmNKlivJJXZbfmhpHt8YaXgj4tE8Rqitbr_kSQKYkXB3lCGXYopEzg6bwR8QObZqVnk3f3ouBZnzU-8HTdP4YLpIRWvDrEyMqmvWSkp8mG_tmRsAlieTn03aNGO4WQ5UY6TEoZ6fzGkScYxZ50nxMNyPGV0EATcAc5l8g";
        caller.call(this, "login", req, LoginRes.class, new ICallback() {
            @Override
            public void callback(APIResponse response) {
                LoginRes res = (LoginRes) response;
            }
        });

        boolean isLeader = false;
        if(isLeader){
            Intent intent = new Intent(this, MainLeaderActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MainStaffActivity.class);
            startActivity(intent);
        }
    }
    private void getToken(){
        try {
            FileInputStream fis = new FileInputStream(this.getFilesDir() + "/token.txt");
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
                String token = stringBuilder.toString();
                ((MyApplication) this.getApplication()).setToken(token);
            }
        } catch (FileNotFoundException ex){
            ((MyApplication) this.getApplication()).setToken("");
        }
    }
}