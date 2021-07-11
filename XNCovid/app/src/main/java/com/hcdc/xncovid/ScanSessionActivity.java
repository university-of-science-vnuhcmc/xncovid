package com.hcdc.xncovid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;
import com.hcdc.xncovid.model.SessionInfo;
import com.hcdc.xncovid.model.UserInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScanSessionActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler   {
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView mScannerView;
    String scanContent, xn_session;
    private  String urlKBYTOnline = "https://kbytcq.khambenh.gov.vn/";
    private  String regexKBYTId = "id=([A-z0-9-]*)";
    int scanQRType = 0; // 0: QR Session, 1: QR ong xn, 2: QR to khai y te lan dau
    ViewGroup contentFrame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scansession);
        try{
            try{
                urlKBYTOnline = ((MyApplication)  getApplication()).getDomain();

                regexKBYTId = ((MyApplication) getApplication()).getId();
            }catch (Exception e){
                urlKBYTOnline = "https://kbytcq.khambenh.gov.vn/";
                regexKBYTId = "id=([A-z0-9-]*)";
            }

            if( getIntent().getExtras() != null){
                scanQRType = getIntent().getExtras().getInt("scan_qr_type");
            }
            SessionInfo sessionInfo = ((MyApplication) getApplication()).getSessionInfo();

            if(sessionInfo != null){
                xn_session = sessionInfo.SessionID + "";
            }

            xn_session = getIntent().getExtras().getString("xn_session");

            int apiVersion = android.os.Build.VERSION.SDK_INT;
            if (apiVersion >= android.os.Build.VERSION_CODES.M) {
                if (!checkPermission()) {
                    requestPermission();
                }
            }
            contentFrame = (ViewGroup) findViewById(R.id.content_frame);
            mScannerView = new ZXingScannerView(this);
            contentFrame.addView(mScannerView);
        }catch (Exception e){
            Log.e("ScanSessionActivity", e.toString(), e);
            new AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }


    @Override
    public void onResume() {
        super.onResume();

        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }
    @Override
    public void onPause() {
        super.onPause();
        // Stop camera on pause
        mScannerView.stopCameraPreview();
        mScannerView.stopCamera();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Stop camera on pause
        mScannerView.stopCameraPreview();
        mScannerView.stopCamera();
    }

    public void clickCancel(View view)
    {
        onBackPressed();
    }


    @Override
    public void handleResult(Result result) {
        String txtScanedResult = result.getText();
        boolean isOnline =false;
        try{
            mScannerView.removeAllViews(); //<- here remove all the views, it will make an Activity having no View
            mScannerView.stopCamera(); //<- then stop the camera

            if((scanQRType == 2) &&txtScanedResult.startsWith(urlKBYTOnline)){
                Pattern p = Pattern.compile(regexKBYTId);
                Matcher m = p.matcher(txtScanedResult);
                m.find();
                String id = m.group(1);
                scanContent = id;
                isOnline = true;
            }else {
                scanContent = txtScanedResult.trim();
            }

            showResult(isOnline);
        }catch (Exception e){
            Log.e("ScanSessionActivity", e.toString(), e);
            new AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }

    private  void showResult(boolean isOnline){
        Class tmpclass = null;
        switch (scanQRType){
            case 0:
                tmpclass = SessionInfoActivity.class;
                break;
            case 1:
                tmpclass = ConfirmXNCodeActivity.class;
                break;

            case 2:
                tmpclass = ConfirmXNCodeActivity.class;
                break;
        }
        if(scanQRType == 2){
            Intent intent=new Intent();
            intent.putExtra("kbyt_uid", scanContent);
            intent.putExtra("is_online", isOnline);
            //intent.putExtra("xn_session", xn_session);
            setResult(2,intent);
            finish();//finishing activity
        }else {
            Intent intent = new Intent(getApplicationContext(), tmpclass);
            intent.putExtra("xn_session", scanContent);
            //intent.putExtra("session_code", xn_session);
            startActivity(intent);
        }

    }
    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                showMessage("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessage(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ScanSessionActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}