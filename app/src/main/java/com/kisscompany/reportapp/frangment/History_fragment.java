package com.kisscompany.reportapp.frangment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.activity.LoginActivity;
import com.kisscompany.reportapp.adapter.historyAdapter;
import com.kisscompany.reportapp.util.PostClass;
import com.kisscompany.reportapp.util.getFeedInfo;

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
        getFeedInfo feedInfo = new getFeedInfo(getActivity(),hisList,History_fragment.class);
        feedInfo.execute("http://cloud.traffy.in.th/attapon/API/private_apis/get_report.php?facebook_id="+ LoginActivity.userName);

        return histView;
    }

}
