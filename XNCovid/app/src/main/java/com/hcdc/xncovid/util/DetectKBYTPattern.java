package com.hcdc.xncovid.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.hcdc.xncovid.MyApplication;
import com.hcdc.xncovid.model.GroupedUserInfo;

import java.util.ArrayList;
import java.util.Hashtable;

import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetectKBYTPattern {
    private static volatile DetectKBYTPattern mInstance = null;
    private  String contentRegexs = "phone::pattern==so_dien_thoai=(?<sodienthoai>[0-9]+),==>key==sodienthoai\r\n" +
            "fullname::pattern==so_dien_thoai=[0-9]+, ten=(?<hoten>[^,]*),==>key==hoten\r\n" +
            "gent::pattern==gioi_tinh=(?<gioitinh>\\d{1})==>key==gioitinh\r\n" +
            "birthdateyear::pattern==namsinh=(?<namsinh>\\d{4})==>key==namsinh\r\n" +
            "address::pattern==dia_chi=(?<diadiem>[^,]*)==>key==diadiem##pattern==xaphuong=.*ten=(?<xaphuong>[^,]+), quanhuyen_id==>key==xaphuong##pattern==quanhuyen=.*ten=(?<quanhuyen>[^,]+), tinhthanh_id==>key==quanhuyen##pattern==tinhthanh=.*ten=(?<tinhthanh>[^,]+), quocgia_id==>key==tinhthanh::out==%diadiem%###, ###%xaphuong%###, ###%quanhuyen%###, ###%tinhthanh%###.";
    private Hashtable<String, KBYTRegex> dic = null;
    private DetectKBYTPattern(Activity context) {
        //contentRegexs = ((MyApplication)  context.getApplication()).getForm();
        if(contentRegexs == null || contentRegexs.isEmpty()){
            Log.e("DetectKBYTPattern", "Regex Content is null or empty");
            return;
        }
        String[] arr = contentRegexs.split("\r\n");
        if(arr == null || arr.length < 1 ){
            Log.e("DetectKBYTPattern", "Regex Arr is empty or null.");
            return;
        }
        Hashtable<String, KBYTRegex> dicTmp = new Hashtable<>();
        for (String item : arr){
            try{
                String[] arrEx = item.split("::");
                KBYTRegex obj = new KBYTRegex();
                String keyword = arrEx[0].trim().toLowerCase();
                obj.setOutputKey(keyword);
                //Pattern
                String[] arr2 = arrEx[1].split("##");
                //pattern==dia_chi=(?<diadiem>[^,]*)==>key==diadiem
                // ##pattern==xaphuong=.*ten=(?<xaphuong>[^,]+), quanhuyen_id==>key==xaphuong
                for (String sub:arr2) {
                    String[] arr3 = sub.split("==>");
                    //pattern==dia_chi=(?<diadiem>[^,]*)
                    // ==>key==diadiem
                    String pattern = "";
                    String extractKey = "";
                    for (String pk: arr3) {
                        String[] txt = pk.split("==");
                        if(txt[0].trim().toLowerCase() == "pattern"){
                            pattern = txt[1];
                        }else {
                            extractKey = txt[1];
                        }
                    }
                    obj.addPattern(pattern, extractKey);
                    if(arrEx.length < 3) //khoi co gep nhieu key
                    {
                        obj.setOutContent(extractKey);
                    }
                }

                if(arrEx.length > 2){
                    String[] outputContens = arrEx[2].split("###");
                    for (String content : outputContens) {
                        obj.setOutputKey(content);
                    }
                }
                dicTmp.put(keyword, obj);
            }catch (Exception e){
                Log.e("DetectKBYTPattern", "Detect fail.");
            }
        }

        dic = dicTmp;
    }

    public static DetectKBYTPattern instance(Activity context) {
        if (mInstance == null)
            mInstance = new DetectKBYTPattern(context);
        return mInstance;
    }

    public GroupedUserInfo DetectInfo(String inputCotent, String uid){
        GroupedUserInfo obj = new GroupedUserInfo(uid, true);
        for (KBYTRegex objRegex: dic.values()) {
            String content = objRegex.ExtractContent(inputCotent);
            switch (objRegex.getOutputKey()){
                case "phone":
                  obj.setPhone(content);
                  break;
                case "fullname":
                    obj.setFullname(content);
                    break;
                case "gent":
                    obj.setGent(Integer.parseInt(content));
                    break;
                case "birthdateyear":
                    obj.setBirthYear(content);
                    break;
                case "address":
                    obj.setAddress(content);
                    break;
                case "district":
                    obj.setDistrict(content);
                    break;
                case "ward":
                    obj.setWard(content);
                    break;
                case "province":
                    obj.setProvince(content);
                    break;
            }

        }
        return obj;
    }
}
