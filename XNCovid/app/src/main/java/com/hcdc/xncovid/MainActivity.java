package com.hcdc.xncovid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

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
        Intent intent = new Intent(this, MainStaffActivity.class);
        startActivity(intent);
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