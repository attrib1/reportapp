package com.kisscompany.reportapp.adapter;

import android.content.Context;
import android.os.Handler;
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
import java.util.Queue;

/**
 * Created by chanpc on 8/9/2016.
 */
public class NewFeed_Adapter extends ArrayAdapter<PostClass> {
    ImageView profile,incident,typeImg;
    TextView text,address,descript,name,typeText;
    int widthInDP;
    private int visible = -1;
    int res;
    Context con;
    Queue<Integer> visibleQueue;
    public NewFeed_Adapter(Context context, List<PostClass> list, Queue<Integer> v) {
        super(context, R.layout.newfeed_layout,list);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        widthInDP = Math.round(dm.widthPixels);
        con = context;
        visibleQueue = v;

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View customView;
        if(getItem(position) ==null) {
            customView= inflater.inflate(R.layout.loading_layout,parent,false);
        }
        else{
            customView = inflater.inflate(R.layout.newfeed_layout, parent, false);
            typeText = (TextView)customView.findViewById(R.id.typeText);
            if(visibleQueue.size()!=0&&visibleQueue.contains(position)) {
                Log.d("visible",""+visibleQueue.peek());
                typeText.setVisibility(View.VISIBLE);
                Handler handle = new Handler();
                handle.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        visibleQueue.remove(position);
                        notifyDataSetChanged();
                    }
                },2000);
                //visibleQueue.poll();
            }
            profile = (ImageView)customView.findViewById(R.id.profile);
            profile.setImageBitmap(getItem(position).getProfilePic());
            incident = (ImageView)customView.findViewById(R.id.incident);
            incident.getLayoutParams().height = widthInDP;
            typeImg = (ImageView)customView.findViewById(R.id.typeImage);
            typeImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    visibleQueue.add(position);
                    notifyDataSetChanged();

                }
            });
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
