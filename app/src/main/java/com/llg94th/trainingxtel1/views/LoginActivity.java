package com.llg94th.trainingxtel1.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.gson.Gson;
import com.llg94th.trainingxtel1.R;
import com.llg94th.trainingxtel1.models.AccountKitResult;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private Button btnLoginByPhone;
    private AccessToken accessToken;
    private AccountKitResult accountKitResult;

    public static final int APP_REQUEST_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        innit();

    }

    private void innit() {
        btnLoginByPhone = (Button) findViewById(R.id.btnLoginByPhone);
        AccountKit.initialize(getApplicationContext());
        if (AccountKit.isInitialized()) {
            accessToken = AccountKit.getCurrentAccessToken();
            if (accessToken != null) {
                Log.d("MY_TAG", accessToken.toString());
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
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
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
                    Log.d("MY_TAG","JSON: "+accountKitResult.toJsonString());
                }else {
                    toastMessage = "";
                }

                // If you have an authorization code, retrieve it from
                // loginResult.getAuthorizationCode()
                // and pass it to your server and exchange it for an access token.

                // Success! Start your next activity...
                //goToMyLoggedInActivity();
            }

            // Surface the result to your user in an appropriate way.
            Toast.makeText(
                    this,
                    toastMessage,
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
}
