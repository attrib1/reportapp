package com.kisscompany.reportapp.frangment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.adapter.historyAdapter;
import com.kisscompany.reportapp.util.PostClass;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class History_fragment extends Fragment {

    ListView hisList;
    View histView;
    public History_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        histView = inflater.inflate(R.layout.fragment_history_fragment, container, false);
        hisList = (ListView)histView.findViewById(R.id.historyList);
        List<PostClass> l = new ArrayList<PostClass>();
        ListAdapter adapter = new historyAdapter(getContext(),l);
        hisList.setAdapter(adapter);
        return histView;
    }

}
