package com.kisscompany.reportapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.util.Category;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by chanpc on 8/15/2016.
 */
public class categoryAdapter extends ArrayAdapter<Category> {
    ImageView image;
    TextView text;

    public categoryAdapter(Context context, List<Category> list) {

        super(context, R.layout.cat_layout,list);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.cat_layout,parent,false);

        image = (ImageView)customView.findViewById(R.id.catIm);
        text = (TextView)customView.findViewById(R.id.catText);
        image.setImageDrawable(ResourcesCompat.getDrawable(customView.getResources(),getItem(position).getPicId(),null));
        text.setText(getItem(position).getText());
        text.setTextColor(Color.parseColor("#000000"));
        return  customView;
    }
}
