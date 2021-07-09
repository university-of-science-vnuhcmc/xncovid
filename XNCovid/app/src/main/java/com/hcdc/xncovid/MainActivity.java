package com.hcdc.xncovid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hcdc.xncovid.model.UserInfo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        } else {
            Intent intent = new Intent(this, MainStaffActivity.class);
            startActivity(intent);
        }
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