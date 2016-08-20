package com.kisscompany.reportapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.adapter.number_adapter;
import com.kisscompany.reportapp.util.Phone;

import java.util.ArrayList;

public class number_Activity extends AppCompatActivity {

    ListView numberList;
    ImageView backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_);
        ((TextView) findViewById(R.id.main_toolbar_title)).setText(getIntent().getStringExtra("title"));
        numberList = (ListView)findViewById(R.id.numberList);
        backButton = (ImageView)findViewById(R.id.getBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ArrayList<Phone> p = getIntent().getParcelableArrayListExtra("numberList");
        ListAdapter adapter = new number_adapter(this,p);
        numberList.setClickable(false);
        numberList.setAdapter(adapter);

    }
}
