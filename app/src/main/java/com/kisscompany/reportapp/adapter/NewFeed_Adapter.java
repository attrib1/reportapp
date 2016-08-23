package com.kisscompany.reportapp.adapter;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
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
    ImageView profile,incident,typeImg;
    TextView text,address,descript,name;
    int widthInDP;
    int res;
    Context con;
    public NewFeed_Adapter(Context context, List<PostClass> list) {
        super(context, R.layout.newfeed_layout,list);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        widthInDP = Math.round(dm.widthPixels);
        con = context;

    }
    @Override
    public View getView(int position,View convertView,ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View customView;
        if(position == getCount()-1 || ((position%9)==1)&&position!=1) {
            customView= inflater.inflate(R.layout.loading_layout,parent,false);
        }
        else{
            customView = inflater.inflate(R.layout.newfeed_layout, parent, false);
            profile = (ImageView)customView.findViewById(R.id.profile);
            profile.setImageBitmap(getItem(position).getProfilePic());
            incident = (ImageView)customView.findViewById(R.id.incident);
            incident.getLayoutParams().height = widthInDP;
            typeImg = (ImageView)customView.findViewById(R.id.typeImage);
            int res = con.getResources().getIdentifier(getItem(position).getType(),"drawable", getContext().getPackageName());
            typeImg.setImageDrawable(ResourcesCompat.getDrawable(con.getResources(),res,null));
            incident.setImageBitmap(getItem(position).getPic());
            text = (TextView) customView.findViewById(R.id.date);
            text.setText(getItem(position).getDate());
            address = (TextView) customView.findViewById(R.id.location);
            address.setText(getItem(position).getAdress());
            descript = (TextView)customView.findViewById(R.id.comment);
            descript.setText(getItem(position).getContent());
            name = (TextView)customView.findViewById(R.id.nameText);
            name.setText(getItem(position).getOwner());
        }


        return  customView;
    }
}
