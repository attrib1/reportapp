package com.kisscompany.reportapp.frangment;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.adapter.NewFeed_Adapter;
import com.kisscompany.reportapp.util.PostClass;
import com.kisscompany.reportapp.util.getFeedInfo;

import org.mortbay.jetty.Main;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Main_men_fragment extends Fragment {

    ListView feed_list;
    SwipeRefreshLayout refresh;
    ArrayList<PostClass> list;
    SwipeRefreshLayout.OnRefreshListener refreshListener;
    public Main_men_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View customView = inflater.inflate(R.layout.fragment_main_men_fragment,container,false);
        refresh = (SwipeRefreshLayout)customView.findViewById(R.id.main_swipe);////refresh bar
        //refresh.setRefreshing(false);
        feed_list = (ListView)customView.findViewById(R.id.newFeedList);

        refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //////refresh task
                getFeedInfo feedInfo = new getFeedInfo(getActivity(),feed_list);///input api
                feedInfo.setCustomEventListener(new getFeedInfo.OnRefreshFinishListener() {
                    @Override
                    public void onRefreshFinished() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refresh.setRefreshing(false);
                            }
                        });
                    }
                });
                feedInfo.execute("https://storage.googleapis.com/traffy_image/pic1.png");
                refresh.setEnabled(false);
            }
        };
        refresh.setOnRefreshListener(refreshListener);
        list = new ArrayList<PostClass>();
        ListAdapter adapter = new NewFeed_Adapter(getActivity(),list);
        feed_list.setAdapter(adapter);
        feed_list.setOnScrollListener(new AbsListView.OnScrollListener( ) {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem ==0){
                    View v = feed_list.getChildAt(0);
                    if(v!=null )
                    {
                        int offset = v.getTop();
                        if(offset ==0) {

                            refresh.setEnabled(true);
                        }
                        else
                            refresh.setEnabled(false);
                    }


                }
            }
        });

        refresh.post(new Runnable() {
            @Override public void run() {
                refresh.setRefreshing(true);
                refreshListener.onRefresh();
            }
        });
        return customView;
    }
    @Override
    public void onPause(){
        Log.d("destroy","destroy");
        refresh.setRefreshing(false);
        if (refresh!=null) {
            refresh.setRefreshing(false);
            refresh.destroyDrawingCache();
            refresh.clearAnimation();
        }

        super.onPause();
    } 

}
