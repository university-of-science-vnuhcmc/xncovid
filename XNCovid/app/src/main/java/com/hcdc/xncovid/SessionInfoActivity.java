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
    private TextView tenphien, province, district, ward, address, chooseTime, chooseDate, cause, leader;
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
            cause=(TextView) findViewById(R.id.cause);
            leader=(TextView) findViewById(R.id.leader);

            tenphien.setText(xn_session);

            final LinearLayout btnConfirm = findViewById(R.id.next);
            getSessionIf();


            btnConfirm.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    JoinTestSessionReq req = new JoinTestSessionReq();
                    req.TestID = xn_session;
                    Caller caller = new Caller();
                    caller.call(SessionInfoActivity.this, "JoinTestSesion", req, JoinTestSessionRes.class, new ICallback() {
                        @Override
                        public void callback(Object response) {
                            try{
                                JoinTestSessionRes res = (JoinTestSessionRes) response;
                                if(res.ReturnCode == 1){
                                    StartMainStaffAcitivity();
                                }   else if(res.ReturnCode == -32) //session da ket thuc
                                {
                                    // Log.e("GroupTest", res.ReturnCode + " - " + res.ReturnMess);
                                    new AlertDialog.Builder(SessionInfoActivity.this)
                                            .setMessage("Phiên xét nghiệm đã kết thúc.")
                                            .setNegativeButton(android.R.string.ok, null)
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();
                                }
                                else if(res.ReturnCode == -31) //session da ket thuc
                                {
                                    // Log.e("GroupTest", res.ReturnCode + " - " + res.ReturnMess);
                                    new AlertDialog.Builder(SessionInfoActivity.this)
                                            .setMessage("Không tìm thấy phiên xét nghiệm.")
                                            .setNegativeButton(android.R.string.ok, null)
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();
                                }
                                else{
                                    Log.e("GroupTest", res.ReturnCode + " - " + res.ReturnMess);
                                    new AlertDialog.Builder(SessionInfoActivity.this)
                                            .setMessage("Tham gia phiên xét nghiệm không thành công. Vui lòng thử lại sau.")
                                            .setNegativeButton(android.R.string.ok, null)
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();
                                }
                            }catch (Exception e){
                                Log.e("SessionInfoActivity", e.toString(), e);
                                new android.app.AlertDialog.Builder(SessionInfoActivity.this)
                                        .setMessage("Lỗi xử lý.")
                                        .setNegativeButton("OK", null)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }

                        }
                    }, null, Request.Method.POST);
                }
            });


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
          if(!m.find()){
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
                              }
                              SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                              SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                              tenphien.setText(res.Data.SessionName);
                              province.setText(res.Data.ProvinceName);
                              district.setText(res.Data.DistrictName);
                              ward.setText(res.Data.WardName);
                              address.setText(res.Data.Address);
                              chooseTime.setText(timeFormat.format(res.Data.TestingDate));
                              chooseDate.setText(dateFormat.format(res.Data.TestingDate));
                              cause.setText(res.Data.Purpose);
                              leader.setText(res.Data.Account);
                          }catch (Exception e){
                              Log.e("GetSession", "", e);
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
                          }
                      }
                      else{
                          Log.e("GetSession", res.ReturnCode + " - " + res.ReturnMess);
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
                      }
                  }catch (Exception e){
                      Log.e("getSessionIf", e.toString(), e);
                      new android.app.AlertDialog.Builder(SessionInfoActivity.this)
                              .setMessage("Lỗi xử lý.")
                              .setNegativeButton("OK", null)
                              .setIcon(android.R.drawable.ic_dialog_alert)
                              .show();
                  }

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
}