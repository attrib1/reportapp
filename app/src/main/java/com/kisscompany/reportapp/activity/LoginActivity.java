package com.kisscompany.reportapp.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.IOUtils;
import com.google.api.services.storage.StorageScopes;
import com.kisscompany.reportapp.R;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    public static String profilePicUrl;
    public static String userName;
    public static String facebookName;
    LoginButton loginButton;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = new ProgressDialog(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {///if new user sign in
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                    Bundle params = new Bundle();
                    params.putString("fields", "id,name,email,gender,cover,picture.type(large)");
                    setFaceBookInfo(newAccessToken, params);

            }
        };
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.loginButton);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Bundle params = new Bundle();
                params.putString("fields", "id,name,email,gender,cover,picture.type(large)");
                setFaceBookInfo(AccessToken.getCurrentAccessToken(),params);

            }

            @Override
            public void onCancel() {
                Log.d("cancel","cancel5");
            }

            @Override
            public void onError(FacebookException error) {
                if(error.getMessage().equals("CONNECTION_FAILURE: CONNECTION_FAILURE"))
                {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                    alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialogBuilder.setTitle("Network Error");
                    alertDialogBuilder.setMessage("No internet connection");
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
             });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onResume(){

        super.onResume();
    }
    public void setFaceBookInfo(AccessToken acces,Bundle params)
    {
        loginButton.setVisibility(View.INVISIBLE);
        new GraphRequest(acces, "me", params, HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if (response != null) {
                            try {
                                JSONObject data = response.getJSONObject();
                                if (data.has("picture")) {
                                    String temp = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                    // set profile image to imageview using Picasso or Native methods
                                    profilePicUrl = temp;
                                }
                                if(data.has("id"))
                                {
                                    userName = data.getString("id");
                                }
                                if(data.has("name"))
                                {
                                    facebookName = data.getString("name");
                                }
                                progress.dismiss();
                                setResult(RESULT_OK);
                                Toast.makeText(getBaseContext(),"Login Complete",Toast.LENGTH_SHORT).show();
                                finish();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            Toast.makeText(getBaseContext(),"Login Fail",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).executeAsync();
    }
    @Override
    public void onBackPressed()
    {
        finish();
    }
}
