package com.kisscompany.reportapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.kisscompany.reportapp.R;

import java.util.List;

/**
 * Created by kak on 10/1/2016.
 */
public class badge_listAdapter extends ArrayAdapter<String> {
    public badge_listAdapter(Context context, List<String> objects) {
        super(context, R.layout.badge_layout, objects);
    }
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.badge_layout,parent,false);
        return customView;
    }
}
