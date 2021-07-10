package com.hcdc.xncovid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRSessionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrsession);
        Bundle bundle = getIntent().getExtras();
        String sessionName = bundle.getString("SessionName");
        Long sessionID = bundle.getLong("SessionID");
        isNew = bundle.getBoolean("IsNew");
        if(!isNew){
            ((LinearLayout) findViewById(R.id.success)).setVisibility(View.GONE);
        }
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(sessionID.toString(), BarcodeFormat.QR_CODE, 300, 300);
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
    }
    private boolean isNew;
    @Override
    public void onBackPressed() {
        if(!isNew){
            super.onBackPressed();
        }
    }
    public void gotoHome(View v){
        Intent intent = new Intent(this, MainLeaderActivity.class);
        startActivity(intent);
    }
}