package com.kisscompany.reportapp.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chanpc on 8/16/2016.
 */
public class number_Parcel implements Parcelable {
    String number,name;

    public number_Parcel(Parcel in) {
        number = in.readString();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(number);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<number_Parcel> CREATOR = new Creator<number_Parcel>() {
        @Override
        public number_Parcel createFromParcel(Parcel in) {
            return new number_Parcel(in);
        }

        @Override
        public number_Parcel[] newArray(int size) {
            return new number_Parcel[size];
        }
    };
}
