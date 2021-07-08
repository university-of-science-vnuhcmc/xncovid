package com.hcdc.xncovid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainLeaderActivity extends AppCompatActivity {
private LinearLayout layoutCreateTest, layoutListTest;
String sessionId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionId = null;
        setContentView(R.layout.activity_main);
        Intent intent=this.getIntent();
        if(intent.getExtras() != null){
            sessionId = intent.getExtras().getString("xn_session");
        }

        setContentView(R.layout.activity_main);

        layoutCreateTest = findViewById(R.id.createTest);
        layoutListTest = findViewById(R.id.listTest);
       // layoutListTest.setEnabled(false);
        layoutListTest.setBackground(getResources().getDrawable( R.drawable.rectangle_menu_disable));
    }
    private void showMessage(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainLeaderActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    public void signOut(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("isLogout", true);
        startActivity(intent);
    }
}