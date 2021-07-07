package com.hcdc.xncovid;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;



public class SessionInfoActivity extends AppCompatActivity {
    String xn_session;
    private TextView tenphien;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=this.getIntent();
        xn_session = getIntent().getExtras().getString("xn_session");
        setContentView(R.layout.activity_session_info);
        tenphien=(TextView) findViewById(R.id.xn_covid19_);
        tenphien.setText(xn_session);
    }

}