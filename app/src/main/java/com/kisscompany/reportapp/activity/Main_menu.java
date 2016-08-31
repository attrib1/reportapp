package com.kisscompany.reportapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
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
import com.facebook.login.LoginManager;
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
    private boolean gps = true;
    ListView drawerList;
    public static boolean loginFlag = false;
    final int RESULT_FALSE = 4;
    static ImageView avatar;
    static TextView profileName;
    private FragmentTabHost tabHost;
    public static HttpRequestFactory requestFactory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        // TODO: Move this to where you establish a user session


        setContentView(R.layout.activity_main_menu);
        Toolbar tool = (Toolbar)findViewById(R.id.toolbarMain);
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
        avatar = (ImageView)findViewById(R.id.avatar);
        profileName = (TextView)findViewById(R.id.userName);
        drawer.setClickable(true);

        drawerList = (ListView)findViewById(R.id.navList);
        String[] list = {"Logout"};
        ListAdapter adapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1,list);
        drawerList.setAdapter(adapter);
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(Main_menu.this,LoginActivity.class);
                startActivityForResult(intent,7);
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
                tabHost.destroyDrawingCache();
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
                gps =  false;
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                return;
            }


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
        Log.d("chanzaa","SDFsdfds");
        if(gps == false) {
            Log.d("chanzaa","in");
            getLocationConnect();
        }
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
   /* @Override
    protected void onActivityResult(int request_code,int result_code,Intent data)
    {
        Log.d("chanzaa","chanzaa");
        if(request_code == 7 && result_code == RESULT_OK)
        {

            Thread t=  new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        setProfilePic();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
            TextView name = (TextView)findViewById(R.id.userName);
            name.setText(LoginActivity.facebookName);
        }
        super.onActivityResult(request_code,result_code,data);
    }*/
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
}
