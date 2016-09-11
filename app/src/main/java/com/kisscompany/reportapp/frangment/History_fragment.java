package com.kisscompany.reportapp.frangment;


import android.graphics.SweepGradient;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.activity.LoginActivity;
import com.kisscompany.reportapp.activity.Main_menu;
import com.kisscompany.reportapp.adapter.historyAdapter;
import com.kisscompany.reportapp.util.GetFeedInfos;
import com.kisscompany.reportapp.util.PostClass;
import com.kisscompany.reportapp.util.getFeedInfo;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class History_fragment extends Fragment {

    ListView hisList;
    View histView;
    GetFeedInfos feedInfo;
    SwipeRefreshLayout swipe;
    SwipeRefreshLayout.OnRefreshListener refreshListener;
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
        hisList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
                    if (hist_loading== false) {
                        hist_loading = true;
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if(!feedInfo.isReady())
                                    {
                                        Object a = 0;
                                        synchronized (a){
                                            a.wait();
                                        }
                                    }
                                    feedInfo.addMoreFeed();
                                    feedInfo.getMoreFeed();


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        t.start();
                    }
                }
            }
        });
        swipe = (SwipeRefreshLayout)histView.findViewById(R.id.swipe);
        feedInfo = new GetFeedInfos(getActivity(),"http://cloud.traffy.in.th/attapon/API/private_apis/get_report.php?app_type=reportapp&facebook_id=" + Main_menu.id,History_fragment.class,hisList);///input api
        feedInfo.setCustomEventListener(new GetFeedInfos.OnRefreshFinishListener() {
            @Override
            public void onRefreshFinished() {
                if(getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipe.setRefreshing(false);
                        }
                    });
                }
            }
        });
        swipe.setEnabled(false);
        swipe.post(new Runnable() {
            @Override
            public void run() {
                swipe.setRefreshing(true);
            }
        });
        if(!Main_menu.profileString.equals("Anonymous")) {
            hisList.setVisibility(View.VISIBLE);
            swipe.setVisibility(View.VISIBLE);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        feedInfo.getFeedJSONArray();
                        feedInfo.getMoreFeed();
                        feedInfo.addMoreFeed();
                        feedInfo.getMoreFeed();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
        else {
            hisList.setVisibility(View.INVISIBLE);
            swipe.setVisibility(View.INVISIBLE);
            swipe.setEnabled(false);
            Toast.makeText(getContext(), "Please login to facebook", Toast.LENGTH_SHORT).show();
        }
        refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        };
        swipe.setOnRefreshListener(refreshListener);
        return histView;
    }
    @Override
    public void onPause()
    {
      //  feedInfo.cancel();
        super.onPause();
    }
    @Override
    public void onResume()
    {
        super.onResume();
    }
    public void refresher(){
        feedInfo.cancel();
    }

}
