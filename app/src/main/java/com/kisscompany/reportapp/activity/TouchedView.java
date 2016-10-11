package com.kisscompany.reportapp.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by kak on 9/7/2016.
 */
public class TouchedView extends View {
    private Paint paint = new Paint();
    private Path path = new Path();
    private Path mPath = new Path();
    Canvas  mCanvas;
    float widthInDP,heightInDP;
    public TouchedView(Context context, AttributeSet attrs){
        super(context, attrs);
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        widthInDP = Math.round(dm.widthPixels);
        heightInDP = Math.round(dm.heightPixels);

    }
    @Override
    protected void onDraw(Canvas canvas)
    {
       // canvas.drawPath(path,paint);
        mCanvas.drawPath(mPath, paint);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float xPos = event.getX();
        float yPos = event.getY();

        if(xPos<widthInDP&&yPos<widthInDP) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(xPos, yPos);
                    mPath.moveTo(xPos *(640/widthInDP), yPos*(640/widthInDP) );
                    return true;
                case MotionEvent.ACTION_MOVE:
                    path.lineTo(xPos, yPos);
                    mPath.lineTo(xPos *(640/widthInDP), yPos*(640/widthInDP));
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                default:
                    return false;
            }
            invalidate();
            return true;
        }
        else
            return false;
    }
    public void setBitmap(Bitmap bit)
    {
        mCanvas = new Canvas(bit);
    }
}

