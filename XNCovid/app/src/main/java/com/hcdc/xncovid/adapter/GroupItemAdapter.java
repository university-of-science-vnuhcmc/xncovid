package com.hcdc.xncovid.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hcdc.xncovid.ListGroupXnActivity;
import com.hcdc.xncovid.MainStaffActivity;
import com.hcdc.xncovid.MyApplication;
import com.hcdc.xncovid.R;
import com.hcdc.xncovid.model.GroupedUserInfo;
import com.hcdc.xncovid.util.Util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.SortedSet;

import androidx.annotation.RequiresApi;

public class GroupItemAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<GroupedUserInfo> list;
    private Context context;
    private SortedSet<String> key;
    private  Hashtable<String, GroupedUserInfo> hashinfo;
    private  int  _maxGroup = 10;
    public GroupItemAdapter(ArrayList<GroupedUserInfo> list, Context context, SortedSet<String> set, Hashtable<String, GroupedUserInfo> hash) {
        this.list = list;
        this.context = context;
        this.key =set;
        this.hashinfo = hash;
        _maxGroup = ((MyApplication)  ((Activity) context).getApplication()).getGroupMaxCount();
    }

    @Override
    public int getCount() {
        int count = (list != null) ? list.size() : 0;
        return count;
    }

    @Override
    public Object getItem(int position) {
        if(list != null && (list.size()-1) >= position){
            return list.get(position);
        }
       return  null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        GroupedUserInfo obj = (GroupedUserInfo) getItem(position);
        GroupedUserInfo objInfo = (GroupedUserInfo) hashinfo.get(obj.getUid());
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_item, null);
            holder.txtIdx =  (TextView)view.findViewById(R.id.item_idx);
            holder.txtUID =  (TextView)view.findViewById(R.id.txt_group_user_id);
            holder.btnDelete = (Button) view.findViewById(R.id.btn_remove);
            holder.layout = (RelativeLayout)  view.findViewById(R.id.item_layout);
            holder.txtInfo = (TextView) view.findViewById(R.id.txt_group_user_info);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        //Handle TextView and display string from your list
       holder.txtUID.setText(obj.getUid());
        holder.txtIdx.setText((position+1) +"");

        holder.btnDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                GroupedUserInfo obj = (GroupedUserInfo) getItem(position);

                String txtUId = "<font color='#08812A'><b>"+obj.getUid()+"</b></font>";
                String htmlcontent = "<i>Việc này sẽ đôn những người phía sau lên trước!</i>";
                new Util().showMessage("Xác nhận xóa mã định danh",
                        txtUId,
                        htmlcontent,
                        "Đồng ý",
                        "Hủy",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                key.remove(obj.getUid());
                                list.remove(position); //or some other task
                                ((TextView) ((Activity) context).findViewById(R.id.txt_count)).setText(getCount()+"/" + _maxGroup);
                                if(getCount() < 1){
                                    ((SeekBar) ((Activity) context).findViewById(R.id.startGroup)).setVisibility(View.GONE);
                                    ((LinearLayout) ((Activity) context).findViewById(R.id.startGroupDisable)).setVisibility(View.VISIBLE);
                                }
                                if(getCount() < _maxGroup){
                                    ((TextView) ((Activity) context).findViewById(R.id.txt_count)).setTextAppearance(R.style.red_20);
                                    ((RelativeLayout) ((Activity) context).findViewById(R.id.lst_xn_users)).setVisibility(View.VISIBLE);
                                }
                                notifyDataSetChanged();
                            }
                        }, null, context);


            }
        });

        if(!obj.isOnline()){
            holder.txtUID.setTextAppearance(R.style.green_28);
            holder.txtIdx.setBackground(view.getResources().getDrawable(R.drawable.rectangle_item_manual));
            holder.txtInfo.setVisibility(View.GONE);
           // RelativeLayout.LayoutParams layoutParams =(RelativeLayout.LayoutParams)holder.txtUID.getLayoutParams();
           // layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
           // layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
           // holder.txtUID.setLayoutParams(layoutParams);
         }else {
            if(objInfo != null){
                holder.txtInfo.setText(objInfo.getFullname() +", "+ objInfo.getBirthYear());
                holder.txtInfo.setTextAppearance(R.style.blue_18);
                holder.txtUID.setTextAppearance(R.style.blue_18);
                holder.txtIdx.setBackground(view.getResources().getDrawable(R.drawable.rectangle_item));
                holder.txtInfo.setVisibility(View.VISIBLE);
             //   RelativeLayout.LayoutParams layoutParams =(RelativeLayout.LayoutParams)holder.txtUID.getLayoutParams();
                //layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
               // layoutParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
               // holder.txtUID.setLayoutParams(layoutParams);
            }else {
                holder.txtUID.setTextAppearance(R.style.blue_28);
                holder.txtIdx.setBackground(view.getResources().getDrawable(R.drawable.rectangle_item));
                holder.txtInfo.setVisibility(View.GONE);
               // RelativeLayout.LayoutParams layoutParams =(RelativeLayout.LayoutParams)holder.txtUID.getLayoutParams();
               // layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
               // layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
               // holder.txtUID.setLayoutParams(layoutParams);
            }
        }
        if(position % 2 == 0){
            holder.layout.setBackground(view.getResources().getDrawable(R.drawable.rectangle_time_session));

        }
        else {
            holder.layout.setBackground(view.getResources().getDrawable(R.drawable.rectangle_item_disable));

        }
        return view;
    }
    static class ViewHolder {
        TextView txtIdx;
        TextView txtUID;
        TextView txtInfo;
        Button btnDelete;
        RelativeLayout layout;
    }
}
