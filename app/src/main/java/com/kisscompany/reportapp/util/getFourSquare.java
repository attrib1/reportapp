package com.kisscompany.reportapp.util;

import android.app.Activity;
import android.app.ListActivity;
import android.graphics.Camera;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Spinner;

import com.kisscompany.reportapp.adapter.LocationAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by chanpc on 8/29/2016.
 */
public class getFourSquare extends AsyncTask<String,String,String> {

    ArrayList<FoursquareVenue> venues;
    ListActivity activity;
    public getFourSquare(ListActivity activity)
    {
        this.activity = activity;
    }
    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection connection=null;
        BufferedReader  reader = null;
        URL url;
        try {
            url = new URL(params[0]);
            connection = (HttpURLConnection)url.openConnection();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer buff = new StringBuffer();
            String line = "";
            while((line = reader.readLine())!=null)
            {
                buff.append(line);
            }
            Log.d("buff",buff.toString());
            venues = parseFoursquare(buff.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(connection!=null)
                connection.disconnect();
            if(reader!=null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return null;
    }
    @Override
    public void onPostExecute(String result)
    {
        ListAdapter adapter = new LocationAdapter(activity,venues);
        activity.setListAdapter(adapter);
    }
    private static ArrayList parseFoursquare(final String response) {

        ArrayList<FoursquareVenue> temp = new ArrayList<FoursquareVenue>();
        try {

            // make an jsonObject in order to parse the response
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.has("response"))
            {
                if(jsonObject.getJSONObject("response").has("venues"))
                {
                    JSONArray jsonArray = jsonObject.getJSONObject("response").getJSONArray("venues");
                    for(int i = 0 ; i <jsonArray.length();i++)
                    {
                        JSONObject currentJSON = jsonArray.getJSONObject(i);
                        FoursquareVenue venue = new FoursquareVenue();
                        venue.setName(currentJSON.getString("name"));
                        currentJSON = currentJSON.getJSONObject("location");
                        if(currentJSON.has("city"))
                            venue.setCity(currentJSON.getString("city"));
                        if(currentJSON.has("country"))
                            venue.setCountry(currentJSON.getString("country"));
                        if(currentJSON.has("postalCode"))
                        venue.setPostal(currentJSON.getString("postalCode"));
                        if(currentJSON.has("state"))
                        venue.setState(currentJSON.getString("state"));
                        temp.add(venue);
                    }
                }
            }
        } catch (Exception e) {
        e.printStackTrace();
        return new ArrayList();
        }
        return temp;

        }
}
