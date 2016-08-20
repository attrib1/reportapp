package com.kisscompany.reportapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.util.Phone;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by chanpc on 8/16/2016.
 */
public class number_adapter extends ArrayAdapter<Phone> {
    TextView number,header;
    public number_adapter(Context context, List<Phone> list) {
        super(context, R.layout.number_list_layout,list);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.number_list_layout,parent,false);
        number = (TextView)customView.findViewById(R.id.number);
        header = (TextView)customView.findViewById(R.id.header);
        number.setText(getItem(position).getNumber());
        header.setText(getItem(position).getName());

        return  customView;
    }
}
