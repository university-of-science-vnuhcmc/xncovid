package com.hcdc.xncovid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.hcdc.xncovid.model.CheckAccountReq;
import com.hcdc.xncovid.model.CheckAccountRes;
import com.hcdc.xncovid.model.EndTestSessionReq;
import com.hcdc.xncovid.model.EndTestSessionRes;
import com.hcdc.xncovid.model.LeaveTestSessionReq;
import com.hcdc.xncovid.model.LogoutReq;
import com.hcdc.xncovid.model.LogoutRes;
import com.hcdc.xncovid.model.Session;
import com.hcdc.xncovid.model.UserInfo;
import com.hcdc.xncovid.util.Caller;
import com.hcdc.xncovid.util.ICallback;
import com.hcdc.xncovid.util.Util;

import java.text.SimpleDateFormat;
import java.util.List;

public class MainStaffActivity extends AppCompatActivity {
private LinearLayout layoutJoinTest, layoutListTest, layoutnewGroup, layoutlistGroup, layoutsessioninfo;
String sessionId;
long accountID;
    int flag = 0;
    public  static  boolean isFirst = false;
    private View mLoading;
private  TextView testName, location, time, leader, sessionTypeName;
private  TextView target_xn_giamsat, reason_xn_giamsat;
private  TextView reason_xn_chidinh, target_xn_chidinh, ralative_target;
private  TextView title_target1, title_cause, title_cause2, title_target2, title_relativeTarget;
    MyApplication myapp = null;
    Session objSession = null;
    UserInfo[] LstUser = null;
    private boolean errorFlag = false;
    SeekBar sb;
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

            if(intent.getExtras() != null){
                flag = intent.getExtras().getInt("flag");
                // sessionId = intent.getExtras().getString("xn_session");
            }

            UserInfo userInfo = ((MyApplication) getApplication()).getUserInfo();

            if(userInfo != null){
                accountID = userInfo.AccountID;
            }
            layoutJoinTest = findViewById(R.id.joinTest);
            layoutListTest = findViewById(R.id.listTest);
            layoutnewGroup = findViewById(R.id.newGroup);
            layoutlistGroup = findViewById(R.id.listGroup);
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
            leader = (TextView) findViewById(R.id.leader);
            sessionTypeName = (TextView) findViewById(R.id.type);

            //xn giam sat
            target_xn_giamsat = (TextView)findViewById(R.id.target1);
            reason_xn_giamsat = (TextView)findViewById(R.id.cause);
            title_target1 = (TextView)findViewById(R.id.title_target1);
            title_cause = (TextView)findViewById(R.id.title_cause);

            //xn chi dinh
            reason_xn_chidinh = (TextView)findViewById(R.id.cause2);
            target_xn_chidinh = (TextView)findViewById(R.id.target2);
            ralative_target = (TextView)findViewById(R.id.relativeTarget);
            title_cause2 = (TextView)findViewById(R.id.title_cause2);
            title_target2 = (TextView)findViewById(R.id.title_target2);
            title_relativeTarget = (TextView)findViewById(R.id.title_relativeTarget);

            target_xn_giamsat.setVisibility(View.GONE);
            reason_xn_giamsat.setVisibility(View.GONE);
            title_target1.setVisibility(View.GONE);
            title_cause.setVisibility(View.GONE);

            reason_xn_chidinh.setVisibility(View.GONE);
            target_xn_chidinh.setVisibility(View.GONE);
            ralative_target.setVisibility(View.GONE);
            title_cause2.setVisibility(View.GONE);
            title_target2.setVisibility(View.GONE);
            title_relativeTarget.setVisibility(View.GONE);

            sb = findViewById(R.id.endSession);
            setUIRef();

        } catch (Exception e){
            Log.e("MainStaffActivity", e.toString(), e);
            new AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            showLoading();
            if(flag == 1){ // tu man hinh gom nhom ve
                objSession = ((MyApplication) getApplication()).getSession(); //Kt session trong cache
                if(objSession != null){
                    sessionId = objSession.SessionID + "";
                    SetupActivit(1);//Da co join vao session
                }else {
                    checkAccount();// chua co thi check account lai
                }
            } else if(flag == 2){ // tu man hinh ket thuc phien xet nghiem
                myapp.setSession(null); //set la null
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
    private boolean flagSlideButton = false;
    private  void  SetupActivit(int caseType){
        if(sessionId != null && sessionId != "")
        {
            //TextView
            testName.setText(objSession.SessionName);
            location.setText((objSession.Address));
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
            time.setText(timeFormat.format(objSession.getTestingDate()));
            sessionTypeName.setText(objSession.CovidTestingSessionTypeName);
            leader.setText(objSession.Account);
            if(objSession.CovidTestingSessionTypeID == 1) //giam sat
            {
                target_xn_giamsat.setText(objSession.CovidTestingSessionObjectName);
                reason_xn_giamsat.setText(objSession.Purpose);

                target_xn_giamsat.setVisibility(View.VISIBLE);
                reason_xn_giamsat.setVisibility(View.VISIBLE);
                title_target1.setVisibility(View.VISIBLE);
                title_cause.setVisibility(View.VISIBLE);

                reason_xn_chidinh.setVisibility(View.GONE);
                target_xn_chidinh.setVisibility(View.GONE);
                ralative_target.setVisibility(View.GONE);
                title_cause2.setVisibility(View.GONE);
                title_target2.setVisibility(View.GONE);
                title_relativeTarget.setVisibility(View.GONE);

            }else if(objSession.CovidTestingSessionTypeID == 2)//chi dinh
            {
                reason_xn_chidinh.setText(objSession.DesignatedReasonName);
                target_xn_chidinh.setText(objSession.CovidTestingSessionObjectName);
                ralative_target.setText(objSession.Purpose);

                target_xn_giamsat.setVisibility(View.GONE);
                reason_xn_giamsat.setVisibility(View.GONE);
                title_target1.setVisibility(View.GONE);
                title_cause.setVisibility(View.GONE);

                reason_xn_chidinh.setVisibility(View.VISIBLE);
                target_xn_chidinh.setVisibility(View.VISIBLE);
                ralative_target.setVisibility(View.VISIBLE);
                title_cause2.setVisibility(View.VISIBLE);
                title_target2.setVisibility(View.VISIBLE);
                title_relativeTarget.setVisibility(View.VISIBLE);
            }
            layoutsessioninfo.setBackground(getResources().getDrawable( R.drawable.rectangle_main_info));

            findViewById(R.id.endSessionDisable).setVisibility(View.GONE);

            sb.setVisibility(View.VISIBLE);
            sb.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    try {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            if (sb.getThumb().getBounds().contains((int) event.getX(), (int) event.getY())) {
                                flagSlideButton = true;
                                sb.onTouchEvent(event);
                            } else {
                                flagSlideButton = false;
                                return false;
                            }
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            if (sb.getProgress() > 95 && flagSlideButton) {
                                endSession();
                                return true;
                            }
                            flagSlideButton = false;
                            sb.setProgress(0);
                        } else {
                            sb.setProgress(0);
                            sb.onTouchEvent(event);
                        }
                    } catch (Exception ex){
                        Log.w("MainStaffActivity", ex.toString());
                    }
                    return true;
                }
            });
            layoutnewGroup.setEnabled(true);
            layoutnewGroup.setBackground(getResources().getDrawable( R.drawable.rectangle_menu_enable));
            hideLoading();
            layoutnewGroup.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ScanSessionActivity.class);
                    intent.putExtra("scan_qr_type", 1);
                    // intent.putExtra("xn_session", sessionId);
                    startActivity(intent);
                }
            });
        }else if(caseType == 0){

            //TextView
            testName.setText("Hiện tại chưa có phiên xét nghiệm");
            location.setText("");

            time.setText("");
            sessionTypeName.setText("");
            leader.setText("");

            target_xn_giamsat.setText("");
            reason_xn_giamsat.setText("");

            reason_xn_chidinh.setText("");
            target_xn_chidinh.setText("");
            ralative_target.setText("");

            target_xn_giamsat.setVisibility(View.GONE);
            reason_xn_giamsat.setVisibility(View.GONE);

            reason_xn_chidinh.setVisibility(View.GONE);
            target_xn_chidinh.setVisibility(View.GONE);
            ralative_target.setVisibility(View.GONE);
            title_cause2.setVisibility(View.GONE);
            title_target2.setVisibility(View.GONE);
            title_relativeTarget.setVisibility(View.GONE);
            title_target1.setVisibility(View.GONE);
            title_cause.setVisibility(View.GONE);

            layoutJoinTest.setEnabled(true);
            layoutJoinTest.setBackground(getResources().getDrawable( R.drawable.rectangle_menu_enable));
            hideLoading();
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
                            return;
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
                                objSession = res.Session;
                               // LstUser = res.LstUser;
                                if(myapp == null){
                                    myapp = new MyApplication();
                                }
                                myapp.setSession(objSession);
                                sessionId = res.Session.SessionID + "";
                                SetupActivit(res.ReturnCode);
                            }else {
                                Log.w("checkAccount", "Session return null");
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
            showLoading();
            if(objSession == null || errorFlag){
                return;
            }
            String htmlcontent = "Điều này sẽ được thông báo đến trưởng nhóm <b>"+objSession.Account+"</b> !";
            new Util().showMessage("Xác nhận thoát khỏi phiên xét nghiệm",
                    objSession.SessionName,
                    htmlcontent,
                    "Thoát",
                    "Hủy",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Caller caller = new Caller();
                            LeaveTestSessionReq req = new LeaveTestSessionReq();
                            req.AccountID = accountID;
                            req.TestSessionID = objSession.SessionID ;
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
                                                            finish();

                                                        }
                                                    })
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                            hideLoading();
                                            return;
                                        }
                                        if(res.ReturnCode != 1){
                                            new androidx.appcompat.app.AlertDialog.Builder(MainStaffActivity.this)
                                                    .setMessage("Lỗi: " + res.ReturnCode + "- Lỗi hệ thống, vui lòng thử lại sau.")
                                                    .setNegativeButton(android.R.string.ok, null)
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                            flagSlideButton = false;
                                            sb.setProgress(0);
                                            hideLoading();
                                            return;
                                        }
                                        objSession = null;
                                        Intent intent = new Intent(getApplicationContext(), MainStaffActivity.class);
                                        intent.putExtra("flag", 2);
                                        startActivity(intent);
                                        finish();
                                    }catch (Exception e){
                                        Log.e("endSession", e.toString(), e);
                                        new AlertDialog.Builder(MainStaffActivity.this)
                                                .setMessage("Lỗi xử lý.")
                                                .setNegativeButton("OK", null)
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();
                                        flagSlideButton = false;
                                        sb.setProgress(0);
                                        hideLoading();
                                    }

                                }
                            }, null, Request.Method.POST);
                        }
                    }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        flagSlideButton = false;
                        sb.setProgress(0);
                        hideLoading();
                    } catch (Exception ex){
                        Log.w("endSession", ex.toString());
                    }
                }}, MainStaffActivity.this);

        }catch (Exception e){
            Log.e("endSession", e.toString(), e);
            new AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            flagSlideButton = false;
            sb.setProgress(0);
            hideLoading();
        }
        }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        try{
            ActivityManager mngr = (ActivityManager) getSystemService( ACTIVITY_SERVICE );

            List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
            String subTitle = "Phiên xét nghiệm đang tham gia: ";
            if(objSession != null){
                subTitle += objSession.SessionName;
            }
            if(taskList.get(0).numActivities == 1 &&
                    taskList.get(0).topActivity.getClassName().equals(this.getClass().getName())) {
                new Util().showMessage("Bạn muốn thoát ứng dụng?",
                        subTitle,
                        "",
                        "Thoát",
                        "Hủy",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Khoi tao lai Activity main
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);

                                // Tao su kien ket thuc app
                                Intent startMain = new Intent(Intent.ACTION_MAIN);
                                startMain.addCategory(Intent.CATEGORY_HOME);
                                startActivity(startMain);
                                finish();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                            }, MainStaffActivity.this);
            }


    }catch (Exception e){
        Log.e("endSession", e.toString(), e);
        new AlertDialog.Builder(this)
                .setMessage("Lỗi xử lý.")
                .setNegativeButton("OK", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    }

    private void setUIRef()
    {
        //Create a Instance of the Loading Layout
        mLoading = findViewById(R.id.my_loading_layout_staff);
    }

    private void showLoading()
    {
        /*Call this function when you want progress dialog to appear*/
        if (mLoading != null)
        {
            mLoading.setVisibility(View.VISIBLE);
        }
    }

    private void hideLoading()
    {
        /*Call this function when you want progress dialog to disappear*/
        if (mLoading != null)
        {
            mLoading.setVisibility(View.GONE);
        }
    }
}