package com.kisscompany.reportapp.activity;

import android.Manifest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.api.client.http.HttpRequestFactory;
import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.frangment.Main_men_fragment;
import com.kisscompany.reportapp.frangment.User_fragment;
import com.kisscompany.reportapp.frangment.Ranking_Fragment;
import com.kisscompany.reportapp.frangment.Report_fragment;
import com.kisscompany.reportapp.frangment.call_fragment;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;


public class Main_menu extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener {

    View v,v2,v3,v4,v5;
    public static TextView title;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    public static String locaText = null;
    private GoogleApiClient googleApiClient;
    private LocationAvailability locationAvailability;
    private static final int REQUEST_LOCATION = 0;
    private LocationRequest locationRequest;
    public static String lat="",lng="";
    private boolean gps = false;
    ListView drawerList;
    public static int loginFlag = 0;
    static TextView profileName;
    private FragmentTabHost tabHost;
    static ProfilePictureView profilePictureView;
    public static HttpRequestFactory requestFactory;
    public static String id;
    public static String profileUrl;
    public static String profileString = "";
    Toolbar tool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        // TODO: Move this to where you establish a user session
        /*sendUserInfo get = new sendUserInfo();
        get.execute("http://cloud.traffy.in.th/attapon/API/private_apis/report_user_profile.php");*/
        init();
        getSharePref();//get old login status
        buildGoogleApiClient();//connect GPS
        googleApiClient.connect();
        DrawerSetUp();
        TabSetUp();



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
            Log.d("location","location");
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationConnect();
            } else {///exit
                if(android.os.Build.VERSION.SDK_INT >= 21)
                {
                    finishAndRemoveTask();
                }
                else
                {
                    finish();
                }
            }
        }
        else if(requestCode == REQUEST_EXTERNAL_STORAGE){
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {//exit
            } else {
                if(android.os.Build.VERSION.SDK_INT >= 21)
                {
                    finishAndRemoveTask();
                }
                else
                {
                    finish();
                }
            }
        }
    }
    private void getLocationConnect() {
        //check runtime permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                gps =  false;
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                return;
            }
            verifyStoragePermissions(this);


        }
        gps = true;
    }
    @Override
    public void onConnected(Bundle bundle) {/// if google API connect get location
       getLocationConnect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    @Override
    public void onLocationChanged(Location location) {//if location change

        lat = String.valueOf(location.getLatitude());
        lng = String.valueOf(location.getLongitude());

        Log.d("Location", "lat=" + lat + " lng " + lng);
        locaText = "lat=" + lat + "&lng=" + lng;


    }
    @Override
    public void onResume(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onResume();
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
    @Override
    protected void onPostCreate(Bundle saveInstant)
    {
        super.onPostCreate(saveInstant);
        toggle.syncState();//synce drawer
    }
    @Override
    protected void onActivityResult(int request_code,int result_code,Intent data)
    {
        if(request_code == 7&& result_code == RESULT_OK)/// result from facebook login
        {
            Main_menu.loginFlag = 1;
            setLoginFlag();// save user value and login status
            Main_menu.profilePictureView.setProfileId(LoginActivity.userName);
            Main_menu.profileName.setText(LoginActivity.facebookName);
            Main_menu.profileString = LoginActivity.facebookName;
            Main_menu.id = LoginActivity.userName;
            Main_menu.profileUrl = LoginActivity.profilePicUrl;
        }
        super.onActivityResult(request_code,result_code,data);
    }
    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
      //  super.onBackPressed();
    }
    public void getSharePref()///check wheter user already login or not
    {
        SharedPreferences sharedPref = getSharedPreferences("Pref",Context.MODE_PRIVATE);//get share pref
        String flag = sharedPref.getString(getString(R.string.login_status),null);//get old login flag from share pref
        if(flag !=null) {
            loginFlag = Integer.parseInt(flag);
            if(loginFlag==1)//if already login
            {
                ///get old value (id,name,profile picurl)
                id = sharedPref.getString("id",null);
                profilePictureView.setProfileId(id);
                profileString = sharedPref.getString("Name",null);
                profileUrl = sharedPref.getString("profileUrl",null);
                profileName.setText(profileString);
            }
            else{
                id = null;
                profilePictureView.setProfileId(id);
                profileString = "Anonymous";
                profileUrl = null;
                profileName.setText(profileString);

            }

        }
        else{
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.login_status), "0");
            id = null;
            profilePictureView.setProfileId(id);
            profileString = "Anonymous";
            profileUrl = null;
            profileName.setText(profileString);
            editor.commit();
            loginFlag = 0;
        }
    }
    private static final int REQUEST_EXTERNAL_STORAGE = 1;////verify permission
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    public void setLoginFlag()//save login value
    {
        SharedPreferences sharedPref = getSharedPreferences("Pref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.login_status), String.valueOf(Main_menu.loginFlag));
        editor.putString("id",LoginActivity.userName);
        editor.putString("Name",LoginActivity.facebookName);
        editor.putString("profileUrl",LoginActivity.profilePicUrl);
        Log.d("FBNAME",LoginActivity.facebookName);
        editor.commit();
    }
    private void init()
    {

        setContentView(R.layout.activity_main_menu);
        tool = (Toolbar)findViewById(R.id.toolbarMain);

        title = (TextView)findViewById(R.id.main_toolbar_title);

        profilePictureView = (ProfilePictureView) findViewById(R.id.avatar);
        profileName = (TextView)findViewById(R.id.userName);

        drawer = (DrawerLayout)findViewById(R.id.drawerLayout);
        drawer.setClickable(true);
        drawerList = (ListView)findViewById(R.id.navList);

        tabHost = (FragmentTabHost)findViewById(R.id.tab);/////////////////create tab host
    }
    private void TabSetUp()
    {
        tabHost.setup(this,getSupportFragmentManager(),android.R.id.tabcontent);
        final TabHost.TabSpec tab1 = tabHost.newTabSpec("tab1");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("tab2");
        TabHost.TabSpec tab3 = tabHost.newTabSpec("tab3");
        TabHost.TabSpec tab4 = tabHost.newTabSpec("tab4");
        TabHost.TabSpec tab5 = tabHost.newTabSpec("tab5");
        // TabHost.TabSpec tab6 = tabHost.newTabSpec("tab6");

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
        tabHost.addTab(tab3,User_fragment.class,null);
        tabHost.addTab(tab4,call_fragment.class,null);
        tabHost.addTab(tab5, Ranking_Fragment.class,null);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String arg0) {

                setTabColor(tabHost);
                if(!arg0.equals("tab1")) {
                    Main_men_fragment fg = (Main_men_fragment) getSupportFragmentManager().findFragmentByTag("tab1");
                    fg.refresher();//cancel loading api thread
                    fg.destroyCache();//destroy drawing cache
                }

            }

        });
        setTabColor(tabHost);
    }
    private void DrawerSetUp()
    {
        final ArrayList<String> list = new ArrayList<String>();
        final ListAdapter adapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1,list);
        drawerList.setAdapter(adapter);
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(loginFlag == 1) {/// logout clear Preference iamge and text
                    LoginManager.getInstance().logOut();
                    profilePictureView.setProfileId(null);
                    profileName.setText("Anonymous");
                    profileString = "Anonymous";
                    SharedPreferences sharedPref = getSharedPreferences("Pref",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit().clear();
                    editor.commit();
                    loginFlag = 0;
                    list.remove(position);
                    ((ArrayAdapter)adapter).notifyDataSetChanged();
                    Toast.makeText(Main_menu.this, "Logout Complete", Toast.LENGTH_SHORT).show();
                    tabHost.setCurrentTab(0);

                }
                else// if not login yet
                {
                    Intent intent = new Intent(Main_menu.this,LoginActivity.class);
                    Toast.makeText(getBaseContext(),"Please login to facebook",Toast.LENGTH_SHORT).show();
                    startActivityForResult(intent,7);
                    tabHost.setCurrentTab(0);
                }
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        toggle = new ActionBarDrawerToggle(this, drawer, tool, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View view) {

                invalidateOptionsMenu();

            }

            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        int re = getResources().getIdentifier("ic_action_name","drawable", getPackageName());
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(ResourcesCompat.getDrawable(getResources(),re,null));
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {///when slide bar is toggled
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    list.clear();
                    if(loginFlag == 1) {

                        list.add("Logout");
                    }
                    else
                    {
                        list.add("Login");
                    }
                    ((ArrayAdapter)adapter).notifyDataSetChanged();
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        drawer.addDrawerListener(toggle);
    }
}
