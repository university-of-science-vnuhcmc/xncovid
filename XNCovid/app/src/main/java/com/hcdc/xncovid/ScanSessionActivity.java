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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.hcdc.xncovid.model.Session;
import com.hcdc.xncovid.util.Util;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Hashtable;
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
    private RelativeLayout lXNInfo;
    private LinearLayout lReadQR, lInputXNCode;
    public View layout_dialog_add;
    LayoutInflater inflater;
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
            Session session = ((MyApplication) getApplication()).getSession();

            if(session != null){
                xn_session = session.SessionID + "";
            }

            xn_session = getIntent().getExtras().getString("xn_session");

            int apiVersion = android.os.Build.VERSION.SDK_INT;
            if (apiVersion >= android.os.Build.VERSION_CODES.M) {
                if (!checkPermission()) {
                    requestPermission();
                }
            }
            lXNInfo = (RelativeLayout) findViewById(R.id.xn_info_progress);
            lXNInfo.setVisibility(View.GONE);

            lReadQR = (LinearLayout) findViewById(R.id.uload_qr);
            lReadQR.setVisibility(View.GONE);

            lInputXNCode = (LinearLayout) findViewById(R.id.input_xn_code);
            lInputXNCode.setVisibility(View.GONE);

            contentFrame = (ViewGroup) findViewById(R.id.content_frame);
            mScannerView = new ZXingScannerView(this);
            contentFrame.addView(mScannerView);

            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            layout_dialog_add = inflater.inflate(R.layout.layout_custom_add_xnccode_dialog, (ViewGroup)   findViewById(R.id.layout_add_xn_code_dialog));
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
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            //the case is because you might be handling multiple request codes here
            case 111:
                Uri selectedImage = imageReturnedIntent.getData();
                InputStream imageStream = null;
                try {
                    //getting the image
                    imageStream = getContentResolver().openInputStream(selectedImage);
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Không tìm thấy hình QR", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                //decoding bitmap
                Bitmap bMap = BitmapFactory.decodeStream(imageStream);
                int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
                // copy pixel data from the Bitmap into the 'intArray' array
                bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

                LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                MultiFormatReader reader = new MultiFormatReader();// use this otherwise
                try {
                    Hashtable<DecodeHintType, Object> decodeHints = new Hashtable<DecodeHintType, Object>();
                    decodeHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
                    decodeHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);

                    Result result = reader.decode(bitmap, decodeHints) ;
                    scanContent =  result.getText().toString();
                    showResult(false);
                } catch (NotFoundException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(scanQRType == 0) //Join Session
        {
            lReadQR.setVisibility(View.VISIBLE);
            lInputXNCode.setVisibility(View.GONE);
            lXNInfo.setVisibility(View.GONE);

            lReadQR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // System.out.println("called hrre>>>");
                    Intent pickIntent = new Intent(Intent.ACTION_PICK);
                    pickIntent.setDataAndType( android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(pickIntent, 111);
                }
            });

        }else  if(scanQRType == 1) // Quet ma XN
        {
            lReadQR.setVisibility(View.GONE);
            lInputXNCode.setVisibility(View.VISIBLE);
            lXNInfo.setVisibility(View.GONE);
            lInputXNCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputXNCode();
                }
            });
        }else //QR type == 2
        {
            lReadQR.setVisibility(View.GONE);
            lInputXNCode.setVisibility(View.GONE);
            lXNInfo.setVisibility(View.VISIBLE);
            if( getIntent().hasExtra("xn_code") == true){
                ((TextView)  findViewById(R.id.txt_Scan_xncode)).setText(getIntent().getExtras().getString("xn_code"));
            }
            int groupd_count = ((MyApplication) getApplication()).getGroupMaxCount();
            if( getIntent().hasExtra("grouped_count") == true){
                int _counting = getIntent().getExtras().getInt("grouped_count");
                ((TextView)  findViewById(R.id.txt_scan_count)).setText(_counting +"/"+ groupd_count);
            }
        }
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

        if(scanQRType == 2){
            Intent intent=new Intent();
            intent.putExtra("kbyt_uid", "");
            intent.putExtra("is_online", true);
            //intent.putExtra("xn_session", xn_session);
            setResult(2,intent);
            finish();//finishing activity
        }else {
            onBackPressed();
        }

    }


    @Override
    public void handleResult(Result result) {
        String txtScanedResult = result.getText();
        boolean isOnline =false;
        try{
            mScannerView.removeAllViews(); //<- here remove all the views, it will make an Activity having no View
            mScannerView.stopCamera(); //<- then stop the camera

            if(scanQRType == 2){
                if(txtScanedResult.startsWith(urlKBYTOnline)) //online
                {
                    Pattern p = Pattern.compile(regexKBYTId);
                    Matcher m = p.matcher(txtScanedResult);
                    m.find();
                    String id = m.group(1);
                    scanContent = id;
                    isOnline = true;
                }else  //offline
                {
                    String tmpContent = txtScanedResult.trim();

                    String[] tmpArr = tmpContent.split("-");

                    if(tmpArr.length < 2){
                        new Util().showMessage("Mã gộp đã tồn tại trong các mẫu gộp của phiên.",
                                "",
                                "<p>Mã định danh này đã tồn tại hoặc không hợp lệ.</p>" +
                                        "<p>Vui lòng quay lại để quét mã khác.",
                                null,
                                "OK",
                                null, null, ScanSessionActivity.this);
                        return;
                    }
                    String _luhnCheck =  Util.GetLuhnCheckDigit(tmpArr[1].trim());
                    if(!_luhnCheck.equals(tmpArr[0].trim())){
                        new Util().showMessage("Mã gộp đã tồn tại trong các mẫu gộp của phiên.",
                                "",
                                "<p>Mã định danh này đã tồn tại hoặc không hợp lệ.</p>" +
                                        "<p>Vui lòng quay lại để quét mã khác.",
                                null,
                                "OK",
                                null, null, ScanSessionActivity.this);
                        return;
                    }
                    scanContent = txtScanedResult.trim();
                }

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
            finish();
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

    private  void  InputXNCode(){
        //------------------- build dialog add new ---------------------
        // Xây dựng cái view
        if (layout_dialog_add.getParent() != null) {// xóa các view ở lần bấm chuột trước
            ((ViewGroup) layout_dialog_add.getParent()).removeAllViews();
        }
        //layout_root should be the name of the "top-level" layout node in the dialog_layout.xml file.
        final EditText item_code = (EditText) layout_dialog_add.findViewById(R.id.item_code);  // editext này lấy ở file layout_custom_dialog

        //Building dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout_dialog_add);
        builder.setTitle("Nhập mã xét nghiệm");

        builder.setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // lấy dữ liệu người dùng nhập cho vào biến
               scanContent = item_code.getText().toString();
                // có dữ liệu rồi thì bạn gọi lệnh ghi vào csdl ở đây nhé
                Toast.makeText(getBaseContext(),"Bạn vừa nhập Mã xét nghiệm: " + scanContent , Toast.LENGTH_SHORT).show();
                dialog.dismiss(); // tắt dialog
                showResult(false);
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // tắt dialog
            }
        });
       builder.create(); // tạo dialog
        builder.show();
    }
}