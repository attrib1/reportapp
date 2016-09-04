package com.kisscompany.reportapp.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.activity.LoginActivity;
import com.kisscompany.reportapp.activity.Main_menu;
import com.kisscompany.reportapp.adapter.NewFeed_Adapter;
import com.kisscompany.reportapp.adapter.historyAdapter;
import com.kisscompany.reportapp.frangment.History_fragment;
import com.kisscompany.reportapp.frangment.Main_men_fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.net.ssl.HttpsURLConnection;

import io.fabric.sdk.android.services.common.SystemCurrentTimeProvider;
import retrofit2.http.Url;

/**
 * Created by chanpc on 8/18/2016.
 */
public class getFeedInfo extends AsyncTask<String,String,String> {


    Activity act;
    ListView listV;
    OnRefreshFinishListener mListener = null;
    List<PostClass> posts;
    ArrayList<PostClass> dumper;
    Class c;
    int index = 0;
    JSONArray JArray;
    long first;
    public getFeedInfo(Activity a,ListView list,Class l,List flist)
    {
        act = a;
        listV = list;
        c = l;
        posts = new ArrayList<PostClass>();
        dumper = new ArrayList<PostClass>();
        posts = flist;
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
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cache-Control", "no-cache");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while((line = reader.readLine())!=null)
            {
                buff.append(line);
            }
            connection.disconnect();
            reader.close();

            if(!buff.toString().equals("[]")) {
                Log.d("refresh",buff.toString());
                JArray = new JSONArray(buff.toString());
                posts = new ArrayList<PostClass>();
                final ListAdapter adapter;
                Queue<Integer> que = new LinkedList<Integer>();
                if (c.equals(Main_men_fragment.class))
                    adapter = new NewFeed_Adapter(act, posts, que);
                else
                    adapter = new historyAdapter(act, posts);
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listV.setAdapter(adapter);
                        listV.setEnabled(false);
                    }
                });
                getTenItem();
            }
            else {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(act.getBaseContext(), "No post", Toast.LENGTH_SHORT).show();
                    }
                });

            }



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
    public Bitmap getPicture(String picName,String fbName,int type) {
        first = System.currentTimeMillis();
        String URI = "" ;
        if(type == 0)
            URI = "https://storage.googleapis.com/" + "traffy_image/"+fbName+"/"+picName+"640x640.jpg";
        else
            URI = "https://storage.googleapis.com/" + "traffy_image/"+fbName+"/"+picName;
        Bitmap bm = null;
        HttpResponse response2 = null;
        while(true) {
            GenericUrl url2 = new GenericUrl(URI);
            try {
                HttpRequest get = Main_menu.requestFactory.buildGetRequest(url2);
                response2 = get.execute();
                bm = BitmapFactory.decodeStream(new BufferedInputStream((response2.getContent())));
                response2.disconnect();
                break;
            } catch (IOException e) {
                URI = "https://storage.googleapis.com/" + "traffy_image/"+fbName+"/"+picName;
                e.printStackTrace();
            }
        }

        Log.d("Picture",String.valueOf(System.currentTimeMillis()-first));
        return bm;
    }
    public void getTenItem() throws JSONException, GeneralSecurityException, IOException {

        ArrayList<PostClass> dummy = new ArrayList<PostClass>();
        int eraseIndex = -1;
        if(posts.size()!=0)
            eraseIndex = posts.size()-1;
        for(int i = index ; i < index+5 && i<JArray.length();i++) {
           if(isCancelled()) {

                return;
            }
            JSONObject JObject = JArray.getJSONObject(i);
            String picture = URLEncoder.encode(JObject.getString("image_id"),"UTF8");
            String name = URLEncoder.encode(JObject.getString("name"),"UTF-8");
            String content = JObject.getString("comment");
            String stat = JObject.getString("status");
            String problem = JObject.getString("problem_type");
            String time = JObject.getString("time_stamp");
             String faceBook = JObject.getString("facebook");
            String address = JObject.getString("address");
            Bitmap BitmapPic = getPicture(picture,name,0);
            PostClass currentPost = new PostClass(BitmapPic,time,address,content,faceBook,problem);
            currentPost.setOwner(name);
            currentPost.setProfilePic(getPicture(faceBook,name,1));
         //   posts.add(currentPost);
            dummy.add(currentPost);

        }
        posts.addAll(dummy);

        dummy.clear();
        index = index +5;

        if(eraseIndex!=-1) {
            posts.remove(eraseIndex);
            for(PostClass l : posts)
            {
                Log.d("post",""+l.getDate());
            }
        }
        if(index < JArray.length())
            posts.add(null);

        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayAdapter feedadapter;
                if (c.equals(Main_men_fragment.class))
                    feedadapter = (NewFeed_Adapter) listV.getAdapter();
                else
                    feedadapter = (historyAdapter) listV.getAdapter();
                if (feedadapter != null) {
                    feedadapter.notifyDataSetChanged();
                    listV.setEnabled(true);
                }
                if (index >= JArray.length()) {
                    if (c.equals(Main_men_fragment.class))
                        Main_men_fragment.flag_loading = true;
                    else
                        History_fragment.hist_loading = true;
                }
                else {
                    if (c.equals(Main_men_fragment.class))
                        Main_men_fragment.flag_loading = false;
                    else
                        History_fragment.hist_loading = false;
                }

            }
        });


    }




}
