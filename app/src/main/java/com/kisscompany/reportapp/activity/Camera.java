package com.kisscompany.reportapp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.support.v4.app.FragmentTabHost;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.util.PostClass;
import com.kisscompany.reportapp.util.getFeedInfo;
import com.kisscompany.reportapp.util.sendFeedInfo;
import com.kisscompany.reportapp.util.uploadImageGoogle;
import com.soundcloud.android.crop.Crop;
import com.soundcloud.android.crop.CropImageActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Camera extends AppCompatActivity {

    String imageLocation;
    int widthInDP;
    TextView send,typeText,cancel;
    File output;
    ImageView colorTab;
    HorizontalScrollView scroll;
    static final int Cam_request = 1;
    String color = null;
    String pic = null;
    ImageView inIm;
    TextView info;
    ProgressDialog progress;
    View currentType = null;
    Bitmap resultImage;
    final String CLIENT_ID = "CWIB5QARTPRLLQIVCYKM5MVXYSQCRGYM1VZ31AJ4DCIMKEJ";
    final String CLIENT_SECRET = "4SDDGS3PYQ5JQOX4WU0XJTXAKH1HSHEFQ1I21V4KHDR15PG";
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
        scroll.requestFocus();
        send = (TextView)findViewById(R.id.sendTxt);
        inIm = (ImageView)findViewById(R.id.tempImg);
        info = (TextView)findViewById(R.id.infoText);
        info.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                info.setText("");
                return false;
            }
        });
        cancel = (TextView)findViewById(R.id.cancelButt);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*setResult(RESULT_CANCELED);
                finish();*/
                Intent intent = new Intent(Camera.this,chooseLocation.class);
                startActivity(intent);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pic != null) {
                    progress = new ProgressDialog(Camera.this);
                    Upload();
                    PostClass sendPost = createPost();
                    sendFeedInfo send = new sendFeedInfo(sendPost, Camera.this);
                    send.setCustomEventListener(new sendFeedInfo.OnRefreshFinishListener() {
                        @Override
                        public void onRefreshFinished() {
                            progress.dismiss();
                            setResult(RESULT_OK);
                            finish();

                        }
                    });
                    send.execute("http://cloud.traffy.in.th/attapon/API/private_apis/report.php");
                    Toast.makeText(Camera.this, "Done posting", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(Camera.this,"เลือกประเภทปัญหา",Toast.LENGTH_SHORT).show();
            }

        });


    }
    @Override
    public void onActivityResult(int request_code,int result_code,Intent data)
    {

        if (request_code == Crop.REQUEST_CROP&& result_code == CropImageActivity.RESULT_OK) {

            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            widthInDP = Math.round(dm.widthPixels);
            inIm.getLayoutParams().height = widthInDP;
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(output.getAbsolutePath(),options);
            options.inSampleSize = calculateInSampleSize(options, 400,400);
            options.inJustDecodeBounds = false;
            resultImage = BitmapFactory.decodeFile(output.getAbsolutePath(),options);
            inIm.setImageBitmap(resultImage);

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
            Log.d("cancel","cancel");
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
        v.setBackgroundColor(Color.parseColor("#4Da8a8a8"));
        pic = v.getTag().toString();

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
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
    public void Upload()
    {
        progress.setCancelable(false);
        progress.setMessage("Uploading ...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //progress.setProgress(0);
        //progress.setMax(100);
        progress.show();
    }
    public PostClass createPost()
    {
        PostClass newPost = new PostClass(resultImage,getDate() ,getAddress(),info.getText().toString(),"ID5580907",pic);

        return newPost;
    }
    public String getDate()
    {
        Date currentDateTime = new Date(); //get date and time2
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(currentDateTime);
    }
    public String getAddress()
    {
        double long1,lati1;
        lati1 = Double.parseDouble(Main_menu.lat);
        long1 = Double.parseDouble(Main_menu.lng);
        if(lati1>0 && long1>0)
        {
            Geocoder geocode = new Geocoder(this, Locale.getDefault());
            List<Address> addresses;

            try {
                addresses = geocode.getFromLocation(lati1,long1, 1);

                String Addres_ = addresses.get(0).getAddressLine(0);
                String Country = addresses.get(0).getCountryName();
                String City = addresses.get(0).getAdminArea();
                String street = addresses.get(0).getPostalCode();
                String df = addresses.get(0).getLocality();
                String af = addresses.get(0).getSubLocality();
                return (Addres_+" "+af+" "+df+" "+City+" "+street);



            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }//if closing. . .
        else
        {
            Toast.makeText(this, "No Vlaue", Toast.LENGTH_LONG).show();
            return null;
        }

    return null;
    }

}
