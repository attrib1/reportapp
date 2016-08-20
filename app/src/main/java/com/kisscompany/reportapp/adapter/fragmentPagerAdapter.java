package com.kisscompany.reportapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by chanpc on 8/17/2016.
 */
public class fragmentPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> list;
    public fragmentPagerAdapter(FragmentManager fm,List<Fragment> listFragment) {
        super(fm);
        list = listFragment;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
