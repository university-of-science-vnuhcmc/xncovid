package com.hcdc.xncovid;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.hcdc.xncovid.adapter.StaffAdapter;
import com.hcdc.xncovid.model.CheckAccountReq;
import com.hcdc.xncovid.model.CheckAccountRes;
import com.hcdc.xncovid.model.EndTestSessionReq;
import com.hcdc.xncovid.model.EndTestSessionRes;
import com.hcdc.xncovid.model.LogoutReq;
import com.hcdc.xncovid.model.LogoutRes;
import com.hcdc.xncovid.model.Session;
import com.hcdc.xncovid.model.UserInfo;
import com.hcdc.xncovid.util.Caller;
import com.hcdc.xncovid.util.ICallback;
import com.hcdc.xncovid.util.Util;

import java.text.SimpleDateFormat;
import java.util.List;

public class MainLeaderActivity extends AppCompatActivity {
    private View mLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_leader);
        Intent intent = getIntent();
        if(intent.getExtras() != null){
            flag = intent.getExtras().getInt("flag");
        }
        setUIRef();
    }
    private int flag = 0;
    @Override
    protected void onResume() {
        super.onResume();
        try {
            MyApplication myapp = (MyApplication) getApplication();
            TextView nameView = findViewById(R.id.name);
            nameView.setText(myapp.getUserInfo().Name);
            if(flag == 1){ // tu man hinh gom nhom ve
                session = myapp.getSession(); //Kt session trong cache
                if(session != null){
                    setupSession();
                } else
                {
                    getCurrentSession();// chua co thi check account lai
                }
            } else if(flag == 2){ // tu man hinh ket thuc phien xet nghiem
                myapp.setSession(null); //set la null
                setupSession();
            } else { // tu MainActivity hoac tu join phien xet nghiem ==> goi check Account
                getCurrentSession();
            }
            flag = 0;
        } catch (Exception ex){
            Log.w("MainLeaderActivity", ex.toString());
            new AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
    private void setupSession(){
        if(session != null){
            findViewById(R.id.createTest).setBackground(getResources().getDrawable( R.drawable.rectangle_menu_disable));
            findViewById(R.id.newGroup).setBackground(getResources().getDrawable( R.drawable.rectangle_menu_enable));
            findViewById(R.id.sessionInfo).setBackground(getResources().getDrawable( R.drawable.rectangle_main_info));
            findViewById(R.id.scanQR).setBackground(getResources().getDrawable( R.drawable.button_scan_qr_session_enable));
            findViewById(R.id.endSessionDisable).setVisibility(View.GONE);
            SeekBar sb = findViewById(R.id.endSession);
            sb.setVisibility(View.VISIBLE);
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
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
                        Log.w("MainLeaderActivity", ex.toString());
                    }
                    return true;
                }
            });
            ((TextView)findViewById(R.id.sessionName)).setText(session.SessionName);
            ((TextView)findViewById(R.id.location)).setText(session.Address);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
            ((TextView)findViewById(R.id.time)).setText(timeFormat.format(session.getTestingDate()));
            ((TextView)findViewById(R.id.type)).setText(session.CovidTestingSessionTypeName);
            if(session.CovidTestingSessionTypeID == 1){
                ((TextView)findViewById(R.id.cause1)).setText(session.Purpose);
                ((TextView)findViewById(R.id.target1)).setText(session.CovidTestingSessionObjectName);
                findViewById(R.id.type1).setVisibility(View.VISIBLE);
                findViewById(R.id.type2).setVisibility(View.GONE);
            } else {
                ((TextView)findViewById(R.id.relativeTarget)).setText(session.Purpose);
                ((TextView)findViewById(R.id.cause2)).setText(session.DesignatedReasonName);
                ((TextView)findViewById(R.id.target2)).setText(session.CovidTestingSessionObjectName);
                findViewById(R.id.type1).setVisibility(View.GONE);
                findViewById(R.id.type2).setVisibility(View.VISIBLE);
            }
            ((TextView)findViewById(R.id.numberStaff)).setText(LstUser == null ? "0 nhân viên" : String.valueOf(LstUser.length) + " nhân viên");
            StaffAdapter adapter = new StaffAdapter(MainLeaderActivity.this, LstUser);

            ListView listView = (ListView) findViewById(R.id.listStaff);
            listView.setAdapter(adapter);
        }
    }
    private boolean flagSlideButton = false;
    private boolean errorFlag = false;
    public void getCurrentSession(){
        Caller caller = new Caller();
        CheckAccountReq req = new CheckAccountReq();
        req.AccountID = ((MyApplication)getApplication()).getUserInfo().AccountID;
        showLoading();
        caller.call(MainLeaderActivity.this, "checkaccount", req, CheckAccountRes.class, new ICallback() {
            @Override
            public void callback(Object response) {
                try {
                    hideLoading();
                    CheckAccountRes res = (CheckAccountRes) response;
                    if(res.ReturnCode != 1 && res.ReturnCode != 0){
                        new AlertDialog.Builder(MainLeaderActivity.this)
                                .setMessage("Lỗi: " + res.ReturnCode)
                                .setNegativeButton(android.R.string.ok, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        errorFlag = true;
                        return;
                    }
                    if(res.ReturnCode == 0){
                        session = null;
                    } else {
                        session = res.Session;
                        LstUser = res.LstUser;
                    }
                    if(errorFlag){
                        findViewById(R.id.createTest).setBackground(getResources().getDrawable( R.drawable.rectangle_menu_disable));
                        findViewById(R.id.newGroup).setBackground(getResources().getDrawable( R.drawable.rectangle_menu_disable));
                    }
                    setupSession();
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
                                        if(res.ReturnCode != 1){
                                            new AlertDialog.Builder(MainLeaderActivity.this)
                                                    .setMessage("Lỗi: " + res.ReturnCode)
                                                    .setNegativeButton(android.R.string.ok, null)
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                            return;
                                        }
                                        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                                        intent.putExtra("isLogout", true);
                                        startActivity(intent);
                                        finish();
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
            if(session != null || errorFlag){
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
            if(session == null || errorFlag){
                return;
            }
            Intent intent = new Intent(this, QRSessionActivity.class);
            intent.putExtra("Session", new Gson().toJson(session));
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
    private Session session;
    private UserInfo[] LstUser;
    public void endSession(){
        try {
            if(session == null || errorFlag){
                return;
            }
            String htmlcontent = String.format("Toàn bộ quá trình sẽ được lưu lại và toàn bộ (%d) nhân viên sẽ bị buộc thoát khỏi phiên xét nghiệm",
                    LstUser == null ? 0 : LstUser.length);
            new Util().showMessage("Xác nhận kết thúc phiên xét nghiệm",
                    session.SessionName,
                    htmlcontent,
                    "Thoát",
                    "Hủy",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Caller caller = new Caller();
                            EndTestSessionReq req = new EndTestSessionReq();
                            req.CovidTestingSessionID = session.SessionID;
                            showLoading();
                            caller.call(MainLeaderActivity.this, "endtestsession4lead", req, EndTestSessionRes.class, new ICallback() {
                                @Override
                                public void callback(Object response) {
                                    try {
                                        hideLoading();
                                        EndTestSessionRes res = (EndTestSessionRes) response;
                                        if(res.ReturnCode != 1){
                                            new androidx.appcompat.app.AlertDialog.Builder(MainLeaderActivity.this)
                                                    .setMessage("Lỗi: " + res.ReturnCode)
                                                    .setNegativeButton(android.R.string.ok, null)
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                            return;
                                        }
                                        session = null;
                                        Intent intent = new Intent(MainLeaderActivity.this, MainLeaderActivity.class);
                                        intent.putExtra("flag", 2);
                                        startActivity(intent);
                                        finish();
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
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                SeekBar sb = findViewById(R.id.endSession);
                                sb.setProgress(0);
                            } catch (Exception ex){
                                Log.w("endSession", ex.toString());
                            }
                        }
                    }, MainLeaderActivity.this, null);
        } catch (Exception ex){
            Log.w("endSession", ex.toString());
            new AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        try {
            ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

            List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
            String subTitle = "Phiên xét nghiệm đang tham gia: ";
            if (session != null) {
                subTitle += session.SessionName;
            }
            if (taskList.get(0).numActivities == 1 &&
                    taskList.get(0).topActivity.getClassName().equals(this.getClass().getName())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Bạn muốn thoát ứng dụng?")
                        .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Khoi tao lai Activity main
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);

                                // Tao su kien ket thuc app
                                Intent startMain = new Intent(Intent.ACTION_MAIN);
                                startMain.addCategory(Intent.CATEGORY_HOME);
                                startActivity(startMain);
                                finish();
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        } catch (Exception e) {
            Log.e("onBackPressed", e.toString(), e);
            new AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
    public void newGroup(View v) {
        try {
            Intent intent = new Intent(getApplicationContext(), ScanSessionActivity.class);
            intent.putExtra("scan_qr_type", 1);
            ((MyApplication)getApplication()).setSession(session);
            startActivity(intent);
        } catch (Exception e) {
            Log.e("newGroup", e.toString(), e);
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
        mLoading = findViewById(R.id.my_loading_layout);
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