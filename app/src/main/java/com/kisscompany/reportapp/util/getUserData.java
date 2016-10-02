package com.kisscompany.reportapp.util;


import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class getUserData extends AsyncTask<String,String,String>{

    HttpURLConnection connection=null;
    URL url;
    BufferedReader reader=null;
    @Override
    protected String doInBackground(String... params) {

        try {
            url = new URL(params[0]);
            connection = (HttpURLConnection)url.openConnection();
            StringBuffer buff = new StringBuffer();
            String line = "";
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while((line = reader.readLine())!=null)
            {
                buff.append(line);
            }
            Log.d("user",buff.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(connection!=null)
                connection.disconnect();
            if(reader !=null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }


        return null;
    }

}