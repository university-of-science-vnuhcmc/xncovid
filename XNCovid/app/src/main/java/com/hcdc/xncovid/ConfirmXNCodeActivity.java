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
    String xn_session, session_code;
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
            _maxGroup =(EditText) findViewById(R.id.input_max_group);
            _isDefault =(CheckBox) findViewById(R.id.checkbox_id);
            int groupd_count = ((MyApplication) getApplication()).getGroupMaxCount();
            isDefault = ((MyApplication) getApplication()).isDefaultMaxGroup();
            if(groupd_count < 1 || isDefault == false){
                _maxGroup.setText(10+"");
            }else {
                _maxGroup.setText(groupd_count+"");
            }
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
                ((MyApplication) getApplication()).setGroupMaxCount(Integer.parseInt(maxGroup));
                isDefault = _isDefault.isChecked();
                ((MyApplication) getApplication()).setDefaultMaxGroup(isDefault);
                Intent intent = new Intent(getApplicationContext(), ListGroupXnActivity.class);
                intent.putExtra("xn_session", xn_session);
                //intent.putExtra("session_code", session_code);
                startActivity(intent);
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