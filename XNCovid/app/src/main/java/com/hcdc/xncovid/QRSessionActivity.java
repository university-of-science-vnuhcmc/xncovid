package com.hcdc.xncovid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class QRSessionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrsession);
        Bundle bundle = getIntent().getExtras();
        String sessionName = bundle.getString("SessionName");
        Long sessionID = bundle.getLong("SessionID");
        isNew = bundle.getBoolean("IsNew");
    }
    private boolean isNew;
    @Override
    public void onBackPressed() {
        if(!isNew){
            super.onBackPressed();
        }
    }
}