package com.hcdc.xncovid;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.google.android.material.snackbar.Snackbar;
import com.hcdc.xncovid.model.GetSessionReq;
import com.hcdc.xncovid.model.GetSessionRes;
import com.hcdc.xncovid.model.GroupTestRes;
import com.hcdc.xncovid.model.JoinTestSessionReq;
import com.hcdc.xncovid.model.JoinTestSessionRes;
import com.hcdc.xncovid.model.UserInfo;
import com.hcdc.xncovid.util.Caller;
import com.hcdc.xncovid.util.ICallback;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SessionInfoActivity extends AppCompatActivity {
    String xn_session;
    private TextView tenphien, province, district, ward, address, chooseTime, chooseDate, leader;
    private TextView sessionTypeName, target_xn_giamsat, reason_xn_giamsat, reason_xn_chidinh, target_xn_chidinh, ralative_target;
     LinearLayout btnConfirm, ltype1, ltype2;
    private View mLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_info);
        try{
            Intent intent=this.getIntent();
            xn_session = getIntent().getExtras().getString("xn_session");

            tenphien=(TextView) findViewById(R.id.sessionName);
            province=(TextView) findViewById(R.id.province);
            district=(TextView) findViewById(R.id.district);
            ward=(TextView) findViewById(R.id.ward);
            address=(TextView) findViewById(R.id.address);
            chooseTime=(TextView) findViewById(R.id.chooseTime);
            chooseDate=(TextView) findViewById(R.id.chooseDate);
            leader=(TextView) findViewById(R.id.leader);
            btnConfirm = findViewById(R.id.next);
            sessionTypeName = (TextView)findViewById(R.id.type);

            //xn giam sat
            ltype1 = (LinearLayout) findViewById(R.id.type1);
            target_xn_giamsat = (TextView)findViewById(R.id.target1);
            reason_xn_giamsat = (TextView)findViewById(R.id.cause1);

            ltype1.setVisibility(View.GONE);

            //xn chi dinh
            ltype2 = (LinearLayout) findViewById(R.id.type2);
            reason_xn_chidinh = (TextView)findViewById(R.id.cause2);
            target_xn_chidinh = (TextView)findViewById(R.id.target2);
            ralative_target = (TextView)findViewById(R.id.relativeTarget);
            ltype2.setVisibility(View.GONE);
            //tenphien.setText(xn_session);

            setUIRef();
            showLoading();

        }catch (Exception e){
            Log.e("SessionInfoActivity", e.toString(), e);
            new android.app.AlertDialog.Builder(this)
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
        //tenphien.setText(xn_session);
        tenphien.setText("");
        province.setText("");
        district.setText("");
        ward.setText("");
        address.setText("");
        chooseTime.setText("");
        chooseDate.setText("");
        leader.setText("");

        sessionTypeName.setText("");
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
        ltype1.setVisibility(View.GONE);
        ltype2.setVisibility(View.GONE);
        getSessionIf();




    }catch (Exception e){
        Log.e("SessionInfoActivity", e.toString(), e);
        new android.app.AlertDialog.Builder(this)
                .setMessage("Lỗi xử lý.")
                .setNegativeButton("OK", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    }

    private void getSessionIf(){
      try{
          Pattern p = Pattern.compile("([0-9]+)");
          Matcher m = p.matcher(xn_session);
          if(m.find() == false){
              new AlertDialog.Builder(SessionInfoActivity.this)
                      .setMessage("Mã phiên xét nghiệm không hợp lệ !")
                      .setNegativeButton(android.R.string.ok,
                              new DialogInterface.OnClickListener() {
                                  @Override
                                  public void onClick(DialogInterface dialog, int which) {
                                      StartMainStaffAcitivity();
                                  }
                              })
                      .setIcon(android.R.drawable.ic_dialog_alert)
                      .show();
              return;
          }
          GetSessionReq req = new GetSessionReq();
          req.SessionID  = Long.parseLong(xn_session);
          Caller caller = new Caller();
          caller.call(SessionInfoActivity.this, "GetSession", req, GetSessionRes.class, new ICallback() {
              @Override
              public void callback(Object response) {
                  try{
                      GetSessionRes res = (GetSessionRes) response;
                      if(res.ReturnCode == 1){
                          try{
                              if(res.Data == null){
                                  new AlertDialog.Builder(SessionInfoActivity.this)
                                          .setMessage("Không lấy được thônn tin phiên xét nghiệm. Vui lòng thử lại sau.")
                                          .setNegativeButton(android.R.string.ok,
                                                  new DialogInterface.OnClickListener() {
                                                      @Override
                                                      public void onClick(DialogInterface dialog, int which) {
                                                          StartMainStaffAcitivity();
                                                      }
                                                  })
                                          .setIcon(android.R.drawable.ic_dialog_alert)
                                          .show();
                                  return;
                              }
                              SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                              SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                              tenphien.setText(res.Data.SessionName);
                              province.setText(res.Data.ProvinceName);
                              district.setText(res.Data.DistrictName);
                              ward.setText(res.Data.WardName);
                              address.setText(res.Data.Address);
                              chooseTime.setText(timeFormat.format(res.Data.getTestingDate()));
                              chooseDate.setText(dateFormat.format(res.Data.getTestingDate()));
                              leader.setText(res.Data.Account);
                              sessionTypeName.setText(res.Data.CovidTestingSessionTypeName);

                              if(res.Data.CovidTestingSessionTypeID == 1) //giam sat
                              {
                                  target_xn_giamsat.setText(res.Data.CovidTestingSessionObjectName);
                                  reason_xn_giamsat.setText(res.Data.Purpose);

                                  ltype1.setVisibility(View.VISIBLE);
                                  target_xn_giamsat.setVisibility(View.VISIBLE);
                                  reason_xn_giamsat.setVisibility(View.VISIBLE);
                              }else if(res.Data.CovidTestingSessionTypeID == 2) //chi dinh
                              {
                                  reason_xn_chidinh.setText(res.Data.DesignatedReasonName);
                                  target_xn_chidinh.setText(res.Data.CovidTestingSessionObjectName);
                                  ralative_target.setText(res.Data.Purpose);
                                  ltype2.setVisibility(View.VISIBLE);
                                  reason_xn_chidinh.setVisibility(View.VISIBLE);
                                  target_xn_chidinh.setVisibility(View.VISIBLE);
                                  ralative_target.setVisibility(View.VISIBLE);
                              }

                              btnConfirm.setOnClickListener(new View.OnClickListener() {
                                  public void onClick(View v) {
                                      showLoading();
                                      JoinTestSessionReq req = new JoinTestSessionReq();
                                      req.TestSessionID =  Long.parseLong(xn_session);
                                      UserInfo userInfo = ((MyApplication) getApplication()).getUserInfo();

                                      if(userInfo != null){
                                          req.AccountID = userInfo.AccountID;
                                      }
                                      Caller caller = new Caller();
                                      caller.call(SessionInfoActivity.this, "JoinTestSession", req, JoinTestSessionRes.class, new ICallback() {
                                          @Override
                                          public void callback(Object response) {
                                              try{
                                                  JoinTestSessionRes res = (JoinTestSessionRes) response;
                                                  if(res.ReturnCode == 1){
                                                      ((MyApplication) getApplication()).setGroupMaxCount(0);
                                                      ((MyApplication) getApplication()).setDefaultMaxGroup(false);
                                                      StartMainStaffAcitivity();
                                                  }   else if(res.ReturnCode == -32) //session da ket thuc
                                                  {
                                                      // Log.e("GroupTest", res.ReturnCode + " - " + res.ReturnMess);
                                                      new AlertDialog.Builder(SessionInfoActivity.this)
                                                              .setMessage("Phiên xét nghiệm đã kết thúc.")
                                                              .setNegativeButton(android.R.string.ok, null)
                                                              .setIcon(android.R.drawable.ic_dialog_alert)
                                                              .show();
                                                      hideLoading();
                                                  }
                                                  else if(res.ReturnCode == -31) //session da ket thuc
                                                  {
                                                      // Log.e("GroupTest", res.ReturnCode + " - " + res.ReturnMess);
                                                      new AlertDialog.Builder(SessionInfoActivity.this)
                                                              .setMessage("Không tìm thấy phiên xét nghiệm.")
                                                              .setNegativeButton(android.R.string.ok, null)
                                                              .setIcon(android.R.drawable.ic_dialog_alert)
                                                              .show();
                                                      hideLoading();
                                                  }
                                                  else{
                                                      Log.e("GroupTest", res.ReturnCode + " - " + res.ReturnMess);
                                                      new AlertDialog.Builder(SessionInfoActivity.this)
                                                              .setMessage("Tham gia phiên xét nghiệm không thành công. Vui lòng thử lại sau.")
                                                              .setNegativeButton(android.R.string.ok, null)
                                                              .setIcon(android.R.drawable.ic_dialog_alert)
                                                              .show();
                                                      hideLoading();
                                                  }
                                              }catch (Exception e){
                                                  Log.e("SessionInfoActivity", e.toString(), e);
                                                  new android.app.AlertDialog.Builder(SessionInfoActivity.this)
                                                          .setMessage("Lỗi xử lý.")
                                                          .setNegativeButton("OK", null)
                                                          .setIcon(android.R.drawable.ic_dialog_alert)
                                                          .show();
                                                  hideLoading();
                                              }

                                          }
                                      }, null, Request.Method.POST);
                                  }
                              });

                          }catch (Exception e){
                              Log.e("GetSession", "", e);
                              new AlertDialog.Builder(SessionInfoActivity.this)
                                      .setMessage("Không lấy được thông tin phiên xét nghiệm. Vui lòng thử lại sau.")
                                      .setNegativeButton(android.R.string.ok,
                                              new DialogInterface.OnClickListener() {
                                                  @Override
                                                  public void onClick(DialogInterface dialog, int which) {
                                                      StartMainStaffAcitivity();
                                                  }
                                              })
                                      .setIcon(android.R.drawable.ic_dialog_alert)
                                      .show();
                          }
                      }else if(res.ReturnCode == -196){
                          Log.e("GetSession", res.ReturnCode + " - " + res.ReturnMess);
                          new AlertDialog.Builder(SessionInfoActivity.this)
                                  .setMessage("Phiên xét nghiệm đã kết thúc. Vi lòng tham gia phiên xét nghiệm khác.")
                                  .setNegativeButton(android.R.string.ok,
                                          new DialogInterface.OnClickListener() {
                                              @Override
                                              public void onClick(DialogInterface dialog, int which) {
                                                  StartMainStaffAcitivity();
                                              }
                                          })
                                  .setIcon(android.R.drawable.ic_dialog_alert)
                                  .show();
                      }
                      else{
                          Log.e("GetSession", res.ReturnCode + " - " + res.ReturnMess);
                          new AlertDialog.Builder(SessionInfoActivity.this)
                                  .setMessage("Không lấy được thông tin phiên xét nghiệm. Vui lòng thử lại sau.")
                                  .setNegativeButton(android.R.string.ok,
                                          new DialogInterface.OnClickListener() {
                                              @Override
                                              public void onClick(DialogInterface dialog, int which) {
                                                  StartMainStaffAcitivity();
                                              }
                                          })
                                  .setIcon(android.R.drawable.ic_dialog_alert)
                                  .show();
                      }
                  }catch (Exception e){
                      Log.e("getSessionIf", e.toString(), e);
                      new android.app.AlertDialog.Builder(SessionInfoActivity.this)
                              .setMessage("Lỗi xử lý.")
                              .setNegativeButton("OK", null)
                              .setIcon(android.R.drawable.ic_dialog_alert)
                              .show();
                  }
                  hideLoading();
              }
          }, null, Request.Method.POST);
      }catch (Exception e){
          Log.e("getSessionIf", e.toString(), e);
          new android.app.AlertDialog.Builder(this)
                  .setMessage("Lỗi xử lý.")
                  .setNegativeButton("OK", null)
                  .setIcon(android.R.drawable.ic_dialog_alert)
                  .show();
      }
    }

    private  void StartMainStaffAcitivity(){
        Intent intent = new Intent(getApplicationContext(), MainStaffActivity.class);
        // intent.putExtra("xn_session", "");
        startActivity(intent);
        finish();
    }

    private void setUIRef()
    {
        //Create a Instance of the Loading Layout
        mLoading = findViewById(R.id.my_loading_layout_info);
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