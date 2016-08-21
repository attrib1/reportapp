package com.kisscompany.reportapp.frangment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.activity.Camera;
import com.kisscompany.reportapp.activity.Main_menu;
import com.kisscompany.reportapp.util.uploadImageGoogle;
import com.soundcloud.android.crop.Crop;
import com.soundcloud.android.crop.CropImageActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class Report_fragment extends Fragment {


    View cameraView;
    ImageView postButton;
    ImageView color;
    ImageView incident;
    ImageView typeImage;
    TextView date,info,address;
    Bitmap resultImage;
    static final int REQUEST = 2;
    public Report_fragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Intent camera = new Intent(getContext(),Camera.class);
        startActivityForResult(camera,REQUEST);
        cameraView = inflater.inflate(R.layout.fragment_report_fragment, container, false);

        date = (TextView)cameraView.findViewById(R.id.dateText);
        incident = (ImageView)cameraView.findViewById(R.id.incidentPic);
        color = (ImageView)cameraView.findViewById(R.id.color);
        typeImage = (ImageView)cameraView.findViewById(R.id.typeImage);
        postButton = (ImageView)cameraView.findViewById(R.id.postButton);
        info = (TextView)cameraView.findViewById(R.id.infoTxt);
        address = (TextView)cameraView.findViewById(R.id.addressText);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentTab(0);
                new uploadImageGoogle(resultImage,getActivity()).execute("353086.jpg");
                Toast.makeText(getContext(),"Done posting",Toast.LENGTH_SHORT).show();
            }
        });

        ArrayList<String> item = new ArrayList<String>();//add spinner item
        for(int i = 0 ;i < 5 ; i++)
        {
            item.add("problem "+(i+1));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),R.layout.spinner_layout,item);
        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        WindowManager wm = (WindowManager) cameraView.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int widthInDP = Math.round(dm.widthPixels);
        incident.getLayoutParams().height = widthInDP;

        return cameraView;
    }
    @Override
    public void onActivityResult(int request_code,int result_code,Intent data)
    {

        if(request_code == REQUEST && result_code == Activity.RESULT_OK)
        {
            resultImage = BitmapFactory.decodeFile(data.getStringExtra("RESULT_STRING"));
//            Bitmap finalPic = Camera.rotateImage(temp,90);
            incident.setImageBitmap(resultImage);
            int res = getResources().getIdentifier(data.getStringExtra("ImId"),"drawable",getActivity().getPackageName());
            typeImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(),res,null));
            getAddress();
            getDate();
            color.setBackgroundColor(Color.parseColor(data.getStringExtra("Color")));
            Log.d("resultOk","OK");
        }
        else if(request_code == REQUEST && result_code == 10)
        {
            Intent camera = new Intent(getContext(),Camera.class);
            startActivityForResult(camera,REQUEST);
            Log.d("resultCancel","OK");
        }
        else{
            Log.d("eventCancel","cancel");
        }


    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume()
    {
        date.requestFocus();
        super.onResume();
    }
    public void getDate()
    {
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy, hh:mm a");
        date.setText(format.format(d));
    }
    public void getAddress()
    {
        double long1,lati1;
        lati1 = Double.parseDouble(Main_menu.lat);
        long1 = Double.parseDouble(Main_menu.lng);
        if(lati1>0 && long1>0)
        {
            Geocoder geocode = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses;

            try {
                addresses = geocode.getFromLocation(lati1,long1, 1);

                String Addres_ = addresses.get(0).getAddressLine(0);
                String Country = addresses.get(0).getCountryName();
                String City = addresses.get(0).getAdminArea();
                String street = addresses.get(0).getPostalCode();
                String df = addresses.get(0).getLocality();
                String af = addresses.get(0).getSubLocality();
                address.setText(Addres_+" "+af+" "+df+" "+City+""+street);



            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }//if closing. . .
        else
        {
            Toast.makeText(getContext(), "No Vlaue", Toast.LENGTH_LONG).show();
        }


    }
    public void setCurrentTab(int tab_index){
        FragmentTabHost mTabHost = (FragmentTabHost)getActivity().findViewById(R.id.tab);
        mTabHost.setCurrentTab(tab_index);
    }




}
