package com.hcdc.xncovid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ConfirmXNCodeActivity extends AppCompatActivity {
    String xn_session, session_code;
    private TextView xn_code;
    private Button btnConfirm;
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