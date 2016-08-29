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
import com.kisscompany.reportapp.util.FoursquareVenue;

import java.util.List;

/**
 * Created by chanpc on 8/29/2016.
 */
public class LocationAdapter extends ArrayAdapter<FoursquareVenue> {
    TextView name,city,state,country,postal;
    public LocationAdapter(Context context, List<FoursquareVenue> list) {
        super(context, R.layout.location_layout,list);

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.location_layout,parent,false);

        name = (TextView)customView.findViewById(R.id.name);
        city = (TextView)customView.findViewById(R.id.city);
        state = (TextView)customView.findViewById(R.id.state);
        postal = (TextView)customView.findViewById(R.id.postal);
        country = (TextView)customView.findViewById(R.id.country);

        name.setText(getItem(position).getName());
        city.setText(getItem(position).getCity());
        state.setText(getItem(position).getState());
        country.setText(getItem(position).getCountry());
        postal.setText(getItem(position).getPostal());


        return  customView;
    }
}
