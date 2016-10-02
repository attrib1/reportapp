package com.kisscompany.reportapp.adapter;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.util.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kak on 9/29/2016.
 */
public class ranking_adapter extends ArrayAdapter<User> {
    ImageView[] numberArray;
    ImageView ranking;
    public ranking_adapter(Context context,List<User> user) {
        super(context,R.layout.ranking_layout,user);
        numberArray = new ImageView[5];

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.ranking_layout,parent,false);
        initComponent(customView);

        User user = getItem(position);
        int score = user.getScore();
        int divider = 10000;
        boolean flag = false;
        for(int i = 4;i >=0;i-- ){
            int unit = score/divider;
            if(unit !=0)
                flag = true;
            score = score % divider;
            divider = divider/10;
            int res = getContext().getResources().getIdentifier("score"+String.valueOf(unit),"drawable", getContext().getPackageName());
            Log.d("Flag",""+flag);
            if(flag ==true)
                numberArray[i].setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(),res,null));

        }
        int res = getContext().getResources().getIdentifier("rank"+String.valueOf(position+1),"drawable", getContext().getPackageName());
        ranking.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(),res,null));

        return  customView;
    }
    private void initComponent(View customView){
        numberArray[0] = (ImageView)customView.findViewById(R.id.bit0);
        numberArray[1] = (ImageView)customView.findViewById(R.id.bit1);
        numberArray[2] = (ImageView)customView.findViewById(R.id.bit2);
        numberArray[3] = (ImageView)customView.findViewById(R.id.bit3);
        numberArray[4] = (ImageView)customView.findViewById(R.id.bit4);

        ranking = (ImageView)customView.findViewById(R.id.ranking);
    }
}
