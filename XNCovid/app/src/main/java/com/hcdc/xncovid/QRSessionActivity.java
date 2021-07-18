package com.hcdc.xncovid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hcdc.xncovid.model.LocateInfor;
import com.hcdc.xncovid.model.Session;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;

public class QRSessionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_qrsession);
            Bundle bundle = getIntent().getExtras();
            Session session = new Gson().fromJson(bundle.getString("Session"), Session.class);

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
}