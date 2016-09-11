package com.kisscompany.reportapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Camera extends AppCompatActivity {

    String location = null;
    int widthInDP;
    TextView send,cancel;
    File output;
    HorizontalScrollView scroll;
    static final int Cam_request = 1;
    TouchedView touch;
    String color = null;
    String pic = null;
    ImageView inIm;
    TextView info,chooseLocation;
    ProgressDialog progress;
    View currentType = null;
    Bitmap resultImage;
    final int LOCATION_REQUEST = 3;
    String exifOrientation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);

        Main_menu.title.setText("เลือกประเภท");

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        widthInDP = Math.round(dm.widthPixels);

        if(savedInstanceState == null) {
            // everything else that doesn't update UI
            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File dir=
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            output=new File(dir, "CameraContentDemo.jpeg");
            camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
            startActivityForResult(camera,Cam_request);
        }
        touch = (TouchedView)findViewById(R.id.touchView);
        touch.getLayoutParams().height = widthInDP;
        touch.bringToFront();
    //    touch.setVisibility(View.INVISIBLE);
        scroll = (HorizontalScrollView)findViewById(R.id.Actype);
        scroll.requestFocus();

        chooseLocation = (TextView)findViewById(R.id.chooseLocation);
        chooseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(Camera.this,chooseLocation.class);
                startActivityForResult(intent,LOCATION_REQUEST);
            }
        });
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
        info.setVisibility(View.INVISIBLE);
        cancel = (TextView)findViewById(R.id.cancelButt);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("camCancel","cancel");
                setResult(RESULT_CANCELED);
                finish();

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Main_menu.loginFlag == 1) {//if already login
                    if(currentType ==null)
                        Toast.makeText(Camera.this, "เลือกประเภทปัญหา", Toast.LENGTH_SHORT).show();
                    else if(location ==null)
                        Toast.makeText(Camera.this, "เลือกสถานที่", Toast.LENGTH_SHORT).show();
                    else {
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
                    }

                }
                else// if not login yet
                {
                    Intent intent = new Intent(Camera.this,LoginActivity.class);
                    Toast.makeText(getBaseContext(),"Please login to facebook",Toast.LENGTH_SHORT).show();
                    startActivityForResult(intent,7);
                }
            }

        });


    }
    @Override
    public void onActivityResult(int request_code,int result_code,Intent data)
    {

        if (request_code == Crop.REQUEST_CROP&& result_code == CropImageActivity.RESULT_OK) {///result from crop

            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            widthInDP = Math.round(dm.widthPixels);
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
            //resultImage= resultImage.copy(Bitmap.Config.ARGB_8888, true);
            inIm.setImageBitmap(resultImage);
            touch.setBitmap(resultImage);
        }
        else if(request_code == Cam_request && result_code == RESULT_OK   ){ ///result from camera
            Log.d("result_code","result = "+result_code);
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
        else if(request_code == LOCATION_REQUEST && result_code ==RESULT_OK)//result from choose location OK
        {
            Bundle bundle = data.getBundleExtra("location");
            location = bundle.getString("name")+"\n";
            if(bundle.getString("state").length() > 0)
                location = location + bundle.getString("state")+" ";
            if(bundle.getString("city").length() > 0)
                location = location + bundle.getString("city")+" ";
            if(bundle.getString("postal").length() > 0)
                location = location + bundle.getString("postal")+" ";
            if(bundle.getString("country").length() > 0)
                location = location + bundle.getString("country")+" ";
            chooseLocation.setText(location);

        }
        else if(request_code == LOCATION_REQUEST&& result_code != RESULT_OK)/// result from facebook login
        {
            Log.d("not ok","not ok");
        }
        else if(request_code == 7&& result_code == RESULT_OK)/// result from facebook login
        {
            Main_menu.loginFlag = 1;
            setLoginFlag();
            Main_menu.profilePictureView.setProfileId(LoginActivity.userName);
            Main_menu.profileName.setText(LoginActivity.facebookName);
            Main_menu.profileString = LoginActivity.facebookName;
            Main_menu.id = LoginActivity.userName;
            Main_menu.profileUrl = LoginActivity.profilePicUrl;
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




    public void Click(View v)//when blick at type picture
    {
        if(currentType!=null)
            currentType.setBackgroundColor(0x00000000);
        currentType = v;
        v.setBackgroundColor(Color.parseColor("#4Da8a8a8"));
        pic = v.getTag().toString();
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
        PostClass newPost = new PostClass(resultImage,getDate() ,chooseLocation.getText().toString(),info.getText().toString(),"ID5580907",pic);

        return newPost;
    }
    public String getDate()
    {
        Date currentDateTime = new Date(); //get date and time2
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(currentDateTime);
    }
  /*  public void setProfilePic() throws IOException {

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
                Main_menu.avatar.setImageBitmap(temp);
            }
        });

    }*/
    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
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
