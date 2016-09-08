package com.kisscompany.reportapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.util.PostClass;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by chanpc on 8/17/2016.
 */
public class historyAdapter extends ArrayAdapter<PostClass> {
    TextView date,address,info,statusText;
    ImageView img,status;
    public historyAdapter(Context context, List<PostClass> list) {
        super(context, R.layout.history_layout,list );
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView;
        if(getItem(position) ==null) {
            customView= inflater.inflate(R.layout.loading_layout,parent,false);
        }
        else {
            customView = inflater.inflate(R.layout.history_layout,parent,false);
            status  = (ImageView)customView.findViewById(R.id.stat);
            date = (TextView) customView.findViewById(R.id.date);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");///get time span
            try {
                Date d = format.parse(getItem(position).getDate());
                DateUtils utils = new DateUtils();
                date.setText(utils.getRelativeTimeSpanString(d.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            address = (TextView) customView.findViewById(R.id.address);
            address.setText(getItem(position).getAdress());
            img = (ImageView) customView.findViewById(R.id.smallincident);
            img.setImageBitmap(getItem(position).getPic());
            statusText = (TextView)customView.findViewById(R.id.statusText);
            status(getItem(position).getStatus());

        }
        return customView;
    }
    public void status(String state)
    {
        if(state.equals("report"))
        {
            statusText.setText("รอการดำเนินการ");
            statusText.setTextColor(Color.parseColor("#ffffff"));
            statusText.setBackgroundColor(Color.parseColor("#ff0000"));
        }
    }
}
