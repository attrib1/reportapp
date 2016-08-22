package com.kisscompany.reportapp.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;


import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.IOUtils;
import com.google.api.services.storage.StorageScopes;
import com.kisscompany.reportapp.activity.Main_menu;
import com.kisscompany.reportapp.frangment.Main_men_fragment;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by chanpc on 8/18/2016.
 */
public class sendFeedInfo extends AsyncTask<String,String,String> {


    PostClass post;//content to post
    HttpURLConnection connection;
    Activity act;
    public sendFeedInfo(PostClass p, Activity a)
    {
        post = p;
        act = a;
    }

    @Override
    protected String doInBackground(String... params) {
        DataOutputStream output = null;
        URL url = null;
        try {
            url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.getDoOutput();
            connection.setRequestMethod("POST");
            connection.connect();

            output = new DataOutputStream(connection.getOutputStream());
            output.writeBytes(getUrlParam());
            output.flush();
            output.close();
          //  int res = connection.getResponseCode();
            //Log.d("SendingCode",String.valueOf(res));
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));//get feedback picName
            String picName = reader.readLine();
            Log.d("Picname",picName);
            connection.disconnect();
         //   reader.close();
            ///end of first phase
            ///begin second phase

            List<String> scopes = new ArrayList<String>();
            scopes.add(StorageScopes.DEVSTORAGE_FULL_CONTROL);
            HttpTransport httpTransport= new com.google.api.client.http.javanet.NetHttpTransport();
            AssetManager am = act.getAssets();
            InputStream inputStream = am.open("Traffy-f869c3fe8e95.p12"); //you should not put the key in assets in prod version.
            //convert key into class File. from inputstream to file. in an aux class.
            File file =stream2file(inputStream);
            JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
            //Google Credentianls
            GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport)
                    .setJsonFactory(JSON_FACTORY)
                    .setServiceAccountId("storage@traffy-cloud.iam.gserviceaccount.com")
                    .setServiceAccountScopes(scopes)
                    .setServiceAccountPrivateKeyFromP12File(file)
                    .build();
            String URI = "https://storage.googleapis.com/" + "traffy_image"+"/"+"353086.jpg";
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory(credential);
            GenericUrl url2 = new GenericUrl(URI);
            //byte array holds the data, in this case the image i want to upload in bytes.
            final Bitmap bitmap = post.getPic();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bitMapData = stream.toByteArray();
            HttpContent contentsend = new ByteArrayContent("image/jpeg", bitMapData );
            HttpRequest putRequest;
            putRequest = requestFactory.buildPutRequest(url2, contentsend);
            HttpResponse response = putRequest.execute();
            String content = response.parseAsString();
            Log.d("debug", "response is:"+response.getStatusCode());
            Log.d("debug", "response content is:"+content);
            response.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getUrlParam() throws IOException {
        String url =  "Name="+URLEncoder.encode(post.getOwner(),"UTF-8");

        url = url+"&Date="+String.valueOf(post.getDate())
                +"&lat="+URLEncoder.encode(Main_menu.lat,"UTF-8")+"&lng="+URLEncoder.encode(Main_menu.lng,"UTF-8")+
        "&Comment="+URLEncoder.encode(post.getContent(),"UTF-8")+"&Address="+URLEncoder.encode(post.getAdress(),"UTF-8")+
        "&IDFacebook="+URLEncoder.encode(post.getFacebookID(),"UTF-8")+"&Status="+URLEncoder.encode(post.getType(),"UTF-8");
        Log.d("sendingParam",url);
        return url;
    }
    @SuppressLint("NewApi") public static File stream2file(InputStream in) throws IOException
    { final File tempFile = File.createTempFile("okkk", null);
        tempFile.deleteOnExit();
        FileOutputStream out = new FileOutputStream(tempFile);
        IOUtils.copy(in, out);
        return tempFile; }
}
