package com.hcdc.xncovid;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class MainStaffActivity extends AppCompatActivity {
private LinearLayout layoutJoinTest, layoutListTest, layoutnewGroup, layoutlistGroup, layoutensession;
String sessionId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionId = null;
        setContentView(R.layout.activity_main_staff);
        Intent intent=this.getIntent();
        if(intent.getExtras() != null){
            sessionId = intent.getExtras().getString("xn_session");
        }

        setContentView(R.layout.activity_main_staff);

       layoutJoinTest =findViewById(R.id.joinTest);
        layoutListTest =findViewById(R.id.listTest);
        layoutnewGroup =findViewById(R.id.newGroup);
        layoutlistGroup =findViewById(R.id.listGroup);
        layoutensession =findViewById(R.id.endSession);

        //layoutJoinTest.setEnabled(false);
        layoutJoinTest.setBackground(getResources().getDrawable( R.drawable.rectangle_menu_disable));
       // layoutListTest.setEnabled(false);
        layoutListTest.setBackground(getResources().getDrawable( R.drawable.rectangle_menu_disable));
        //layoutnewGroup.setEnabled(false);
        layoutnewGroup.setBackground(getResources().getDrawable( R.drawable.rectangle_menu_disable));
        //layoutJoinTest.setEnabled(false);
        layoutlistGroup.setBackground(getResources().getDrawable( R.drawable.rectangle_menu_disable));

        if(sessionId != null && sessionId != "")
        {
            layoutnewGroup.setEnabled(true);
            layoutnewGroup.setBackground(getResources().getDrawable( R.drawable.rectangle_menu_enable));
            layoutnewGroup.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ScanSessionActivity.class);
                    intent.putExtra("scan_qr_type", 1);
                    startActivity(intent);
                }
            });

            layoutensession.setEnabled(true);
            layoutensession.setBackground(getResources().getDrawable( R.drawable.end_session_enable));
            layoutensession.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showMessage("You need to allow access to both the permissions",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), ScanSessionActivity.class);
                                    // intent.putExtra("xn_session", "");
                                    startActivity(intent);
                                }
                            });

                }
            });
        }else {
            layoutJoinTest.setEnabled(true);
            layoutJoinTest.setBackground(getResources().getDrawable( R.drawable.rectangle_menu_enable));
            layoutJoinTest.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ScanSessionActivity.class);
                    intent.putExtra("scan_qr_type", 0);
                    startActivity(intent);
                }
            });
        }
    }
    private void showMessage(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainStaffActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}