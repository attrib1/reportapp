 package com.kisscompany.reportapp.frangment;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.activity.Camera;
import com.kisscompany.reportapp.activity.Main_menu;
import com.soundcloud.android.crop.Crop;
import com.soundcloud.android.crop.CropImageActivity;

import java.io.File;
import java.io.IOException;

 /**
 * A simple {@link Fragment} subclass.
 */
public class Report_fragment extends Fragment {


     View cameraView;
     ImageView incident;
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
        incident = (ImageView)cameraView.findViewById(R.id.incidentPic);
        return cameraView;
    }
     @Override
     public void onActivityResult(int request_code,int result_code,Intent data)
     {

        if(request_code == REQUEST && result_code == Activity.RESULT_OK)
        {
            Bitmap temp = BitmapFactory.decodeFile(data.getStringExtra("RESULT_STRING"));


            Bitmap finalPic = Camera.rotateImage(temp,90);
            incident.setImageBitmap(finalPic);
        }

     }




}
