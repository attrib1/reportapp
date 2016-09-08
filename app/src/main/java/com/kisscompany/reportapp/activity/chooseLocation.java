package com.kisscompany.reportapp.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.util.FoursquareVenue;
import com.kisscompany.reportapp.util.getFourSquare;

public class chooseLocation extends ListActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location);
        getFourSquare get = new getFourSquare(this);
        get.execute("https://api.foursquare.com/v2/venues/search?client_id=LO4ENGV3CEEHAY02HPU4RI4OYAD5BDILSOHJTZ0STXM4IPDK&client_secret=M1POSZ5J5IFJ45MAIFWLYEOVJWPBNAIPISUMP5H4SRXGZKBZ&v=20140806&ll="+ Main_menu.lat+","+Main_menu.lng+"&intent=browse&radius=100&locale=th");
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("name",((FoursquareVenue)getListAdapter().getItem(position)).getName());
                bundle.putString("city",((FoursquareVenue)getListAdapter().getItem(position)).getCity());
                bundle.putString("country",((FoursquareVenue)getListAdapter().getItem(position)).getCountry());
                bundle.putString("postal",((FoursquareVenue)getListAdapter().getItem(position)).getPostal());
                bundle.putString("state",((FoursquareVenue)getListAdapter().getItem(position)).getState());
                intent.putExtra("location",bundle);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

}
