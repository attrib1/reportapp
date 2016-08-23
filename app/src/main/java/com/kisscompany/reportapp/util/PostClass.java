package com.kisscompany.reportapp.util;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by chanpc on 8/9/2016.
 */
public class PostClass {

    String Owner="Chan",Content,status;
    Bitmap mainPic,profile;
    String adress;
    String date;
    String facebookID;
    String type;
    long    postID;
    int like;
    public PostClass(Bitmap b,String d,String l,String des,String id,String t)
    {
        mainPic = b;
        date = d;
        adress = l;
        Content = des;
        facebookID = id;
        type = t;
    }
    public String getOwner()
    {
        return Owner;
    }
    public String getDate(){
        return date;
    }
    public String getContent()
    {
        return Content;
    }
    public String getStatus(){ return status; }
    public int getLike(){ return like; }
    public long getPostID()
    {
        return postID;
    }
    public Bitmap getPic()
    {
        return mainPic;
    }
    public String getAdress()
    {
        return adress;
    }
    public String getFacebookID(){
        return facebookID;
    }
    public String getType()
    {
        return  type;
    }
    public void setOwner(String owner)
    {
        Owner = owner;
    }
    public Bitmap getProfilePic()
    {
        return profile;
    }
    public void setProfilePic(Bitmap m)
    {
        profile = m;
    }
    public void setFacebookID(String a)
    {
        facebookID = a;
    }
}
