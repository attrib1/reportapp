package com.kisscompany.reportapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.frangment.Main_men_fragment;
import com.kisscompany.reportapp.frangment.Noti_fragment;
import com.kisscompany.reportapp.frangment.Report_fragment;
import com.kisscompany.reportapp.frangment.call_fragment;
import com.kisscompany.reportapp.frangment.twitt_fragment;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Main_menu extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener {

    View v,v2,v3,v4,v5,v6;
    public static String locaText = null;
    private GoogleApiClient googleApiClient;
    private LocationAvailability locationAvailability;
    private static final int REQUEST_LOCATION = 0;
    private LocationRequest locationRequest;
    public static String lat="",lng="";
    private FragmentTabHost tabHost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        buildGoogleApiClient();//connect GPS
        googleApiClient.connect();

        tabHost = (FragmentTabHost)findViewById(R.id.tab);/////////////////create tab host
        tabHost.setup(this,getSupportFragmentManager(),android.R.id.tabcontent);
        Resources res = getResources();
        TabHost.TabSpec tab1 = tabHost.newTabSpec("tab1");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("tab2");
        TabHost.TabSpec tab3 = tabHost.newTabSpec("tab3");
        TabHost.TabSpec tab4 = tabHost.newTabSpec("tab4");
        TabHost.TabSpec tab5 = tabHost.newTabSpec("tab5");
        TabHost.TabSpec tab6 = tabHost.newTabSpec("tab6");

        v = LayoutInflater.from(this).inflate(R.layout.tab_layout,null);

        tab1.setIndicator(v);

        v2 = LayoutInflater.from(this).inflate(R.layout.tab2_layout,null);

        v3 = LayoutInflater.from(this).inflate(R.layout.tab3_layout,null);
        tab2.setIndicator(v2);
        tab3.setIndicator(v3);

        v4 = LayoutInflater.from(this).inflate(R.layout.tab4_layout,null);
        tab4.setIndicator(v4);

        v5 = LayoutInflater.from(this).inflate(R.layout.tab5_layout,null);
        tab5.setIndicator(v5);



        tabHost.addTab(tab1,Main_men_fragment.class,null);
        tabHost.addTab(tab2,Report_fragment.class,null);
        tabHost.addTab(tab3,Noti_fragment.class,null);
        tabHost.addTab(tab4,call_fragment.class,null);
        tabHost.addTab(tab5,twitt_fragment.class,null);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String arg0) {

              /*  if(tabHost.getCurrentTab() == 1)
                {
                    Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivity(camera);
                }*/
                setTabColor(tabHost);

            }

        });
        setTabColor(tabHost);
        //tabHost.addTab(tab2,Report_fragment.class,null);



    }
    public void setTabColor(TabHost tabhost) {

        ImageView view;
        TextView text;
        int res;


        for(int i=0;i<tabhost.getTabWidget().getChildCount();i++) {

            text = (TextView)tabhost.getTabWidget().getChildAt(i).findViewById(R.id.header);
            text.setTextColor(Color.GRAY);
            tabhost.getTabWidget().getChildAt(i).setBackgroundColor(0xfff3f3f3);
            view = (ImageView)tabhost.getTabWidget().getChildAt(i).findViewById(R.id.tabIcon);
            Log.d("getTag",view.getTag().toString());
            res = getResources().getIdentifier(view.getTag().toString()+"_gray","drawable",getPackageName());
            view.setImageDrawable(ResourcesCompat.getDrawable(getResources(),res,null));
        }//unselected
        text = (TextView)tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).findViewById(R.id.header);
        text.setTextColor(Color.WHITE);
        view = (ImageView)tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).findViewById(R.id.tabIcon);
        res = getResources().getIdentifier(view.getTag().toString()+"_white","drawable",getPackageName());
        view.setImageDrawable(ResourcesCompat.getDrawable(getResources(),res,null));
        tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setBackgroundColor(Color.parseColor("#3d70b6")); //2nd tab selected
    }
    private  void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationConnect();
            } else {
                // Permission was denied or request was cancelled
            }
        }
    }
    private void getLocationConnect() {
        //check runtime permission

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= 23) {

                if
                        (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showMessageOKCancel("อนุญาตให้ App Traffy Bus เข้าถึงตำแหน่งปัจจุบันของคุณ",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= 23) {
                                        requestPermissions(new
                                                        String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                REQUEST_LOCATION);
                                    }
                                }
                            });
                    return;
                }
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            locationAvailability =
                    LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);

            locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                    locationRequest, this);


            LocationManager manager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(Main_menu.this, "คุณไม่ได้เปิด GPS", Toast.LENGTH_SHORT).show();
            }


        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener
            onClickListener) {
        new AlertDialog.Builder(Main_menu.this)
                .setMessage(message)
                .setPositiveButton("ตกลง", onClickListener)
                .setNegativeButton("ยกเลิก", null)
                .create()
                .show();
    }
    @Override
    public void onConnected(Bundle bundle) {
        getLocationConnect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    @Override
    public void onLocationChanged(Location location) {

        lat = String.valueOf(location.getLatitude());
        lng = String.valueOf(location.getLongitude());

        Log.d("Location", "lat=" + lat + " lng " + lng);
        locaText = "lat=" + lat + "&lng=" + lng;


    }


}
