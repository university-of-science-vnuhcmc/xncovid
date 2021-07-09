package com.hcdc.xncovid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ConfirmXNCodeActivity extends AppCompatActivity {
    String xn_session, session_code;
    private TextView xn_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_xncode);
        Intent intent=this.getIntent();
        xn_session = getIntent().getExtras().getString("xn_session");
        xn_code=(TextView) findViewById(R.id.txt_xn_code);
        xn_code.setText(xn_session);

        if(getIntent().hasExtra("session_code")){
            session_code = getIntent().getExtras().getString("session_code");
        }
        final Button btnConfirm = findViewById(R.id.btnxncode_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListGroupXnActivity.class);
                intent.putExtra("xn_session", xn_session);
                intent.putExtra("session_code", session_code);
                startActivity(intent);
            }
        });

    }
}