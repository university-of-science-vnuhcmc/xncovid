package com.hcdc.xncovid;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hcdc.xncovid.adapter.GroupItemAdapter;
import com.hcdc.xncovid.model.GroupedUserInfo;
import com.hcdc.xncovid.util.Util;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.SortedSet;
import java.util.TreeSet;

public class ListGroupXnActivity extends AppCompatActivity {
    String xn_session; //ma ong nghiem
    private TextView xn_code, txt_total;
    GroupItemAdapter groupAdapter;
     ArrayList<GroupedUserInfo> list;
    ListView listViewGroupItem;
    String kbyt_id; //ma dinh danh ng khai bao y te
   SortedSet<String> setUID;
   LinearLayout btnStartGroup;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_group_xn);
        xn_session = getIntent().getExtras().getString("xn_session");
        xn_code=(TextView) findViewById(R.id.txt_xncode);
        xn_code.setText(xn_session);
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
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2)
        {
            kbyt_id =data.getStringExtra("kbyt_uid");
            boolean isOnline = data.getBooleanExtra("is_online", false);
            GroupedUserInfo newObj = null;
            if( kbyt_id != null && !kbyt_id.isEmpty()){

               //kt co trung uid ko, co thi bao loi va bo qua
                if(setUID.contains(kbyt_id)){
                    new AlertDialog.Builder(this)
                            .setTitle("Thêm mã định danh không thành công")
                            .setMessage("Mã đinh danh đã tồn tại trong mẫu xét nghiệm này!")
                            .setIcon(android.R.drawable.ic_delete)
                            .setNeutralButton("OK",null)
                            .show();
                }
                else {
                    newObj = new GroupedUserInfo(kbyt_id, isOnline);
                    list.add(newObj);
                    setUID.add((kbyt_id));
                    txt_total.setText("("+groupAdapter.getCount()+"/10)");
                    btnStartGroup.setBackground(getResources().getDrawable(R.drawable.rectangle_btn_group_enable));
                    groupAdapter.notifyDataSetChanged();
                }

            }

        }
    }
}