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
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
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
import com.soundcloud.android.crop.Crop;
import com.soundcloud.android.crop.CropImageActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

 /**
 * A simple {@link Fragment} subclass.
 */
public class Report_fragment extends Fragment {


     View cameraView;
     Spinner typeSpinner;
     ImageView postButton;
     ImageView color;
     ImageView incident;
     ImageView typeImage;
     EditText description;
     TextView date,info;
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
        typeSpinner = (Spinner)cameraView.findViewById(R.id.typeSpin);
        description = (EditText)cameraView.findViewById(R.id.descriptText);
        typeImage = (ImageView)cameraView.findViewById(R.id.typeImage);
        postButton = (ImageView)cameraView.findViewById(R.id.postButton);
        info = (TextView)cameraView.findViewById(R.id.infoTxt);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postButton.setVisibility(View.INVISIBLE);
                description.setBackground(null);
                description.setEnabled(false);
                typeSpinner.setBackground(null);
                info.setBackground(null);
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
        typeSpinner.setAdapter(adapter);

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
            Bitmap temp = BitmapFactory.decodeFile(data.getStringExtra("RESULT_STRING"));
//            Bitmap finalPic = Camera.rotateImage(temp,90);
            incident.setImageBitmap(temp);
            int res = getResources().getIdentifier(data.getStringExtra("ImId"),"drawable",getActivity().getPackageName());
            typeImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(),res,null));
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
         description.setText("");
         super.onPause();
     }

     @Override
     public void onResume()
     {
         date.requestFocus();
         super.onResume();
     }




}
