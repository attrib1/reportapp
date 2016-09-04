package com.kisscompany.reportapp.frangment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.activity.LoginActivity;
import com.kisscompany.reportapp.activity.Main_menu;
import com.kisscompany.reportapp.adapter.historyAdapter;
import com.kisscompany.reportapp.util.PostClass;
import com.kisscompany.reportapp.util.getFeedInfo;

import org.json.JSONException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class History_fragment extends Fragment {

    ListView hisList;
    View histView;
    static public boolean hist_loading = false;
    public History_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        histView = inflater.inflate(R.layout.fragment_history_fragment, container, false);
        hisList = (ListView)histView.findViewById(R.id.historyList);
        List<PostClass> list = new ArrayList<PostClass>();
        final getFeedInfo feedInfo = new getFeedInfo(getActivity(),hisList,History_fragment.class,list);
        feedInfo.execute("http://cloud.traffy.in.th/attapon/API/private_apis/get_report.php?app_type=reportapp&facebook_id="+ Main_menu.id);
        feedInfo.setCustomEventListener(new getFeedInfo.OnRefreshFinishListener() {
            @Override
            public void onRefreshFinished() {

            }
        });

        hisList.setOnScrollListener(new AbsListView.OnScrollListener( ) {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
                {
                    if(hist_loading == false)
                    {
                        hist_loading = true;

                        Log.d("newItem","new");
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    feedInfo.getTenItem();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (GeneralSecurityException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        t.start();


                    }
                }
            }
        });
        return histView;
    }

}
