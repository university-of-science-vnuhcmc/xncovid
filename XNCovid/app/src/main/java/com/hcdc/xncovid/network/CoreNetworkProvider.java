package com.hcdc.xncovid.network;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.hcdc.xncovid.network.service.CoreService;

import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CoreNetworkProvider {
    private static final String TAG = CoreNetworkProvider.class.getSimpleName();

    private static volatile CoreNetworkProvider mInstance = null;
    private Retrofit retrofit;
    private CoreNetworkProvider() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://45.122.249.68:7070/")
                .client( getUnsafeOkHttpClient().build())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .build();
    }
    public static OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static CoreNetworkProvider self() {
        if (mInstance == null)
            mInstance = new CoreNetworkProvider();
        return mInstance;
    }

    private <T> T getService(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }
    public void login(String tokenID, String email, final CoreCallBack.With<LoginUserRes> coreCallBack) {
        getService(CoreService.class).login(tokenID, email).enqueue(new Callback<LoginUserRes>() {
            @Override
            public void onResponse(Call<LoginUserRes> call, Response<LoginUserRes> response) {
                if (coreCallBack != null) {
                    coreCallBack.run(response.body());
                }
            }

            @Override
            public void onFailure(Call<LoginUserRes> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }
}
