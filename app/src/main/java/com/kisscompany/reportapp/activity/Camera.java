package com.kisscompany.reportapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kisscompany.reportapp.R;
import com.soundcloud.android.crop.Crop;
import com.soundcloud.android.crop.CropImageActivity;

import java.io.File;

public class Camera extends AppCompatActivity {

    String imageLocation;
    TextView send;
    HorizontalScrollView scroll;
    static final int Cam_request = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        scroll = (HorizontalScrollView)findViewById(R.id.Actype);
        startActivityForResult(camera,Cam_request);
        send = (TextView)findViewById(R.id.sendTxt);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK,new Intent().putExtra("RESULT_STRING",imageLocation));
                finish();
            }
        });
    }
    @Override
    public void onActivityResult(int request_code,int result_code,Intent data)
    {

        if (request_code == Crop.REQUEST_CROP&& result_code == CropImageActivity.RESULT_OK) {
            ImageView inIm = (ImageView)findViewById(R.id.tempImg);

            Bitmap temp = BitmapFactory.decodeFile(imageLocation);


            Bitmap finalPic = rotateImage(temp,90);

            inIm.setImageBitmap(finalPic);



        }
        else if(request_code == Cam_request && result_code != RESULT_CANCELED){
            String[] projection = new String[]{
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
                    Uri uri = Uri.fromFile(imageFile);
                    Crop.of(uri,uri).asSquare().start(this);
                }
            }
        }
        else
        {
            setResult(RESULT_CANCELED);
            finish();
        }


    }


    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    public void Click(View v)
    {

    }
}
