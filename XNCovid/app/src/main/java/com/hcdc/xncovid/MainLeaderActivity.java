package com.hcdc.xncovid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.hcdc.xncovid.model.CheckAccountReq;
import com.hcdc.xncovid.model.CheckAccountRes;
import com.hcdc.xncovid.model.EndTestSessionReq;
import com.hcdc.xncovid.model.EndTestSessionRes;
import com.hcdc.xncovid.model.GetStaffConfigReq;
import com.hcdc.xncovid.model.GetStaffConfigRes;
import com.hcdc.xncovid.model.SessionInfo;
import com.hcdc.xncovid.util.Caller;
import com.hcdc.xncovid.util.ICallback;
import com.hcdc.xncovid.util.Util;

import java.text.SimpleDateFormat;

public class MainLeaderActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_leader);
        MyApplication myapp = (MyApplication) getApplication();
        TextView nameView = findViewById(R.id.name);
        nameView.setText(myapp.getUserInfo().Name);
        getCurrentSession();
        if(errorFlag){
            findViewById(R.id.createTest).setBackground(getResources().getDrawable( R.drawable.rectangle_menu_disable));
        }
        if(sessionInfo != null){
            findViewById(R.id.createTest).setBackground(getResources().getDrawable( R.drawable.rectangle_menu_disable));
            findViewById(R.id.sessionInfo).setBackground(getResources().getDrawable( R.drawable.rectangle_main_info));
            findViewById(R.id.scanQR).setBackground(getResources().getDrawable( R.drawable.button_scan_qr_session_enable));
            findViewById(R.id.endSession).setBackground(getResources().getDrawable( R.drawable.end_session_enable));
            ((TextView)findViewById(R.id.sessionName)).setText(sessionInfo.SessionName);
            ((TextView)findViewById(R.id.location)).setText(sessionInfo.getFullAddress());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
            ((TextView)findViewById(R.id.time)).setText(timeFormat.format(sessionInfo.TestingDate));
            ((TextView)findViewById(R.id.cause)).setText(sessionInfo.Purpose);
            ((TextView)findViewById(R.id.numberStaff)).setText(sessionInfo.Participants == null ? 0 : sessionInfo.Participants.length);
        }
    }
    private boolean errorFlag = false;
    public void getCurrentSession(){
        Caller caller = new Caller();
        CheckAccountReq req = new CheckAccountReq();
        req.AccountID = ((MyApplication)getApplication()).getUserInfo().AccountID;
        caller.call(MainLeaderActivity.this, "checkaccount", req, CheckAccountRes.class, new ICallback() {
            @Override
            public void callback(Object response) {
                CheckAccountRes res = (CheckAccountRes) response;
                if(res.returnCode != 1 && res.returnCode != 0){
                    new AlertDialog.Builder(MainLeaderActivity.this)
                            .setMessage("Lỗi: " + res.returnCode)
                            .setNegativeButton(android.R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    errorFlag = true;
                    return;
                }
                if(res.returnCode == 0){
                    sessionInfo = null;
                } else {

                }
            }
        }, null, Request.Method.POST);
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
        if(sessionInfo != null || errorFlag){
            return;
        }
        Intent intent = new Intent(this, CreateSessionActivity.class);
        startActivity(intent);
    }
    public void showQR(View v){
        if(sessionInfo == null || errorFlag){
            return;
        }
        Intent intent = new Intent(this, QRSessionActivity.class);
        intent.putExtra("SessionName", sessionInfo.SessionName);
        intent.putExtra("SessionID", sessionInfo.ID);
        intent.putExtra("IsNew", false);
        startActivity(intent);
    }
    private SessionInfo sessionInfo;
    public void endSession(View v){
        if(sessionInfo == null || errorFlag){
            return;
        }
        String htmlcontent = String.format("Toàn bộ quá trình sẽ được lưu lại và toàn bộ (%d) nhân viên sẽ bị buộc thoát khỏi phiên xét nghiệm",
                sessionInfo.Participants == null ? 0 : sessionInfo.Participants.length);
        new Util().showMessage("Xác nhận kết thúc phiên xét nghiệm",
                sessionInfo.SessionName,
                htmlcontent,
                "Thoát",
                "Hủy",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Caller caller = new Caller();
                        EndTestSessionReq req = new EndTestSessionReq();
                        req.CovidTestingSessionID = sessionInfo.ID;
                        caller.call(MainLeaderActivity.this, "endtestsession4lead", req, EndTestSessionRes.class, new ICallback() {
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
                }, null, MainLeaderActivity.this);
    }
}