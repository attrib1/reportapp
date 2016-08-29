package com.kisscompany.reportapp.activity;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.util.getFourSquare;

public class chooseLocation extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location);
        getFourSquare get = new getFourSquare(this);
        get.execute("https://api.foursquare.com/v2/venues/search?client_id=LO4ENGV3CEEHAY02HPU4RI4OYAD5BDILSOHJTZ0STXM4IPDK&client_secret=M1POSZ5J5IFJ45MAIFWLYEOVJWPBNAIPISUMP5H4SRXGZKBZ&v=20140806&ll=14.075911,100.602131&intent=browse&radius=20&locale=th");
    }
}
