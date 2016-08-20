package com.kisscompany.reportapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.util.PostClass;

import java.util.List;

/**
 * Created by chanpc on 8/17/2016.
 */
public class historyAdapter extends ArrayAdapter<PostClass> {
    public historyAdapter(Context context, List<PostClass> list) {
        super(context, R.layout.history_layout,list );
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.history_layout,parent,false);

        return  customView;
    }
}
