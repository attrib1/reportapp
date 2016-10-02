package com.kisscompany.reportapp.util;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.http.Url;

/**
 * Created by kak on 9/27/2016.
 */
public class sendUserInfo extends AsyncTask<String,String,String> {
    HttpURLConnection connection=null;
    URL url;
    DataOutputStream output=null;
    @Override
    protected String doInBackground(String... params) {
        try {
            url = new URL(params[0]);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            //connection.setDoOutput(true);
            //connection.setDoInput(true);
            connection.connect();
            output = new DataOutputStream(connection.getOutputStream());
            output.writeBytes(getUrlParam());
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            Log.d("feedback",reader.readLine());
            reader.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(connection!=null)
                connection.disconnect();
            if(output!=null)
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }
    private String getUrlParam(){
        String param = "name=chan2&facebook_id=102";

        return param;
    }
}
