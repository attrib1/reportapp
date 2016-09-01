package com.kisscompany.reportapp.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


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
import com.kisscompany.reportapp.activity.Camera;
import com.kisscompany.reportapp.activity.LoginActivity;
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
import java.util.UUID;
/**
 * Created by chanpc on 8/18/2016.
 */
public class sendFeedInfo extends AsyncTask<String,String,String> {

    OnRefreshFinishListener mListener;
    PostClass post;//content to post
    HttpURLConnection connection;
    Activity act;
    String picName;
    String fbName;
    boolean ok;
    public sendFeedInfo(PostClass p, Activity a)
    {
        post = p;
        act = a;
    }

    public interface OnRefreshFinishListener {
        void onRefreshFinished();
    }

    public void setCustomEventListener(OnRefreshFinishListener eventListener) {
        mListener = eventListener;
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
            post.setOwner(Main_menu.profileString);
            post.setFacebookID(Main_menu.id);
            UUID uniqueKey = UUID.randomUUID();
            picName = uniqueKey.toString();
            fbName = URLEncoder.encode(Main_menu.profileString,"UTF-8");
            //output.writeBytes(getUrlParam());

            createFolder("https://storage.googleapis.com/" + "traffy_image"+"/"+fbName+"/");
            ok = savePicture("https://storage.googleapis.com/" + "traffy_image"+"/"+fbName+"/"+URLEncoder.encode(picName,"UTF-8"),post.getPic());
            if(ok)
                ok = savePicture("https://storage.googleapis.com/" + "traffy_image"+"/"+fbName+"/"+ URLEncoder.encode(Main_menu.id,"UTF-8"),getProfilePic());
            if(ok) {
                output.writeBytes(getUrlParam());
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                output.flush();
                output.close();
            }

            connection.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mListener.onRefreshFinished();
        return null;
    }
    public String getUrlParam() throws IOException {
        Log.d("debuggg",post.getFacebookID());
        String url =  "name="+URLEncoder.encode(post.getOwner(),"UTF-8");

        url = url+"&date="+String.valueOf(post.getDate())
                +"&lat="+URLEncoder.encode(Main_menu.lat,"UTF-8")+"&lng="+URLEncoder.encode(Main_menu.lng,"UTF-8")+
        "&comment="+URLEncoder.encode(post.getContent(),"UTF-8")+"&address="+URLEncoder.encode(post.getAdress(),"UTF-8")+
                "&status="+URLEncoder.encode("report","UTF-8")+"&problemtype="+URLEncoder.encode(post.getType(),"UTF-8")+
        "&facebookid="+URLEncoder.encode(post.getFacebookID(),"UTF-8")+"&imageid="+URLEncoder.encode(picName,"UTF-8")+"&apptype=reportapp";
        Log.d("sendingParam",url);
        return url;
    }
    @SuppressLint("NewApi") public static File stream2file(InputStream in) throws IOException
    { final File tempFile = File.createTempFile("okkk", null);
        tempFile.deleteOnExit();
        FileOutputStream out = new FileOutputStream(tempFile);
        IOUtils.copy(in, out);
        return tempFile; }
    public boolean savePicture(String url,Bitmap bm) {
        Log.d("sendURL",url);
        GenericUrl url2 = new GenericUrl(url);
        //byte array holds the data, in this case the image i want to upload in bytes.
        Log.d("actualSize",String.valueOf(bm.getByteCount()));
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitMapData = stream.toByteArray();
        HttpContent contentsend = new ByteArrayContent("image/jpeg", bitMapData );
        HttpRequest putRequest;

        HttpResponse response = null;
        final long time = System.currentTimeMillis();
            while(System.currentTimeMillis()-time < 10000) {
                try {
                    Thread.sleep(100);
                    putRequest = Main_menu.requestFactory.buildPutRequest(url2, contentsend);
                    response = putRequest.execute();
                    response.disconnect();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        //  String content = response.parseAsString();
     //   Log.d("debug", "response is:"+response.getStatusCode());
     //   Log.d("debug", "response content is:"+content);
        return false;
    }
    public static Bitmap getProfilePic() throws IOException {
        URL facebookProfileURL= new URL(Main_menu.profileUrl);
       // Bitmap bitmap = BitmapFactory.decodeStream(facebookProfileURL.openConnection().getInputStream());
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(facebookProfileURL.openConnection().getInputStream(),null,options);
        options.inSampleSize = Camera.calculateInSampleSize(options, 100,100);
        options.inJustDecodeBounds = false;
        Bitmap temp = BitmapFactory.decodeStream(facebookProfileURL.openConnection().getInputStream(),null,options);
        return temp;
    }
    public void createFolder(String url) throws IOException {
        Log.d("sendURL",url);
        GenericUrl url2 = new GenericUrl(url);
        //byte array holds the data, in this case the image i want to upload in bytes.
        byte[] b = new byte[0];
        HttpContent contentsend = new ByteArrayContent("image/jpeg", b);
        HttpRequest putRequest;
        putRequest = Main_menu.requestFactory.buildPutRequest(url2, contentsend);
        HttpResponse response = putRequest.execute();
        //  String content = response.parseAsString();
        //   Log.d("debug", "response is:"+response.getStatusCode());
        //   Log.d("debug", "response content is:"+content);
        response.disconnect();
    }
    public void checkResize() throws IOException {

        String    URI = "https://storage.googleapis.com/" + "traffy_image/"+fbName+"/"+picName+"640x640.jpg";
        GenericUrl url2 = new GenericUrl(URI);
        HttpRequest get = Main_menu.requestFactory.buildGetRequest(url2);
        HttpResponse response2 = get.execute();
    }
    @Override
    public void onPostExecute(String result)
    {
       if(ok==false)
           Toast.makeText(act.getBaseContext(),"Upload fail, please try again",Toast.LENGTH_SHORT).show();
        else
           Toast.makeText(act.getBaseContext(),"Upload Complete",Toast.LENGTH_SHORT).show();
    }
}
