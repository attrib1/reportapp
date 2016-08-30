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

public class LoginActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    public static String profilePicUrl;
    public static String userName;
    public static String facebookName;
    public static HttpRequestFactory requestFactory;
    LoginButton loginButton;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("cancel","cancel10");
        FacebookSdk.sdkInitialize(getApplicationContext());
        progress = new ProgressDialog(this);
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                Log.d("same", "same");
                    Bundle params = new Bundle();
                    params.putString("fields", "id,name,email,gender,cover,picture.type(large)");
                    setFaceBookInfo(newAccessToken, params);

            }
        };
        setContentView(R.layout.activity_login);
        Log.d("cancel","cancel8");
        callbackManager = CallbackManager.Factory.create();
        Log.d("cancel","cancel7");
        loginButton = (LoginButton) findViewById(R.id.loginButton);

        try {
            requestFactory = getCredential();
            Log.d("cancel","cancel");
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("cancel","cancel3");
               // Login();
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
        Log.d("cancel","cancel2");
    }
    public HttpRequestFactory getCredential() throws GeneralSecurityException, IOException {
        List<String> scopes = new ArrayList<String>();
        scopes.add(StorageScopes.DEVSTORAGE_FULL_CONTROL);
        HttpTransport httpTransport= new com.google.api.client.http.javanet.NetHttpTransport();
        AssetManager am = getAssets();
        InputStream inputStream = am.open("Traffy-f869c3fe8e95.p12"); //you should not put the key in assets in prod version.
        File file =stream2file(inputStream);
        JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
        GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId("storage@traffy-cloud.iam.gserviceaccount.com")
                .setServiceAccountScopes(scopes)
                .setServiceAccountPrivateKeyFromP12File(file)
                .build();

        HttpRequestFactory requestFactory = httpTransport.createRequestFactory(credential);
        Log.d("finishCredential","finsih");

        return requestFactory;
    }
    @SuppressLint("NewApi") public static File stream2file(InputStream in) throws IOException
    { final File tempFile = File.createTempFile("okkk", null);
        tempFile.deleteOnExit();
        FileOutputStream out = new FileOutputStream(tempFile);
        IOUtils.copy(in, out);
        return tempFile; }
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
                                Log.d("MyName",data.toString());
                                if (data.has("picture")) {
                                    String temp = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                    // set profile image to imageview using Picasso or Native methods
                                    profilePicUrl = temp;
                                }
                                if(data.has("id"))
                                {
                                    userName = data.getString("id");
                                    Log.d("id",userName);
                                }
                                if(data.has("name"))
                                {
                                    facebookName = data.getString("name");
                                    Log.d("username",facebookName);
                                }
                                progress.dismiss();
                                finish();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).executeAsync();
    }
    public void Login()
    {
        progress.setCancelable(false);
        progress.setMessage("Logging in ...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setProgress(0);
        progress.setMax(100);
        progress.show();
    }
}
