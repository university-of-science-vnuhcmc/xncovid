package com.hcdc.xncovid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class ConfirmXNCodeActivity extends AppCompatActivity {
    String xn_session;
    int grouped_count;
    private TextView xn_code;
    private Button btnConfirm;
    private EditText _maxGroup;
    private CheckBox  _isDefault;
    private boolean isDefault;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_confirm_xncode);
            Intent intent=this.getIntent();
            xn_session = getIntent().getExtras().getString("xn_session");
            xn_code=(TextView) findViewById(R.id.txt_xn_code);
            xn_code.setText(xn_session);
            btnConfirm = findViewById(R.id.btnxncode_confirm);
            //if(getIntent().hasExtra("session_code")){
            //    session_code = getIntent().getExtras().getString("session_code");
            //}
            grouped_count = getIntent().getExtras().getInt("grouped_count");
            _maxGroup =(EditText) findViewById(R.id.input_max_group);
            _isDefault =(CheckBox) findViewById(R.id.checkbox_id);
            int groupd_count = ((MyApplication) getApplication()).getGroupMaxCount();
            isDefault = ((MyApplication) getApplication()).isDefaultMaxGroup();
           // if(groupd_count < 1 || isDefault == false){
           //     _maxGroup.setText(10+"");
            // }else {
           //     _maxGroup.setText(groupd_count+"");
           // }
            _maxGroup.setText(groupd_count+"");
            _isDefault.setChecked(isDefault);
        }catch (Exception e){
            Log.e("ConfirmXNCodeActivity", e.toString(), e);
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
        try {

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String maxGroup =  _maxGroup.getText().toString();
                if(maxGroup == null || maxGroup == ""){
                    new AlertDialog.Builder(ConfirmXNCodeActivity.this)
                            .setMessage("Vui lòng nhập số lượng tối đa gộp cho mẫu này!")
                            .setNegativeButton("OK", null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }
                int _max =  0;
                try {
                    _max =  Integer.parseInt(maxGroup);
                }catch (Exception e){
                    _max = 0;
                }

                if(_max < 1){
                    new AlertDialog.Builder(ConfirmXNCodeActivity.this)
                            .setMessage("Vui lòng nhập số lượng tối đa gộp cho mẫu này! (tối thiểu là 1)")
                            .setNegativeButton("OK", null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }

                if(_max < grouped_count){
                    new AlertDialog.Builder(ConfirmXNCodeActivity.this)
                            .setMessage("Vui lòng nhập số lượng mẫu Lớn hơn hoặc bằng số lượngg mẫu đã gộp ("+ grouped_count +" mẫu).")
                            .setNegativeButton("OK", null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }

                ((MyApplication) getApplication()).setGroupMaxCount(_max);
                isDefault = _isDefault.isChecked();
                ((MyApplication) getApplication()).setDefaultMaxGroup(isDefault);
               // Intent intent = new Intent(getApplicationContext(), ListGroupXnActivity.class);
                //intent.putExtra("xn_session", xn_session);
                //intent.putExtra("session_code", session_code);
                //startActivity(intent);
                Intent intent=new Intent();
                setResult(4,intent);
                finish();
            }
        });
    }catch (Exception e){
        Log.e("ConfirmXNCodeActivity", e.toString(), e);
        new AlertDialog.Builder(this)
                .setMessage("Lỗi xử lý.")
                .setNegativeButton("OK", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    }
}