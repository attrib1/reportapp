package com.kisscompany.reportapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
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
import java.util.List;
import java.util.Locale;


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
    View currentType = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        widthInDP = Math.round(dm.widthPixels);

        if(savedInstanceState == null) {
            // everything else that doesn't update UI
            verifyStoragePermissions(this);
            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File dir=
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            output=new File(dir, "CameraContentDemo.jpeg");
            camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
            startActivityForResult(camera,Cam_request);
        }
        scroll = (HorizontalScrollView)findViewById(R.id.Actype);
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
                    Log.d("resultOk1","ok");
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


          /*  try {
                rotateImage(90);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }*/
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            widthInDP = Math.round(dm.widthPixels);
            inIm.getLayoutParams().height = widthInDP;
           /* final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(output.getAbsolutePath(),options);
            options.inSampleSize = calculateInSampleSize(options, 400,400);
            options.inJustDecodeBounds = false;*/

            inIm.setImageBitmap(BitmapFactory.decodeFile(output.getAbsolutePath()));

        }
        else if(request_code == Cam_request && result_code != RESULT_CANCELED){

            ExifInterface ei = null;
            try {

                ei = new ExifInterface(output.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            String exifOrientation = ei
                    .getAttribute(ExifInterface.TAG_ORIENTATION);
            Bitmap bitmap = BitmapFactory.decodeFile(output.getAbsolutePath());
            switch(exifOrientation) {
                case "6":
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case "3":
                    bitmap =rotateImage(bitmap, 180);
                    break;
                case "8":
                    bitmap = rotateImage(bitmap, 270);
                    break;
                default:
                    break;
            }
            try {
                FileOutputStream fileout = new FileOutputStream(output);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileout);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Crop.of(Uri.fromFile(output),Uri.fromFile(output)).asSquare().start(this);
           // Log.d("ImageSize",String.valueOf(output.length()));
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
        if(currentType!=null)
            currentType.setBackgroundColor(0x00000000);
        currentType = v;
        v.setBackgroundColor(Color.parseColor("#000000"));
        switch(v.getId()){
            case R.id.type1:  color = "#4D00ff00";
                typeText.setText("ปัญหา1");

                break;
            case R.id.type2: color = "#4Dffff00";
                typeText.setText("ปัญหา2");
                break;
            case R.id.type3: color = "#4Dafff00";
                typeText.setText("ปัญหา3");
                break;
            case R.id.type4: color = "#4Dca1265";
                typeText.setText("ปัญหา4");
                break;
            case R.id.type5: color = "#4Dfcfa00";
                typeText.setText("ปัญหา5");
                break;
            case R.id.type6: color = "#4Da0fb00";
                typeText.setText("ปัญหา6");
                break;
        }
        pic = v.getTag().toString();
        colorTab.setBackgroundColor(Color.parseColor(color));
       // typeText.setText(v.getTag().toString());


    }
    public Bitmap scaleDown(Bitmap original)
    {
        int width  = original.getWidth();
        int height = original.getHeight();
        int newWidth = 200;
        int newHeight = 200;

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
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


}
