package com.kisscompany.reportapp.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.util.PostClass;

import java.util.List;

/**
 * Created by chanpc on 8/9/2016.
 */
public class NewFeed_Adapter extends ArrayAdapter<PostClass> {
    ImageView profile,incident;
    TextView text;
    int widthInDP;
    public NewFeed_Adapter(Context context, List<PostClass> list) {
        super(context, R.layout.newfeed_layout,list);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        widthInDP = Math.round(dm.widthPixels);
    }
    @Override
    public View getView(int position,View convertView,ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View customView;
        if(position < 0) {
            customView = inflater.inflate(R.layout.newfeed_layout, parent, false);
            profile = (ImageView)customView.findViewById(R.id.profile);
            incident = (ImageView)customView.findViewById(R.id.incident);
            incident.getLayoutParams().height = widthInDP;
            incident.setImageBitmap(getItem(position).getPic());
            text = (TextView) customView.findViewById(R.id.date);
        }
        else
            customView= inflater.inflate(R.layout.loading_layout,parent,false);

        return  customView;
    }
}
