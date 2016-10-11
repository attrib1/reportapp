package com.kisscompany.reportapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.util.PostClass;
import com.kisscompany.reportapp.util.sendFeedInfo;
import com.soundcloud.android.crop.Crop;
import com.soundcloud.android.crop.CropImageActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Camera extends AppCompatActivity {

    int widthInDP;
    TextView next;
    File output;
    static final int Cam_request = 1;
    TouchedView touch;
    ImageView inIm,painting,sticker;
    View currentTool = null;
    Bitmap resultImage;
    String exifOrientation;
    float dx,dy;
    float x,y;
    final int MODIFY_PICTURE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_modify);

        Main_menu.title.setText("เลือกประเภท");

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        widthInDP = Math.round(dm.widthPixels);


        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        output=new File(dir, "CameraContentDemo.jpeg");
        camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
        startActivityForResult(camera,Cam_request);
      //  sticker = (ImageView)findViewById(R.id.sticker);
        init();


     /*   inIm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        sticker.setEnabled(true);
                        sticker.setVisibility(View.VISIBLE);
                        Log.d("touched",""+sticker.getScaleX()+" "+sticker.getScaleY());

                        x = event.getX()+v.getX()-(float)sticker.getWidth()/2;
                        y = event.getY()+v.getY()-(float)sticker.getHeight()/2;
                        if(x < v.getWidth()-sticker.getWidth()&& x >= 0 && y < v.getY()+v.getHeight()-sticker.getHeight() && y > v.getY()) {
                            sticker.setX(x);
                            sticker.setY(y);
                        }
                        dx = x-v.getX();
                        dy = y-v.getY();
                    }
                    break;
                    case MotionEvent.ACTION_MOVE: {
                    }
                    break;
                    case MotionEvent.ACTION_UP: {

                        //your stuff
                    }
                }

                return false;
            }
        });*/

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Camera.this,Picture_modify.class);
                intent.putExtra("picture",output.getAbsolutePath());
                try {
                    resultImage.compress(Bitmap.CompressFormat.JPEG,100,new FileOutputStream(output.getAbsolutePath()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent,MODIFY_PICTURE);
            }

        });


    }
    @Override
    public void onActivityResult(int request_code,int result_code,Intent data)
    {

        if (request_code == Crop.REQUEST_CROP&& result_code == CropImageActivity.RESULT_OK) {///result from crop

            inIm.getLayoutParams().height = widthInDP;
            resultImage = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(output.getAbsolutePath()), 640, 640, true);
            switch(exifOrientation) {
                case "6":
                    resultImage = rotateImage(resultImage, 90);
                    break;
                case "3":
                    resultImage =rotateImage(resultImage, 180);
                    break;
                case "8":
                    resultImage = rotateImage(resultImage, 270);
                    break;
                default:
                    break;
            }
            inIm.setImageBitmap(resultImage);
            touch.setBitmap(resultImage);
        }
        else if(request_code == Cam_request && result_code == RESULT_OK && output!=null ){ ///result from camera
            ExifInterface ei = null;
            try {

                ei = new ExifInterface(output.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            exifOrientation = ei
                    .getAttribute(ExifInterface.TAG_ORIENTATION);
            Crop.of(Uri.fromFile(output),Uri.fromFile(output)).asSquare().start(this);
        }
        else if(request_code == MODIFY_PICTURE && result_code == RESULT_OK)
        {
            finish();
        }

    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    private Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage) {//combine 2 image
        Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        canvas.drawBitmap(secondImage, dx, dy, null);
        return result;
    }
    private void init()
    {

        touch = (TouchedView)findViewById(R.id.touchView);
        touch.setVisibility(View.INVISIBLE);
        touch.setEnabled(false);
        touch.getLayoutParams().height = widthInDP;
        touch.bringToFront();
        next = (TextView)findViewById(R.id.nextButton);
        inIm = (ImageView)findViewById(R.id.tempImg);
        painting = (ImageView)findViewById(R.id.painting);
        sticker = (ImageView)findViewById(R.id.sticker);

        painting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundColor(Color.parseColor("#4Da8a8a8"));
                sticker.setBackgroundColor(0x00000000);
                touch.setEnabled(true);
                touch.setVisibility(View.VISIBLE);
            }
        });
        sticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundColor(Color.parseColor("#4Da8a8a8"));
                painting.setBackgroundColor(0x00000000);
                touch.setEnabled(false);
                touch.setVisibility(View.INVISIBLE);
            }
        });
    }
}
