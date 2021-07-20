package com.hcdc.xncovid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.hcdc.xncovid.model.LoginReq;
import com.hcdc.xncovid.model.LoginRes;
import com.hcdc.xncovid.model.UserInfo;
import com.hcdc.xncovid.util.Caller;
import com.hcdc.xncovid.util.ICallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken("277490021506-7qovlnbruk63n0snvgleu3ul8ak8df6l.apps.googleusercontent.com")
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            SignInButton signInButton = findViewById(R.id.sign_in_button);
            signInButton.setSize(SignInButton.SIZE_WIDE);
            findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.sign_in_button:
                            signIn();
                            break;
                    }
                }
            });

            Intent intent = this.getIntent();
            Bundle bundle = getIntent().getExtras();
            if(bundle != null && !bundle.isEmpty()){
                Boolean isLogout = bundle.getBoolean("isLogout");
                if(isLogout){
                    signOut();
                    return;
                }
            }

            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if(account != null){
                if(account.isExpired()){
                    signOut();
                } else{
                    login(account);
                }
            }
        } catch (Exception ex){
            Log.w("LoginActivity", ex.toString());
            new android.app.AlertDialog.Builder(this)
                    .setMessage("Lỗi xử lý.")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String filename = "userinfo";
                        String fileContents = "";
                        try{
                            File file = new File(getBaseContext().getFilesDir(),filename);
                            if(file.exists()){
                                FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
                                fos.write(fileContents.getBytes());
                            }
                        }  catch (FileNotFoundException ex){
                            Log.w("Logout", ex.toString());
                        }catch (IOException e) {
                            Log.w("Logout", e.toString());
                        }
                        ((MyApplication) getApplication()).setUserInfo(null);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            login(account);
        } catch (ApiException e) {
            Log.w("Login", "signInResult:failed code=" + e.getStatusCode());
            new AlertDialog.Builder(this)
                    .setTitle("Đăng nhập không thành công.")
                    .setMessage("Lỗi: " + e.getStatusCode())
                    .setNegativeButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    private void login(GoogleSignInAccount account){
        Caller caller = new Caller();
        LoginReq req = new LoginReq();
        req.Email = account.getEmail();
        req.TokenID = account.getIdToken();
        caller.call(this, "login", req, LoginRes.class, new ICallback() {
            @Override
            public void callback(Object response) {
                LoginRes res = (LoginRes) response;
                if(res.ReturnCode == 1){
                    UserInfo userInfo = new UserInfo();
                    userInfo.Email = account.getEmail();
                    userInfo.Name = res.FullName;
                    userInfo.Role = res.Role;
                    userInfo.Token = res.Token;
                    userInfo.AccountID = res.AccountID;
                    String filename = "userinfo";
                    try{
                        File file = new File(getFilesDir(),filename);
                        if(!file.exists()){
                            file.createNewFile();
                        }
                        FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
                        fos.write(new Gson().toJson(userInfo).getBytes());
                    }  catch (FileNotFoundException ex){
                        Log.w("Login", ex.toString());
                    }catch (IOException e) {
                        Log.w("Login", e.toString());
                    }
                    MyApplication myapp = ((MyApplication) getApplication());
                    myapp.setUserInfo(userInfo);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if(res.ReturnCode == 2) {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setMessage("Email của Bạn chưa được phân quyền sử dụng.")
                            .setNegativeButton(android.R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else{
                    new AlertDialog.Builder(LoginActivity.this)
                            .setMessage("Đăng nhập không thành công. Vui lòng thử lại sau.")
                            .setNegativeButton(android.R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        }, null, Request.Method.POST);
    }
}