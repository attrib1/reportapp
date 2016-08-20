package com.kisscompany.reportapp.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chanpc on 8/15/2016.
 */
public class Phone implements Parcelable {

    String number,name;

    public Phone(String nu,String na)
    {
        number = na;
        name = nu;
    }

    protected Phone(Parcel in) {
        number = in.readString();
        name = in.readString();
    }

    public static final Creator<Phone> CREATOR = new Creator<Phone>() {
        @Override
        public Phone createFromParcel(Parcel in) {
            return new Phone(in);
        }

        @Override
        public Phone[] newArray(int size) {
            return new Phone[size];
        }
    };

    public String getNumber()
    {
        return number;
    }
    public String getName()
    {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(number);
        dest.writeString(name);
    }
}
