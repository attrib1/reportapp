package com.kisscompany.reportapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.util.PostClass;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by chanpc on 8/17/2016.
 */
public class historyAdapter extends ArrayAdapter<PostClass> {
    TextView date,address,info;
    ImageView img;
    public historyAdapter(Context context, List<PostClass> list) {
        super(context, R.layout.history_layout,list );
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.history_layout,parent,false);
        date = (TextView)customView.findViewById(R.id.date);
        date.setText(getItem(position).getDate());
        address = (TextView)customView.findViewById(R.id.address);
        address.setText(getItem(position).getAdress());
        info = (TextView)customView.findViewById(R.id.infotext);
        info.setText(getItem(position).getContent());
        img = (ImageView)customView.findViewById(R.id.smallincident);
        img.setImageBitmap(getItem(position).getPic());
        return  customView;
    }
}
