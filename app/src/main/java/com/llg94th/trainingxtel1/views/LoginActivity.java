package com.llg94th.trainingxtel1.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.llg94th.trainingxtel1.R;
import com.llg94th.trainingxtel1.models.AccountKitResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    public static final String MY_TAG = "MY_TAG";

    private Button btnLoginByPhone, btnLoginWithFB;
    private com.facebook.accountkit.AccessToken accessToken;
    private AccountKitResult accountKitResult;
    private AccountKitLoginResult loginResult;
    private CallbackManager callbackManager;
    public static final int APP_REQUEST_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        innit();

    }

    private void innit() {
        checkPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS});
        btnLoginByPhone = (Button) findViewById(R.id.btnLoginByPhone);
        btnLoginWithFB = (Button) findViewById(R.id.btnLoginWithFB);
//Gen hashkey
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.llg94th.trainingxtel1",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
        //LOGIN BY PHONE
        AccountKit.initialize(getApplicationContext());
        if (AccountKit.isInitialized()) {
            accessToken = AccountKit.getCurrentAccessToken();
            if (accessToken != null) {
                Log.d(MY_TAG, accessToken.toString());
            } else {
                //Handle new or logged out user
            }
        }
        btnLoginByPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoginPhone(view);
            }
        });

        //LOGIN WITH FACEBOOK
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");
                        accountKitResult = AccountKitResult.getInstant(getApplicationContext());
                        accountKitResult.setAccess_token_key(loginResult.getAccessToken().getToken());
                        sendRequesToSever(accountKitResult,false);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        btnLoginWithFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email"));
            }
        });
    }

    public void onLoginPhone(final View view) {
        final Intent intent = new Intent(LoginActivity.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.CODE); // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        configurationBuilder.setDefaultCountryCode("+84");
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
                //showErrorActivity(loginResult.getError());
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
            } else {
                if (loginResult.getAuthorizationCode() != null) {
                    toastMessage = loginResult.getAuthorizationCode();
                    accountKitResult = AccountKitResult.getInstant(getApplicationContext());
                    accountKitResult.setAuthorization_code(loginResult.getAuthorizationCode());
                    sendRequesToSever(accountKitResult,true);
                } else {
                    toastMessage = "";
                }
            }
            Toast.makeText(
                    this,
                    toastMessage,
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void checkPermission(String[] list) {
        Log.d(MY_TAG, "checkPermission: list.length=" + list.length);
        if (ContextCompat.checkSelfPermission(this, list[0]) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, list[1]) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, list[2]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this, list, 6969);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 6969) {
            for (int i = 0; i < grantResults.length; i++) {
                Log.d(MY_TAG, permissions[i] + ":" + grantResults[i]);
                if (ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, permissions[1]) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, permissions[2]) != PackageManager.PERMISSION_GRANTED) {
                    checkPermission(permissions);
                }
            }
        }
    }

    private void sendRequesToSever(AccountKitResult account,boolean isByPhone){
        Log.d(MY_TAG,account.toJsonString());
        String link;
        if(isByPhone){
            link = "http://124.158.5.112:9180/nipum/v1.0/m/user/accountkit/login";
        }else {
            link = "http://124.158.5.112:9180/nipum/v1.0/m/user/fb/login";
        }
        Ion.with(getApplicationContext())
                .load(link)
                .setJsonObjectBody(account.toJsonObject())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        if (result!=null){
                            Toast.makeText(LoginActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}