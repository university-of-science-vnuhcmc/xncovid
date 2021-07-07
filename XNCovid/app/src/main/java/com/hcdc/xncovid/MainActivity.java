package com.hcdc.xncovid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

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
        // call CheckLogin api
        boolean isLeader = false;
        if(isLeader){
            setContentView(R.layout.activity_main);
        } else {
            setContentView(R.layout.activity_main_staff);
        }
        setTitle("Hồ Công Hoài");
        CardView createTest = findViewById(R.id.createTest);
        createTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setTitle("Hồ Công Hoài 1");
            }
        });
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