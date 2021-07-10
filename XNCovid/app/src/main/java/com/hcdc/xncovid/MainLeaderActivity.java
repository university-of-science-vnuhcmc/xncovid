package com.hcdc.xncovid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.hcdc.xncovid.model.EndTestSessionReq;
import com.hcdc.xncovid.model.EndTestSessionRes;
import com.hcdc.xncovid.model.GetStaffConfigReq;
import com.hcdc.xncovid.model.GetStaffConfigRes;
import com.hcdc.xncovid.model.SessionInfo;
import com.hcdc.xncovid.util.Caller;
import com.hcdc.xncovid.util.ICallback;

public class MainLeaderActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_leader);
        MyApplication myapp = (MyApplication) getApplication();
        TextView nameView = findViewById(R.id.name);
        nameView.setText(myapp.getUserInfo().Name);
        if(sessionInfo != null){
            findViewById(R.id.createTest).setBackground(getResources().getDrawable( R.drawable.rectangle_menu_disable));
            findViewById(R.id.sessionInfo).setBackground(getResources().getDrawable( R.drawable.rectangle_main_info));
            findViewById(R.id.scanQR).setBackground(getResources().getDrawable( R.drawable.button_scan_qr_session_enable));
            findViewById(R.id.endSession).setBackground(getResources().getDrawable( R.drawable.end_session_enable));
        }
    }
    public void signOut(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn muốn đăng xuất?")
                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                        intent.putExtra("isLogout", true);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Hủy", null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }
    public void createSession(View v){
        if(sessionInfo != null){
            return;
        }
        Intent intent = new Intent(this, CreateSessionActivity.class);
        startActivity(intent);
    }
    public void showQR(View v){
        if(sessionInfo == null){
            return;
        }
        Intent intent = new Intent(this, QRSessionActivity.class);
        intent.putExtra("SessionName", sessionInfo.Name);
        intent.putExtra("SessionID", sessionInfo.ID);
        intent.putExtra("IsNew", false);
        startActivity(intent);
    }
    private SessionInfo sessionInfo;
    public void endSession(View v){
        if(sessionInfo == null){
            return;
        }
        Caller caller = new Caller();
        EndTestSessionReq req = new EndTestSessionReq();
        req.CovidTestingSessionID = sessionInfo.ID;
        caller.call(this, "endtestsession4lead", req, EndTestSessionRes.class, new ICallback() {
            @Override
            public void callback(Object response) {
                EndTestSessionRes res = (EndTestSessionRes) response;
                if(res.returnCode != 1){
                    new androidx.appcompat.app.AlertDialog.Builder(MainLeaderActivity.this)
                            .setMessage("Lỗi: " + res.returnCode)
                            .setNegativeButton(android.R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }
                sessionInfo = null;
            }
        }, null, Request.Method.POST);
    }
}