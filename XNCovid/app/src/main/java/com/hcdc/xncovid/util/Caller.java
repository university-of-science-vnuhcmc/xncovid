package com.hcdc.xncovid.util;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.hcdc.xncovid.MainLeaderActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Map;

public class Caller {
    public void call(Context context, String apiName, APIRequest req, Type type, ICallback callback){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://xncovid.uit.edu.vn:7070/api/";
        url = url + apiName;
        JSONObject jsonReq;
        try{
            jsonReq = new JSONObject(new Gson().toJson(req));
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
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonReq,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        APIResponse res = new Gson().fromJson(response.toString(), type);
                        callback.callback(res);
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
    }
}
