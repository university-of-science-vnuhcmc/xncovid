package com.hcdc.xncovid.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.hcdc.xncovid.MainStaffActivity;
import com.hcdc.xncovid.R;

import androidx.appcompat.app.AppCompatActivity;

public class Util  {
    public void showMessage(String title, String subtitle, String message, String okLabel, String cancelLabel, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener, Context context,
                            Drawable drawIcon) {
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
        final  View icDialog = customLayout.findViewById(R.id.ic_dialog);
       if(icDialog != null){
           icDialog.setBackground(drawIcon);
           icDialog.setVisibility(View.VISIBLE);
       }else {
           icDialog.setVisibility(View.GONE);
       }


        txtTitle.setText(android.text.Html.fromHtml(title));
        txtSubTitle.setText(android.text.Html.fromHtml(subtitle));
        txtcontent.setText(android.text.Html.fromHtml(message));

        builder.setView(customLayout);

        builder.setPositiveButton(okLabel, okListener)
                .setNegativeButton(cancelLabel, cancelListener);

        // create and show
        // the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static String GetLuhnCheckDigit(String number)
    {
        int sum = 0;
        boolean alt = true;
        char[] digits = number.toCharArray();
        for (int i = digits.length - 1; i >= 0; i--)
        {
            int curDigit = (digits[i] - 48);
            if (alt)
            {
                curDigit *= 2;
                if (curDigit > 9)
                    curDigit -= 9;
            }
            sum += curDigit;
            alt = !alt;
        }
        if ((sum % 10) == 0)
        {
            return "0";
        }
        return (10 - (sum % 10)) + "";
    }
}
