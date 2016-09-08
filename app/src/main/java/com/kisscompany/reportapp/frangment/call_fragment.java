package com.kisscompany.reportapp.frangment;


import android.app.Activity;
import android.content.Intent;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.activity.Main_menu;
import com.kisscompany.reportapp.activity.number_Activity;
import com.kisscompany.reportapp.adapter.categoryAdapter;
import com.kisscompany.reportapp.util.Category;
import com.kisscompany.reportapp.util.Phone;
import com.kisscompany.reportapp.util.number_Parcel;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.TooManyListenersException;

/**
 * A simple {@link Fragment} subclass.
 */
public class call_fragment extends Fragment {

    ListView catListView;
    View categoryView;
    public call_fragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Main_menu.title.setText("เกี่ยวกับแอพนี้");
        categoryView = inflater.inflate(R.layout.about_layout, container, false);




        return categoryView;
    }
    public ArrayList<Category> getCat()
    {
        ArrayList<Category> cList= new ArrayList<Category>();
        ArrayList<Phone> p = new ArrayList<Phone>();
        p.add(new Phone("เหตุด่วน-เหตุร้าย","191"));
        p.add(new Phone("แจ้งอุบัติเหตุทางหลวง","1193"));
        cList.add(new Category(R.drawable.ic_heart,"เบอร์ฉุกเฉิน",p));

        p = new ArrayList<Phone>();
        p.add(new Phone("แท็กซี่นครชัย","028789000"));
        cList.add(new Category(R.drawable.ic_heart,"แท็กซี่",p));

        p = new ArrayList<Phone>();


        cList.add(new Category(R.drawable.ic_heart,"โรงพยาบาล",p));

        return cList;
    }
}
