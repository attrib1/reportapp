package com.kisscompany.reportapp.frangment;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.activity.Main_menu;
import com.kisscompany.reportapp.adapter.NewFeed_Adapter;
import com.kisscompany.reportapp.adapter.historyAdapter;
import com.kisscompany.reportapp.util.GetFeedInfos;
import com.kisscompany.reportapp.util.PostClass;
import com.kisscompany.reportapp.util.getFeedInfo;

import org.json.JSONException;
import org.mortbay.jetty.Main;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * A simple {@link Fragment} subclass.
 */
public class Main_men_fragment extends Fragment {
    int x = 0;
    ListView feed_list;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    SwipeRefreshLayout refresh;
    ArrayList<PostClass> list;
    GetFeedInfos feedInfo;
    SwipeRefreshLayout.OnRefreshListener refreshListener;
    static public boolean flag_loading = false;
    List<PostClass> feeds;
    int loading = 1;
    public Main_men_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Main_menu.title.setText("รายงานใหม่");
        // Inflate the layout for this fragment
        Log.d("create","Create");
        View customView = inflater.inflate(R.layout.fragment_main_men_fragment,container,false);
        setRetainInstance(true);
        refresh = (SwipeRefreshLayout)customView.findViewById(R.id.main_swipe);////refresh bar
        //refresh.setRefreshing(false);
        feed_list = (ListView)customView.findViewById(R.id.newFeedList);
        feedInfo = new GetFeedInfos(getActivity(),"http://cloud.traffy.in.th/attapon/API/private_apis/get_report.php?limit=100&app_type=reportapp",Main_men_fragment.class,feed_list);///input api
        feedInfo.setCustomEventListener(new GetFeedInfos.OnRefreshFinishListener() {
            @Override
            public void onRefreshFinished() {
                if(getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refresh.setRefreshing(false);
                            refresh.setEnabled(true);
                        }
                    });
                }
            }
        });
        refresh.post(new Runnable() {
            @Override
            public void run() {
                refresh.setRefreshing(true);
                refresh.setEnabled(false);
            }
        });

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
        refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //////refresh task
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            feedInfo.getFeedJSONArray();
                            feedInfo.refreshFeed();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                });
                t.start();
                refresh.setEnabled(false);
            }
        };
                            refresh.setOnRefreshListener(refreshListener);

                            feed_list.setOnScrollListener(new AbsListView.OnScrollListener( ) {
                                @Override
                                public void onScrollStateChanged(AbsListView view, int scrollState) {

                                }

                                @Override
                                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                    if (firstVisibleItem ==0){
                                        View v = feed_list.getChildAt(0);
                                        if(v!=null ) {
                                            int offset = v.getTop();
                                            if (offset == 0) {

                                                refresh.setEnabled(true);
                        }
                        else
                            refresh.setEnabled(false);
                    }
                }
                else if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0) {
                        if (flag_loading == false) {
                            flag_loading = true;
                            Thread t = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {

                                        x++;
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
                else
                    refresh.setEnabled(false);
            }

        });


        return customView;
    }
    @Override
    public void onPause(){
       // refresh.setRefreshing(false);
       /* if (refresh!=null) {
           refresh.setRefreshing(false);
            refresh.destroyDrawingCache();
            refresh.clearAnimation();
        }*/
        super.onPause();
    }
    @Override
    public void onResume(){


        super.onResume();
    }
    @Override
    public void onDestroy()
    {
        Log.d("destroy","destroy");
        super.onDestroy();
    }

    public void setrefresh()
    {
        refresh.setRefreshing(true);
    }
    public void destroyCache()
    {
        refresh.setRefreshing(false);
        refresh.destroyDrawingCache();
        refresh.clearAnimation();
    }
    public void refresher()
    {
        feedInfo.cancel();
    }
}
