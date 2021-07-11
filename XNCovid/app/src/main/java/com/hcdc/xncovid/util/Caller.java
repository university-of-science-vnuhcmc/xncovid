package com.hcdc.xncovid.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.hcdc.xncovid.LoginActivity;
import com.hcdc.xncovid.MainActivity;
import com.hcdc.xncovid.MainLeaderActivity;
import com.hcdc.xncovid.MyApplication;
import com.hcdc.xncovid.model.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

public class Caller {
    public void call(Context context, String apiName, APIRequest req, Type type, ICallback callback, String url, int method){
        try{
            HttpsTrustManager.allowAllSSL();
            RequestQueue queue = Volley.newRequestQueue(context);
            if(url == null || url.isEmpty()){
                url = "https://xncovid.uit.edu.vn:7070/api/";
                UserInfo userInfo = ((MyApplication)((Activity)(context)).getApplication()).getUserInfo();
                if(userInfo != null){
                    req.Email = userInfo.Email;
                    req.Token = userInfo.Token;
                }
            }
            url = url + apiName;

            JSONObject jsonReq = null;
            try{
                if(req != null){
                    jsonReq = new JSONObject(new Gson().toJson(req));
                }
            } catch (JSONException e)
            {
                Log.w("Call " + apiName, "Parse request object fail.");
                new AlertDialog.Builder(context)
                        .setMessage("Lỗi xử lý.")
                        .setNegativeButton("OK", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return;
            }
            switch (method){
                case Request.Method.POST:
                    JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonReq,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try{
                                        APIResponse res = new Gson().fromJson(response.toString(), type);
                                        if(res.returnCode == 99){
                                        /*new AlertDialog.Builder(context)
                                            .setMessage("Vui lòng đăng nhập lại.")
                                            .setNegativeButton("OK", null)
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();*/
                                            Intent intent = new Intent(context, LoginActivity.class);
                                            intent.putExtra("isLogout", true);
                                            context.startActivity(intent);
                                            return;
                                        }
                                        callback.callback(res);
                                    } catch (Exception ex){
                                        Log.w("Call " + apiName, ex.toString());
                                        new AlertDialog.Builder(context)
                                                .setMessage("Lỗi kết nối. Vui lòng thử lại.")
                                                .setNegativeButton("OK", null)
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.w("Call " + apiName, String.valueOf(error.networkResponse.statusCode));
                            new AlertDialog.Builder(context)
                                    .setMessage("Lỗi kết nối. Vui lòng thử lại. ErrorCode: " + String.valueOf(error.networkResponse.statusCode))
                                    .setNegativeButton("OK", null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    });

                    queue.add(jsonRequest);
                    break;
                case Request.Method.GET:
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    callback.callback(response);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.w("Call " + apiName, String.valueOf(error.networkResponse.statusCode));
                            new AlertDialog.Builder(context)
                                    .setMessage("Lỗi kết nối. Vui lòng thử lại. ErrorCode: " + String.valueOf(error.networkResponse.statusCode))
                                    .setNegativeButton("OK", null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    });
                    queue.add(jsonObjectRequest);
                    break;
            }
        } catch (Exception ex){
            Log.w("Call " + apiName, ex.toString());
            new AlertDialog.Builder(context)
                    .setMessage("Lỗi kết nối. Vui lòng thử lại.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}
