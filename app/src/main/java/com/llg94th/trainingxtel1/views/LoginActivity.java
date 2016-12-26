package com.llg94th.trainingxtel1.views;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import com.llg94th.trainingxtel1.R;
import com.llg94th.trainingxtel1.models.AccountKitResult;
import com.llg94th.trainingxtel1.presenters.MainPresenter;
import com.llg94th.trainingxtel1.views.inf.BasicActivityInterface;

import java.util.Arrays;
import java.util.Collections;

public class LoginActivity extends MyBasicActivity implements View.OnClickListener, BasicActivityInterface {

    private AccountKitResult accountKitResult;
    private MainPresenter presenter;
    private CallbackManager callbackManager;
    public static final int APP_REQUEST_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        innit();

    }

    private void innit() {
        checkPermission(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS
        });
        presenter = new MainPresenter(this);
        findViewById(R.id.btnLoginByPhone).setOnClickListener(this);
        findViewById(R.id.btnLoginWithFB).setOnClickListener(this);
        //LOGIN BY PHONE
        AccountKit.initialize(getApplicationContext());
        if (AccountKit.isInitialized()) {
            com.facebook.accountkit.AccessToken accessToken = AccountKit.getCurrentAccessToken();
            if (accessToken != null) {
                log(accessToken.toString());
            }
        }
        //LOGIN WITH FACEBOOK

        innitFacebookSDK();
    }

    private void innitFacebookSDK() {
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");
                        accountKitResult = AccountKitResult.getInstant(getApplicationContext());
                        accountKitResult.setAccess_token_key(loginResult.getAccessToken().getToken());
                        presenter.sendAccountResultToSever(accountKitResult, false);
                    }

                    @Override
                    public void onCancel() {
                        showToast("Login Cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        showToast(exception.getMessage());
                    }
                });
    }

    public void onLoginPhone(final View view) {
        final Intent intent = new Intent(LoginActivity.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.CODE);
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
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
            } else {
                if (loginResult.getAuthorizationCode() != null) {
                    toastMessage = loginResult.getAuthorizationCode();
                    accountKitResult = AccountKitResult.getInstant(getApplicationContext());
                    accountKitResult.setAuthorization_code(loginResult.getAuthorizationCode());
                    presenter.sendAccountResultToSever(accountKitResult, true);
                } else {
                    toastMessage = "";
                }
            }
            showToast(toastMessage);
        }
    }

    private void checkPermission(String[] list) {
        log("checkPermission: list.length=" + list.length);
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
                log(permissions[i] + ":" + grantResults[i]);
                if (ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, permissions[1]) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, permissions[2]) != PackageManager.PERMISSION_GRANTED) {
                    checkPermission(permissions);
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoginByPhone:
                onLoginPhone(v);
                break;
            case R.id.btnLoginWithFB:
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Collections.singletonList("email"));
                break;
            default:
                break;
        }
    }

    @Override
    public void showToast(String mesages) {
        Toast.makeText(this, mesages, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Activity getActivity() {
        return this;
    }
}