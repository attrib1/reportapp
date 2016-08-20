package com.kisscompany.reportapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.util.getFeedInfo;
import com.kisscompany.reportapp.util.uploadImageGoogle;
import com.soundcloud.android.crop.Crop;
import com.soundcloud.android.crop.CropImageActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class Camera extends AppCompatActivity {

    String imageLocation;
    int widthInDP;
    TextView send,typeText;
    File output;
    ImageView reTake;
    ImageView colorTab;
    HorizontalScrollView scroll;
    static final int Cam_request = 1;
    String color = null;
    String pic = null;
    ImageView inIm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        widthInDP = Math.round(dm.widthPixels);

        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir=
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        output=new File(dir, "CameraContentDemo.jpeg");
        camera.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(output));
        scroll = (HorizontalScrollView)findViewById(R.id.Actype);
        startActivityForResult(camera,Cam_request);
        send = (TextView)findViewById(R.id.sendTxt);
        colorTab = (ImageView)findViewById(R.id.colorTab);
        inIm = (ImageView)findViewById(R.id.tempImg);
        typeText = (TextView)findViewById(R.id.typeTxt);
        reTake = (ImageView)findViewById(R.id.reTake);
        reTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent intent = new Intent();
                setResult(10);
                finish();
            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(color!=null) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("RESULT_STRING", output.getAbsolutePath());
                    resultIntent.putExtra("Color", color);
                    resultIntent.putExtra("ImId",pic);
                    setResult(RESULT_OK, resultIntent);

                    finish();
                }
                else
                {
                    Toast.makeText(Camera.this,"Please choose incident type",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    @Override
    public void onActivityResult(int request_code,int result_code,Intent data)
    {

        if (request_code == Crop.REQUEST_CROP&& result_code == CropImageActivity.RESULT_OK) {


            try {
                rotateImage(90);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            widthInDP = Math.round(dm.widthPixels);
            inIm.getLayoutParams().height = widthInDP;
            Bitmap temp = BitmapFactory.decodeFile(output.getAbsolutePath());

            inIm.setImageBitmap(temp);
            uploadImageGoogle get = null;
            get = new uploadImageGoogle(temp,"",this,inIm);
            get.execute("https://www.googleapis.com/upload/storage/v1/b/traffy_image/o?uploadType=media&name=");


        }
        else if(request_code == Cam_request && result_code != RESULT_CANCELED){
            /*String[] projection = new String[]{
                    MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.ImageColumns.DATE_TAKEN,
                    MediaStore.Images.ImageColumns.MIME_TYPE
            };
            final Cursor cursor = getContentResolver()
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                            null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

            // Put it in the image view
            if (cursor.moveToFirst()) {
                imageLocation = cursor.getString(1);
                File imageFile = new File(imageLocation);
                if (imageFile.exists()) {   // TODO: is there a better way to do this?
                    inIm.setImageBitmap(BitmapFactory.decodeFile(imageLocation));
                    Uri uri = Uri.fromFile(imageFile);
                    Crop.of(uri,uri).asSquare().start(this);
                }
            }*/

            Crop.of(Uri.fromFile(output),Uri.fromFile(output)).asSquare().start(this);

            //rotateImage(90);
            //inIm.getLayoutParams().height = widthInDP;
         /*   getFeedInfo feed = new getFeedInfo(inIm,this);
            feed.execute("https://storage.googleapis.com/traffy_image/353086.png");*/


        }
        else
        {
            setResult(RESULT_CANCELED);
            finish();
        }


    }


    public void rotateImage(float angle) throws FileNotFoundException {
        Bitmap temp = BitmapFactory.decodeFile(output.getAbsolutePath());
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        FileOutputStream out = new FileOutputStream(output);
        temp  = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(),matrix,true
                );
        temp.compress(Bitmap.CompressFormat.JPEG,100,out);
    }

    public void Click(View v)
    {
        switch(v.getId()){
            case R.id.type1:  color = "#4D00ff00";
                break;
            case R.id.type2: color = "#4Dffff00";
                break;
        }
        pic = v.getTag().toString();
        colorTab.setBackgroundColor(Color.parseColor(color));
        typeText.setText(v.getTag().toString());


    }
    public Bitmap scaleDown(Bitmap original)
    {
        int width  = original.getWidth();
        int height = original.getHeight();
        int newWidth = width;
        int newHeight = width;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);

        original = Bitmap.createBitmap(original,0,0,newWidth,newHeight,null,false);
        inIm.setImageBitmap(original);
        //Toast.makeText(getBaseContext(),NewImg.getWidth()+" "+NewImg.getHeight(),Toast.LENGTH_SHORT).show();
        //BitmapDrawable bmd = new BitmapDrawable(NewImg);
        //inIm.setImageDrawable(bmd);
        return original;
    }

}
