package com.kisscompany.reportapp.util;

import java.util.HashMap;

/**
 * Created by kak on 9/24/2016.
 */
public class User {
    public String facebookID;
    public int postAmount;
    HashMap<String,Integer> badges;
    int score;
    public User(String facebookID,int score)
    {
        this.facebookID = facebookID;
        this.score = score;
    }
    public void setBadges(HashMap<String,Integer> bades)
    {
        this.badges = bades;
    }
    public HashMap<String,Integer> getBadges()
    {
        return badges;
    }
    public void setPostAmount(int postAmount)
    {
        this.postAmount = postAmount;
    }
    public int getPostAmount()
    {
        return postAmount;
    }
    public void setScore(int score)
    {
        this.score = score;
    }
    public int getScore()
    {
        return score;
    }
}
