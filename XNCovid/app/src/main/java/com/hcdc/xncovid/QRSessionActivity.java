package com.hcdc.xncovid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hcdc.xncovid.model.LocateInfor;
import com.hcdc.xncovid.model.Session;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

public class QRSessionActivity extends AppCompatActivity {
    private Session session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_qrsession);
            Bundle bundle = getIntent().getExtras();
            session = new Gson().fromJson(bundle.getString("Session"), Session.class);

            ((TextView) findViewById(R.id.sessionName)).setText(session.SessionName);
            ((TextView) findViewById(R.id.location)).setText(session.Address);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
            ((TextView)findViewById(R.id.time)).setText(timeFormat.format(session.getTestingDate()));
            ((TextView)findViewById(R.id.type)).setText(session.CovidTestingSessionTypeName);
            ((TextView) findViewById(R.id.leader)).setText(session.Account);
            if(session.CovidTestingSessionTypeID == 1){
                ((TextView)findViewById(R.id.cause1)).setText(session.Purpose);
                ((TextView)findViewById(R.id.target1)).setText(session.CovidTestingSessionObjectName);
                findViewById(R.id.type1).setVisibility(View.VISIBLE);
                findViewById(R.id.type2).setVisibility(View.GONE);
            } else {
                ((TextView)findViewById(R.id.relativeTarget)).setText(session.Purpose);
                ((TextView)findViewById(R.id.cause2)).setText(session.DesignatedReasonName);
                ((TextView)findViewById(R.id.target2)).setText(session.CovidTestingSessionObjectName);
                findViewById(R.id.type1).setVisibility(View.GONE);
                findViewById(R.id.type2).setVisibility(View.VISIBLE);
            }

            QRCodeWriter writer = new QRCodeWriter();
            try {
                BitMatrix bitMatrix = writer.encode(String.valueOf(session.SessionID), BarcodeFormat.QR_CODE, 300, 300);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }
                ((ImageView) findViewById(R.id.qr)).setImageBitmap(bmp);

            } catch (WriterException e) {
                e.printStackTrace();
            }
        } catch (Exception ex){
            Log.w("QRSessionActivity", ex.toString());
            new AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
    public void gotoHome(View v){
        try {
            finish();
        } catch (Exception ex){
            Log.w("gotoHome", ex.toString());
            new AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
    public void save(View v){
        try {
            if(!checkPermission()){
                requestPermission();
                return;
            }
            saveImage();
        } catch (Exception ex){
                Log.w("save", ex.toString());
                new AlertDialog.Builder(this)
                        .setMessage("Lỗi xử lý.")
                        .setNegativeButton("OK", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
        }
    }
    public void share(View v){
        try {
            if(!saveImageForShare()){
                new AlertDialog.Builder(this)
                        .setMessage("Lỗi xử lý.")
                        .setNegativeButton("OK", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
            File imagePath = new File(getCacheDir(), "images");
            File newFile = new File(imagePath, "image.jpg");
            Uri contentUri = FileProvider.getUriForFile(this, "com.hcdc.xncovid.fileprovider", newFile);

            if (contentUri != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua"));
            }
        } catch (Exception ex){
            Log.w("share", ex.toString());
            new AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
    private boolean saveImageForShare(){
        try {

            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.jpg"); // overwrites this image every time
            View content = getWindow().getDecorView().findViewById(R.id.screen);
            content.setDrawingCacheEnabled(true);
            Bitmap bitmap = content.getDrawingCache();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();
            return true;
        } catch (IOException e) {
            Log.w("saveImageForShare", e.toString());
            return false;
        }
    }
    private void saveImage(){
        try {
            View content = getWindow().getDecorView().findViewById(R.id.screen);
            content.setDrawingCacheEnabled(true);
            Bitmap bitmap = content.getDrawingCache();
            saveImageQ(bitmap);
            return;
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveImageQ(bitmap);
                return;
            }

            File imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            String filename = System.currentTimeMillis() + ".jpg";
            File image = new File(imagesDir, filename);
            FileOutputStream stream = new FileOutputStream(image);
            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)) {
                throw new IOException("Failed to save bitmap.");
            }
            stream.flush();
            stream.close();
            Toast.makeText(this, "Lưu thành công.", Toast.LENGTH_LONG).show();*/
        } catch (Exception e){
            Log.e("saveImage", e.toString(), e);
            new AlertDialog.Builder(QRSessionActivity.this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
    private void saveImageQ(Bitmap bitmap){
        try {
            String relativeLocation = Environment.DIRECTORY_PICTURES;
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
            contentValues.put(MediaStore.Images.Media.TITLE, session.SessionName + " - " + timeFormat.format(session.getTestingDate()));
            contentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
            contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation);
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 1);
            }

            ContentResolver resolver = getContentResolver();
            Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            try {
                OutputStream stream = resolver.openOutputStream(uri);
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)) {
                    throw new IOException("Failed to save bitmap.");
                }
                stream.flush();
                stream.close();
            } catch (IOException ex) {
                if (uri != null) {
                    resolver.delete(uri, null, null);
                }
                Log.e("saveImage", ex.toString(), ex);
                new AlertDialog.Builder(QRSessionActivity.this)
                        .setMessage("Lỗi xử lý.")
                        .setNegativeButton("OK", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return;
            } finally {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0);
                }
            }
            Toast.makeText(this, "Lưu thành công.", Toast.LENGTH_LONG).show();
        } catch (Exception e){
            Log.e("saveImage", e.toString(), e);
            new AlertDialog.Builder(QRSessionActivity.this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0) {

                    boolean accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (!accepted) {
                        Toast.makeText(getApplicationContext(), "Lưu ảnh không thành công. Không có quyền lưu ảnh.", Toast.LENGTH_LONG).show();
                    } else
                    {
                        saveImage();
                    }
                }
                break;
        }
    }
}