package com.kisscompany.reportapp.util;

import java.util.ArrayList;

/**
 * Created by chanpc on 8/15/2016.
 */
public class Category {
    int Picid;
    String category;
    ArrayList<Phone> phoneList;
    public Category(int id, String name,ArrayList<Phone> list)
    {
        Picid = id;
        category = name;
        phoneList = list;

    }
    public int getPicId()
    {
        return Picid;
    }
    public String getText()
    {
        return category;
    }
    public ArrayList<Phone> getPhoneList()
    {
        return phoneList;
    }
}
