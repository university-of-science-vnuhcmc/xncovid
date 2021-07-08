package com.hcdc.xncovid.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.hcdc.xncovid.MainStaffActivity;
import com.hcdc.xncovid.R;

import androidx.appcompat.app.AppCompatActivity;

public class Util  {
    public void showMessage(String title, String subtitle, String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // set the custom layout
        final View customLayout
                = ((Activity) context).getLayoutInflater()
                .inflate(
                        R.layout.activity_customdialog,
                        null);
        final TextView txtTitle = customLayout.findViewById(R.id.dialog_title);
        final TextView txtSubTitle = customLayout.findViewById(R.id.dialog_subtitle);
        final TextView txtcontent = customLayout.findViewById(R.id.content_1);

        txtTitle.setText(android.text.Html.fromHtml(title));
        txtSubTitle.setText(android.text.Html.fromHtml(subtitle));
        txtcontent.setText(android.text.Html.fromHtml(message));

        builder.setView(customLayout);

        builder.setPositiveButton("Thoát", okListener)
                .setNegativeButton("Hủy", cancelListener);

        // create and show
        // the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
