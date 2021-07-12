package com.hcdc.xncovid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.hcdc.xncovid.adapter.GroupItemAdapter;
import com.hcdc.xncovid.model.CitizenInfor;
import com.hcdc.xncovid.model.GroupTestReq;
import com.hcdc.xncovid.model.GroupTestRes;
import com.hcdc.xncovid.model.GroupedUserInfo;
import com.hcdc.xncovid.model.Session;
import com.hcdc.xncovid.model.UserInfo;
import com.hcdc.xncovid.util.Caller;
import com.hcdc.xncovid.util.DetectKBYTPattern;
import com.hcdc.xncovid.util.ICallback;
import com.hcdc.xncovid.util.Util;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.SortedSet;
import java.util.TreeSet;



public class ListGroupXnActivity extends AppCompatActivity {
    String xn_session, session_code; //ma ong nghiem
    private TextView xn_code, txt_total;
    GroupItemAdapter groupAdapter;
     ArrayList<GroupedUserInfo> list;
    ListView listViewGroupItem;
    String kbyt_id; //ma dinh danh ng khai bao y te
   SortedSet<String> setUID;
   LinearLayout btnStartGroup;
    Hashtable<String, GroupedUserInfo> hashObj;
    Boolean isStop  = false;
    Session session = null;
    private View mLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_list_group_xn);
            xn_session = getIntent().getExtras().getString("xn_session");
            xn_code=(TextView) findViewById(R.id.txt_xncode);
            xn_code.setText(xn_session);

             session = ((MyApplication) getApplication()).getSession();

            if(session != null){
                session_code = session.SessionID + "";
            }else {
                new AlertDialog.Builder(ListGroupXnActivity.this)
                        .setMessage("Không tìm thấy phiên xét nghiệm tham gia.")
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

            //if(getIntent().hasExtra("session_code")){
            //    session_code = getIntent().getExtras().getString("session_code");
            //}

            if(setUID == null){
                setUID = new TreeSet<>();
            }
            if(list == null){
                list = new ArrayList<>();
            }

            groupAdapter = new GroupItemAdapter(list, this, setUID);
            listViewGroupItem = (ListView) findViewById(R.id.list_ds_xn);
            listViewGroupItem.setAdapter(groupAdapter);

            RelativeLayout btnScan = findViewById((R.id.lst_xn_users));
            btnScan.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(ListGroupXnActivity.this,ScanSessionActivity.class);
                    intent.putExtra("scan_qr_type", 2); //scan to khai bao y te
                    startActivityForResult(intent, 2);// Activity is started with requestCode 2

                }
            });
            txt_total = findViewById(R.id.txt_count);
            btnStartGroup = findViewById(R.id.btn_confirm_new_group);
            btnStartGroup.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    if(groupAdapter.getCount() < 10){
                        String txtUId = "<font color='#001AFF'><b>"+xn_session+"</b></font>";
                        String htmlcontent = "<font color='#FF0000'><i>! Hiện chỉ mới có "
                                +groupAdapter.getCount()
                                +" / 10 người, bạn muốn có tiếp tục</i></font>";
                        new Util().showMessage("Thực hiện gom nhóm có mã xét nghiệm",
                                txtUId,
                                htmlcontent,
                                "Tiếp tục",
                                "Hủy",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        isStop = true;
                                        if(!UploadGroupXN()){
                                            isStop = false;
                                        }
                                    }
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        isStop = false;
                                    }
                                }, ListGroupXnActivity.this);
                    }
                }
            });
            setUIRef();
        }catch (Exception e){
            Log.e("ListGroupXnActivity", e.toString(), e);
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
            if(session != null){
                session_code = session.SessionID + "";
            }else {
                new AlertDialog.Builder(ListGroupXnActivity.this)
                        .setMessage("Không tìm thấy phiên xét nghiệm tham gia.")
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
            Log.e("ListGroupXnActivity", e.toString(), e);
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
        intent.putExtra("flag", 1);//co cho biet la tu view list group
        startActivity(intent);
        finish();
    }
    public  Boolean UploadGroupXN(){
        final Boolean[] isOK = {true};
        showLoading();
        try{
            GroupTestReq req  = new GroupTestReq();
            req.CovidTestingSessionID = Long.parseLong(session_code);
            UserInfo userInfo = ((MyApplication) getApplication()).getUserInfo();

            if(userInfo != null){
                req.AccountID = userInfo.AccountID;
            }

            req.CovidSpecimenCode = xn_session;
            req.SpecimenAmount = groupAdapter.getCount() + "";
            ArrayList<CitizenInfor> tmp = new ArrayList<>();
            for (String uid: setUID) {
                CitizenInfor objReq = new CitizenInfor();
                if(hashObj != null && hashObj.containsKey(uid)) //co trong hash
                {
                    GroupedUserInfo obj = hashObj.get(uid);
                    objReq.Address = obj.getAddress();
                    objReq.FullName = obj.getFullname();
                    objReq.HandPhone = obj.getPhone();
                    objReq.QRCode = obj.getUid();
                    objReq.QRCodeType = obj.isOnline() == true ? 1 : 2;
                    objReq.YearOfBirth = obj.getBirthYear();
                    objReq.PartnerWardID = obj.getWardID();
                    objReq.PartnerWardName = obj.getWard();
                    objReq.PartnerDistrictID = obj.getDistrictID();
                    objReq.PartnerDistrictName = obj.getDistrict();
                    objReq.PartnerProvinceID = obj.getProvinceID();
                    objReq.PartnerProvinceName = obj.getProvince();
                    tmp.add(objReq);
                    continue;
                }
                //truong hop con lai la online ma chua co lay duoc thong tin
                objReq.QRCode = uid;
                objReq.QRCodeType =  1;
                tmp.add(objReq);
            }
            req.CitizenInfor = tmp;
            Caller caller = new Caller();
            caller.call(this, "GroupTest", req, GroupTestRes.class, new ICallback() {
                @Override
                public void callback(Object response) {
                    try{
                        GroupTestRes res = (GroupTestRes) response;
                        if (res.ReturnCode == 1) {
                            StartMainStaffAcitivity();
                        } else if (res.ReturnCode == -17) //ma ong nghiem da ton tai ton phein xet nghiem
                        {
                            Log.e("GroupTest", res.ReturnCode + " - " + res.ReturnMess);
                            new AlertDialog.Builder(ListGroupXnActivity.this)
                                    .setMessage("Mã ống nghiệm đã tồn tại trong phiên xét nghiệm.")
                                    .setNegativeButton(android.R.string.ok, null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            isOK[0] = false;
                        }
                        else if (res.ReturnCode == -16) //trung QR
                        {
                            Log.e("GroupTest", res.ReturnCode + " - " + res.ReturnMess);
                            String strcontent = "Danh sách mã tồn tại:";
                            String[] arrIds = res.ReturnMess.split("|");
                            for (String uid: arrIds) {
                                strcontent += "<br/><font color='#FF0000'><i>" + uid + "</i></font>";
                            }
                            new Util().showMessage("Mã gộp đã tồn tại trong các mẫu gộp củ phiên.",
                                    strcontent,
                                    null,
                                    null,
                                    "OK",
                                    null, null, ListGroupXnActivity.this);
                            isOK[0] = false;
                        }
                        else if (res.ReturnCode == -31 || res.ReturnCode == -32)
                        {
                            Log.e("GroupTest", res.ReturnCode + " - " + res.ReturnMess);
                            new AlertDialog.Builder(ListGroupXnActivity.this)
                                    .setMessage("Phiên xét nghiêm đã kết thúc hoặc không tồn tại.")
                                    .setNegativeButton(android.R.string.ok, null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            isOK[0] = false;
                        }
                        else{
                            Log.e("GroupTest", res.ReturnCode + " - " + res.ReturnMess);
                            new AlertDialog.Builder(ListGroupXnActivity.this)
                                    .setMessage("Tạo nhóm xét nghiệm gộp không thành công. Vui lòng thử lại sau.")
                                    .setNegativeButton(android.R.string.ok, null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            isOK[0] = false;
                        }
                    }catch (Exception e){
                        Log.e("ListGroupXnActivity", e.toString(), e);
                        new android.app.AlertDialog.Builder(ListGroupXnActivity.this)
                                .setMessage("Lỗi xử lý.")
                                .setNegativeButton("OK", null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        isOK[0] = false;
                    }
                    hideLoading();

                }
            }, null, Request.Method.POST);
            hideLoading();
            return isOK[0];
        } catch (Exception e){
            Log.e("ListGroupXnActivity", e.toString(), e);
            new android.app.AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK",  null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            isOK[0] = false;
            hideLoading();
            return   isOK[0] ;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            // check if the request code is same as what is passed  here it is 2
            if(requestCode==2)
            {
                kbyt_id =data.getStringExtra("kbyt_uid");
                boolean isOnline = data.getBooleanExtra("is_online", false);
                GroupedUserInfo newObj = null;
                if( kbyt_id != null && !kbyt_id.isEmpty()){

                    //kt co trung uid ko, co thi bao loi va bo qua
                    if(setUID.contains(kbyt_id)){
                        Toast.makeText(this, "Mã đinh danh đã tồn tại trong mẫu xét nghiệm này!",
                                Toast.LENGTH_LONG).show();
                    }
                    else {
                        newObj = new GroupedUserInfo(kbyt_id, isOnline);
                        list.add(newObj);
                        setUID.add((kbyt_id));
                        txt_total.setText("("+groupAdapter.getCount()+"/10)");
                        if(groupAdapter.getCount() >= 10){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                txt_total.setTextAppearance(R.style.blue_20);
                            }else {
                                txt_total.setTextAppearance(this , R.style.blue_20);
                            }
                        }
                        btnStartGroup.setBackground(getResources().getDrawable(R.drawable.rectangle_btn_group_enable));
                        if(isOnline){
                            new CrawlKBYTAsyncTask(this).execute(kbyt_id);
                        }
                        else {
                            AddNewGroupItem(newObj);
                        }
                        groupAdapter.notifyDataSetChanged();
                    }

                }

            }
        } catch (Exception e){
            Log.e("onActivityResult", e.toString(), e);
            new android.app.AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }

    public void AddNewGroupItem(GroupedUserInfo info){
        try {
            if(info == null){
                return;
            }

            String uid = info.getUid();

            if(!setUID.contains(uid)){
                return;
            }
            if(hashObj == null){
                hashObj = new Hashtable<String, GroupedUserInfo>();
            }
            if(hashObj.containsKey(uid)){
                return;
            }
            hashObj.put(uid, info);
        }catch (Exception e){
            Log.e("AddNewGroupItem", e.toString(), e);
            new android.app.AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }

    private class CrawlKBYTAsyncTask extends AsyncTask<String, Void, GroupedUserInfo> {

        //khai báo Activity để lưu trữ địa chỉ của MainActivity
        Activity contextCha;
        private String urlGetUserInfo  = "https://kbytcq.khambenh.gov.vn/api/v1/tokhai_yte/";
        //constructor này được truyền vào là MainActivity
        public CrawlKBYTAsyncTask(Activity ctx)
        {
            contextCha=ctx;
            try {
                urlGetUserInfo = ((MyApplication) ctx.getApplication()).getUrl(); //"https://kbytcq.khambenh.gov.vn/";

            }catch (Exception e){
                Log.e("CrawlKBYTAsyncTask", e.toString(), e);
            }

        }

        @Override
        protected GroupedUserInfo doInBackground(String... kbytID) {
            try{
                if(!isStop){
                    Caller caller = new Caller();

                    String api =  kbytID[0];
                    caller.call(contextCha, api, null, String.class, new ICallback() {
                        @Override
                        public void callback(Object response) {
                            try{
                                JSONObject objJSON = (JSONObject) response;
                                String strContent = objJSON.toString();
                                AddNewGroupItem( DetectKBYTPattern.instance(contextCha).DetectInfo(strContent, kbytID[0]));

                            }catch (Exception e){
                                Log.e("doInBackground", e.toString(), e);
                            }

                        }
                    }, urlGetUserInfo, Request.Method.GET);

                }
                return  null;
            }catch (Exception e){
                Log.e("doInBackground", e.toString(), e);
                return  null;
            }

        }


        //hàm này sẽ được thực hiện đầu tiên
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }


        /**
         * sau khi tiến trình thực hiện xong thì hàm này sảy ra
         */
        @Override
        protected void onPostExecute(GroupedUserInfo result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if(!isStop){
                AddNewGroupItem(result);
            }
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