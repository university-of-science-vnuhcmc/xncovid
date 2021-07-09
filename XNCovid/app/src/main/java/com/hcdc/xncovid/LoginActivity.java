package com.hcdc.xncovid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.hcdc.xncovid.model.LoginReq;
import com.hcdc.xncovid.model.LoginRes;
import com.hcdc.xncovid.util.APIResponse;
import com.hcdc.xncovid.util.Caller;
import com.hcdc.xncovid.util.ICallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("202164736204-6ftlinikq9rp3men5sap6tp5eerpe57h.apps.googleusercontent.com")
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
                        String filename = "token.txt";
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
                        ((MyApplication) getApplication()).setToken("");
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
        req.email = "account.getEmail()";
        req.tokenid = account.getIdToken();
        caller.call(this, "login", req, LoginRes.class, new ICallback() {
            @Override
            public void callback(APIResponse response) {
                LoginRes res = (LoginRes) response;
                if(res.returnCode == 1){
                    String filename = "token.txt";
                    try{
                        File file = new File(getFilesDir(),filename);
                        if(!file.exists()){
                            file.createNewFile();
                        }
                        FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
                        fos.write(res.Token.getBytes());
                    }  catch (FileNotFoundException ex){
                        Log.w("Login", ex.toString());
                    }catch (IOException e) {
                        Log.w("Login", e.toString());
                    }
                    MyApplication myapp = ((MyApplication) getApplication());
                    myapp.setToken(res.Token);
                    myapp.setDomain(res.Domain);
                    myapp.setForm(res.Form);
                    myapp.setUrl(res.Url);
                    myapp.setId(res.Id);
                    myapp.setRole(res.Role);
                    myapp.setName(res.CustomerName);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else if(res.returnCode == 2) {
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
        });
    }
}