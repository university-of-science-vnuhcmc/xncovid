package com.hcdc.xncovid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.hcdc.xncovid.util.Util;

public class MainStaffActivity extends AppCompatActivity {
private LinearLayout layoutJoinTest, layoutListTest, layoutnewGroup, layoutlistGroup, layoutensession;
String sessionId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionId = null;
        setContentView(R.layout.activity_main_staff);
        MyApplication myapp = (MyApplication) getApplication();
        TextView nameView = findViewById(R.id.name);
        nameView.setText(myapp.getName());
        Intent intent=this.getIntent();
        if(intent.getExtras() != null){
            sessionId = intent.getExtras().getString("xn_session");
        }

        layoutJoinTest =findViewById(R.id.joinTest);
        layoutListTest =findViewById(R.id.listTest);
        layoutnewGroup =findViewById(R.id.newGroup);
        layoutlistGroup =findViewById(R.id.listGroup);
        layoutensession =findViewById(R.id.endSession);

        //layoutJoinTest.setEnabled(false);
        layoutJoinTest.setBackground(getResources().getDrawable( R.drawable.rectangle_menu_disable));
       // layoutListTest.setEnabled(false);
        layoutListTest.setBackground(getResources().getDrawable( R.drawable.rectangle_menu_disable));
        //layoutnewGroup.setEnabled(false);
        layoutnewGroup.setBackground(getResources().getDrawable( R.drawable.rectangle_menu_disable));
        //layoutJoinTest.setEnabled(false);
        layoutlistGroup.setBackground(getResources().getDrawable( R.drawable.rectangle_menu_disable));

        if(sessionId != null && sessionId != "")
        {
            layoutnewGroup.setEnabled(true);
            layoutnewGroup.setBackground(getResources().getDrawable( R.drawable.rectangle_menu_enable));
            layoutnewGroup.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ScanSessionActivity.class);
                    intent.putExtra("scan_qr_type", 1);
                    intent.putExtra("xn_session", sessionId);
                    startActivity(intent);
                }
            });
            String htmlcontent = "Điều này sẽ được thông báo đến trưởng nhóm <b>Nguyễn Văn B</b> !";

            layoutensession.setEnabled(true);
            layoutensession.setBackground(getResources().getDrawable( R.drawable.end_session_enable));
            layoutensession.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                   new Util().showMessage("Xác nhận thoát khỏi phiên xét nghiệm",
                            "XN_Covid19_HCM_123",
                            htmlcontent,
                            "Thoát",
                            "Hủy",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), MainStaffActivity.class);
                                    // intent.putExtra("xn_session", "");
                                    startActivity(intent);
                                }
                            }, null, MainStaffActivity.this);

                }
            });
        }else {
            layoutJoinTest.setEnabled(true);
            layoutJoinTest.setBackground(getResources().getDrawable( R.drawable.rectangle_menu_enable));
            layoutJoinTest.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ScanSessionActivity.class);
                    intent.putExtra("scan_qr_type", 0);
                    startActivity(intent);
                }
            });
        }
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
    private void showMessage(String tile, String subtitle, String message, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainStaffActivity.this);
        // set the custom layout
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.activity_customdialog,
                        null);
        final TextView txtTitle = customLayout.findViewById(R.id.dialog_title);
        final TextView txtSubTitle = customLayout.findViewById(R.id.dialog_subtitle);
        final TextView txtcontent = customLayout.findViewById(R.id.content_1);

        txtTitle.setText(tile);
        txtSubTitle.setText(subtitle);
        txtcontent.setText(android.text.Html.fromHtml(message));

        builder.setView(customLayout);
    }
}