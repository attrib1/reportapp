package com.kisscompany.reportapp.util;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by chanpc on 8/18/2016.
 */
public class sendFeedInfo extends AsyncTask<String,String,String> {


    PostClass post;//content to post
    HttpsURLConnection connection;
    Activity act;
    sendFeedInfo(PostClass p, Activity a)
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
            String urlParams = getUrlParam();
            connection = (HttpsURLConnection) url.openConnection();
            connection.getDoOutput();
            connection.setRequestMethod("POST");
            output = new DataOutputStream(connection.getOutputStream());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(connection!=null)
                connection.disconnect();
        }

        return null;
    }
    public String getUrlParam() throws IOException {
        String url =  "Name="+URLEncoder.encode(post.getDate(),"UTF-8");
        Date d = new Date();//get unix date
        long unixtime = d.getTime();
        url = url+"dateTime="+String.valueOf(unixtime)+"Name="+URLEncoder.encode(post.getOwner(),"UTF-8")+"status="+post.getStatus()
                +"like="+String.valueOf(post.getLike());
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String picName = reader.readLine();
        uploadImageGoogle upload = new uploadImageGoogle(post.getPic(),act);
        upload.execute(picName+".jpg");
        return url;
    }

}
