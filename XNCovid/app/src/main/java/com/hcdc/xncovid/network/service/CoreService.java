package com.hcdc.xncovid.network.service;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;

public interface CoreService {
    @GET("api/Login")
    @FormUrlEncoded
    Call<LoginUserRes> login(@Field("TokenID") String tokenid,
                             @Field("Email") String email);
}
