package com.hcdc.xncovid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScanSessionActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler   {
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView mScannerView;
    String scanContent;
    private  String urlKBYTOnline = "https://kbytcq.khambenh.gov.vn/";
    private  String regexKBYTId = "id=([A-z0-9-]*)";
    private String urlGetUserInfo = "https://kbytcq.khambenh.gov.vn/";
    private  String contentRegexs = "phone::pattern==so_dien_thoai=(?<sodienthoai>[0-9]+),==>key==sodienthoai\n" +
            "fullname::pattern==so_dien_thoai=[0-9]+, ten=(?<hoten>[^,]*),==>key==hoten\n" +
            "gent::pattern==gioi_tinh=(?<gioitinh>\\d{1})==>key==gioitinh\n" +
            "birthdateyear::pattern==namsinh=(?<namsinh>\\d{4})==>key==namsinh\n" +
            "address::pattern==dia_chi=(?<diadiem>[^,]*)==>key==diadiem##pattern==xaphuong=.*ten=(?<xaphuong>[^,]+), quanhuyen_id==>key==xaphuong##pattern==quanhuyen=.*ten=(?<quanhuyen>[^,]+), tinhthanh_id==>key==quanhuyen##pattern==tinhthanh=.*ten=(?<tinhthanh>[^,]+), quocgia_id==>key==tinhthanh::out==%diadiem%###, ###%xaphuong%###, ###%quanhuyen%###, ###%tinhthanh%###.";
    int scanQRType = 0; // 0: QR Session, 1: QR ong xn, 2: QR to khai y te
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scansession);

        if( getIntent().getExtras() != null){
            scanQRType = getIntent().getExtras().getInt("scan_qr_type");

        }

        int apiVersion = android.os.Build.VERSION.SDK_INT;
        if (apiVersion >= android.os.Build.VERSION_CODES.M) {
            if (!checkPermission()) {
                requestPermission();
            }
        }

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
        mScannerView.startCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);

    }
    @Override
    public void onPause() {
        super.onPause();
        // Stop camera on pause
        mScannerView.stopCamera();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Stop camera on pause
        mScannerView.stopCamera();
    }

    public void clickCancel(View view)
    {

        onBackPressed();
    }


    @Override
    public void handleResult(Result result) {
        String txtScanedResult = result.getText();

        if(scanQRType == 2 &&txtScanedResult.startsWith(urlKBYTOnline)){
                Pattern p = Pattern.compile(regexKBYTId);
                Matcher m = p.matcher(txtScanedResult);
                m.find();
                String id = m.group(1);
                scanContent = id;
        }else {
            scanContent = txtScanedResult.trim();
        }

        showResult();
    }

    private  void showResult(){
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
        Intent intent = new Intent(getApplicationContext(), tmpclass);
        intent.putExtra("xn_session", scanContent);
        startActivity(intent);
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