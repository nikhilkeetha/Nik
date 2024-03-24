package com.nik.Services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.nik.R;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.ServiceConfigurationError;

public class WidgetService extends Service {

    int LAYOUT_FLAG;
    View mFloatingView;
    WindowManager windowManager;
    ImageView imageViewClose;
    RelativeLayout layoutWidget;

    
    float height,width;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        //inflate widget layout
        mFloatingView= LayoutInflater.from(this).inflate(R.layout.layout_widget,null);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //initial postion
        layoutParams.gravity= Gravity.TOP|Gravity.RIGHT;
        layoutParams.x=0;
        layoutParams.y=100;

        //layout params for close button
        WindowManager.LayoutParams imageParams = new WindowManager.LayoutParams(140,
                140,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        imageParams.gravity=Gravity.BOTTOM|Gravity.CENTER;
        imageParams.y=100;

        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        imageViewClose=new ImageView(this);
        imageViewClose.setImageResource(R.drawable.close);
        imageViewClose.setVisibility(View.INVISIBLE);
        windowManager.addView(imageViewClose,imageParams);
        windowManager.addView(mFloatingView,layoutParams);
        mFloatingView.setVisibility(View.VISIBLE);


        height=windowManager.getDefaultDisplay().getHeight();
        width=windowManager.getDefaultDisplay().getWidth();

        LottieAnimationView animationView = mFloatingView.findViewById(R.id.Anim);
        animationView.setScale(1.0f);
        animationView.setAnimation(R.raw.nice);
        animationView.setRepeatCount(LottieDrawable.INFINITE);
        animationView.playAnimation();

        layoutWidget=(RelativeLayout) mFloatingView.findViewById(R.id.layout_widget);

        //drag movement for widget
        layoutWidget.setOnTouchListener(new View.OnTouchListener() {
            int initialX,initialY;
            float initialTouchX,initialTouchY;
            long startClickTime;
            int MAX_CLICK_DURATION=200;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:

                        startClickTime= Calendar.getInstance().getTimeInMillis();
                        imageViewClose.setVisibility(View.VISIBLE);

                        initialX= layoutParams.x;
                        initialY= layoutParams.y;

                        initialTouchX=event.getRawX();
                        initialTouchY=event.getRawY();

                        return true;
                    case MotionEvent.ACTION_UP:
                        long clickDuration=Calendar.getInstance().getTimeInMillis()-startClickTime;
                        imageViewClose.setVisibility(View.GONE);

                        layoutParams.x=initialX+(int)(initialTouchX-event.getRawX());
                        layoutParams.y=initialY+(int)(event.getRawY()-initialTouchY);

                        if(clickDuration<MAX_CLICK_DURATION)
                        {
                            Toast.makeText(WidgetService.this, "test", Toast.LENGTH_SHORT).show();
                        }else {
                            //remove widget
                            if(layoutParams.y>(height*0.6))
                            {
                                stopSelf();
                            }
                        }

                        return true;
                    case MotionEvent.ACTION_MOVE:

                        //cal X&Y co-ordinates of view
                        layoutParams.x=initialX+(int)(initialTouchX-event.getRawX());
                        layoutParams.y=initialY+(int)(event.getRawY()-initialTouchY);

                        //update layout with new
                        windowManager.updateViewLayout(mFloatingView,layoutParams);

                        if(layoutParams.y>(height*0.6))
                        {
                            imageViewClose.setImageResource(R.drawable.close_red);
                        }else {
                            imageViewClose.setImageResource(R.drawable.close);
                        }
                        return true;
                }
                return false;
            }
        });



        return START_STICKY;
     }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mFloatingView!=null)
        {
            windowManager.removeView(mFloatingView);
        }

        if(imageViewClose!=null)
        {
            windowManager.removeView(imageViewClose );
        }
    }
}
