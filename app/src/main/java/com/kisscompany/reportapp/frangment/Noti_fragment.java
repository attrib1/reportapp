package com.kisscompany.reportapp.frangment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.activity.Main_menu;
import com.kisscompany.reportapp.adapter.fragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Noti_fragment extends Fragment {

    ViewPager viewPager;
    View notiView;
    History_fragment history_fragment;
    public Noti_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Main_menu.title.setText("ประวัติการรายงาน");
        notiView = inflater.inflate(R.layout.fragment_noti_fragment, container, false);
        viewPager = (ViewPager)notiView.findViewById(R.id.noti_pager);
        List<Fragment> listFragment = new ArrayList<Fragment>();
        history_fragment = new History_fragment();
        listFragment.add(history_fragment);
        listFragment.add(new picture_Fragment());
        fragmentPagerAdapter adapter = new fragmentPagerAdapter(getChildFragmentManager() ,listFragment);
        viewPager.setAdapter( adapter);
        Log.d("create","again");
        return notiView;
    }
    public Fragment getHistoryFragment()
    {
        return history_fragment;
    }
    @Override
    public void onDestroy(){
        Log.d("Destroy","destroy");
        ((History_fragment)getHistoryFragment()).refresher();
        super.onDestroy();

    }


}
