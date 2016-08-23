package com.kisscompany.reportapp.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.IOUtils;
import com.google.api.services.storage.StorageScopes;
import com.kisscompany.reportapp.activity.LoginActivity;
import com.kisscompany.reportapp.activity.Main_menu;
import com.kisscompany.reportapp.adapter.NewFeed_Adapter;
import com.kisscompany.reportapp.adapter.historyAdapter;
import com.kisscompany.reportapp.frangment.Main_men_fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.http.Url;

/**
 * Created by chanpc on 8/18/2016.
 */
public class getFeedInfo extends AsyncTask<String,String,String> {


    Activity act;
    ListView listV;
    OnRefreshFinishListener mListener = null;
    List<PostClass> posts;
    Class c;
    public getFeedInfo(Activity a,ListView list,Class l)
    {
        act = a;
        listV = list;
        c = l;
        posts = new ArrayList<PostClass>();
    }
    public interface OnRefreshFinishListener {
        void onRefreshFinished();
    }

    public void setCustomEventListener(OnRefreshFinishListener eventListener) {
        mListener = eventListener;
    }
    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            StringBuffer buff = new StringBuffer();
            String line= "";
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Cache-Control", "no-cache");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while((line = reader.readLine())!=null)
            {
                buff.append(line);
            }
            Log.d("reportM",buff.toString());
            JSONArray JArray = new JSONArray(buff.toString());
            for(int i = 0 ; i < 1;i++) {
                JSONObject JObject = JArray.getJSONObject(i);
                String picture = JObject.getString("id");
                String name = JObject.getString("name");
                //int like = JObject.getInt("like");
                String content = JObject.getString("comment");
                String stat = JObject.getString("status");
                String time = JObject.getString("time_stamp");
                String faceBook = JObject.getString("facebook_id");
                String address = JObject.getString("address");
                Bitmap BitmapPic = getPicture(picture,name);
               PostClass currentPost = new PostClass(BitmapPic,time,address,content,faceBook,stat);
                currentPost.setOwner(name);
                currentPost.setProfilePic(getPicture(faceBook,name));
                posts.add(currentPost);
                // re establish image url
            }
            connection.disconnect();
            reader.close();
            posts.add(null);
            final ListAdapter adapter;
            if(c.equals(Main_men_fragment.class))
                adapter = new NewFeed_Adapter(act,posts);
            else
                adapter = new historyAdapter(act,posts);
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listV.setAdapter(adapter);
                }
            });


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(mListener!=null)
            mListener.onRefreshFinished();
        return null;
    }
    @Override
    protected void onPostExecute(String result)
    {

    }


    public Bitmap getPicture(String picName,String fbName) throws GeneralSecurityException, IOException {
        //String URI = "https://storage.googleapis.com/" + "traffy_image/"+picName;
        Log.d("picNmae",picName);
        String URI =  "https://storage.googleapis.com/" + "traffy_image/"+URLEncoder.encode(fbName,"UTF-8")+"/"+picName;
        GenericUrl url2 = new GenericUrl(URI);
        HttpRequest get = LoginActivity.requestFactory.buildGetRequest(url2);
        HttpResponse response2 = get.execute();
        final Bitmap bm = BitmapFactory.decodeStream(response2.getContent());
        response2.disconnect();
        Log.d("getPic",String.valueOf(bm.getByteCount()));
        return bm;
    }


}
