package com.kisscompany.reportapp.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.OAuth2Utils;
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
import com.twitter.sdk.android.core.models.ImageValue;


import org.apache.http.HttpEntity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by chanpc on 8/18/2016.
 */
public class  uploadImageGoogle extends AsyncTask<String,String,String> {
    Bitmap image;
    Activity current;
    public uploadImageGoogle(Bitmap i, Activity act)
    {
        image = i;
        current = act;

    }
    @Override
    protected String doInBackground(String... params) {
        OutputStream output = null;
        HttpsURLConnection connection = null;
        URL url = null;
        try {
            String token= "AIzaSyDhKJvSmQcvieWpmKNgzNk56tu3DRPyo4k";
            List<String> scopes = new ArrayList<String>();
            scopes.add(StorageScopes.DEVSTORAGE_FULL_CONTROL);
            HttpTransport httpTransport= new com.google.api.client.http.javanet.NetHttpTransport();
            AssetManager am = current.getAssets();
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
            String URI = "https://storage.googleapis.com/" + "traffy_image"+"/"+params[0];
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory(credential);
            GenericUrl url2 = new GenericUrl(URI);
            //byte array holds the data, in this case the image i want to upload in bytes.
            final Bitmap bitmap = image;
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
        } finally {

        }

        return null;
    }
    @SuppressLint("NewApi") public static File stream2file(InputStream in) throws IOException
    { final File tempFile = File.createTempFile("okkk", null);
        tempFile.deleteOnExit();
        FileOutputStream out = new FileOutputStream(tempFile);
        IOUtils.copy(in, out);
        return tempFile; }
}
