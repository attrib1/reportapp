package com.kisscompany.reportapp.frangment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.kisscompany.reportapp.R;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import io.fabric.sdk.android.Fabric;

/**
 * A simple {@link Fragment} subclass.
 */
public class twitt_fragment extends Fragment {

    ProgressBar progress;
    ListView twitter;
    View twitterView;
    public twitt_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        twitterView = inflater.inflate(R.layout.fragment_twitt_fragment, container, false);
        // Inflate the layout for this fragment
        twitter = (ListView) twitterView.findViewById(R.id.twitterList);
        progress = (ProgressBar)twitterView.findViewById(R.id.progressBar);

        TwitterAuthConfig authConfig = new TwitterAuthConfig("Liq1M74Wl5FItzPzKDW1E73kb", "Jet9MZuvI5NqtR5eqeqBvgQzzCeKt8gk4NninD0sXVNXrhIZyB");
        Fabric.with(getContext(), new Twitter(authConfig));
        UserTimeline userTimeline = new UserTimeline.Builder().screenName("Traffy").build();
        TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(getContext())
                .setTimeline(userTimeline)
                .build();
        twitter.setAdapter(adapter);
       /* twitter.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                progress.setVisibility(View.INVISIBLE);
                twitter.removeOnLayoutChangeListener(this);

            }
        });
        twitter.deferNotifyDataSetChanged();*/

        return twitterView;
    }

}
