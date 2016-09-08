package com.kisscompany.reportapp.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.kisscompany.reportapp.activity.Main_menu;
import com.kisscompany.reportapp.adapter.NewFeed_Adapter;
import com.kisscompany.reportapp.adapter.historyAdapter;
import com.kisscompany.reportapp.frangment.History_fragment;
import com.kisscompany.reportapp.frangment.Main_men_fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mortbay.util.ajax.JSON;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by kak on 9/8/2016.
 */
public class GetFeedInfos {
    ListView feedList;
    List<PostClass> feeds;
    ArrayList<PostClass> newPosts;
    Activity activity;
    private String urlString = null;
    private final Object MONITOR_OBJECT = 0;
    private int eraseIndex = -1;
    int lastPostIndex = 0;
    boolean cancel = false;
    OnRefreshFinishListener mListener = null;
    JSONArray JArray = null;
    boolean ready = false;
    Class c;
    PostClass recentFeed = null;
    public GetFeedInfos(Activity activity,String u,Class c,ListView feedList)
    {
        this.feedList = feedList;
        feeds = new ArrayList<PostClass>();
        newPosts = new ArrayList<PostClass>();
        ListAdapter adapter;
        if (c.equals(Main_men_fragment.class))
            adapter = new NewFeed_Adapter(activity,feeds);
        else
            adapter = new historyAdapter(activity,feeds);
        feedList.setAdapter(adapter);
        this.activity = activity;
        urlString = u;
        this.c = c;
    }
    public interface OnRefreshFinishListener {

        void onRefreshFinished();
    }

    public void setCustomEventListener(OnRefreshFinishListener eventListener) {
        mListener = eventListener;
    }
    public void refreshFeed() throws JSONException, UnsupportedEncodingException {///get only new feed to the head
        Stack<PostClass> stack = new Stack<PostClass>();
       // Log.d("recent",recentFeed.getDate()+" "+recentFeed.getFacebookID());
        synchronized (MONITOR_OBJECT) {//only allow one thread to update post
            for (int i = 0; i < JArray.length(); i++) {
                if(isCancelled())
                    return;
                JSONObject JObject = JArray.getJSONObject(i);
                String picture = URLEncoder.encode(JObject.getString("image_id"), "UTF8");
                String name = URLEncoder.encode(JObject.getString("name"), "UTF-8");
                String content = JObject.getString("comment");
                String stat = JObject.getString("status");
                String problem = JObject.getString("problem_type");
                String time = JObject.getString("time_stamp");
                String faceBook = JObject.getString("facebook");
                String address = JObject.getString("address");
                if (recentFeed.getDate().equals(time) && recentFeed.getFacebookID().equals(faceBook))
                    break;
                Bitmap BitmapPic = getPicture(picture, name, 0);
                PostClass currentPost = new PostClass(BitmapPic, time, address, content, faceBook, problem);
                currentPost.setOwner(name);
                currentPost.setStatus(stat);
                currentPost.setProfilePic(getPicture(faceBook, name, 1));
                stack.push(currentPost);
            }
            while(!stack.isEmpty())
            {
                feeds.add(0,stack.pop());
            }
            recentFeed = feeds.get(0);//update recent feed
            final ArrayAdapter feedadapter;
            if (c.equals(Main_men_fragment.class))
                feedadapter = (NewFeed_Adapter) feedList.getAdapter();
            else
                feedadapter = (historyAdapter) feedList.getAdapter();
            Log.d("notify","noti");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (feedadapter != null) {
                        feedadapter.notifyDataSetChanged();
                    }
                }
            });
        }
        if(mListener!=null)
            mListener.onRefreshFinished();
    }
    public void getMoreFeed() throws JSONException, UnsupportedEncodingException {///get more feed
        if(feeds.size()!=0)
            eraseIndex = feeds.size()-1;
        if(JArray==null)
            return;
        for(int i = lastPostIndex ; i < lastPostIndex+5 && i<JArray.length();i++) {
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
            currentPost.setStatus(stat);
            currentPost.setProfilePic(getPicture(faceBook,name,1));
            //   posts.add(currentPost);
            Log.d("notify","noti");
            newPosts.add(currentPost);

        }
        ready = true;
        Object a = 0;
        synchronized (a){
            a.notifyAll();
        }
    }
    public void addMoreFeed() throws InterruptedException {///add more feed to the tail
        if(JArray ==null||isCancelled())
            return;
        synchronized (MONITOR_OBJECT) {///only allow one thread to update post at a time
            feeds.addAll(newPosts);
            if(feeds.size()!=0)
                recentFeed = feeds.get(0);
            ready = false;
            newPosts.clear();
            lastPostIndex = lastPostIndex + 5;
            if (eraseIndex != -1) {
                feeds.remove(eraseIndex);
            }
            if (lastPostIndex < JArray.length())
                feeds.add(null);
            final ArrayAdapter feedadapter;
            if (c.equals(Main_men_fragment.class))
                feedadapter = (NewFeed_Adapter) feedList.getAdapter();
            else
                feedadapter = (historyAdapter) feedList.getAdapter();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (feedadapter != null) {
                        feedadapter.notifyDataSetChanged();
                    }
                }
            });
        }
        if(mListener!=null)
            mListener.onRefreshFinished();
       if (lastPostIndex >= JArray.length()) {
            if (c.equals(Main_men_fragment.class))
                Main_men_fragment.flag_loading = true;
            else
                History_fragment.hist_loading = true;
        }
        else{
           Thread.sleep(500);
           if (c.equals(Main_men_fragment.class))
               Main_men_fragment.flag_loading = false;
           else
               History_fragment.hist_loading = false;
       }
    }
    public void addNewFeed(){

    }
    public void getFeedJSONArray()
    {
        try {
            URL url = new URL(urlString);
            StringBuffer buff = new StringBuffer();
            String line = "";
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cache-Control", "no-cache");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                buff.append(line);
            }
            connection.disconnect();
            reader.close();
            if(!buff.toString().equals("[]")) {

                JArray = new JSONArray(buff.toString());
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public Bitmap getPicture(String picName,String fbName,int type) {
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

        return bm;
    }
    public boolean isCancelled()
    {
        return cancel;
    }
    public void cancel()
    {
        cancel = true;
    }
    public boolean isReady()
    {
        return ready;
    }
}
