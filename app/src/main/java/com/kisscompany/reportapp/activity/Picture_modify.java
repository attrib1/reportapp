package com.kisscompany.reportapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kisscompany.reportapp.R;
import com.kisscompany.reportapp.util.PostClass;
import com.kisscompany.reportapp.util.sendFeedInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Picture_modify extends AppCompatActivity {

    final int FACEBOOK_REQUEST = 7;
    final int LOCATION_REQUEST = 3;
    int widthInDP;
    String pic,location;

    ImageView picture,backPress;
    Bitmap bitmap;
    TextView chooseLocation,send;
    View currentType;
    ProgressDialog progress;
    HorizontalScrollView scroll;
    EditText description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);

        getScreen();
        init();
        bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("picture"));
        picture.setImageBitmap(bitmap);
        picture.getLayoutParams().height = widthInDP;
    }
    @Override
    public void onActivityResult(int request_code,int result_code,Intent data)
    {
        if(request_code == LOCATION_REQUEST && result_code ==RESULT_OK)//result from choose location OK
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
            Log.d("location_bitch",location);

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
    }
    private void init()
    {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//hide keyboard
        picture = (ImageView)findViewById(R.id.tempImg);
        send = (TextView)findViewById(R.id.send);
        scroll = (HorizontalScrollView)findViewById(R.id.Actype);
        description = (EditText)findViewById(R.id.infoText);
        chooseLocation = (TextView)findViewById(R.id.chooseLocation);
        backPress = (ImageView)findViewById(R.id.backButton);

        scroll.requestFocus();
        picture.requestFocus();


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Main_menu.loginFlag == 1) {//if already login
                    if(currentType ==null)
                        Toast.makeText(Picture_modify.this, "เลือกประเภทปัญหา", Toast.LENGTH_SHORT).show();
                    else if(location ==null)
                        Toast.makeText(Picture_modify.this, "เลือกสถานที่", Toast.LENGTH_SHORT).show();
                    else {
                        progress = new ProgressDialog(Picture_modify.this);
                        Upload();
                        PostClass sendPost = createPost();
                        sendFeedInfo send = new sendFeedInfo(sendPost, Picture_modify.this);
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
                    Intent intent = new Intent(Picture_modify.this,LoginActivity.class);
                    Toast.makeText(getBaseContext(),"Please login to facebook",Toast.LENGTH_SHORT).show();
                    startActivityForResult(intent,FACEBOOK_REQUEST);
                }
            }
        });
        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        chooseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(Picture_modify.this,chooseLocation.class);
                startActivityForResult(intent,LOCATION_REQUEST);
            }
        });



    }
    public PostClass createPost()
    {
        // Bitmap b = ((BitmapDrawable)sticker.getDrawable()).getBitmap();
        //resultImage = createSingleImageFromMultipleImages(resultImage,Bitmap.createScaledBitmap(b,b.getWidth()/2,b.getHeight()/2,false));
        Log.d("location",chooseLocation.getText().toString());
        PostClass newPost = new PostClass(bitmap,getDate() ,chooseLocation.getText().toString(),description.getText().toString(),"ID5580907",pic);

        return newPost;
    }
    public String getDate()
    {
        Date currentDateTime = new Date(); //get date and time2
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(currentDateTime);
    }
    public void Click(View v)//when blick at type picture
    {
        if(currentType!=null)
            currentType.setBackgroundColor(0x00000000);
        currentType = v;
        currentType.setBackgroundColor(Color.parseColor("#4Da8a8a8"));
        pic = v.getTag().toString();
    }
    public void Upload()
    {
        progress.setCancelable(false);
        progress.setMessage("Uploading ...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
    }
    private void getScreen()
    {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        widthInDP = Math.round(dm.widthPixels);
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
