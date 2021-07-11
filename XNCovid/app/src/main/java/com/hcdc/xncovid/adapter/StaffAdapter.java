package com.hcdc.xncovid.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.hcdc.xncovid.R;
import com.hcdc.xncovid.model.GroupedUserInfo;
import com.hcdc.xncovid.model.UserInfo;
import com.hcdc.xncovid.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

public class StaffAdapter extends BaseAdapter {
    private Activity context;
    private UserInfo[] lstUser;
    public StaffAdapter(Activity context, UserInfo[] lstUser){
        this.context = context;
        this.lstUser = lstUser;
    }
    @Override
    public int getCount() {
        if(lstUser != null){
            return lstUser.length;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(lstUser != null && 0 <= position && position < lstUser.length){
            return lstUser[position];
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = context.getLayoutInflater().inflate(R.layout.list_item, parent, false);
        }
        UserInfo user = (UserInfo) getItem(position);
        ((TextView) convertView.findViewById(R.id.name)).setText((position + 1) + "/ " + user.Name);
        ((TextView) convertView.findViewById(R.id.email)).setText(user.Email);
        return convertView;
    }
}
