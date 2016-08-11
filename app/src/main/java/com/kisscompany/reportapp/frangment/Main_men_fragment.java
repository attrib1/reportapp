 package com.kisscompany.reportapp.frangment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.adapter.NewFeed_Adapter;
import com.kisscompany.reportapp.util.NewFeed;

import java.util.ArrayList;

 /**
 * A simple {@link Fragment} subclass.
 */
public class Main_men_fragment extends Fragment {

    ListView feed_list;
     ArrayList<NewFeed> list;
    public Main_men_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View customView = inflater.inflate(R.layout.fragment_main_men_fragment,container,false);
        feed_list = (ListView)customView.findViewById(R.id.newFeedList);
        list = new ArrayList<NewFeed>();
        list.add(new NewFeed());
        list.add(new NewFeed());
        list.add(new NewFeed());
        ListAdapter adapter = new NewFeed_Adapter(getActivity(),list);
        feed_list.setAdapter(adapter);
        return customView;
    }

}
