package com.kisscompany.reportapp.frangment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.adapter.badge_listAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class badges_Fragment extends Fragment {

    GridView badgeGrid;
    public badges_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View customView = inflater.inflate(R.layout.fragment_picture_, container, false);
        badgeGrid = (GridView)customView.findViewById(R.id.badgeGrid);
        List<String> badges = new ArrayList<String>();
        badges.add("noob");
        badges.add("noob");
        badges.add("noob");
        badges.add("noob");
        badges.add("noob");
        badges.add("noob");
        badges.add("noob");
        badges.add("noob");
        badges.add("noob");
        ListAdapter adapter = new badge_listAdapter(getContext(),badges);
        badgeGrid.setAdapter(adapter);
        return customView;


    }

}
