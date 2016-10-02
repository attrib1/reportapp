package com.kisscompany.reportapp.frangment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.adapter.ranking_adapter;
import com.kisscompany.reportapp.util.User;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Ranking_Fragment extends ListFragment {


    public Ranking_Fragment() {
        // Required empty public constructor
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rankingView = inflater.inflate(R.layout.fragment_ranking, container, false);
        List<User> list = new ArrayList<User>();
       // User u = new User("10304",103);
        list.add(new User("10304",96842));
        list.add(new User("10304",320));
        list.add(new User("10304",5));
        ListAdapter adapter = new ranking_adapter(getContext(),list);
        setListAdapter(adapter);
        return rankingView;
    }


}
