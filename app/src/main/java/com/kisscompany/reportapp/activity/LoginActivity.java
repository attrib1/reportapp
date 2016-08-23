package com.kisscompany.reportapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    public static String profilePicUrl;
    public static String userName;
    public static String facebookName;
    public static HttpRequestFactory requestFactory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.loginButton);
        try {
            requestFactory = getCredential();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Bundle params = new Bundle();
                params.putString("fields", "id,name,email,gender,cover,picture.type(large)");
                new GraphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET,
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
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).executeAsync();

                Intent intent = new Intent(LoginActivity.this,Main_menu.class);
                startActivity(intent);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }

             });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
    public void onPause(){
        LoginManager.getInstance().logOut();
        super.onPause();
    }
}
