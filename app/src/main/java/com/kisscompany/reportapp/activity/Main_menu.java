package com.kisscompany.reportapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.IOUtils;
import com.google.api.services.storage.StorageScopes;
import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.frangment.History_fragment;
import com.kisscompany.reportapp.frangment.Main_men_fragment;
import com.kisscompany.reportapp.frangment.Noti_fragment;
import com.kisscompany.reportapp.frangment.Report_fragment;
import com.kisscompany.reportapp.frangment.call_fragment;
import com.kisscompany.reportapp.frangment.twitt_fragment;
import com.kisscompany.reportapp.util.sendFeedInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.fabric.sdk.android.Fabric;


public class Main_menu extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener {

    View v,v2,v3,v4,v5,v6;
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
    final int RESULT_FALSE = 4;
    static ImageView avatar;
    static TextView profileName;
    private FragmentTabHost tabHost;
    static ProfilePictureView profilePictureView;
    public static HttpRequestFactory requestFactory;
    public static String id;
    public static String profileUrl;
    public static String profileString = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        // TODO: Move this to where you establish a user session


        setContentView(R.layout.activity_main_menu);
        Toolbar tool = (Toolbar)findViewById(R.id.toolbarMain);

      //  initSharePref();
      /* SharedPreferences sharedPref = getSharedPreferences("Pref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit().clear();
        editor.commit();*/
        profilePictureView = (ProfilePictureView) findViewById(R.id.avatar);
        profileName = (TextView)findViewById(R.id.userName);
        getSharePref();
        //setSupportActionBar(tool);
        try {
            requestFactory = getCredential();
            Log.d("cancel","cancel");
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
            buildGoogleApiClient();//connect GPS
            googleApiClient.connect();

            drawer = (DrawerLayout)findViewById(R.id.drawerLayout);

            drawer.setClickable(true);

            drawerList = (ListView)findViewById(R.id.navList);
            final ArrayList<String> list = new ArrayList<String>();
            if(loginFlag == 1)
                list.add("Logout");
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
        title = (TextView)findViewById(R.id.main_toolbar_title);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
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
        tabHost = (FragmentTabHost)findViewById(R.id.tab);/////////////////create tab host
        tabHost.setup(this,getSupportFragmentManager(),android.R.id.tabcontent);
        Resources res = getResources();
        final TabHost.TabSpec tab1 = tabHost.newTabSpec("tab1");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("tab2");
        TabHost.TabSpec tab3 = tabHost.newTabSpec("tab3");
        TabHost.TabSpec tab4 = tabHost.newTabSpec("tab4");
     //   TabHost.TabSpec tab5 = tabHost.newTabSpec("tab5");
        TabHost.TabSpec tab6 = tabHost.newTabSpec("tab6");

        v = LayoutInflater.from(this).inflate(R.layout.tab_layout,null);

        tab1.setIndicator(v);

        v2 = LayoutInflater.from(this).inflate(R.layout.tab2_layout,null);

        v3 = LayoutInflater.from(this).inflate(R.layout.tab3_layout,null);
        tab2.setIndicator(v2);
        tab3.setIndicator(v3);

        v4 = LayoutInflater.from(this).inflate(R.layout.tab4_layout,null);
        tab4.setIndicator(v4);

    /*    v5 = LayoutInflater.from(this).inflate(R.layout.tab5_layout,null);
        tab5.setIndicator(v5);
*/


        tabHost.addTab(tab1,Main_men_fragment.class,null);
        tabHost.addTab(tab2,Report_fragment.class,null);
        tabHost.addTab(tab3,Noti_fragment.class,null);
        tabHost.addTab(tab4,call_fragment.class,null);
     //   tabHost.addTab(tab5,twitt_fragment.class,null);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String arg0) {

              /*  if(tabHost.getCurrentTab() == 1)
                {
                    Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivity(camera);
                }*/
              //  tabHost.destroyDrawingCache();
                setTabColor(tabHost);
                Main_men_fragment fg = (Main_men_fragment) getSupportFragmentManager().findFragmentByTag("tab1");
              //  fg.refresher();//cancel loading api thread
                fg.destroyCache();//destroy drawing cache
             /*   Noti_fragment fg2 = (Noti_fragment) getSupportFragmentManager().findFragmentByTag("tab3");
                if(fg2!=null)
                ((History_fragment)fg2.getHistoryFragment()).refresher();*/
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
            Log.d("storage","storage");
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
    @Override
    public void onResume(){
        FacebookSdk.sdkInitialize(getApplicationContext());

        super.onResume();
    }
    @Override
    public void onDestroy()
    {
        Log.d("Destroyed","end");
        super.onDestroy();
    }
    @Override
    protected void onPostCreate(Bundle saveInstant)
    {
        super.onPostCreate(saveInstant);
        toggle.syncState();
    }
    @Override
    protected void onActivityResult(int request_code,int result_code,Intent data)
    {
        if(request_code == 7&& result_code == RESULT_OK)/// result from facebook login
        {
        Main_menu.loginFlag = 1;
        setLoginFlag();
        Main_menu.profilePictureView.setProfileId(LoginActivity.userName);
        Main_menu.profileName.setText(LoginActivity.facebookName);
        Main_menu.profileString = LoginActivity.facebookName;
        Main_menu.id = LoginActivity.userName;
        Main_menu.profileUrl = LoginActivity.profilePicUrl;
        }
        super.onActivityResult(request_code,result_code,data);
    }
    public void setProfilePic() throws IOException {
        URL facebookProfileURL= new URL(LoginActivity.profilePicUrl);
        // Bitmap bitmap = BitmapFactory.decodeStream(facebookProfileURL.openConnection().getInputStream());
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(facebookProfileURL.openConnection().getInputStream(),null,options);
        options.inSampleSize = Camera.calculateInSampleSize(options, 100,100);
        options.inJustDecodeBounds = false;
        final Bitmap temp = BitmapFactory.decodeStream(facebookProfileURL.openConnection().getInputStream(),null,options);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                avatar.setImageBitmap(temp);
            }
        });

    }
    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
      //  super.onBackPressed();
    }
    public HttpRequestFactory getCredential() throws GeneralSecurityException, IOException {
        List<String> scopes = new ArrayList<String>();
        scopes.add(StorageScopes.DEVSTORAGE_FULL_CONTROL);
        HttpTransport httpTransport= new com.google.api.client.http.javanet.NetHttpTransport();
        AssetManager am = getAssets();
        InputStream inputStream = am.open("Traffy-f869c3fe8e95.p12"); //you should not put the key in assets in prod version.
        File file =stream2file(inputStream);
        JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
        GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId("storage@traffy-cloud.iam.gserviceaccount.com")
                .setServiceAccountScopes(scopes)
                .setServiceAccountPrivateKeyFromP12File(file)
                .build();

        HttpRequestFactory requestFactory = httpTransport.createRequestFactory(credential);
        Log.d("finishCredential","finsih");

        return requestFactory;
    }
    @SuppressLint("NewApi") public static File stream2file(InputStream in) throws IOException
    { final File tempFile = File.createTempFile("okkk", null);
        tempFile.deleteOnExit();
        FileOutputStream out = new FileOutputStream(tempFile);
        IOUtils.copy(in, out);
        return tempFile; }
    public void getSharePref()
    {
        SharedPreferences sharedPref = getSharedPreferences("Pref",Context.MODE_PRIVATE);
        String flag = sharedPref.getString(getString(R.string.login_status),null);
     //   Log.d("loginWoi",flag);
        if(flag !=null) {
            loginFlag = Integer.parseInt(flag);
            if(loginFlag==1)
            {
                id = sharedPref.getString("id",null);


                profilePictureView.setProfileId(id);
                profileString = sharedPref.getString("Name",null);
                profileUrl = sharedPref.getString("profileUrl",null);
                Log.d("profileUrl",profileUrl);
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
    public void setLoginFlag()
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

}
