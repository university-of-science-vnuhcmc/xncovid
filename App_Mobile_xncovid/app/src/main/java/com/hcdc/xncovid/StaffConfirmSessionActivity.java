package com.hcdc.xncovid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StaffConfirmSessionActivity extends AppCompatActivity {
String xn_session;
private TextView tenphien;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=this.getIntent();
        xn_session = getIntent().getExtras().getString("xn_session");
        setContentView(R.layout.activity_staffconfimxn);
        tenphien=(TextView) findViewById(R.id.xn_covid19_);
        tenphien.setText(xn_session);
    }

}
