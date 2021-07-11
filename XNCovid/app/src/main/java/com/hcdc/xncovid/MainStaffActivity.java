package com.hcdc.xncovid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hcdc.xncovid.model.CheckAccountReq;
import com.hcdc.xncovid.model.CheckAccountRes;
import com.hcdc.xncovid.model.EndTestSessionReq;
import com.hcdc.xncovid.model.EndTestSessionRes;
import com.hcdc.xncovid.model.GetStaffConfigReq;
import com.hcdc.xncovid.model.GetStaffConfigRes;
import com.hcdc.xncovid.model.LogoutReq;
import com.hcdc.xncovid.model.LogoutRes;
import com.hcdc.xncovid.model.Session;
import com.hcdc.xncovid.model.SessionInfo;
import com.hcdc.xncovid.model.UserInfo;
import com.hcdc.xncovid.util.Caller;
import com.hcdc.xncovid.util.ICallback;
import com.hcdc.xncovid.util.Util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

public class MainStaffActivity extends AppCompatActivity {
private LinearLayout layoutJoinTest, layoutListTest, layoutnewGroup, layoutlistGroup, layoutensession, layoutsessioninfo;
String sessionId;
long accountID;
private  TextView testName, location, time, cause, leader;
    MyApplication myapp = null;
    SessionInfo objSession = null;
    private boolean errorFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            sessionId = null;
            setContentView(R.layout.activity_main_staff);
            myapp = (MyApplication) getApplication();
            TextView nameView = findViewById(R.id.name);
            nameView.setText(myapp.getUserInfo().Name);
            Intent intent = this.getIntent();
            int flag = 0;
            if(intent.getExtras() != null){
                flag = intent.getExtras().getInt("flag");
                // sessionId = intent.getExtras().getString("xn_session");
            }


            layoutJoinTest = findViewById(R.id.joinTest);
            layoutListTest = findViewById(R.id.listTest);
            layoutnewGroup = findViewById(R.id.newGroup);
            layoutlistGroup = findViewById(R.id.listGroup);
            layoutensession = findViewById(R.id.endSession);
            layoutsessioninfo = findViewById(R.id.layout_main_session_info_staff);

            //layoutJoinTest.setEnabled(false);
            layoutJoinTest.setBackground(getResources().getDrawable( R.drawable.rectangle_menu_disable));
            // layoutListTest.setEnabled(false);
            layoutListTest.setBackground(getResources().getDrawable( R.drawable.rectangle_menu_disable));
            //layoutnewGroup.setEnabled(false);
            layoutnewGroup.setBackground(getResources().getDrawable( R.drawable.rectangle_menu_disable));
            //layoutJoinTest.setEnabled(false);
            layoutlistGroup.setBackground(getResources().getDrawable( R.drawable.rectangle_menu_disable));
            layoutsessioninfo.setBackground(getResources().getDrawable( R.drawable.rectangle_main_info_disable));

            //TextView
            testName = (TextView) findViewById(R.id.testName);
            location= (TextView) findViewById(R.id.location);
            time = (TextView) findViewById(R.id.time);
            cause = (TextView) findViewById(R.id.cause);
            leader = (TextView) findViewById(R.id.leader);

            if(flag == 1){ // tu man hinh gom nhom ve
                objSession = ((MyApplication) getApplication()).getSessionInfo(); //Kt session trong cache
                if(objSession != null){
                    sessionId = objSession.SessionID + "";
                    SetupActivit(1);//Da co join vao session
                }else {
                    checkAccount();// chua co thi check account lai
                }
            } else if(flag == 2){ // tu man hinh ket thuc phien xet nghiem
                myapp.setSessionInfo(null); //set la null
                SetupActivit(0);//chua join
            } else { // tu MainActivity hoac tu join phien xet nghiem ==> goi check Account
                checkAccount();
            }
        } catch (Exception e){
            Log.e("MainStaffActivity", e.toString(), e);
            new AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }

    private  void  SetupActivit(int caseType){
        if(sessionId != null && sessionId != "")
        {
            //TextView
            testName.setText(objSession.SessionName);
            location.setText((objSession.Address));
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
            time.setText(timeFormat.format(objSession.TestingDate));
            cause.setText(objSession.Purpose);
            leader.setText(objSession.Account);

            layoutsessioninfo.setBackground(getResources().getDrawable( R.drawable.rectangle_main_info));


            layoutnewGroup.setEnabled(true);
            layoutnewGroup.setBackground(getResources().getDrawable( R.drawable.rectangle_menu_enable));
            layoutnewGroup.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ScanSessionActivity.class);
                    intent.putExtra("scan_qr_type", 1);
                    // intent.putExtra("xn_session", sessionId);
                    startActivity(intent);
                }
            });

            layoutensession.setEnabled(true);
            layoutensession.setBackground(getResources().getDrawable( R.drawable.end_session_enable));
            layoutensession.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    endSession();
                }
            });

        }else if(caseType == 0){

            //TextView
            testName.setText("Hiện tại chưa có phiên xét nghiệm");
            location.setText("");

            time.setText("");
            cause.setText("");
            leader.setText("");



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

    public void signOut(View v) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Bạn muốn đăng xuất?")
                    .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Caller caller = new Caller();
                            LogoutReq req = new LogoutReq();
                            caller.call(MainStaffActivity.this, "logout", req, LogoutRes.class, new ICallback() {
                                @Override
                                public void callback(Object response) {
                                    try{
                                        LogoutRes res = (LogoutRes) response;
                                        if(res.ReturnCode != 1){
                                            new AlertDialog.Builder(MainStaffActivity.this)
                                                    .setMessage("Lỗi: " + res.ReturnCode)
                                                    .setNegativeButton(android.R.string.ok, null)
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                            return;
                                        }
                                        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                                        intent.putExtra("isLogout", true);
                                        startActivity(intent);
                                    }catch (Exception e){
                                        Log.w("signOut", e.toString());
                                        new AlertDialog.Builder(MainStaffActivity.this)
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

    private void checkAccount(){
        try {
            Caller caller = new Caller();
            CheckAccountReq req = new CheckAccountReq();
            req.AccountID = accountID;
            caller.call(this, "CheckAccount", req, CheckAccountRes.class, new ICallback() {
                @Override
                public void callback(Object response) {
                    try{
                        CheckAccountRes res = (CheckAccountRes) response;
                        if(res.ReturnCode == 0) // khong co dang join session nao het
                        {
                            SetupActivit(res.ReturnCode);
                        }
                        if(res.ReturnCode != 1){
                            new androidx.appcompat.app.AlertDialog.Builder(MainStaffActivity.this)
                                    .setMessage("Lỗi: " + res.ReturnCode + "Lỗi hệ thống, vui lòng thử lại sau.")
                                    .setNegativeButton(android.R.string.ok, null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            errorFlag = true;
                        }else {
                            //ReturnCode = 1 ==> co dang join vao session
                            if(res.Session != null){
                                objSession = new SessionInfo();
                                objSession = (SessionInfo) res.Session;
                                objSession.LstUser = res.LstUser;
                                if(myapp == null){
                                    myapp = new MyApplication();
                                }
                                myapp.setSessionInfo(objSession);
                                SetupActivit(res.ReturnCode);
                            }else {
                                Log.w("checkAccount", "SessionInfo return null");
                            }

                        }
                    } catch (Exception e){
                        Log.e("checkAccount", e.toString(), e);
                        new androidx.appcompat.app.AlertDialog.Builder(MainStaffActivity.this)
                                .setMessage("Lỗi hệ thống, vui lòng thử lại sau.")
                                .setNegativeButton(android.R.string.ok, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        errorFlag = true;

                    }
                }
            }, null, Request.Method.POST);
        } catch (Exception e){
            Log.e("checkAccount", e.toString(), e);
            new androidx.appcompat.app.AlertDialog.Builder(MainStaffActivity.this)
                    .setMessage("Lỗi hệ thống, vui lòng thử lại sau.")
                    .setNegativeButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            errorFlag = true;

        }
    }

    public void endSession(){
        try{

            if(objSession == null || errorFlag){
                return;
            }
            String htmlcontent = "Điều này sẽ được thông báo đến trưởng nhóm <b>Nguyễn Văn B</b> !";
            new Util().showMessage("Xác nhận thoát khỏi phiên xét nghiệm",
                    objSession.SessionName,
                    htmlcontent,
                    "Thoát",
                    "Hủy",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Caller caller = new Caller();
                            EndTestSessionReq req = new EndTestSessionReq();
                            req.CovidTestingSessionID = objSession.SessionID;
                            caller.call(MainStaffActivity.this, "LeaveTestSession", req, EndTestSessionRes.class, new ICallback() {
                                @Override
                                public void callback(Object response) {
                                    try{
                                        EndTestSessionRes res = (EndTestSessionRes) response;
                                        if(res.ReturnCode == -61 || res.ReturnCode == -62){
                                            new androidx.appcompat.app.AlertDialog.Builder(MainStaffActivity.this)
                                                    .setMessage("Không tìm thấy phiên xét nghiệm hoặc đã kết thúc.")
                                                    .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            objSession = null;
                                                            Intent intent = new Intent(getApplicationContext(), MainStaffActivity.class);
                                                            intent.putExtra("flag", 2);
                                                            startActivity(intent);

                                                        }
                                                    })
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                            return;
                                        }
                                        if(res.ReturnCode != 1){
                                            new androidx.appcompat.app.AlertDialog.Builder(MainStaffActivity.this)
                                                    .setMessage("Lỗi: " + res.ReturnCode + "- Lỗi hệ thống, vui lòng thử lại sau.")
                                                    .setNegativeButton(android.R.string.ok, null)
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                            return;
                                        }
                                        objSession = null;
                                        Intent intent = new Intent(getApplicationContext(), MainStaffActivity.class);
                                        intent.putExtra("flag", 2);
                                        startActivity(intent);
                                    }catch (Exception e){
                                        Log.e("endSession", e.toString(), e);
                                        new AlertDialog.Builder(MainStaffActivity.this)
                                                .setMessage("Lỗi xử lý.")
                                                .setNegativeButton("OK", null)
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();
                                    }

                                }
                            }, null, Request.Method.POST);
                        }
                    }, null, MainStaffActivity.this);

        }catch (Exception e){
            Log.e("endSession", e.toString(), e);
            new AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        }

}