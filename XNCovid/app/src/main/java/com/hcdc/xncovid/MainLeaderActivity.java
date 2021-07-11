package com.hcdc.xncovid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.hcdc.xncovid.adapter.StaffAdapter;
import com.hcdc.xncovid.model.CheckAccountReq;
import com.hcdc.xncovid.model.CheckAccountRes;
import com.hcdc.xncovid.model.EndTestSessionReq;
import com.hcdc.xncovid.model.EndTestSessionRes;
import com.hcdc.xncovid.model.GetStaffConfigReq;
import com.hcdc.xncovid.model.GetStaffConfigRes;
import com.hcdc.xncovid.model.LogoutReq;
import com.hcdc.xncovid.model.LogoutRes;
import com.hcdc.xncovid.model.SessionInfo;
import com.hcdc.xncovid.util.Caller;
import com.hcdc.xncovid.util.ICallback;
import com.hcdc.xncovid.util.Util;

import java.text.SimpleDateFormat;

public class MainLeaderActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main_leader);
            MyApplication myapp = (MyApplication) getApplication();
            TextView nameView = findViewById(R.id.name);
            nameView.setText(myapp.getUserInfo().Name);
            //getCurrentSession();
            if(errorFlag){
                findViewById(R.id.createTest).setBackground(getResources().getDrawable( R.drawable.rectangle_menu_disable));
            }
            if(sessionInfo != null){
                findViewById(R.id.createTest).setBackground(getResources().getDrawable( R.drawable.rectangle_menu_disable));
                findViewById(R.id.sessionInfo).setBackground(getResources().getDrawable( R.drawable.rectangle_main_info));
                findViewById(R.id.scanQR).setBackground(getResources().getDrawable( R.drawable.button_scan_qr_session_enable));
                findViewById(R.id.endSession).setBackground(getResources().getDrawable( R.drawable.end_session_enable));
                ((TextView)findViewById(R.id.sessionName)).setText(sessionInfo.SessionName);
                ((TextView)findViewById(R.id.location)).setText(sessionInfo.Address);
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                ((TextView)findViewById(R.id.time)).setText(timeFormat.format(sessionInfo.TestingDate));
                ((TextView)findViewById(R.id.cause)).setText(sessionInfo.Purpose);
                ((TextView)findViewById(R.id.numberStaff)).setText(sessionInfo.LstUser == null ? 0 : sessionInfo.LstUser.length);
                StaffAdapter adapter = new StaffAdapter(this, sessionInfo.LstUser);

                ListView listView = (ListView) findViewById(R.id.listStaff);
                listView.setAdapter(adapter);
            }
        } catch (Exception ex){
            Log.w("MainLeaderActivity", ex.toString());
            new AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
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
                try {
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
                } catch (Exception ex){
                    Log.w("MainLeaderActivity", ex.toString());
                    new AlertDialog.Builder(MainLeaderActivity.this)
                            .setMessage("Lỗi xử lý.")
                            .setNegativeButton("OK", null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        }, null, Request.Method.POST);
    }
    public void signOut(View v) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Bạn muốn đăng xuất?")
                    .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Caller caller = new Caller();
                            LogoutReq req = new LogoutReq();
                            caller.call(MainLeaderActivity.this, "logout", req, LogoutRes.class, new ICallback() {
                                @Override
                                public void callback(Object response) {
                                    try{
                                        LogoutRes res = (LogoutRes) response;
                                        if(res.returnCode != 1){
                                            new AlertDialog.Builder(MainLeaderActivity.this)
                                                    .setMessage("Lỗi: " + res.returnCode)
                                                    .setNegativeButton(android.R.string.ok, null)
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                            return;
                                        }
                                        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                                        intent.putExtra("isLogout", true);
                                        startActivity(intent);
                                    } catch (Exception ex){
                                        Log.w("signOut", ex.toString());
                                        new AlertDialog.Builder(MainLeaderActivity.this)
                                                .setMessage("Lỗi xử lý.")
                                                .setNegativeButton("OK", null)
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();
                                    }
                                }
                            }, null, Request.Method.POST);
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        } catch (Exception ex){
            Log.w("signOut", ex.toString());
            new AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
    public void createSession(View v){
        try {
            if(sessionInfo != null || errorFlag){
                return;
            }
            Intent intent = new Intent(this, CreateSessionActivity.class);
            startActivity(intent);
        } catch (Exception ex){
            Log.w("createSession", ex.toString());
            new AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
    public void showQR(View v){
        try {
            if(sessionInfo == null || errorFlag){
                return;
            }
            Intent intent = new Intent(this, QRSessionActivity.class);
            intent.putExtra("SessionName", sessionInfo.SessionName);
            intent.putExtra("SessionID", sessionInfo.SessionID);
            intent.putExtra("IsNew", false);
            startActivity(intent);
        } catch (Exception ex){
            Log.w("showQR", ex.toString());
            new AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
    private SessionInfo sessionInfo;
    public void endSession(View v){
        try {
            if(sessionInfo == null || errorFlag){
                return;
            }
            String htmlcontent = String.format("Toàn bộ quá trình sẽ được lưu lại và toàn bộ (%d) nhân viên sẽ bị buộc thoát khỏi phiên xét nghiệm",
                    sessionInfo.LstUser == null ? 0 : sessionInfo.LstUser.length);
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
                            req.CovidTestingSessionID = sessionInfo.SessionID;
                            caller.call(MainLeaderActivity.this, "endtestsession4lead", req, EndTestSessionRes.class, new ICallback() {
                                @Override
                                public void callback(Object response) {
                                    try {
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
                                        Intent intent = new Intent(MainLeaderActivity.this, MainLeaderActivity.class);
                                        startActivity(intent);
                                    } catch (Exception ex){
                                        Log.w("endSession", ex.toString());
                                        new AlertDialog.Builder(MainLeaderActivity.this)
                                                .setMessage("Lỗi xử lý.")
                                                .setNegativeButton("OK", null)
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();
                                    }
                                }
                            }, null, Request.Method.POST);
                        }
                    }, null, MainLeaderActivity.this);
        } catch (Exception ex){
            Log.w("endSession", ex.toString());
            new AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}