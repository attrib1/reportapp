package com.kisscompany.reportapp.frangment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.activity.Main_menu;
import com.kisscompany.reportapp.util.getFeedInfo;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import io.fabric.sdk.android.Fabric;

/**
 * A simple {@link Fragment} subclass.
 */
public class twitt_fragment extends ListFragment {
    View twitterView;
    SwipeRefreshLayout swipe;
    SwipeRefreshLayout.OnRefreshListener refreshListener;
    TwitterAuthConfig authConfig;
    UserTimeline userTimeline;
    TweetTimelineListAdapter adapter;
    public twitt_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem ==0){
                    View v = getListView().getChildAt(0);
                    if(v!=null )
                    {
                        int offset = v.getTop();
                 //       Toast.makeText(getContext(), String.valueOf(v.getTop()), Toast.LENGTH_SHORT).show();
                        if(offset ==0) {

                            swipe.setEnabled(true);
                        }
                        else {
                            swipe.setEnabled(false);
                            Log.d("offset",String.valueOf(offset));
                          //  Toast.makeText(getContext(),String.valueOf(offset),Toast.LENGTH_SHORT).show();
                        }
                    }


                }
            }
        });

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Main_menu.title.setText("Twitter");
        twitterView = inflater.inflate(R.layout.fragment_twitt_fragment, container, false);
        // Inflate the layout for this fragment
        swipe = (SwipeRefreshLayout)twitterView.findViewById(R.id.twitter_swipe);///doing swipe to update function
        refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.refresh(new Callback<TimelineResult<Tweet>>() {
                    @Override
                    public void success(Result<TimelineResult<Tweet>> result) {
                        swipe.setRefreshing(false);
                    }
                    @Override
                    public void failure(TwitterException exception) {

                    }
                });
                swipe.setRefreshing(false);
            }
        };
        swipe.setOnRefreshListener(refreshListener);
        swipe.post(new Runnable() {
            @Override public void run() {
                swipe.setRefreshing(true);
                refreshListener.onRefresh();
            }
        });
        Log.d("createView","view");
        return twitterView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }
    @Override
    public void onResume()
    {
        TwitterAuthConfig authConfig = new TwitterAuthConfig("Liq1M74Wl5FItzPzKDW1E73kb", "Jet9MZuvI5NqtR5eqeqBvgQzzCeKt8gk4NninD0sXVNXrhIZyB");
        Fabric.with(getContext(), new Twitter(authConfig));
        UserTimeline userTimeline = new UserTimeline.Builder().screenName("Traffy").build();
        adapter = new TweetTimelineListAdapter.Builder(getContext())
                .setTimeline(userTimeline)
                .build();
        setListAdapter(adapter);
        super.onResume();
    }
    @Override
    public void onPause(){
        Log.d("destroy","destroy");
        swipe.setRefreshing(false);
        if (swipe!=null) {
            swipe.setRefreshing(false);
            swipe.destroyDrawingCache();
            swipe.clearAnimation();
        }
        super.onPause();
    }

}
