package com.hcdc.xncovid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainLeaderActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_leader);
        MyApplication myapp = (MyApplication) getApplication();
        TextView nameView = findViewById(R.id.name);
        nameView.setText(myapp.getUserInfo().Name);
    }
    public void signOut(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn muốn đăng xuất?")
                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                        intent.putExtra("isLogout", true);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Hủy", null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }
    public void createSession(View v){
        Intent intent = new Intent(this, CreateSessionActivity.class);
        startActivity(intent);
    }
    public void showQR(View v){
        Intent intent = new Intent(this, QRSessionActivity.class);
        intent.putExtra("SessionName", "");
        intent.putExtra("SessionID", (long)1000);
        intent.putExtra("IsNew", false);
        startActivity(intent);
    }
    public void endSession(View v){
        Intent intent = new Intent(this, QRSessionActivity.class);
        intent.putExtra("SessionName", "");
        intent.putExtra("SessionID", (long)1000);
        intent.putExtra("IsNew", false);
        startActivity(intent);
    }
}