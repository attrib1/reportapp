package com.kisscompany.reportapp.util;

import android.graphics.Bitmap;

/**
 * Created by chanpc on 8/9/2016.
 */
public class PostClass {

    String Owner,Content,Date,status;
    Bitmap mainPic,profile;
    String adress;
    long    postID;
    int like;
    public PostClass(Bitmap b)
    {
        mainPic = b;
    }
    public String getOwner()
    {
        return Owner;
    }
    public String getDate(){
        return Date;
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
}
